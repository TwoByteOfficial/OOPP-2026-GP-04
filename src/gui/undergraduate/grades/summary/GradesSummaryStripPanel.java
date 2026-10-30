package gui.undergraduate.grades.summary;

import gui.admin.AdminTheme;
import gui.undergraduate.grades.model.GpaSummaryData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Metric cards container displaying 4 key academic figures: CGPA, SGPA, Completed Credits, and Standing.
 */
public class GradesSummaryStripPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JLabel cgpaValLabel;
    private final JLabel sgpaValLabel;
    private final JLabel creditsValLabel;
    private final JLabel standingValLabel;

    public GradesSummaryStripPanel() {
        setOpaque(false);
        setLayout(new GridLayout(1, 4, 15, 0));
        setPreferredSize(new Dimension(0, 75));

        cgpaValLabel = new JLabel("3.65");
        sgpaValLabel = new JLabel("3.72");
        creditsValLabel = new JLabel("48 Credits");
        standingValLabel = new JLabel("Dean's List");

        add(createMiniStatCard(cgpaValLabel, "Cumulative GPA (CGPA)", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard(sgpaValLabel, "Current SGPA (Sem I)", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard(creditsValLabel, "Earned Degree Credits", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard(standingValLabel, "Academic Standing", AdminTheme.MEDIUM_GREEN));
    }

    public void updateSummary(GpaSummaryData data) {
        if (data != null) {
            cgpaValLabel.setText(String.format("%.2f", data.getCgpa()));
            sgpaValLabel.setText(String.format("%.2f", data.getCurrentSgpa()));
            creditsValLabel.setText(data.getCompletedCredits() + " Credits");
            standingValLabel.setText(data.getAcademicStanding());
        }
    }

    private JPanel createMiniStatCard(JLabel valLbl, String subVal, Color badgeColor) {
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(38, 38));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        valLbl.setFont(AdminTheme.font(Font.BOLD, 15));
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