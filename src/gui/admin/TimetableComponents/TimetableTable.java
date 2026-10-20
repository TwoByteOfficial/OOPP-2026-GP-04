package gui.admin.TimetableComponents;

import model.TimetableEntry;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class TimetableTable extends JTable {

    private DefaultTableModel model;
    private List<TimetableEntry> entryList = new ArrayList<>();

    public TimetableTable() {
        String[] columns = { "Slot ID", "Day", "Time Window", "Course ID", "Component", "Venue", "Dept", "Batch", "Lecturer ID" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        setModel(model);

        setFont(new Font("Segoe UI", Font.PLAIN, 15));
        setRowHeight(44);
        setBackground(Color.WHITE);
        setForeground(new Color(0x21, 0x25, 0x29));
        setSelectionBackground(new Color(0xC8, 0xE6, 0xC9));
        setSelectionForeground(new Color(0x1B, 0x5E, 0x20));
        setShowGrid(true);
        setGridColor(new Color(0xE0, 0xE0, 0xE0));

        JTableHeader header = getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 46));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setBackground(new Color(0xE8, 0xF5, 0xE9));
                l.setForeground(new Color(0x1B, 0x5E, 0x20));
                l.setFont(new Font("Segoe UI", Font.BOLD, 16));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(0xC8, 0xE6, 0xC9)));
                return l;
            }
        });

        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF9, 0xFB, 0xF9));
                    c.setForeground(new Color(0x21, 0x25, 0x29));
                }
                return c;
            }
        });

        int[] widths = { 90, 120, 200, 110, 130, 180, 110, 90, 110 };
        for (int i = 0; i < widths.length && i < getColumnModel().getColumnCount(); i++) {
            getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    public void setTimetables(List<TimetableEntry> entries) {
        this.entryList = entries;
        model.setRowCount(0);
        for (TimetableEntry t : entries) {
            String timeWindow = (t.getStartTime() != null ? t.getStartTime().toString() : "08:00") + " - " + (t.getEndTime() != null ? t.getEndTime().toString() : "10:00");
            String deptText = "Dept " + t.getDepartmentId();

            model.addRow(new Object[]{
                    "TT-" + t.getTimetableId(),
                    t.getDayOfWeek() != null ? t.getDayOfWeek() : "Monday",
                    timeWindow,
                    "CID-" + t.getCourseId(),
                    t.getComponent() != null ? t.getComponent() : "Theory",
                    t.getVenue() != null ? t.getVenue() : "Main Auditorium",
                    deptText,
                    t.getBatchYear() > 0 ? t.getBatchYear() : 2024,
                    t.getLecturerId() != null ? "UID-" + t.getLecturerId() : "Unassigned"
            });
        }
    }

    public TimetableEntry getSelectedTimetable() {
        int selectedRow = getSelectedRow();
        if (selectedRow >= 0 && selectedRow < entryList.size()) {
            return entryList.get(selectedRow);
        }
        return null;
    }
}
