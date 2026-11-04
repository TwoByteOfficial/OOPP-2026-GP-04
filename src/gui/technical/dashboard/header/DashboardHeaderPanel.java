package gui.technical.dashboard.header;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Top banner header displayed on the Technical Officer Dashboard.
 * Left side: Dashboard title and descriptive subtitle
 * Right side: Officer avatar with name initials, full name, and user role
 */
public class DashboardHeaderPanel extends JPanel {

    // Theme color palette for header
    private final Color NAVY = new Color(24, 58, 99);
    private final Color MUTED = new Color(105, 118, 135);

    /**
     * Constructs the header panel.
     *
     * @param fullName Full name of the logged-in officer
     * @param role     User role name ("Technical Officer")
     */
    public DashboardHeaderPanel(String fullName, String role) {

        setLayout(new BorderLayout(20, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 8, 0));

        // Center section: Title and subtitle
        add(buildTitleSection(), BorderLayout.CENTER);

        // Right section: User badge with avatar
        add(buildUserSection(fullName, role), BorderLayout.EAST);
    }

    /**
     * Builds the title section with dashboard heading and subtitle description.
     *
     * @return JPanel containing the titles
     */
    private JPanel buildTitleSection() {

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Technical Officer Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 27));
        title.setForeground(NAVY);

        JLabel subtitle = new JLabel("Attendance, medical records and department operations");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);

        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitle);

        return panel;
    }

    /**
     * Builds the right-side user badge with initials circle, full name, and role.
     *
     * @param fullName Officer's full name
     * @param role     Officer's role
     * @return JPanel containing avatar and name details
     */
    private JPanel buildUserSection(String fullName, String role) {

        JPanel section = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        section.setOpaque(false);

        JPanel userInfo = new JPanel(new BorderLayout(10, 0));
        userInfo.setBackground(Color.WHITE);
        userInfo.setBorder(new EmptyBorder(7, 10, 7, 12));

        // Circular avatar showing officer initials
        JLabel avatar = new JLabel(getInitials(fullName), SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(new Color(15, 118, 110));
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        avatar.setPreferredSize(new Dimension(42, 42));

        // Vertical stack of name and role
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

        section.add(userInfo);

        return section;
    }

    /**
     * Extracts uppercase 1- or 2-letter initials from a person's full name.
     * For example: "Praveen Silva" -> "PS"
     *
     * @param name Input full name string
     * @return Uppercase initials string, or "TO" if name is blank
     */
    private String getInitials(String name) {

        if (name == null || name.isBlank()) {
            return "TO";
        }

        String[] parts = name.trim().split("\\s+");

        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }

        return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
