package gui.undergraduate.dashboard.overview;

import dao.undergraduate.UndergraduateDashboardDAO.DashboardMetricsDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Mid-section analytics cards rendering multi-colored progress bars and quick academic status.
 */
public class OverviewBreakdownPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JLabel modulesTotalSubLabel;
    private final JLabel safeLabel;
    private final JProgressBar safeBar;
    private final JLabel borderLabel;
    private final JProgressBar borderBar;
    private final JLabel atRiskLabel;
    private final JProgressBar atRiskBar;

    private final JLabel pendingMedLabel;
    private final JLabel caBelow40Label;
    private final JLabel weeklySlotsLabel;

    public OverviewBreakdownPanel() {
        setOpaque(false);
        setLayout(new GridLayout(1, 2, 14, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 215));
        setPreferredSize(new Dimension(0, 215));

        // 1. Left Card: Attendance Distribution
        JPanel attCard = createCurvedCard();
        attCard.setLayout(new BoxLayout(attCard, BoxLayout.Y_AXIS));

        JLabel title1 = new JLabel("Attendance Distribution");
        title1.setFont(AdminTheme.font(Font.BOLD, 16));
        title1.setForeground(AdminTheme.FOREST_GREEN);
        title1.setAlignmentX(Component.LEFT_ALIGNMENT);

        modulesTotalSubLabel = new JLabel("Total enrolled course modules: 0");
        modulesTotalSubLabel.setFont(AdminTheme.font(Font.PLAIN, 12));
        modulesTotalSubLabel.setForeground(AdminTheme.MUTED);
        modulesTotalSubLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        attCard.add(title1);
        attCard.add(Box.createVerticalStrut(2));
        attCard.add(modulesTotalSubLabel);
        attCard.add(Box.createVerticalStrut(14));

        safeLabel = new JLabel("0 modules (0.0%)");
        safeBar = createBar(AdminTheme.FOREST_GREEN);
        attCard.add(createProgressRow("Above 80% (Eligibility Safe)", safeLabel, safeBar));
        attCard.add(Box.createVerticalStrut(10));

        borderLabel = new JLabel("0 modules (0.0%)");
        borderBar = createBar(AdminTheme.WARNING);
        attCard.add(createProgressRow("Exactly 80% (Borderline)", borderLabel, borderBar));
        attCard.add(Box.createVerticalStrut(10));

        atRiskLabel = new JLabel("0 modules (0.0%)");
        atRiskBar = createBar(AdminTheme.DANGER);
        attCard.add(createProgressRow("Below 80% (Not Eligible)", atRiskLabel, atRiskBar));

        // 2. Right Card: Academic Quick Status
        JPanel statusCard = createCurvedCard();
        statusCard.setLayout(new BoxLayout(statusCard, BoxLayout.Y_AXIS));

        JLabel title2 = new JLabel("Academic Quick Status");
        title2.setFont(AdminTheme.font(Font.BOLD, 16));
        title2.setForeground(AdminTheme.FOREST_GREEN);
        title2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub2 = new JLabel("Overview of ongoing semester evaluation items");
        sub2.setFont(AdminTheme.font(Font.PLAIN, 12));
        sub2.setForeground(AdminTheme.MUTED);
        sub2.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusCard.add(title2);
        statusCard.add(Box.createVerticalStrut(2));
        statusCard.add(sub2);
        statusCard.add(Box.createVerticalStrut(14));

        pendingMedLabel = new JLabel("0 Pending");
        caBelow40Label = new JLabel("0 Modules");
        weeklySlotsLabel = new JLabel("0 Sessions");

        statusCard.add(createStatusRow("Pending Medical Submissions", pendingMedLabel, AdminTheme.WARNING));
        statusCard.add(Box.createVerticalStrut(12));
        statusCard.add(createStatusRow("Continuous Assessments Below 40%", caBelow40Label, AdminTheme.DANGER));
        statusCard.add(Box.createVerticalStrut(12));
        statusCard.add(createStatusRow("Active Weekly Timetable Slots", weeklySlotsLabel, AdminTheme.FOREST_GREEN));

        add(attCard);
        add(statusCard);
    }

    public void updateMetrics(DashboardMetricsDTO dto, int timetableSlotsCount) {
        if (dto == null) return;
        int total = dto.enrolledCount;
        modulesTotalSubLabel.setText("Total enrolled course modules: " + total);

        if (total > 0) {
            double safePct = (dto.safeAttendanceCount * 100.0) / total;
            double borderPct = (dto.borderlineAttendanceCount * 100.0) / total;
            double riskPct = (dto.atRiskAttendanceCount * 100.0) / total;

            safeLabel.setText(String.format("%d modules (%.1f%%)", dto.safeAttendanceCount, safePct));
            safeBar.setValue((int) Math.round(safePct));

            borderLabel.setText(String.format("%d modules (%.1f%%)", dto.borderlineAttendanceCount, borderPct));
            borderBar.setValue((int) Math.round(borderPct));

            atRiskLabel.setText(String.format("%d modules (%.1f%%)", dto.atRiskAttendanceCount, riskPct));
            atRiskBar.setValue((int) Math.round(riskPct));
        }

        pendingMedLabel.setText(dto.pendingMedicals + " Pending");
        caBelow40Label.setText(dto.caBelow40Count + " Modules");
        weeklySlotsLabel.setText(timetableSlotsCount + " Sessions");
    }

    private JProgressBar createBar(Color color) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(0);
        bar.setPreferredSize(new Dimension(0, 7));
        bar.setForeground(color);
        bar.setBackground(AdminTheme.MINT);
        bar.setBorderPainted(false);
        return bar;
    }

    private JPanel createProgressRow(String label, JLabel valLbl, JProgressBar bar) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(AdminTheme.font(Font.PLAIN, 12));
        lbl.setForeground(new Color(0x33, 0x33, 0x33));

        valLbl.setFont(AdminTheme.font(Font.BOLD, 12));
        valLbl.setForeground(AdminTheme.DEEP_GREEN);

        header.add(lbl, BorderLayout.WEST);
        header.add(valLbl, BorderLayout.EAST);

        row.add(header, BorderLayout.NORTH);
        row.add(bar, BorderLayout.SOUTH);
        return row;
    }

    private JPanel createStatusRow(String label, JLabel valLbl, Color indicatorColor) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        left.setOpaque(false);

        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(5, 14));
        indicator.setBackground(indicatorColor);

        JLabel lbl = new JLabel(label);
        lbl.setFont(AdminTheme.font(Font.PLAIN, 13));
        lbl.setForeground(new Color(0x33, 0x33, 0x33));

        left.add(indicator);
        left.add(lbl);

        valLbl.setFont(AdminTheme.font(Font.BOLD, 13));
        valLbl.setForeground(AdminTheme.DEEP_GREEN);

        row.add(left, BorderLayout.WEST);
        row.add(valLbl, BorderLayout.EAST);
        return row;
    }

    private JPanel createCurvedCard() {
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
        card.setBorder(new EmptyBorder(14, 16, 14, 16));
        return card;
    }
}