package gui.undergraduate.dashboard.overview;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Mid-section analytics cards rendering multi-colored progress bars
 * and quick academic status rows with smooth rounded corners and strict left alignment.
 */
public class OverviewBreakdownPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    public OverviewBreakdownPanel() {
        setOpaque(false);
        setLayout(new GridLayout(1, 2, 15, 0));
        setPreferredSize(new Dimension(0, 215));

        add(buildAttendanceCard());
        add(buildStatusCard());
    }

    private JPanel buildAttendanceCard() {
        JPanel card = createCurvedCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Attendance Distribution");
        title.setFont(AdminTheme.font(Font.BOLD, 17));
        title.setForeground(AdminTheme.FOREST_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Total enrolled course modules: 6");
        sub.setFont(AdminTheme.font(Font.PLAIN, 13));
        sub.setForeground(AdminTheme.MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(2));
        card.add(sub);
        card.add(Box.createVerticalStrut(14));

        card.add(createProgressRow("Above 80% (Eligibility Safe)", "5 modules (83.3%)", 83, AdminTheme.FOREST_GREEN));
        card.add(Box.createVerticalStrut(10));
        card.add(createProgressRow("Exactly 80% (Borderline)", "1 module (16.7%)", 17, AdminTheme.WARNING));
        card.add(Box.createVerticalStrut(10));
        card.add(createProgressRow("Below 80% (Not Eligible)", "0 modules (0.0%)", 0, AdminTheme.DANGER));

        return card;
    }

    private JPanel buildStatusCard() {
        JPanel card = createCurvedCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Academic Quick Status");
        title.setFont(AdminTheme.font(Font.BOLD, 17));
        title.setForeground(AdminTheme.FOREST_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(20));

        card.add(createStatusRow("Pending Medical Submissions", "1 Pending", AdminTheme.WARNING));
        card.add(Box.createVerticalStrut(14));
        card.add(createStatusRow("Continuous Assessments Below 40%", "0 Modules", AdminTheme.DANGER));
        card.add(Box.createVerticalStrut(14));
        card.add(createStatusRow("Active Weekly Timetable Slots", "14 Sessions", AdminTheme.FOREST_GREEN));

        return card;
    }

    private JPanel createProgressRow(String label, String stat, int percentage, Color barColor) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(AdminTheme.font(Font.PLAIN, 13));
        lbl.setForeground(new Color(0x33, 0x33, 0x33));

        JLabel val = new JLabel(stat);
        val.setFont(AdminTheme.font(Font.BOLD, 13));
        val.setForeground(AdminTheme.DEEP_GREEN);

        header.add(lbl, BorderLayout.WEST);
        header.add(val, BorderLayout.EAST);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(percentage);
        bar.setPreferredSize(new Dimension(0, 7));
        bar.setForeground(barColor);
        bar.setBackground(AdminTheme.MINT);
        bar.setBorderPainted(false);

        row.add(header, BorderLayout.NORTH);
        row.add(bar, BorderLayout.SOUTH);
        return row;
    }

    private JPanel createStatusRow(String label, String value, Color indicatorColor) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(6, 16));
        indicator.setBackground(indicatorColor);

        JLabel lbl = new JLabel(label);
        lbl.setFont(AdminTheme.font(Font.PLAIN, 14));
        lbl.setForeground(new Color(0x33, 0x33, 0x33));

        left.add(indicator);
        left.add(lbl);

        JLabel val = new JLabel(value);
        val.setFont(AdminTheme.font(Font.BOLD, 14));
        val.setForeground(AdminTheme.DEEP_GREEN);

        row.add(left, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    /**
     * Creates a white panel with smooth anti-aliased curved corners.
     */
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
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        return card;
    }
}