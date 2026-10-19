package gui.undergraduate.dashboard.summary;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Base summary metric card component matching Admin Dashboard aesthetics.
 * Renders individual cards with smooth rounded corners and clean inter-card spacing.
 */
public class SummaryCardPanel extends JPanel {

    private String iconText;
    private Color accentColor;
    private String statValue;
    private String statTitle;
    private String statSubtitle;

    private boolean isContainer = false;

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    /**
     * Default constructor rendering the horizontal 4-card metric container strip.
     */
    public SummaryCardPanel() {
        this.isContainer = true;
        setOpaque(false);
        setLayout(new GridLayout(1, 4, 15, 0));
        setPreferredSize(new Dimension(0, 75));

        add(new SummaryCardPanel("", AdminTheme.FOREST_GREEN, "6", "Modules Active", "Enrolled Courses"));
        add(new SummaryCardPanel("", AdminTheme.FOREST_GREEN, "84.5%", "Attendance", "Overall Rate"));
        add(new SummaryCardPanel("", AdminTheme.FOREST_GREEN, "3.65", "SGPA", "Academic Standing"));
        add(new SummaryCardPanel("", AdminTheme.FOREST_GREEN, "2", "Approved Medicals", "All Processed"));
    }

    /**
     * Parameterized constructor used by individual summary card subclasses.
     */
    public SummaryCardPanel(String icon, Color accentColor, String value, String title, String subtitle) {
        this.isContainer = false;
        this.iconText = icon;
        this.accentColor = (accentColor != null) ? accentColor : AdminTheme.FOREST_GREEN;
        this.statValue = value;
        this.statTitle = title;
        this.statSubtitle = subtitle;

        initCardUI();
    }

    private void initCardUI() {
        setOpaque(false);
        setLayout(new BorderLayout(12, 0));
        setBorder(new EmptyBorder(12, 14, 12, 14));
        setPreferredSize(new Dimension(0, 75));

        // Left rounded green accent badge
        JPanel iconBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setPreferredSize(new Dimension(38, 38));

        // Right text container
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        String displayText;
        if (statValue != null && !statValue.trim().isEmpty()) {
            displayText = statValue + " " + (statTitle != null ? statTitle : "");
        } else {
            displayText = (statTitle != null) ? statTitle : "";
        }

        JLabel statLabel = new JLabel(displayText.trim());
        statLabel.setFont(AdminTheme.font(Font.BOLD, 14));
        statLabel.setForeground(AdminTheme.DEEP_GREEN);

        JLabel descLabel = new JLabel(statSubtitle != null ? statSubtitle : "");
        descLabel.setFont(AdminTheme.font(Font.PLAIN, 12));
        descLabel.setForeground(AdminTheme.MUTED);

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(statLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(descLabel);
        textPanel.add(Box.createVerticalGlue());

        add(iconBadge, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Do not paint single wide background if this instance is the container strip
        if (isContainer) {
            super.paintComponent(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill smooth rounded white card background
        g2.setColor(AdminTheme.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);

        // Draw soft outer border
        g2.setColor(CARD_BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);

        g2.dispose();
        super.paintComponent(g);
    }
}