import java.util.TreeMap;

public class Radio {
    private static final float MIN_FREQUENCY = 88.0f; // Minimum allowable frequency in MHz
    private static final float MAX_FREQUENCY = 108.0f; // Maximum allowable frequency in MHz
    private static final int MIN_VOLUME = 0; // Minimum allowable volume level
    private static final int MAX_VOLUME = 100; // Maximum allowable volume level

    private float frequency; // Current radio frequency
    private boolean isOn;     // Radio power state (On/Off)

    private final SoundPlayer soundPlayer;  // Sound player object
    private final TreeMap<Double, String> stations; // Map of stations with associated frequencies and song files

    // Constructor
    public Radio() {
        soundPlayer = new SoundPlayer();

        // Initialize stations map and add some frequencies with associated song files
        stations = new TreeMap<>();
        stations.put(88.0, "resources/audio01.wav");
        stations.put(93.0, "resources/audio02.wav");
        stations.put(98.0, "resources/audio03.wav");
        stations.put(103.0, "resources/audio04.wav");
        stations.put(108.0, "resources/audio05.wav");
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
    private double getNearestStation(double frequency) {
        // Find the nearest key (frequency) in the TreeMap
        Double lowerKey = stations.floorKey(frequency);
        Double higherKey = stations.ceilingKey(frequency);

        if (lowerKey == null) return higherKey;
        if (higherKey == null) return lowerKey;

        // Return the nearest key
        return (frequency - lowerKey < higherKey - frequency) ? lowerKey : higherKey;
    }

    // Method to tune the radio to a new frequency
    public void tune(float newFrequency) {
        soundPlayer.stop(); // Stop any currently playing sound

        if (isOn) {
            if (newFrequency >= MIN_FREQUENCY && newFrequency <= MAX_FREQUENCY) {
                frequency = newFrequency;
                System.out.println("Tuned to " + frequency + " MHz");

                double nearestStation = getNearestStation(frequency);
                double distance = Math.abs(nearestStation - frequency);

                // If exact match, play the station song
                if (distance == 0) {
                    soundPlayer.play(stations.get(nearestStation), true);
                } else {
                    // Play nearest station song but with added noise
                    // The closer to the station, the lesser the noise

                    // Convert distance to intensity
                    float intensity = (float) distance / 2.5f; // Assuming maximum distance is 2.5
                    if (intensity > 1.0f) intensity = 1.0f; // Clamp to 1.0

                    // Now play the song with the calculated noise intensity
                    soundPlayer.playWithNoise(stations.get(nearestStation), intensity, true);
                }

            } else {
                System.out.println("Invalid frequency: " + newFrequency + " MHz. Must be between " + MIN_FREQUENCY + " and " + MAX_FREQUENCY + " MHz.");
            }
        } else {
            System.out.println("Radio is off. Please turn it on to tune.");
        }
    }
}
