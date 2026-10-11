package gui.lecturer;

import gui.Login;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LecturerDashboard extends JFrame {

    private static final Color MINT   = new Color(0xE8, 0xF5, 0xE9);
    private static final Color PASTEL = new Color(0xC8, 0xE6, 0xC9);
    private static final Color FOREST = new Color(0x38, 0x8E, 0x3C);
    private static final Color DEEP   = new Color(0x1B, 0x5E, 0x20);
    private static final Color WHITE  = Color.WHITE;

    public LecturerDashboard(long userId, String username, String fullName,
                             String email, String role) {
        setTitle("UniMis - Lecturer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(MINT);

        JLabel title = new JLabel("Lecturer Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(FOREST);
        title.setBorder(new EmptyBorder(50, 20, 10, 20));
        root.add(title, BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(PASTEL);
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
        wrap.setBackground(MINT);
        wrap.add(card);
        root.add(wrap, BorderLayout.CENTER);

        JButton logout = new JButton("Logout");
        logout.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logout.setForeground(WHITE);
        logout.setBackground(FOREST);
        logout.setFocusPainted(false);
        logout.setBorderPainted(false);
        logout.setOpaque(true);
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logout.setPreferredSize(new Dimension(150, 44));
        logout.addActionListener(e -> {
            dispose();
            Login.showLogin();
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 25));
        bottom.setBackground(MINT);
        bottom.add(logout);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
        setLocationRelativeTo(null);
    }

    private static void addRow(JPanel p, GridBagConstraints gc, int row,
                               String label, String value) {
        gc.gridx = 0; gc.gridy = row;
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 18));
        l.setForeground(DEEP);
        p.add(l, gc);

        gc.gridx = 1;
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        v.setForeground(DEEP);
        p.add(v, gc);
    }
}