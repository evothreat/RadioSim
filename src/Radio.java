import java.util.TreeMap;

public class Radio {
    private static final double MIN_FREQUENCY = 88.0; // Minimum allowable frequency in MHz
    private static final double MAX_FREQUENCY = 108.0; // Maximum allowable frequency in MHz
    private static final int MIN_VOLUME = 0; // Minimum allowable volume level
    private static final int MAX_VOLUME = 100; // Maximum allowable volume level

    private double frequency; // Current radio frequency
    private boolean isOn;     // Radio power state (On/Off)

    private final SoundPlayer soundPlayer;
    private final TreeMap<Double, String> stations;

    // Constructor
    public Radio() {
        soundPlayer = new SoundPlayer();

        // Initialize stations map and add some frequencies with associated song files
        stations = new TreeMap<>();
        stations.put(88.5, "resources/eterna-cancao.wav");
        stations.put(92.7, "resources/rock-doo-wop.wav");
        stations.put(95.5, "resources/cumbia-mexican-banda.wav");
        // Add more as necessary
    }

    // Method to turn on the radio
    public void turnOn() {
        isOn = true;
        tune(MIN_FREQUENCY); // Tune to the default frequency
        System.out.println("Radio is now ON");
    }

    // Method to turn off the radio
    public void turnOff() {
        isOn = false;
        soundPlayer.stop(); // Stop any currently playing sound
        System.out.println("Radio is now OFF");
    }

    public boolean isOn() {
        return isOn;
    }

    public double getFrequency() {
        return frequency;
    }

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

    private double getNearestStation(double frequency) {
        // Find the nearest key (frequency) in the TreeMap
        Double lowerKey = stations.floorKey(frequency);
        Double higherKey = stations.ceilingKey(frequency);

        if (lowerKey == null) return higherKey;
        if (higherKey == null) return lowerKey;

        // Return the nearest key
        return (frequency - lowerKey < higherKey - frequency) ? lowerKey : higherKey;
    }

    // Modify the tune method
    public void tune(double newFrequency) {
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
                    float intensity = (float) distance / 10.0f; // Assuming maximum distance is 10.0
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
