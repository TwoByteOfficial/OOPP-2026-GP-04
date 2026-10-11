package gui.lecturer;

import gui.login.Login;
import gui.login.LoginTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Lecturer dashboard. Same constructor signature as the other dashboards,
 * so Login's DashboardFactory map can build it via a method reference.
 */
public class LecturerDashboard extends JFrame {

    private final long userId;
    private final String username, fullName, email, role;

    public LecturerDashboard(long userId, String username, String fullName,
                             String email, String role) {
        this.userId   = userId;
        this.username = username;
        this.fullName = fullName;
        this.email    = email;
        this.role     = role;

        setTitle("UniMis - Lecturer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(LoginTheme.MINT);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(root);
        setLocationRelativeTo(null);
    }

    /* ---------- Header ---------- */

    private JPanel buildHeader() {
        JLabel title = new JLabel("Lecturer Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(LoginTheme.FOREST);
        title.setBorder(new EmptyBorder(50, 20, 10, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(LoginTheme.MINT);
        header.add(title, BorderLayout.CENTER);
        return header;
    }

    /* ---------- Centre : profile card ---------- */

    private JPanel buildCenter() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(LoginTheme.PASTEL);
        card.setBorder(new EmptyBorder(30, 60, 30, 60));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 12, 8, 12);
        gc.anchor = GridBagConstraints.WEST;

        addRow(card, gc, 0, "User ID:",   String.valueOf(userId));
        addRow(card, gc, 1, "Username:",  username);
        addRow(card, gc, 2, "Full Name:", fullName);
        addRow(card, gc, 3, "Email:",     email);
        addRow(card, gc, 4, "Role:",      role);

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(LoginTheme.MINT);
        wrap.add(card);
        return wrap;
    }

    /* ---------- Footer : logout ---------- */

    private JPanel buildFooter() {
        JButton logout = LoginTheme.button("Logout", LoginTheme.F_LABEL);
        logout.setPreferredSize(new Dimension(150, 44));
        logout.addActionListener(e -> {
            dispose();
            Login.showLogin();
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 25));
        bottom.setBackground(LoginTheme.MINT);
        bottom.add(logout);
        return bottom;
    }

    /* ---------- Row helper (used inside the card) ---------- */

    private static void addRow(JPanel p, GridBagConstraints gc, int row,
                               String label, String value) {
        gc.gridx = 0; gc.gridy = row;
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 18));
        l.setForeground(LoginTheme.DEEP);
        p.add(l, gc);

        gc.gridx = 1;
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        v.setForeground(LoginTheme.DEEP);
        p.add(v, gc);
    }
}