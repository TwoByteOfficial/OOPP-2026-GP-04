
package gui.lecturer.Dashboard.DashboardComponents;

import static gui.lecturer.SharedComponents.Colors.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CourseModulesTable extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color BORDER_GREEN = new Color(200, 230, 201);
    private static final Color PALE_GREEN = new Color(232, 245, 233);


    private final DefaultTableModel tableModel;

    public CourseModulesTable() {

        setLayout(new BorderLayout(0, 14));
        setBackground(WHITE);

        setBorder(new CompoundBorder(
                new LineBorder(BORDER_GREEN, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel titleLabel = new JLabel("My Course Modules");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(DEEP_GREEN);

        String[] columns = {
                "Code",
                "Course Name",
                "Credits",
                "CA Weight",
                "Students"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable courseTable = new JTable(tableModel);
        courseTable.setRowHeight(34);
        courseTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        courseTable.setFillsViewportHeight(true);
        courseTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        courseTable.setSelectionBackground(PALE_GREEN);
        courseTable.setSelectionForeground(DEEP_GREEN);
        courseTable.setGridColor(GRID_GREEN);

        courseTable.getTableHeader().setBackground(PALE_GREEN);
        courseTable.getTableHeader().setForeground(DEEP_GREEN);
        courseTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));
        courseTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(new LineBorder(BORDER_GREEN, 1));

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }


    public void setCourses(List<Object[]> courses) {

        tableModel.setRowCount(0);

        if (courses == null) {
            return;
        }

        for (Object[] course : courses) {

            Object[] row = {
                    course[1],  // Course Code
                    course[2],  // Course Name
                    course[3],  // Total Credits
                    course[6] == null
                            ? "N/A"
                            : course[6].toString() + "%", // CA Weight
                    course[7]   // Student Count
            };

            tableModel.addRow(row);
        }
    }

}
