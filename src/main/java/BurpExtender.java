import burp.IBurpExtender;
import burp.IBurpExtenderCallbacks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BurpExtender implements IBurpExtender {
    private JLabel clockLabel;
    private JPanel bottomPanel;
    private Timer timer;
    private JFrame burpFrame; // store the main Burp frame

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        callbacks.setExtensionName("ClockBar");

        SwingUtilities.invokeLater(() -> {
            burpFrame = getBurpFrame();
            if (burpFrame == null) {
                callbacks.printOutput("Cannot find Burp Suite main frame!");
                return;
            }

            callbacks.printOutput("ClockBar extension loaded!");

            bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            clockLabel = new JLabel("⏱ Loading...");

            bottomPanel.add(clockLabel);

            burpFrame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
            burpFrame.revalidate();
            burpFrame.repaint();

            startClockUpdater();
        });

        callbacks.registerExtensionStateListener(() -> {
            SwingUtilities.invokeLater(() -> {
                stopClockUpdater();
                if (burpFrame != null && bottomPanel != null) {
                    burpFrame.getContentPane().remove(bottomPanel);
                    burpFrame.revalidate();
                    burpFrame.repaint();
                }
                callbacks.printOutput("ClockBar extension unloaded!");
            });
        });
    }

    // Get the main Burp Suite JFrame
    private JFrame getBurpFrame() {
        for (Frame f : Frame.getFrames()) {
            if (f instanceof JFrame && f.isVisible() && f.getTitle().contains("Burp Suite")) {
                return (JFrame) f;
            }
        }
        return null;
    }

    // Start a timer to update the clock every second
    private void startClockUpdater() {
        timer = new Timer(1000, e -> {
            String dateTime = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss z").format(new Date());
            clockLabel.setText("⏱ " + dateTime);
        });
        timer.start();
    }

    // Stop the timer when the extension is unloaded
    private void stopClockUpdater() {
        if (timer != null) {
            timer.stop();
        }
    }
}
