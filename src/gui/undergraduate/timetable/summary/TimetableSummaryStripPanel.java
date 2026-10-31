package gui.undergraduate.timetable.summary;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Metric cards strip displaying 4 key timetable summary statistics
 * with optically centered 1:1 aspect ratio square badges.
 */
public class TimetableSummaryStripPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JLabel hoursValLabel;
    private final JLabel modulesValLabel;
    private final JLabel labsValLabel;
    private final JLabel daysValLabel;

    public TimetableSummaryStripPanel() {
        setOpaque(false);
        setLayout(new GridLayout(1, 4, 12, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        setPreferredSize(new Dimension(0, 78));

        hoursValLabel = new JLabel("0 Hours");
        modulesValLabel = new JLabel("0 Modules");
        labsValLabel = new JLabel("0 Labs");
        daysValLabel = new JLabel("0 Days");

        add(createMiniStatCard("⏱️", hoursValLabel, "Weekly Workload", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("📚", modulesValLabel, "Active Courses", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("🔬", labsValLabel, "Practical Sessions", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("📅", daysValLabel, "Active Lecture Days", AdminTheme.FOREST_GREEN));
    }

    public void updateMetrics(int weeklyHours, int activeCourses, int practicalSessions, int activeDays) {
        hoursValLabel.setText(weeklyHours + " Hours");
        modulesValLabel.setText(activeCourses + " Modules");
        labsValLabel.setText(practicalSessions + " Labs");
        daysValLabel.setText(activeDays + " Days");
    }

    public void updateSummary(Object summaryObj) {
        if (summaryObj == null) return;
        int hours = 0;
        int modules = 0;
        int labs = 0;
        int days = 0;

        try {
            Field[] fields = summaryObj.getClass().getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                String name = f.getName().toLowerCase();
                Object val = f.get(summaryObj);
                if (val == null) continue;

                if (name.contains("hour") || name.contains("workload")) {
                    hours = toInt(val);
                } else if (name.contains("module") || name.contains("course")) {
                    modules = toInt(val);
                } else if (name.contains("lab") || name.contains("practical")) {
                    labs = toInt(val);
                } else if (name.contains("day")) {
                    days = toInt(val);
                }
            }

            if (hours == 0 && modules == 0 && labs == 0 && days == 0) {
                for (Method m : summaryObj.getClass().getDeclaredMethods()) {
                    if (m.getParameterCount() == 0) {
                        String mName = m.getName().toLowerCase();
                        Object val = m.invoke(summaryObj);
                        if (val == null) continue;

                        if (mName.contains("hour")) hours = toInt(val);
                        else if (mName.contains("module") || mName.contains("course")) modules = toInt(val);
                        else if (mName.contains("lab") || mName.contains("practical")) labs = toInt(val);
                        else if (mName.contains("day")) days = toInt(val);
                    }
                }
            }
        } catch (Exception ignored) {}

        updateMetrics(hours, modules, labs, days);
    }

    public void updateData(Object summaryObj) {
        updateSummary(summaryObj);
    }

    private int toInt(Object val) {
        if (val instanceof Number) return ((Number) val).intValue();
        try {
            return Integer.parseInt(String.valueOf(val));
        } catch (Exception e) {
            return 0;
        }
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