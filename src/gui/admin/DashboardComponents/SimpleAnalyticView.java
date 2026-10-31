
package gui.admin.DashboardComponents;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SimpleAnalyticView extends JPanel {

    private final Color WHITE        = Color.WHITE;
    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color MUTED        = new Color(0x4F, 0x6B, 0x52);

    public SimpleAnalyticView(Map<String, Integer> metrics) {

        setLayout(null);
        setBounds(0, 0, 1620, 130);
        setOpaque(false);

        int totalUsers = metrics.getOrDefault("admins", 0)
                + metrics.getOrDefault("lecturers", 0)
                + metrics.getOrDefault("technical_officers", 0)
                + metrics.getOrDefault("undergraduates", 0);

        int totalUG = metrics.getOrDefault("undergraduates", 0);
        int totalLecturers = metrics.getOrDefault("lecturers", 0);
        int totalCourses = metrics.getOrDefault("courses", 0);

        // Dashboard Statistic Cards

        addCustomStatCard("Total Users", totalUsers + " Users Active", "📊", 0, 0);
        addCustomStatCard("Undergraduates", totalUG + " Students Registered", "🎓", 400, 0);
        addCustomStatCard("Lecturers", totalLecturers + " Staff Active", "📝", 800, 0);
        addCustomStatCard("Active Courses", totalCourses + " Modules Live", "📚", 1200, 0);
    }

    private void addCustomStatCard(String title, String countValue, String emoji, int x, int y) {
        // Main Card
        JPanel card = new JPanel(null) {

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Card Background
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                // Card Border
                g2.setColor(new Color(0x1B, 0x5E, 0x20, 30));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };

        card.setBounds(x, y, 380, 110);
        card.setOpaque(false);

        // Icon Background
        JPanel iconPlaceholder = new JPanel(new GridBagLayout()) {

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Green Rounded Background
                g2.setColor(FOREST_GREEN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };

        iconPlaceholder.setBounds(20, 20, 52, 52);
        iconPlaceholder.setOpaque(false);

        // Emoji Icon
        JLabel emojiLabel = new JLabel(emoji);

        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 27));
        emojiLabel.setForeground(Color.WHITE);
        emojiLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emojiLabel.setVerticalAlignment(SwingConstants.CENTER);

        iconPlaceholder.add(emojiLabel);

        card.add(iconPlaceholder);

        // Statistic Value
        JLabel valueLabel = new JLabel(countValue);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(DEEP_GREEN);
        valueLabel.setBounds(88, 22, 270, 34);

        card.add(valueLabel);

        // Card Title
        JLabel labelText = new JLabel(title);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        labelText.setForeground(MUTED);
        labelText.setBounds(88, 56, 270, 22);
        card.add(labelText);
        // Add Card to Dashboard
        add(card);
    }
}
