package gui.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** White header bar with page title, notification bell and user chip. */
public class AdminTopBar extends JPanel {

    private final JLabel titleLbl = new JLabel("Admin Dashboard");
    private final JLabel subLbl   = new JLabel("Faculty of Technology Academic Management System");
    private final JLabel userLbl  = new JLabel("System Admin");
    private final JLabel roleLbl  = new JLabel("Administrator");
    private final JLabel avatar   = new JLabel("AD", SwingConstants.CENTER);
    private final JLabel notifCount = new JLabel("0", SwingConstants.CENTER);

    public AdminTopBar() {
        setLayout(new BorderLayout());
        setBackground(AdminTheme.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AdminTheme.BORDER),
                new EmptyBorder(18, 32, 18, 32)));

        /* --- Left: title + subtitle --- */
        titleLbl.setFont(AdminTheme.F_H1);
        titleLbl.setForeground(AdminTheme.PRIMARY);
        subLbl.setFont(AdminTheme.F_SMALL);
        subLbl.setForeground(AdminTheme.MUTED);

        JPanel left = new JPanel();
        left.setBackground(AdminTheme.WHITE);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(titleLbl);
        left.add(Box.createVerticalStrut(4));
        left.add(subLbl);
        add(left, BorderLayout.WEST);

        /* --- Right: bell + user chip --- */
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 22, 0));
        right.setBackground(AdminTheme.WHITE);

        notifCount.setOpaque(true);
        notifCount.setBackground(AdminTheme.DANGER);
        notifCount.setForeground(AdminTheme.WHITE);
        notifCount.setFont(new Font("Segoe UI", Font.BOLD, 9));
        notifCount.setPreferredSize(new Dimension(18, 18));

        JLabel bell = new JLabel("\uD83D\uDD14");
        bell.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        bell.setForeground(AdminTheme.PRIMARY);
        bell.setCursor(AdminTheme.HAND);

        JPanel bellWrap = new JPanel(null);
        bellWrap.setBackground(AdminTheme.WHITE);
        bellWrap.setPreferredSize(new Dimension(28, 28));
        bell.setBounds(0, 0, 24, 24);
        notifCount.setBounds(14, -2, 16, 16);
        bellWrap.add(bell);
        bellWrap.add(notifCount);

        avatar.setOpaque(true);
        avatar.setBackground(AdminTheme.PRIMARY);
        avatar.setForeground(AdminTheme.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        avatar.setPreferredSize(new Dimension(42, 42));
        avatar.setBorder(BorderFactory.createLineBorder(AdminTheme.PRIMARY, 1));

        userLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLbl.setForeground(AdminTheme.TEXT);
        roleLbl.setFont(AdminTheme.F_SMALL);
        roleLbl.setForeground(AdminTheme.MUTED);

        JPanel userText = new JPanel();
        userText.setBackground(AdminTheme.WHITE);
        userText.setLayout(new BoxLayout(userText, BoxLayout.Y_AXIS));
        userText.add(userLbl);
        userText.add(roleLbl);

        JPanel chip = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        chip.setBackground(AdminTheme.WHITE);
        chip.add(avatar);
        chip.add(userText);

        right.add(bellWrap);
        right.add(chip);
        add(right, BorderLayout.EAST);
    }

    public void setTitle(String title, String subtitle) {
        titleLbl.setText(title);
        subLbl.setText(subtitle);
    }

    public void setUser(String fullName, String role, String initials) {
        userLbl.setText(fullName);
        roleLbl.setText(role);
        avatar.setText(initials);
    }

    public void setNotificationCount(int n) {
        notifCount.setText(String.valueOf(n));
        notifCount.setVisible(n > 0);
    }
}