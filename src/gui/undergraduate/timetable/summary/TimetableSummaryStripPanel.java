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
 * Dynamically binds and renders weekly lecture workload hours, active course modules,
 * practical lab allocations, and scheduled lecture day counts via direct metrics or reflection.
 */
public class TimetableSummaryStripPanel extends JPanel {

    // Card styling constants for 16px corner curves and subtle perimeter border
    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    // Metric counter labels for dynamic summary display
    private final JLabel hoursValLabel;
    private final JLabel modulesValLabel;
    private final JLabel labsValLabel;
    private final JLabel daysValLabel;

    /**
     * Constructs the timetable summary strip panel with a 4-column grid layout
     * and default zero-state placeholder metric cards.
     */
    public TimetableSummaryStripPanel() {
        // Base layout configuration and fixed vertical strip dimensions
        setOpaque(false);
        setLayout(new GridLayout(1, 4, 12, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        setPreferredSize(new Dimension(0, 78));

        // Initialize metric value labels with baseline defaults
        hoursValLabel = new JLabel("0 Hours");
        modulesValLabel = new JLabel("0 Modules");
        labsValLabel = new JLabel("0 Labs");
        daysValLabel = new JLabel("0 Days");

        // -------------------------------------------------------------
        // Summary Stat Cards Assembly (4 Key Scheduling Metrics)
        // -------------------------------------------------------------
        add(createMiniStatCard("⏱️", hoursValLabel, "Weekly Workload", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("📚", modulesValLabel, "Active Courses", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("🔬", labsValLabel, "Practical Sessions", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("📅", daysValLabel, "Active Lecture Days", AdminTheme.FOREST_GREEN));
    }

    /**
     * Directly updates the displayed metric values using raw integer counts.
     *
     * @param weeklyHours       Total timetabled contact hours per week
     * @param activeCourses     Number of distinct enrolled modules scheduled
     * @param practicalSessions Number of scheduled practical lab classes
     * @param activeDays        Number of weekdays with at least one academic session
     */
    public void updateMetrics(int weeklyHours, int activeCourses, int practicalSessions, int activeDays) {
        hoursValLabel.setText(weeklyHours + " Hours");
        modulesValLabel.setText(activeCourses + " Modules");
        labsValLabel.setText(practicalSessions + " Labs");
        daysValLabel.setText(activeDays + " Days");
    }

    /**
     * Dynamically extracts metric values from a generic summary data transfer object
     * via reflection (matching field names or zero-argument getter methods) to maintain loose coupling.
     *
     * @param summaryObj Summary object or DTO holding timetable aggregate statistics
     */
    public void updateSummary(Object summaryObj) {
        if (summaryObj == null) return;
        int hours = 0;
        int modules = 0;
        int labs = 0;
        int days = 0;

        try {
            // Attempt extraction from declared class fields
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

            // Fallback: If reflection via fields returned zeros, attempt zero-arg getter methods
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

    /**
     * Alias method for updateSummary to support alternative data-binding conventions.
     *
     * @param summaryObj Data object containing timetable statistics
     */
    public void updateData(Object summaryObj) {
        updateSummary(summaryObj);
    }

    /**
     * Safely converts numeric or string data values into primitive integers.
     *
     * @param val Raw object value to parse
     * @return Integer value or 0 on failure
     */
    private int toInt(Object val) {
        if (val instanceof Number) return ((Number) val).intValue();
        try {
            return Integer.parseInt(String.valueOf(val));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Factory method creating a single stat card with a 1:1 square emoji badge and metric text.
     *
     * @param iconSymbol Unicode emoji symbol rendered inside the badge
     * @param valLbl     Dynamic JLabel presenting the metric count
     * @param subVal     Static subtitle describing the metric classification
     * @param badgeColor Accent fill color for the squircle badge
     * @return Fully configured stat card panel
     */
    private JPanel createMiniStatCard(String iconSymbol, JLabel valLbl, String subVal, Color badgeColor) {
        // -------------------------------------------------------------
        // Rounded Card Container with Anti-Aliased Border
        // -------------------------------------------------------------
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw rounded white card background
                g2.setColor(AdminTheme.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
                // Draw subtle border perimeter stroke
                g2.setColor(CARD_BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        // -------------------------------------------------------------
        // 1:1 Aspect Ratio Squircle Badge with Visual-Centered Emoji
        // -------------------------------------------------------------
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

                    // Compute precise typographic bounds for visual glyph centering
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

        // -------------------------------------------------------------
        // Right Text Panel (Vertical Stack: Stat Value & Subtitle)
        // -------------------------------------------------------------
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        valLbl.setFont(AdminTheme.font(Font.BOLD, 13));
        valLbl.setForeground(AdminTheme.DEEP_GREEN);

        JLabel subLbl = new JLabel(subVal);
        subLbl.setFont(AdminTheme.font(Font.PLAIN, 11));
        subLbl.setForeground(AdminTheme.MUTED);

        // Center labels vertically inside text panel
        textPanel.add(Box.createVerticalGlue());
        textPanel.add(valLbl);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(subLbl);
        textPanel.add(Box.createVerticalGlue());

        // Assemble card
        card.add(iconBadge, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }
}