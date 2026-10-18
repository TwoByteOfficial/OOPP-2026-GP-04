package gui.admin.DashboardComponents;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Shared base card used by all dashboard summary widgets.
 * Mirrors the .stat-card style from styles.css.
 */
class StatCard extends JPanel {

    protected StatCard(String icon, Color iconBg,
                       String title, String value, String footer) {
        setLayout(new BorderLayout(16, 0));
        setBackground(AdminTheme.WHITE);
        setBorder(AdminTheme.cardBorder());

        /* Icon badge */
        JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER);
        iconLbl.setPreferredSize(new Dimension(52, 52));
        iconLbl.setOpaque(true);
        iconLbl.setBackground(iconBg);
        iconLbl.setForeground(Color.WHITE);
        iconLbl.setFont(new Font(AdminTheme.FONT_FAMILY, Font.PLAIN, 21));

        /* Text block */
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(AdminTheme.FONT_TINY);
        titleLbl.setForeground(AdminTheme.MUTED);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(AdminTheme.FONT_STAT);
        valueLbl.setForeground(AdminTheme.TEXT);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel footerLbl = new JLabel(footer);
        footerLbl.setFont(AdminTheme.FONT_TINY);
        footerLbl.setForeground(AdminTheme.MUTED);
        footerLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        text.add(titleLbl);
        text.add(Box.createVerticalStrut(2));
        text.add(valueLbl);
        text.add(Box.createVerticalStrut(2));
        text.add(footerLbl);

        add(iconLbl, BorderLayout.WEST);
        add(text,    BorderLayout.CENTER);
    }
}