
package gui.lecturer.Dashboard.DashboardComponents;

import javax.swing.*;
import java.awt.*;

public class DashboardTitleBar extends JPanel {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color TEXT_GRAY =
            new Color(96, 112, 100);

    private final JLabel welcomeLabel = new JLabel();

    private final JLabel subtitleLabel = new JLabel(
            "Here's your academic overview for the current semester."
    );

    public DashboardTitleBar(String lecturerName) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        welcomeLabel.setFont(
                new Font("SansSerif", Font.BOLD, 26)
        );
        welcomeLabel.setForeground(DEEP_GREEN);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        subtitleLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(welcomeLabel);
        add(Box.createVerticalStrut(8));
        add(subtitleLabel);

        updateGreeting("", lecturerName);
    }

    public void updateGreeting(
            String title,
            String lecturerName
    ) {
        String name = (lecturerName == null
                || lecturerName.isBlank())
                ? "Lecturer"
                : lecturerName.trim();

        // Remove an existing title from the full name.
        name = name.replaceFirst(
                "(?i)^(Dr\\.|Prof\\.|Mr\\.|Ms\\.)\\s*",
                ""
        ).trim();

        String prefix = (title == null || title.isBlank())
                ? ""
                : title.trim() + " ";

        welcomeLabel.setText(
                "Good morning, " + prefix + name + "!"
        );

        revalidate();
        repaint();
    }
}
