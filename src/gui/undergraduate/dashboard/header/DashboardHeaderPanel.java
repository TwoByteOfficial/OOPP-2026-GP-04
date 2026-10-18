package gui.undergraduate.dashboard.header;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 1.1.1 - Undergraduate Dashboard Header Panel
 * Left side: Title & Welcome Subtitle
 * Right side: Student Profile Badge (Name, Reg No / Role)
 */
public class DashboardHeaderPanel extends JPanel {

    public DashboardHeaderPanel(String fullName, String username, String role) {
        setOpaque(false);
        setLayout(new BorderLayout(20, 0));
        setBorder(new EmptyBorder(0, 0, 15, 0));

        // 1.1.1.1 - Title & Subtitle Section
        add(buildTitleSection(), BorderLayout.CENTER);

        // 1.1.1.2 - Student Profile Badge (Notification / Profile Chip)
        add(buildUserSection(fullName, username, role), BorderLayout.EAST);
    }

    private JPanel buildTitleSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Undergraduate Portal");
        title.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 26));
        title.setForeground(AdminTheme.DEEP_GREEN);

        JLabel subtitle = new JLabel("Academic performance, course attendance and notices");
        subtitle.setFont(new Font(AdminTheme.FONT_FAMILY, Font.PLAIN, 15));
        subtitle.setForeground(AdminTheme.MUTED);

        panel.add(title);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitle);

        return panel;
    }

    private JPanel buildUserSection(String fullName, String username, String role) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setOpaque(false);

        // Student Info (Full Name & Reg Number / Role)
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(fullName != null && !fullName.isEmpty() ? fullName : "Undergraduate Student");
        nameLabel.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 15));
        nameLabel.setForeground(AdminTheme.DEEP_GREEN);
        nameLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        String regDisplay = (username != null ? username.toUpperCase() : "STUDENT") + " • " + (role != null ? role : "Undergraduate");
        JLabel roleLabel = new JLabel(regDisplay);
        roleLabel.setFont(new Font(AdminTheme.FONT_FAMILY, Font.PLAIN, 13));
        roleLabel.setForeground(AdminTheme.MUTED);
        roleLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(roleLabel);

        // Avatar Circle (Student initials with Forest Green background)
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AdminTheme.FOREST_GREEN);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(42, 42));
        avatar.setOpaque(false);
        avatar.setLayout(new GridBagLayout());

        String initials = getInitials(fullName != null ? fullName : username);
        JLabel avatarText = new JLabel(initials);
        avatarText.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 14));
        avatarText.setForeground(Color.WHITE);
        avatar.add(avatarText);

        panel.add(textPanel);
        panel.add(avatar);

        return panel;
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "UG";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}