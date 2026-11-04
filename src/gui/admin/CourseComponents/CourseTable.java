package gui.admin.CourseComponents;

import model.Course;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class CourseTable extends JTable {

    private DefaultTableModel model;
    private List<Course> courseList = new ArrayList<>();

    public CourseTable() {
        String[] columns = { "ID", "Code", "Course Name", "Credits", "CA %", "Final %", "Dept", "Academic Year", "Semester", "Status" };
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
                if (column == 9) { // Status column
                    setFont(new Font("Segoe UI", Font.BOLD, 15));
                    if ("Active".equalsIgnoreCase(String.valueOf(value))) {
                        setForeground(new Color(0x38, 0x8E, 0x3C));
                    } else {
                        setForeground(new Color(0xDC, 0x35, 0x45));
                    }
                }
                return c;
            }
        });

        int[] widths = { 50, 100, 320, 90, 80, 80, 80, 130, 110, 100 };
        for (int i = 0; i < widths.length && i < getColumnModel().getColumnCount(); i++) {
            getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    public void setCourses(List<Course> courses) {
        this.courseList = courses;
        model.setRowCount(0);
        for (Course c : courses) {
            String deptText = "Dept " + c.getDepartmentId();
            if (c.getDepartmentId() == 1) deptText = "ICT";
            else if (c.getDepartmentId() == 2) deptText = "ET";
            else if (c.getDepartmentId() == 3) deptText = "BST";

            model.addRow(new Object[]{
                    c.getCourseId(),
                    c.getCourseCode(),
                    c.getCourseName(),
                    c.getTotalCredits() != null ? c.getTotalCredits() : "3.0",
                    c.getCaWeight() != null ? c.getCaWeight() + "%" : "30%",
                    c.getFinalWeight() != null ? c.getFinalWeight() + "%" : "70%",
                    deptText,
                    c.getAcademicYear() != null ? c.getAcademicYear() : "2024/2025",
                    c.getSemester() != null ? c.getSemester() : "Semester 1",
                    c.getStatus() != null ? c.getStatus() : "Active"
            });
        }
    }

    public Course getSelectedCourse() {
        int selectedRow = getSelectedRow();
        if (selectedRow >= 0 && selectedRow < courseList.size()) {
            return courseList.get(selectedRow);
        }
        return null;
    }
}
