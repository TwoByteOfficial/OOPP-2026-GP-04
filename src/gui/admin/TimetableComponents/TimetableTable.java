package gui.admin.TimetableComponents;

import dao.admin.AdminCourseDAO;
import dao.admin.AdminUserDAO;
import gui.admin.AdminTheme;
import model.Course;
import model.Lecturer;
import model.TimetableEntry;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimetableTable extends JTable {

    private DefaultTableModel model;
    private List<TimetableEntry> entryList = new ArrayList<>();

    public TimetableTable() {
        String[] columns = { "Slot ID", "Day", "Time Window", "Course (Code & Name)", "Component", "Venue", "Department", "Batch", "Lecturer" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        setModel(model);

        setFont(AdminTheme.FONT_SMALL);
        setRowHeight(44);
        setBackground(AdminTheme.WHITE);
        setForeground(new Color(0x21, 0x25, 0x29));
        setSelectionBackground(AdminTheme.PASTEL_GREEN);
        setSelectionForeground(AdminTheme.DEEP_GREEN);
        setShowGrid(true);
        setGridColor(new Color(0xE0, 0xE0, 0xE0));

        JTableHeader header = getTableHeader();
        header.setFont(AdminTheme.FONT_BODY_BOLD);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 46));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setBackground(AdminTheme.MINT);
                l.setForeground(AdminTheme.DEEP_GREEN);
                l.setFont(AdminTheme.FONT_BODY_BOLD);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 1, AdminTheme.PASTEL_GREEN));
                return l;
            }
        });

        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? AdminTheme.WHITE : new Color(0xF9, 0xFB, 0xF9));
                    c.setForeground(new Color(0x21, 0x25, 0x29));
                }
                return c;
            }
        });

        int[] widths = { 90, 110, 160, 320, 120, 130, 120, 90, 180 };
        for (int i = 0; i < widths.length && i < getColumnModel().getColumnCount(); i++) {
            getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    public void setTimetables(List<TimetableEntry> entries) {
        setTimetables(entries, null, null);
    }

    public void setTimetables(List<TimetableEntry> entries, AdminCourseDAO courseDAO, AdminUserDAO userDAO) {
        this.entryList = entries != null ? entries : new ArrayList<>();
        model.setRowCount(0);

        Map<Integer, String> courseMap = new HashMap<>();
        Map<Long, String> lecturerMap = new HashMap<>();

        if (courseDAO != null) {
            try {
                List<Course> courses = courseDAO.getAllCourses();
                if (courses != null) {
                    for (Course c : courses) courseMap.put(c.getCourseId(), c.getCourseCode() + " - " + c.getCourseName());
                }
            } catch (Exception ignored) {}
        }

        if (userDAO != null) {
            try {
                List<User> lecturers = userDAO.getUsersByRole("Lecturer");
                if (lecturers != null) {
                    for (User u : lecturers) {
                        String title = (u instanceof Lecturer && ((Lecturer) u).getTitle() != null) ? ((Lecturer) u).getTitle() + " " : "";
                        lecturerMap.put(u.getUserId(), title + u.getFullName());
                    }
                }
            } catch (Exception ignored) {}
        }

        for (TimetableEntry t : entryList) {
            String timeWindow = (t.getStartTime() != null && t.getEndTime() != null) ? t.getStartTime() + " - " + t.getEndTime() : "N/A";
            String courseText = courseMap.getOrDefault(t.getCourseId(), "Course ID " + t.getCourseId());
            String lecturerText = t.getLecturerId() != null ? lecturerMap.getOrDefault(t.getLecturerId(), "Lecturer ID " + t.getLecturerId()) : "Unassigned";

            model.addRow(new Object[]{
                    "TT-" + t.getTimetableId(),
                    t.getDayOfWeek() != null ? t.getDayOfWeek() : "N/A",
                    timeWindow,
                    courseText,
                    t.getComponent() != null ? t.getComponent() : "Lecture",
                    t.getVenue() != null ? t.getVenue() : "N/A",
                    "Dept " + t.getDepartmentId(),
                    t.getBatchYear(),
                    lecturerText
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
