package gui.admin.CourseComponents;

import gui.admin.AdminTheme;
import model.Course;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CourseTable - Self-contained custom JTable component (and scroll pane container)
 * for displaying course module datasets with styled headers, alternating rows, and status indicators.
 */
public class CourseTable extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private List<Course> currentDisplayedCourses = new ArrayList<>();

    public CourseTable() {
        setLayout(new BorderLayout());
        setBackground(AdminTheme.WHITE);

        String[] columns = {
                "Course Code",
                "Course Name",
                "Credits (T/P/Tot)",
                "Department",
                "Semester",
                "CA / Final Wt (%)",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(42);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(AdminTheme.PASTEL_GREEN);
        table.setBackground(AdminTheme.WHITE);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(AdminTheme.MINT);
        header.setForeground(AdminTheme.DEEP_GREEN);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 46));
        header.setReorderingAllowed(false);

        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // Column Width Allocations
        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        table.getColumnModel().getColumn(1).setPreferredWidth(280);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);
        table.getColumnModel().getColumn(5).setPreferredWidth(160);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);

        // Custom Cell Renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);

                c.setFont(new Font("Segoe UI", Font.PLAIN, 15));

                if (isSelected) {
                    c.setBackground(AdminTheme.PASTEL_GREEN);
                    c.setForeground(AdminTheme.DEEP_GREEN);
                } else {
                    c.setBackground(row % 2 == 0 ? AdminTheme.WHITE : AdminTheme.MINT);
                    c.setForeground(Color.BLACK);
                }

                if (column == 6) { // Status Column
                    String status = String.valueOf(value);
                    setFont(new Font("Segoe UI", Font.BOLD, 15));
                    if ("Active".equalsIgnoreCase(status)) {
                        setForeground(AdminTheme.FOREST_GREEN);
                    } else {
                        setForeground(AdminTheme.DANGER);
                    }
                }

                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(AdminTheme.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setCourses(List<Course> courses) {
        this.currentDisplayedCourses = (courses != null) ? courses : new ArrayList<>();
        tableModel.setRowCount(0);

        for (Course c : currentDisplayedCourses) {
            String creditsStr = c.getTheoryCredits() + "T / " + c.getPracticalCredits() + "P (" + c.getTotalCredits() + ")";

            String deptText = "Dept " + c.getDepartmentId();
            if (c.getDepartmentId() == 1) deptText = "ICT";
            else if (c.getDepartmentId() == 2) deptText = "Engineering Tech";
            else if (c.getDepartmentId() == 3) deptText = "Bio Systems Tech";

            String weightsStr = c.getCaWeight() + "% / " + c.getFinalWeight() + "%";

            Object[] rowData = {
                    c.getCourseCode(),
                    c.getCourseName(),
                    creditsStr,
                    deptText,
                    c.getSemester() != null ? c.getSemester() : "Semester I",
                    weightsStr,
                    c.getStatus() != null ? c.getStatus() : "Active"
            };

            tableModel.addRow(rowData);
        }
    }

    public Course getSelectedCourse() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < currentDisplayedCourses.size()) {
            return currentDisplayedCourses.get(selectedRow);
        }
        return null;
    }

    public JTable getTable() {
        return table;
    }
}
