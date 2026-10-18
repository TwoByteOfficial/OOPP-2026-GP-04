package gui.admin.DashboardComponents;

import javax.swing.*;
import java.awt.*;

public class SimpleAnalyticView extends JPanel {
    private final Color WHITE        = Color.WHITE;
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color MUTED        = new Color(0x4F, 0x6B, 0x52);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);

    public SimpleAnalyticView() {
        setLayout(null);
        setBounds(0, 0, 1620, 140);
        setOpaque(false);

        // Render 4 wide horizontal metrics across the 1620px viewport matrix width limits
        addAnalyticCard("System Utilization Rate", "94.2%", 20, 0, new Color(0x38, 0x8E, 0x3C));
        addAnalyticCard("Active Server Nodes", "4 / 4 Live", 420, 0, new Color(0x1B, 0x5E, 0x20));
        addAnalyticCard("Database Queries / sec", "128 req/s", 820, 0, new Color(0x81, 0xC7, 0x84));
        addAnalyticCard("API Gateway Latency", "12 ms", 1220, 0, new Color(0x38, 0x8E, 0x3C));
    }

    private void addAnalyticCard(String title, String value, int x, int y, Color textCol) {
        JPanel card = new JPanel(null);
        card.setBackground(WHITE);
        card.setBounds(x, y, 380, 120);
        card.setBorder(BorderFactory.createLineBorder(PASTEL_GREEN, 1));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(MUTED);
        lblTitle.setBounds(24, 22, 330, 20);
        card.add(lblTitle);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblVal.setForeground(textCol);
        lblVal.setBounds(24, 52, 330, 38);
        card.add(lblVal);

        add(card);
    }
}
