package gui.undergraduate.grades.summary;

import dao.undergraduate.UndergraduateGradesDAO.TranscriptSummaryDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Metric cards container displaying 4 key GPA and academic standing statistics
 * with true visual pixel-centered 1:1 square badges.
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
        setLayout(new GridLayout(1, 4, 12, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        setPreferredSize(new Dimension(0, 78));

        cgpaValLabel = new JLabel("0.00");
        sgpaValLabel = new JLabel("0.00");
        creditsValLabel = new JLabel("0 Credits");
        standingValLabel = new JLabel("Good Standing");

        add(createMiniStatCard("🏆", cgpaValLabel, "Cumulative GPA (CGPA)", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("🎯", sgpaValLabel, "Current SGPA (Sem I)", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("🎖️", creditsValLabel, "Earned Degree Credits", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("⚠️", standingValLabel, "Academic Standing", AdminTheme.FOREST_GREEN));
    }

    public void updateSummary(TranscriptSummaryDTO summary) {
        if (summary == null) return;
        extractAndApply(summary);
    }

    public void updateData(TranscriptSummaryDTO summary) {
        updateSummary(summary);
    }

    public void updateSummary(Object summary) {
        if (summary != null) {
            extractAndApply(summary);
        }
    }

    private void extractAndApply(Object summary) {
        double cgpa = 0.0;
        double sgpa = 0.0;
        int credits = 0;
        String standing = "Good Standing";

        try {
            Field[] fields = summary.getClass().getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                String name = f.getName().toLowerCase();
                Object val = f.get(summary);
                if (val == null) continue;

                if (name.contains("cgpa")) {
                    cgpa = toDouble(val);
                } else if (name.contains("sgpa") || name.contains("gpa")) {
                    sgpa = toDouble(val);
                } else if (name.contains("credit")) {
                    credits = toInt(val);
                } else if (name.contains("standing") || name.contains("status")) {
                    standing = String.valueOf(val);
                }
            }

            // Fallback to getters if fields were not directly populated
            if (cgpa == 0.0 && sgpa == 0.0 && credits == 0) {
                for (Method m : summary.getClass().getDeclaredMethods()) {
                    if (m.getParameterCount() == 0) {
                        String mName = m.getName().toLowerCase();
                        Object val = m.invoke(summary);
                        if (val == null) continue;

                        if (mName.contains("cgpa")) {
                            cgpa = toDouble(val);
                        } else if (mName.contains("sgpa")) {
                            sgpa = toDouble(val);
                        } else if (mName.contains("credit")) {
                            credits = toInt(val);
                        } else if (mName.contains("standing")) {
                            standing = String.valueOf(val);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        updateMetrics(cgpa, sgpa, credits, standing);
    }

    private double toDouble(Object val) {
        if (val instanceof Number) return ((Number) val).doubleValue();
        try {
            return Double.parseDouble(String.valueOf(val));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int toInt(Object val) {
        if (val instanceof Number) return ((Number) val).intValue();
        try {
            return Integer.parseInt(String.valueOf(val));
        } catch (Exception e) {
            return 0;
        }
    }

    public void updateMetrics(double cgpa, double sgpa, int earnedCredits, String standing) {
        cgpaValLabel.setText(String.format("%.2f", cgpa));
        sgpaValLabel.setText(String.format("%.2f", sgpa));
        creditsValLabel.setText(earnedCredits + " Credits");
        standingValLabel.setText(standing != null && !standing.isEmpty() ? standing : "Good Standing");
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