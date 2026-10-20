package gui.admin.DashboardComponents;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Top 4 metric cards row with live database aggregations.
 */
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

        addCustomStatCard("Total Users", totalUsers + " Users Active", 0, 0);
        addCustomStatCard("Undergraduates", totalUG + " Students Registered", 413, 0);
        addCustomStatCard("Lecturers", totalLecturers + " Staff Active", 826, 0);
        addCustomStatCard("Active Courses", totalCourses + " Modules Live", 1240, 0);
    }

    private void addCustomStatCard(String title, String countValue, int x, int y) {
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(0x1B, 0x5E, 0x20, 30));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setBounds(x, y, 380, 110);
        card.setOpaque(false);

        JPanel iconPlaceholder = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FOREST_GREEN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        iconPlaceholder.setBounds(20, 29, 52, 52);
        iconPlaceholder.setOpaque(false);
        card.add(iconPlaceholder);

        JLabel valueLabel = new JLabel(countValue);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); // 22 -> 24
        valueLabel.setForeground(DEEP_GREEN);
        valueLabel.setBounds(88, 22, 280, 34);
        card.add(valueLabel);

        JLabel labelText = new JLabel(title);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // 12 -> 14
        labelText.setForeground(MUTED);
        labelText.setBounds(88, 58, 280, 22);
        card.add(labelText);

        add(card);
    }
}
