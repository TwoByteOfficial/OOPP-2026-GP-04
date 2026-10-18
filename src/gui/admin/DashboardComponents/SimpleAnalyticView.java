package gui.admin.DashboardComponents;

import javax.swing.*;
import java.awt.*;
import java.util.Map; // Added Import

public class SimpleAnalyticView extends JPanel {
    private final Color WHITE        = Color.WHITE;
    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color MUTED        = new Color(0x4F, 0x6B, 0x52);

    // Modified Constructor Signature to ingest live database state
    public SimpleAnalyticView(Map<String, Integer> metrics) {
        setLayout(null);
        setBounds(0, 0, 1620, 130);
        setOpaque(false);

        // Derive structural math sums for cumulative totals
        int totalUsers = metrics.getOrDefault("admins", 0)
                + metrics.getOrDefault("lecturers", 0)
                + metrics.getOrDefault("technical_officers", 0)
                + metrics.getOrDefault("undergraduates", 0);

        int totalUG = metrics.getOrDefault("undergraduates", 0);
        int totalLecturers = metrics.getOrDefault("lecturers", 0);
        int totalCourses = metrics.getOrDefault("courses", 0);

        // Renders dynamic updates straight from schema tracking metrics tables
        addCustomStatCard("Total Users", totalUsers + " Users Active", 20, 0, "fa-users");
        addCustomStatCard("Undergraduates", totalUG + " Students Registered", 420, 0, "fa-user-graduate");
        addCustomStatCard("Lecturers", totalLecturers + " Staff Active", 820, 0, "fa-chalkboard-user");
        addCustomStatCard("Active Courses", totalCourses + " Modules Live", 1220, 0, "fa-book-open");
    }

    private void addCustomStatCard(String title, String countValue, int x, int y, String mockIcon) {
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
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(DEEP_GREEN);
        valueLabel.setBounds(88, 24, 270, 32);
        card.add(valueLabel);

        JLabel labelText = new JLabel(title);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelText.setForeground(MUTED);
        labelText.setBounds(88, 56, 270, 20);
        card.add(labelText);

        add(card);
    }
}
