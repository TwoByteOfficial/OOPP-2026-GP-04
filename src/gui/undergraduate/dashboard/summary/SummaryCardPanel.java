package gui.undergraduate.dashboard.summary;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Reusable base summary card matching UniMis AdminTheme aesthetics.
 */
public class SummaryCardPanel extends JPanel {

    public SummaryCardPanel(String iconText, Color iconBg, String value, String title, String subtitle) {
        setLayout(new BorderLayout(14, 0));
        setBackground(AdminTheme.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD2, 0xD7, 0xE1), 1),
                new EmptyBorder(16, 18, 16, 18)
        ));

        // Icon Box
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(iconBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        iconPanel.setPreferredSize(new Dimension(46, 46));
        iconPanel.setOpaque(false);
        iconPanel.setLayout(new GridBagLayout());

        JLabel iconLabel = new JLabel(iconText);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setForeground(Color.WHITE);
        iconPanel.add(iconLabel);

        add(iconPanel, BorderLayout.WEST);

        // Text Info (Value, Title, Subtitle)
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(AdminTheme.FONT_STAT);
        valLabel.setForeground(AdminTheme.DEEP_GREEN);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AdminTheme.FONT_BODY_BOLD);
        titleLabel.setForeground(AdminTheme.DEEP_GREEN);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(AdminTheme.FONT_TINY);
        subLabel.setForeground(AdminTheme.MUTED);

        textPanel.add(valLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(subLabel);

        add(textPanel, BorderLayout.CENTER);
    }
}