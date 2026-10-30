package gui.undergraduate.timetable.summary;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Reusable 16px smooth rounded metric card for weekly timetable statistics.
 */
public class TimetableStatCard extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JLabel valueLabel;

    public TimetableStatCard(String initialValue, String subTitle, Color badgeColor) {
        setOpaque(false);
        setLayout(new BorderLayout(12, 0));
        setBorder(new EmptyBorder(12, 14, 12, 14));

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

        valueLabel = new JLabel(initialValue);
        valueLabel.setFont(AdminTheme.font(Font.BOLD, 14));
        valueLabel.setForeground(AdminTheme.DEEP_GREEN);

        JLabel subLabel = new JLabel(subTitle);
        subLabel.setFont(AdminTheme.font(Font.PLAIN, 12));
        subLabel.setForeground(AdminTheme.MUTED);

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(subLabel);
        textPanel.add(Box.createVerticalGlue());

        add(badge, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
    }

    public void setValue(String val) {
        valueLabel.setText(val);
    }

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
}