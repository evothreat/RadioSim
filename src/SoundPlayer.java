import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SoundPlayer {
    private Clip clip;
    private float volume;  // Volume attribute stored as a float between 0.0f (silent) and 1.0f (full volume)

    public SoundPlayer() {
        volume = 0.75f;  // Default volume is at 75%
    }

    public void playAndLoop(String filename) {
        play(filename, true);
    }

    public void play(String filename, boolean loop) {
        try {
            // Close previous clip if it's still open
            if (clip != null && clip.isOpen()) {
                clip.close();
            }
            URL url = getClass().getResource(filename);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioIn);

            applyVolume();  // Apply the volume before playing the clip

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playWithNoise(String filename, double distance) {
        try {
            // Load song audio stream
            AudioInputStream songStream = AudioSystem.getAudioInputStream(new File(filename));

            // Load white noise audio stream
            AudioInputStream noiseStream = AudioSystem.getAudioInputStream(new File("whitenoise.wav"));

            // Define an array to hold the bytes from the audio input stream
            byte[] songBytes = new byte[songStream.available()];
            byte[] noiseBytes = new byte[noiseStream.available()];

            songStream.read(songBytes);
            noiseStream.read(noiseBytes);

            // Calculate the mixing factor based on distance
            float mixFactor = (float) distance / 10.0f;  // Assume max distance is 10.0 for full noise
            if (mixFactor > 1.0f) mixFactor = 1.0f;  // Clamp to 1.0

            // Mix the bytes of song and noise
            for (int i = 0; i < songBytes.length && i < noiseBytes.length; i++) {
                songBytes[i] = (byte) (songBytes[i] * (1 - mixFactor) + noiseBytes[i] * mixFactor);
            }

            // Convert the mixed bytes back to an audio input stream
            ByteArrayInputStream mixedInputStream = new ByteArrayInputStream(songBytes);
            AudioInputStream mixedStream = new AudioInputStream(mixedInputStream, songStream.getFormat(), songBytes.length / songStream.getFormat().getFrameSize());

            // Play the mixed audio
            clip = AudioSystem.getClip();
            clip.open(mixedStream);
            clip.start();

            // Close streams
            songStream.close();
            noiseStream.close();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void setVolume(float volume) {
        if (volume < 0f || volume > 1f) {
            throw new IllegalArgumentException("Volume value must be between 0 and 1");
        }
        this.volume = volume;
        if (clip != null && clip.isOpen()) {  // Adjust volume even if the clip is not running, but it's open
            applyVolume();
        }
    }

    private void applyVolume() {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    public float getVolume() {
        return volume;
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}
