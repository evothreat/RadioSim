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
        frequency = MIN_FREQUENCY;  // Default frequency when the radio is turned on
        isOn = false;      // Radio is initially off

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
        System.out.println("Radio is now ON");
    }

    // Method to turn off the radio
    public void turnOff() {
        isOn = false;
        System.out.println("Radio is now OFF");
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

    // Method to get the current radio status
    public void getStatus() {
        System.out.println("Radio is " + (isOn ? "ON" : "OFF"));
        if (isOn) {
            System.out.println("Current Frequency: " + frequency + " MHz");
            System.out.println("Volume Level: " + (int) (soundPlayer.getVolume() * 100));
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
                    soundPlayer.playAndLoop(stations.get(nearestStation));
                } else {
                    // Play nearest station song but with added noise
                    // The closer to the station, the lesser the noise
                    // For simplicity, this step is represented conceptually:
                    soundPlayer.playWithNoise(stations.get(nearestStation), distance);
                }

            } else {
                System.out.println("Invalid frequency: " + newFrequency + " MHz. Must be between " + MIN_FREQUENCY + " and " + MAX_FREQUENCY + " MHz.");
            }
        } else {
            System.out.println("Radio is off. Please turn it on to tune.");
        }
    }
}