import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class RadioController {

    private final Radio radioSim = new Radio();
    private final JLabel statusLabel = new JLabel();
    private final JLabel helpLabel = new JLabel();

    public RadioController() {
        JFrame frame = new JFrame("RadioSim");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        bindKeyWithAction(panel, KeyEvent.VK_UP, "increaseVolume", e -> radioSim.changeVolume(radioSim.getVolume() + 10));
        bindKeyWithAction(panel, KeyEvent.VK_DOWN, "decreaseVolume", e -> radioSim.changeVolume(radioSim.getVolume() - 10));
        bindKeyWithAction(panel, KeyEvent.VK_LEFT, "decreaseFrequency", e -> radioSim.tune(radioSim.getFrequency() - 0.5));
        bindKeyWithAction(panel, KeyEvent.VK_RIGHT, "increaseFrequency", e -> radioSim.tune(radioSim.getFrequency() + 0.5));
        bindKeyWithAction(panel, KeyEvent.VK_O, "togglePower", e -> {
            if (radioSim.isOn()) {
                radioSim.turnOff();
            } else {
                radioSim.turnOn();
            }
        });

        statusLabel.setFont(new Font(statusLabel.getFont().getName(), Font.BOLD, 16));
        statusLabel.setBorder(new EmptyBorder(15, 15, 0, 0));
        updateStatusLabel();

        helpLabel.setText(getHelpText());
        helpLabel.setFont(new Font(helpLabel.getFont().getName(), Font.PLAIN, 16));
        helpLabel.setBorder(new EmptyBorder(15, 15, 0, 0));

        panel.add(statusLabel, BorderLayout.NORTH);
        panel.add(helpLabel, BorderLayout.CENTER);

        frame.setContentPane(panel);
        frame.setVisible(true);
    }



    private void bindKeyWithAction(JPanel panel, int keyCode, String id, ActionListener action) {
        InputMap inputMap = panel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = panel.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(keyCode, 0), id);
        actionMap.put(id, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(e);
                updateStatusLabel();
            }
        });
    }

    private void updateStatusLabel() {
        String status = "<html><font size=5>Radio is: " + (radioSim.isOn() ? "ON" : "OFF");
        status += "<br>Current Frequency: " + radioSim.getFrequency() + " MHz";
        status += "<br>Volume: " + radioSim.getVolume() + "%</font></html>";
        statusLabel.setText(status);
    }

    private String getHelpText() {
        return "<html>" +
                "↑: Increase volume<br>" +
                "↓: Decrease volume<br>" +
                "←: Decrease frequency<br>" +
                "→: Increase frequency<br>" +
                "O: Toggle radio ON/OFF" +
                "</html>";
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(RadioController::new);
    }
}
