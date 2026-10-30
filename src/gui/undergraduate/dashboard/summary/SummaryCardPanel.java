package gui.undergraduate.dashboard.summary;

import dao.undergraduate.UndergraduateDashboardDAO.DashboardMetricsDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

/**
 * Responsive 4-card metric strip with true optical pixel-centered 1:1 badges.
 */
public class SummaryCardPanel extends JPanel {

    private String iconSymbol;
    private Color accentColor;
    private String statValue;
    private String statTitle;
    private String statSubtitle;
    private boolean isContainer = false;

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private SummaryCardPanel coursesCard;
    private SummaryCardPanel attendanceCard;
    private SummaryCardPanel gpaCard;
    private SummaryCardPanel medicalsCard;

    private JLabel statLabel;
    private JLabel descLabel;

    public SummaryCardPanel() {
        this.isContainer = true;
        setOpaque(false);
        setLayout(new GridLayout(1, 4, 12, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        setPreferredSize(new Dimension(0, 78));

        coursesCard = new SummaryCardPanel("📖", AdminTheme.FOREST_GREEN, "0", "Modules Active", "Enrolled Courses");
        attendanceCard = new SummaryCardPanel("📊", AdminTheme.FOREST_GREEN, "0.0%", "Attendance", "Overall Rate");
        gpaCard = new SummaryCardPanel("🎓", AdminTheme.FOREST_GREEN, "0.00", "SGPA", "Academic Standing");
        medicalsCard = new SummaryCardPanel("🏥", AdminTheme.FOREST_GREEN, "0", "Approved Medicals", "All Processed");

        add(coursesCard);
        add(attendanceCard);
        add(gpaCard);
        add(medicalsCard);
    }

    public SummaryCardPanel(String icon, Color accentColor, String value, String title, String subtitle) {
        this.isContainer = false;
        this.iconSymbol = icon;
        this.accentColor = (accentColor != null) ? accentColor : AdminTheme.FOREST_GREEN;
        this.statValue = value;
        this.statTitle = title;
        this.statSubtitle = subtitle;
        initCardUI();
    }

    private void initCardUI() {
        setOpaque(false);
        setLayout(new BorderLayout(10, 0));
        setBorder(new EmptyBorder(10, 12, 10, 12));

        // 1:1 Aspect Ratio Badge with True Pixel-Perfect Optical Visual Center
        JPanel iconBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int size = 38;
                int bx = (getWidth() - size) / 2;
                int by = (getHeight() - size) / 2;
                g2.setColor(accentColor);
                g2.fillRoundRect(bx, by, size, size, 10, 10);

                if (iconSymbol != null && !iconSymbol.isEmpty()) {
                    Font font = new Font("Segoe UI Emoji", Font.PLAIN, 16);
                    g2.setFont(font);
                    g2.setColor(Color.WHITE);

                    // Optical sub-pixel bounding box centering
                    GlyphVector gv = font.createGlyphVector(g2.getFontRenderContext(), iconSymbol);
                    Rectangle2D bounds = gv.getVisualBounds();

                    float tx = (float) (bx + (size - bounds.getWidth()) / 2.0 - bounds.getX());
                    float ty = (float) (by + (size - bounds.getHeight()) / 2.0 - bounds.getY());
                    g2.drawString(iconSymbol, tx, ty);
                }
                g2.dispose();
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setPreferredSize(new Dimension(38, 38));

        // Right Text Panel
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        String displayText = (statValue != null && !statValue.trim().isEmpty())
                ? (statValue + " " + (statTitle != null ? statTitle : "")).trim()
                : (statTitle != null ? statTitle : "");

        statLabel = new JLabel(displayText);
        statLabel.setFont(AdminTheme.font(Font.BOLD, 13));
        statLabel.setForeground(AdminTheme.DEEP_GREEN);

        descLabel = new JLabel(statSubtitle != null ? statSubtitle : "");
        descLabel.setFont(AdminTheme.font(Font.PLAIN, 11));
        descLabel.setForeground(AdminTheme.MUTED);

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(statLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(descLabel);
        textPanel.add(Box.createVerticalGlue());

        add(iconBadge, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
    }

    public void updateValues(String val, String title, String subtitle) {
        if (statLabel != null) statLabel.setText((val + " " + title).trim());
        if (descLabel != null) descLabel.setText(subtitle);
    }

    public void updateDashboardMetrics(DashboardMetricsDTO dto) {
        if (dto == null) return;
        coursesCard.updateValues(String.valueOf(dto.enrolledCount), "Modules Active", "Enrolled Courses");
        attendanceCard.updateValues(String.format("%.1f%%", dto.overallAttendance), "Attendance", "Overall Rate");
        gpaCard.updateValues(String.format("%.2f", dto.sgpa), "SGPA", "Academic Standing");
        medicalsCard.updateValues(String.valueOf(dto.approvedMedicals), "Approved Medicals",
                dto.pendingMedicals > 0 ? dto.pendingMedicals + " Pending Review" : "All Processed");
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isContainer) {
            super.paintComponent(g);
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(AdminTheme.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.setColor(CARD_BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.dispose();
        super.paintComponent(g);
    }
}