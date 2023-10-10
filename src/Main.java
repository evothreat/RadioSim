public class Main {
    public static void main(String[] args) {
        try {
            // Create a radio instance
            Radio myRadio = new Radio();

            // Turn on the radio
            myRadio.turnOn();

            // Tune to a specific frequency
            // Play first station
            myRadio.changeVolume(25);
            myRadio.tune(90.5);
            Thread.sleep(10000);

            // Play second station
            myRadio.changeVolume(50);
            myRadio.tune(88.5);
            Thread.sleep(10000);

            // Play third station
            myRadio.changeVolume(75);
            myRadio.tune(92.7);
            Thread.sleep(5000);
            myRadio.changeVolume(100);
            Thread.sleep(5000);

            // Get the current radio status
            myRadio.getStatus();

            // Turn off the radio
            myRadio.turnOff();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}