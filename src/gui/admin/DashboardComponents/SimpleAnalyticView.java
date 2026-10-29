package gui.admin.DashboardComponents;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SimpleAnalyticView extends JPanel {

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
        addCustomStatCard("Undergraduates", totalUG + " Students Registered", 400, 0);
        addCustomStatCard("Lecturers", totalLecturers + " Staff Active", 800, 0);
        addCustomStatCard("Active Courses", totalCourses + " Modules Live", 1200, 0);
    }

    private void addCustomStatCard(String title, String countValue, int x, int y) {
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AdminTheme.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(AdminTheme.CARD_SHADOW_30);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
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
                g2.setColor(AdminTheme.FOREST_GREEN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        iconPlaceholder.setBounds(20, 20, 52, 52);
        iconPlaceholder.setOpaque(false);
        card.add(iconPlaceholder);

        JLabel valueLabel = new JLabel(countValue);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(AdminTheme.DEEP_GREEN);
        valueLabel.setBounds(88, 22, 270, 34);
        card.add(valueLabel);

        JLabel labelText = new JLabel(title);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        labelText.setForeground(AdminTheme.MUTED);
        labelText.setBounds(88, 56, 270, 22);
        card.add(labelText);

        add(card);
    }
}