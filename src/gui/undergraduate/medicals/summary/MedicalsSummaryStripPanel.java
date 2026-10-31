package gui.undergraduate.medicals.summary;

import dao.undergraduate.UndergraduateMedicalDAO.MedicalMetricsDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

/**
 * Metric cards strip displaying 4 key medical submission metrics
 * with optically centered 1:1 aspect ratio square badges.
 */
public class MedicalsSummaryStripPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JLabel totalValLabel;
    private final JLabel approvedValLabel;
    private final JLabel pendingValLabel;
    private final JLabel rejectedValLabel;

    public MedicalsSummaryStripPanel() {
        setOpaque(false);
        setLayout(new GridLayout(1, 4, 12, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        setPreferredSize(new Dimension(0, 78));

        totalValLabel = new JLabel("0 Submissions");
        approvedValLabel = new JLabel("0 Approved");
        pendingValLabel = new JLabel("0 Pending");
        rejectedValLabel = new JLabel("0 Rejected");

        add(createMiniStatCard("📋", totalValLabel, "Total Applications", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("✅", approvedValLabel, "Verified & Counted", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("⏳", pendingValLabel, "Awaiting Review", new Color(0xE6, 0x7E, 0x22))); // Warning Amber
        add(createMiniStatCard("❌", rejectedValLabel, "Declined / Invalid", new Color(0x7F, 0x8C, 0x8D))); // Muted Slate
    }

    public void updateCounts(MedicalMetricsDTO metrics) {
        if (metrics == null) return;
        updateCounts(metrics.totalCount, metrics.approvedCount, metrics.pendingCount, metrics.rejectedCount);
    }

    public void updateCounts(int total, int approved, int pending, int rejected) {
        totalValLabel.setText(total + " Submissions");
        approvedValLabel.setText(approved + " Approved");
        pendingValLabel.setText(pending + " Pending");
        rejectedValLabel.setText(rejected + " Rejected");
    }

    private JPanel createMiniStatCard(String iconSymbol, JLabel valLbl, String subVal, Color badgeColor) {
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
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

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
                g2.setColor(badgeColor);
                g2.fillRoundRect(bx, by, size, size, 10, 10);

                if (iconSymbol != null && !iconSymbol.isEmpty()) {
                    Font font = new Font("Segoe UI Emoji", Font.PLAIN, 16);
                    g2.setFont(font);
                    g2.setColor(Color.WHITE);

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

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        valLbl.setFont(AdminTheme.font(Font.BOLD, 13));
        valLbl.setForeground(AdminTheme.DEEP_GREEN);

        JLabel subLbl = new JLabel(subVal);
        subLbl.setFont(AdminTheme.font(Font.PLAIN, 11));
        subLbl.setForeground(AdminTheme.MUTED);

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(valLbl);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(subLbl);
        textPanel.add(Box.createVerticalGlue());

        card.add(iconBadge, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }
}