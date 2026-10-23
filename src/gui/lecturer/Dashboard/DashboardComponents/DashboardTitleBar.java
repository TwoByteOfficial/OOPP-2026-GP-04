
package gui.lecturer.Dashboard.DashboardComponents;

import javax.swing.*;
import java.awt.*;

public class DashboardTitleBar extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color TEXT_GRAY = new Color(96, 112, 100);

    public DashboardTitleBar(String lecturerName) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        String name = (lecturerName == null || lecturerName.isBlank())
                ? "Lecturer"
                : lecturerName;

        JLabel welcomeLabel =
                new JLabel("Good morning, " + name + "!");

        welcomeLabel.setFont(
                new Font("SansSerif", Font.BOLD, 26));
        welcomeLabel.setForeground(DEEP_GREEN);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel =
                new JLabel("Here's your academic overview for the current semester.");

        subtitleLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(welcomeLabel);
        add(Box.createVerticalStrut(8));
        add(subtitleLabel);
    }
}
