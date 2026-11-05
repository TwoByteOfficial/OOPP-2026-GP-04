package gui.undergraduate.notices.summary;

import dao.undergraduate.UndergraduateNoticeDAO.NoticeCountsDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Metric cards strip displaying active bulletins, circulars, exam notices, and emergency alerts.
 * Features 1:1 aspect ratio square badges with pixel-perfect visual-centered glyphs.
 * Provides dynamic multi-source binding for notices metrics via DTOs, entity lists, or reflection.
 */
public class NoticesSummaryStripPanel extends JPanel {

    // Card styling constants for 16px corner curves and subtle perimeter border
    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    // Metric counter labels for dynamic summary display
    private final JLabel totalValLabel;
    private final JLabel circularsValLabel;
    private final JLabel examValLabel;
    private final JLabel emergencyValLabel;

    /**
     * Initializes the summary strip with a 4-column grid layout and default placeholder metric cards.
     */
    public NoticesSummaryStripPanel() {
        // Base layout configuration and fixed vertical strip dimensions
        setOpaque(false);
        setLayout(new GridLayout(1, 4, 12, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        setPreferredSize(new Dimension(0, 78));

        // Initialize counter value labels with zero-state defaults
        totalValLabel = new JLabel("0 Notices");
        circularsValLabel = new JLabel("0 Circulars");
        examValLabel = new JLabel("0 Notices");
        emergencyValLabel = new JLabel("0 Emergency");

        // -------------------------------------------------------------
        // Summary Stat Cards Assembly (4 Key Notice Classifications)
        // -------------------------------------------------------------
        add(createMiniStatCard("📢", totalValLabel, "Active Bulletins", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("📜", circularsValLabel, "Academic Updates", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("📝", examValLabel, "Exam Department", AdminTheme.FOREST_GREEN));
        add(createMiniStatCard("🚨", emergencyValLabel, "Immediate Action", new Color(0xD3, 0x54, 0x00)));
    }

    /**
     * Directly updates the counter labels using explicit integer counts.
     *
     * @param total     Total active notices published
     * @param circulars Academic circulars and general updates count
     * @param exam      Exam-specific notices count
     * @param emergency Emergency and urgent alerts count
     */
    public void updateCounts(int total, int circulars, int exam, int emergency) {
        totalValLabel.setText(total + " Notices");
        circularsValLabel.setText(circulars + " Circulars");
        examValLabel.setText(exam + " Notices");
        emergencyValLabel.setText(emergency + " Emergency");
    }

    /**
     * Updates counters using a typed NoticeCountsDTO transfer object.
     *
     * @param counts DTO containing notice classification metrics
     */
    public void updateCounts(NoticeCountsDTO counts) {
        if (counts == null) return;
        extractAndApply(counts);
    }

    /**
     * Overloaded coordinator method that detects the incoming object type
     * (DTO, List, or generic object) and dispatches to the appropriate parser.
     *
     * @param countsObj Data container or collection containing notice statistics
     */
    public void updateCounts(Object countsObj) {
        if (countsObj instanceof NoticeCountsDTO) {
            updateCounts((NoticeCountsDTO) countsObj);
        } else if (countsObj instanceof List<?>) {
            updateCounts((List<?>) countsObj);
        } else if (countsObj != null) {
            extractAndApply(countsObj);
        }
    }

    /**
     * Calculates and updates counts by inspecting a list of notice objects,
     * classifying each item into its respective category based on notice type strings.
     *
     * @param noticesList List of notice entity or DTO objects
     */
    public void updateCounts(List<?> noticesList) {
        if (noticesList == null) return;
        int total = noticesList.size();
        int circulars = 0;
        int exam = 0;
        int emergency = 0;

        for (Object item : noticesList) {
            String type = extractValue(item, "getNoticeType", "getType", "noticeType", "type").toLowerCase();
            if (type.contains("academic") || type.contains("circular") || type.contains("general")) {
                circulars++;
            } else if (type.contains("exam")) {
                exam++;
            } else if (type.contains("emergency") || type.contains("urgent")) {
                emergency++;
            }
        }
        updateCounts(total, circulars, exam, emergency);
    }

    /**
     * Alias method for updateCounts to satisfy summary update listeners.
     */
    public void updateSummary(Object summaryObj) {
        updateCounts(summaryObj);
    }

    /**
     * Alias method for updateCounts to support alternate data-binding conventions.
     */
    public void updateData(Object summaryObj) {
        updateCounts(summaryObj);
    }

    /**
     * Dynamically extracts metric field values or getter method results from a summary object
     * via reflection to ensure loose coupling with backend DTO changes.
     */
    private void extractAndApply(Object summaryObj) {
        int total = 0;
        int circulars = 0;
        int exam = 0;
        int emergency = 0;

        try {
            // Attempt extraction from declared class fields
            Field[] fields = summaryObj.getClass().getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                String name = f.getName().toLowerCase();
                Object val = f.get(summaryObj);
                if (val == null) continue;

                if (name.contains("total") || name.equals("allnotices") || name.equals("count")) {
                    total = toInt(val);
                } else if (name.contains("circular") || name.contains("academic")) {
                    circulars = toInt(val);
                } else if (name.contains("exam")) {
                    exam = toInt(val);
                } else if (name.contains("emergency") || name.contains("urgent")) {
                    emergency = toInt(val);
                }
            }

            // Fallback: If fields yielded zero, attempt extraction via zero-arg getter methods
            if (total == 0 && circulars == 0 && exam == 0 && emergency == 0) {
                for (Method m : summaryObj.getClass().getDeclaredMethods()) {
                    if (m.getParameterCount() == 0) {
                        String mName = m.getName().toLowerCase();
                        Object val = m.invoke(summaryObj);
                        if (val == null) continue;

                        if (mName.contains("total")) total = toInt(val);
                        else if (mName.contains("circular") || mName.contains("academic")) circulars = toInt(val);
                        else if (mName.contains("exam")) exam = toInt(val);
                        else if (mName.contains("emergency") || mName.contains("urgent")) emergency = toInt(val);
                    }
                }
            }
        } catch (Exception ignored) {}

        updateCounts(total, circulars, exam, emergency);
    }

    /**
     * Extracts string values from an object using candidate method names or public fields.
     */
    private String extractValue(Object obj, String... methodsOrFields) {
        if (obj == null) return "";
        for (String name : methodsOrFields) {
            try {
                Method m = obj.getClass().getMethod(name);
                Object val = m.invoke(obj);
                if (val != null) return String.valueOf(val);
            } catch (Exception ignored) {}
            try {
                Field f = obj.getClass().getField(name);
                Object val = f.get(obj);
                if (val != null) return String.valueOf(val);
            } catch (Exception ignored) {}
        }
        return "";
    }

    /**
     * Converts a raw numeric or string object into an integer safely.
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