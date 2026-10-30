package gui.undergraduate.courses.summary;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Metric cards container displaying 4 key credit and module statistics with 16px rounded borders.
 */
public class CoursesSummaryStripPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    public CoursesSummaryStripPanel() {
        setOpaque(false);
        setLayout(new GridLayout(1, 4, 15, 0));
        setPreferredSize(new Dimension(0, 75));

        add(createMiniStatCard("16 Credits", "Registered Total", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("5 Modules", "Core Mandatory", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("1 Module", "Elective Track", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("100% Valid", "Dean Registered", AdminTheme.FOREST_GREEN));
    }

    private JPanel createMiniStatCard(String mainVal, String subVal, Color badgeColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AdminTheme.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
                g2.setColor(CARD_BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel badge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(badgeColor);
                int size = 38;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.fillRoundRect(x, y, size, size, 10, 10);
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(38, 38));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel valLbl = new JLabel(mainVal);
        valLbl.setFont(AdminTheme.font(Font.BOLD, 14));
        valLbl.setForeground(AdminTheme.DEEP_GREEN);

        JLabel subLbl = new JLabel(subVal);
        subLbl.setFont(AdminTheme.font(Font.PLAIN, 12));
        subLbl.setForeground(AdminTheme.MUTED);

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(valLbl);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(subLbl);
        textPanel.add(Box.createVerticalGlue());

        card.add(badge, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }
}