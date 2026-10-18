package gui.admin.DashboardComponents;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Simple analytics card.
 * For now shows the logged-in administrator's details for testing.
 * Will later host charts and real analytics data.
 */
public class SimpleAnalyticView extends JPanel {

    private final long   userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    public SimpleAnalyticView(long userId, String username, String fullName,
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
        setBorder(AdminTheme.cardBorder());
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        /* Header */
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel title = new JLabel("Overview");
        title.setFont(AdminTheme.FONT_H3);
        title.setForeground(AdminTheme.PRIMARY);

        JLabel hint = new JLabel("Logged-in Administrator (test data)");
        hint.setFont(AdminTheme.FONT_TINY);
        hint.setForeground(AdminTheme.MUTED);

        header.add(title, BorderLayout.WEST);
        header.add(hint,  BorderLayout.EAST);

        /* Details grid */
        JPanel details = new JPanel(new GridBagLayout());
        details.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addRow(details, gbc, row++, "User ID:",   String.valueOf(userId));
        addRow(details, gbc, row++, "Username:",  safe(username));
        addRow(details, gbc, row++, "Full Name:", safe(fullName));
        addRow(details, gbc, row++, "Email:",     safe(email));
        addRow(details, gbc, row,   "Role:",      safe(role));

        add(header,  BorderLayout.NORTH);
        add(details, BorderLayout.CENTER);
    }

    private void addRow(JPanel parent, GridBagConstraints gbc, int row,
                        String label, String value) {
        JLabel l = new JLabel(label);
        l.setFont(AdminTheme.FONT_BODY_BOLD);
        l.setForeground(AdminTheme.TEXT);

        JLabel v = new JLabel(value);
        v.setFont(AdminTheme.FONT_BODY);
        v.setForeground(new Color(50, 55, 70));

        gbc.gridx = 0; gbc.gridy = row; parent.add(l, gbc);
        gbc.gridx = 1; gbc.gridy = row; parent.add(v, gbc);
    }

    private static String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}