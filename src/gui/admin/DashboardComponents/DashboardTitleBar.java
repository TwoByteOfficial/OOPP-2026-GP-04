package gui.admin.DashboardComponents;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Top bar for the dashboard page.
 * Shows the page title, subtitle, notification bell placeholder,
 * and the logged-in admin avatar + name/role.
 */
public class DashboardTitleBar extends JPanel {

    private final long   userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    public DashboardTitleBar(long userId, String username, String fullName,
                             String email, String role) {
        this.userId   = userId;
        this.username = username;
        this.fullName = fullName;
        this.email    = email;
        this.role     = role;

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(AdminTheme.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AdminTheme.BORDER),
                AdminTheme.emptyBorder(22, 35, 22, 35)));

        add(buildLeft(),  BorderLayout.WEST);
        add(buildRight(), BorderLayout.EAST);
    }

    private JPanel buildLeft() {
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(AdminTheme.FONT_H1);
        title.setForeground(AdminTheme.PRIMARY);

        JLabel subtitle = new JLabel("Faculty of Technology Academic Management System");
        subtitle.setFont(AdminTheme.FONT_SMALL);
        subtitle.setForeground(AdminTheme.MUTED);
        subtitle.setBorder(AdminTheme.emptyBorder(4, 0, 0, 0));

        left.add(title);
        left.add(subtitle);
        return left;
    }

    private JPanel buildRight() {
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        right.setOpaque(false);

        /* Notification bell (placeholder) */
        JButton bell = new JButton("🔔");
        bell.setFont(new Font(AdminTheme.FONT_FAMILY, Font.PLAIN, 18));
        bell.setForeground(AdminTheme.PRIMARY);
        bell.setBorder(BorderFactory.createEmptyBorder());
        bell.setContentAreaFilled(false);
        bell.setFocusPainted(false);
        bell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bell.setToolTipText("Notifications");

        /* Avatar + user block */
        JPanel userBlock = new JPanel(new BorderLayout(10, 0));
        userBlock.setOpaque(false);

        JLabel avatar = new JLabel(initialsOf(fullName), SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(42, 42));
        avatar.setOpaque(true);
        avatar.setBackground(AdminTheme.PRIMARY);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 13));
        avatar.setBorder(BorderFactory.createLineBorder(AdminTheme.PRIMARY_DARK, 1));

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);

        JLabel nameLbl = new JLabel(safe(fullName));
        nameLbl.setFont(AdminTheme.FONT_BODY_BOLD);
        nameLbl.setForeground(AdminTheme.TEXT);

        JLabel roleLbl = new JLabel(safe(role));
        roleLbl.setFont(AdminTheme.FONT_TINY);
        roleLbl.setForeground(AdminTheme.MUTED);

        namePanel.add(nameLbl);
        namePanel.add(roleLbl);

        userBlock.add(avatar,    BorderLayout.WEST);
        userBlock.add(namePanel, BorderLayout.CENTER);

        right.add(bell);
        right.add(userBlock);
        return right;
    }

    /* ---------- Helpers ---------- */

    private static String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    private static String initialsOf(String name) {
        if (name == null || name.isBlank()) return "AD";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length && sb.length() < 2; i++) {
            sb.append(Character.toUpperCase(parts[i].charAt(0)));
        }
        return sb.toString();
    }
}