import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

public class SoundPlayer {
    private Clip clip;  // Current audio clip
    private float volume;  // Volume attribute stored as a float between 0.0f (silent) and 1.0f (full volume)

    // Constructor
    public SoundPlayer() {
        volume = 0.75f;
    }

    // Method to play a sound file
    public void play(String filename, boolean loop) {
        URL url = getClass().getResource(filename);
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            playStream(audioIn, loop);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    // Method to play a sound file with added noise
    // noiseIntensity is a float between 0.0f (no noise) and 1.0f (full noise)
    public void playWithNoise(String filename, float noiseIntensity, boolean loop) {
        if (noiseIntensity < 0f || noiseIntensity > 1f) {
            throw new IllegalArgumentException("Noise intensity value must be between 0 and 1");
        }

        try {
            URL url = getClass().getResource(filename);
            AudioInputStream songStream = AudioSystem.getAudioInputStream(url);

            byte[] songBytes = new byte[songStream.available()];
            songStream.read(songBytes);

            for (int i = 0; i < songBytes.length; i++) {
                songBytes[i] += (byte) (noiseIntensity * (byte) (Math.random() * 256 - 128)); // Adjust by noise intensity
            }

            ByteArrayInputStream mixedInputStream = new ByteArrayInputStream(songBytes);
            AudioInputStream mixedStream = new AudioInputStream(mixedInputStream, songStream.getFormat(), songBytes.length / songStream.getFormat().getFrameSize());

            playStream(mixedStream, loop);

            songStream.close();
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }


    // Method to play an audio stream
    private void playStream(AudioInputStream audioStream, boolean loop) {
        try {
            stop();
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            applyVolume();

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    // Method to set the current volume
    public void setVolume(float volume) {
        if (volume < 0f || volume > 1f) {
            throw new IllegalArgumentException("Volume value must be between 0 and 1");
        }
        this.volume = volume;
        if (clip != null && clip.isOpen()) {
            applyVolume();
        }
    }

    // Method to apply the current volume to the clip
    private void applyVolume() {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    // Method to get the current volume
    public float getVolume() {
        return volume;
    }

    // Method to stop the current audio clip
    public void stop() {
        if (clip != null && clip.isOpen()) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.close();
        }
    }
}
