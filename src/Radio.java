import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Radio {
    private static final float MIN_FREQUENCY = 88.0f; // Minimum allowable frequency in MHz
    private static final float MAX_FREQUENCY = 108.0f; // Maximum allowable frequency in MHz
    private static final int MIN_VOLUME = 0; // Minimum allowable volume level
    private static final int MAX_VOLUME = 100; // Maximum allowable volume level
    private static final float MAX_NOISE_DISTANCE = 2.5f; // Maximum distance from a station to play full noise

    private float frequency; // Current radio frequency
    private boolean isOn;     // Radio power state (On/Off)

    private final SoundPlayer soundPlayer;  // Sound player object
    private final TreeMap<Float, String> stations; // Map of stations with associated frequencies and song files
    private final Map<Float, Integer> stationProgress; // Map to store start positions for each station

    // Constructor
    public Radio() {
        soundPlayer = new SoundPlayer();

        // Initialize stations map and add some frequencies with associated song files
        stations = new TreeMap<>();
        stations.put(88.0f, "resources/audio01.wav");
        stations.put(93.0f, "resources/audio02.wav");
        stations.put(98.0f, "resources/audio03.wav");
        stations.put(103.0f, "resources/audio04.wav");
        stations.put(108.0f, "resources/audio05.wav");

        // Initialize station progress map
        stationProgress = new HashMap<>();
    }

    // Method to turn on the radio
    public void turnOn() {
        isOn = true;
        tune(MIN_FREQUENCY); // Tune to the default frequency
        System.out.println("Radio is now ON");
    }

    // Method to turn off the radio
    public void turnOff() {
        soundPlayer.stop(); // Stop any currently playing sound
        isOn = false;
        System.out.println("Radio is now OFF");
    }

    // Method to check if the radio is on
    public boolean isOn() {
        return isOn;
    }

    // Method to get the current radio frequency
    public float getFrequency() {
        return frequency;
    }

    // Method to get the current radio volume
    public int getVolume() {
        return (int) (soundPlayer.getVolume() * 100);
    }

    // Method to change the radio volume
    public void changeVolume(int newVolume) {
        if (isOn) {
            if (newVolume >= MIN_VOLUME && newVolume <= MAX_VOLUME) {
                soundPlayer.setVolume(newVolume / 100f);
                System.out.println("Volume set to " + newVolume);
            } else {
                System.out.println("Invalid volume level: " + newVolume + ". Must be between " + MIN_VOLUME + " and " + MAX_VOLUME + ".");
            }
        } else {
            System.out.println("Radio is off. Please turn it on to adjust the volume.");
        }
    }

    // Method to get the nearest station to a given frequency
    private float getNearestStation(float frequency) {
        // Find the nearest key (frequency) in the TreeMap
        Float lowerKey = stations.floorKey(frequency);
        Float higherKey = stations.ceilingKey(frequency);

        if (lowerKey == null) return higherKey;
        if (higherKey == null) return lowerKey;

        // Return the nearest key
        return (frequency - lowerKey < higherKey - frequency) ? lowerKey : higherKey;
    }

    public void tune(float newFrequency) {
        if (!isOn) {
            System.out.println("Radio is off. Please turn it on to tune.");
            return;
        }

        if (newFrequency < MIN_FREQUENCY || newFrequency > MAX_FREQUENCY) {
            System.out.println("Invalid frequency: " + newFrequency + " MHz. Must be between " + MIN_FREQUENCY + " and " + MAX_FREQUENCY + " MHz.");
            return;
        }

        float nearestStation = getNearestStation(frequency);

        if (soundPlayer.isPlaying()) {
            stationProgress.put(nearestStation, soundPlayer.getPlaybackTime());
            soundPlayer.stop();
        }

        frequency = newFrequency;
        System.out.println("Tuned to " + frequency + " MHz");

        float distance = Math.abs(nearestStation - frequency);

        // If exact match, play the station song
        if (distance == 0) {
            soundPlayer.play(stations.get(nearestStation), stationProgress.getOrDefault(nearestStation, 0), true);
        } else {
            // Convert distance to intensity
            float intensity = Math.min(distance / MAX_NOISE_DISTANCE, 1.0f);

            // Play nearest station song but with added noise
            // The closer to the station, the lesser the noise
            soundPlayer.playWithNoise(
                    stations.get(nearestStation),
                    intensity,
                    stationProgress.getOrDefault(nearestStation, 0),
                    true
            );
        }
    }
}
