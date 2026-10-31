package gui.undergraduate.timetable.table;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Class schedule directory table panel displaying weekly academic lecture entries.
 * Optimized column allocations prevent title truncation and ensure centered alignments.
 */
public class TimetableDirectoryPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JTable table;
    private final DefaultTableModel model;
    private final List<Object> masterList = new ArrayList<>();
    private final Consumer<Integer> onSelectListener;
    private String currentDayFilter = "ALL";

    public TimetableDirectoryPanel() {
        this(null);
    }

    public TimetableDirectoryPanel(Consumer<Integer> onSelectListener) {
        this.onSelectListener = onSelectListener;

        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(16, 18, 16, 18));

        // Header Title Row
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel titleLbl = new JLabel("Class Schedule Directory");
        titleLbl.setFont(AdminTheme.font(Font.BOLD, 15));
        titleLbl.setForeground(AdminTheme.FOREST_GREEN);

        JLabel subLbl = new JLabel("Select a session to view hall & instructor details");
        subLbl.setFont(AdminTheme.font(Font.PLAIN, 12));
        subLbl.setForeground(AdminTheme.MUTED);

        topRow.add(titleLbl, BorderLayout.WEST);
        topRow.add(subLbl, BorderLayout.EAST);
        add(topRow, BorderLayout.NORTH);

        String[] headers = {"Day", "Time", "Code", "Course Name", "Type", "Venue"};
        model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFont(AdminTheme.font(Font.PLAIN, 12));
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(AdminTheme.PASTEL_GREEN);
        table.setSelectionForeground(AdminTheme.DEEP_GREEN);
        table.setFocusable(false);

        JTableHeader th = table.getTableHeader();
        th.setFont(AdminTheme.font(Font.BOLD, 12));
        th.setBackground(AdminTheme.MINT);
        th.setForeground(AdminTheme.DEEP_GREEN);
        th.setPreferredSize(new Dimension(0, 34));
        th.setReorderingAllowed(false);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD2, 0xD7, 0xE1)));

        // Column width allocations to prevent text clipping
        table.getColumnModel().getColumn(0).setPreferredWidth(65);
        table.getColumnModel().getColumn(0).setMaxWidth(75);

        table.getColumnModel().getColumn(1).setPreferredWidth(85);
        table.getColumnModel().getColumn(1).setMaxWidth(95);

        table.getColumnModel().getColumn(2).setPreferredWidth(55);
        table.getColumnModel().getColumn(2).setMaxWidth(65);

        // Course Name expanded to 235px to display full title without truncation
        table.getColumnModel().getColumn(3).setPreferredWidth(235);

        table.getColumnModel().getColumn(4).setPreferredWidth(55);
        table.getColumnModel().getColumn(4).setMaxWidth(65);

        table.getColumnModel().getColumn(5).setPreferredWidth(95);

        // Alignments: Center for Day, Time, Code, Type, and Venue
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(center);

        // Alignments: Left for Course Name
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setBorder(new EmptyBorder(0, 8, 0, 8));
        table.getColumnModel().getColumn(3).setCellRenderer(left);

        // Type Column Custom Badge Style Renderer
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, r, c);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(AdminTheme.font(Font.BOLD, 12));
                String text = String.valueOf(val);
                if ("Lab".equalsIgnoreCase(text) || "Practical".equalsIgnoreCase(text)) {
                    lbl.setForeground(new Color(0x29, 0x80, 0xB9));
                } else {
                    lbl.setForeground(new Color(0x2E, 0x7D, 0x32));
                }
                return lbl;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row != -1 && this.onSelectListener != null) {
                    this.onSelectListener.accept(row);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xEAEAEA)));
        scroll.getViewport().setBackground(AdminTheme.WHITE);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scroll, BorderLayout.CENTER);
    }

    public void setTimetableData(List<?> list) {
        masterList.clear();
        if (list != null) {
            masterList.addAll(list);
        }
        applyFilter(currentDayFilter);
    }

    public void setEntries(List<?> list) {
        setTimetableData(list);
    }

    public void updateData(List<?> list) {
        setTimetableData(list);
    }

    public void filterByDay(String dayName) {
        this.currentDayFilter = (dayName == null || dayName.trim().isEmpty() || "ALL".equalsIgnoreCase(dayName) || "ALL DAYS".equalsIgnoreCase(dayName))
                ? "ALL" : dayName.trim();
        applyFilter(this.currentDayFilter);
    }

    private void applyFilter(String day) {
        model.setRowCount(0);
        for (Object item : masterList) {
            String itemDay = extractString(item, "getDayOfWeek", "dayOfWeek", "getDay", "day");
            if ("ALL".equalsIgnoreCase(day) || itemDay.equalsIgnoreCase(day)) {
                String time = extractString(item, "getTimeSlot", "timeSlot", "getTime", "time");
                String code = extractString(item, "getCourseCode", "courseCode", "getCode", "code");
                String name = extractString(item, "getCourseName", "courseName", "getName", "name");
                String type = extractString(item, "getSessionType", "sessionType", "getType", "type");
                String venue = extractString(item, "getVenue", "venue", "getLocation", "location");

                if (type.isEmpty()) type = "Theory";
                model.addRow(new Object[]{itemDay, time, code, name, type, venue});
            }
        }
        selectFirstRow();
    }

    public void selectFirstRow() {
        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    private String extractString(Object obj, String... candidates) {
        if (obj == null) return "";
        for (String c : candidates) {
            try {
                Method m = obj.getClass().getMethod(c);
                Object res = m.invoke(obj);
                if (res != null) return String.valueOf(res);
            } catch (Exception ignored) {}
            try {
                Object res = obj.getClass().getField(c).get(obj);
                if (res != null) return String.valueOf(res);
            } catch (Exception ignored) {}
        }
        return "";
    }

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
}