
package gui.technical.dashboard.header;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardHeaderPanel extends JPanel {

    private final Color NAVY = new Color(24, 58, 99);
    private final Color MUTED = new Color(105, 118, 135);

    public DashboardHeaderPanel(String fullName, String role) {

        setLayout(new BorderLayout(20, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 8, 0));

        add(buildTitleSection(), BorderLayout.CENTER);
        add(buildUserSection(fullName, role), BorderLayout.EAST);
    }

    private JPanel buildTitleSection() {

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Technical Officer Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 27));
        title.setForeground(NAVY);

        JLabel subtitle = new JLabel(
                "Attendance, medical records and department operations");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);

        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitle);

        return panel;
    }

    private JPanel buildUserSection(String fullName, String role) {

        JPanel section = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 12, 0));
        section.setOpaque(false);

        // Notification button
        JButton notificationButton = new JButton("\uD83D\uDD14");
        notificationButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        notificationButton.setPreferredSize(new Dimension(54, 54));
        notificationButton.setBackground(Color.WHITE);
        notificationButton.setFocusPainted(false);
        notificationButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // User information
        JPanel userInfo = new JPanel(new BorderLayout(10, 0));
        userInfo.setBackground(Color.WHITE);
        userInfo.setBorder(new EmptyBorder(7, 10, 7, 12));

        JLabel avatar = new JLabel(getInitials(fullName), SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(new Color(15, 118, 110));
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        avatar.setPreferredSize(new Dimension(42, 42));

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(fullName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(NAVY);

        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(MUTED);

        details.add(nameLabel);
        details.add(Box.createVerticalStrut(4));
        details.add(roleLabel);

        userInfo.add(avatar, BorderLayout.WEST);
        userInfo.add(details, BorderLayout.CENTER);

        section.add(notificationButton);
        section.add(userInfo);

        return section;
    }

    private String getInitials(String name) {

        if (name == null || name.isBlank()) {
            return "TO";
        }

        String[] parts = name.trim().split("\\s+");

        if (parts.length == 1) {
            return parts[0].substring(
                    0, Math.min(2, parts[0].length())).toUpperCase();
        }

        return (parts[0].charAt(0) + "" +
                parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
