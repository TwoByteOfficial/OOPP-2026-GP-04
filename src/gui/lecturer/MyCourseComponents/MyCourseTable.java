
package gui.lecturer.MyCourseComponents;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.function.IntConsumer;

public class MyCourseTable extends JPanel {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private static final Color BORDER_GREEN =
            new Color(200, 230, 201);

    private final DefaultTableModel tableModel;
    private final JTable courseTable;

    public MyCourseTable(IntConsumer onShowCourse) {

        setLayout(new BorderLayout());
        setOpaque(false);

        String[] columns = {
                "Course ID",
                "Code",
                "Course",
                "Credits",
                "Theory",
                "Practical",
                "CA Weight",
                "Enrolled Students",
                "Actions"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8;
            }
        };

        courseTable = new JTable(tableModel);

        courseTable.setRowHeight(40);
        courseTable.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        courseTable.setFillsViewportHeight(true);
        courseTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        courseTable.setSelectionBackground(PALE_GREEN);
        courseTable.setSelectionForeground(DEEP_GREEN);
        courseTable.setGridColor(BORDER_GREEN);
        courseTable.setAutoCreateRowSorter(true);

        courseTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        courseTable.getTableHeader().setBackground(PALE_GREEN);
        courseTable.getTableHeader().setForeground(DEEP_GREEN);
        courseTable.getTableHeader().setReorderingAllowed(false);

        // Hide Course ID from the visible table.
        courseTable.removeColumn(
                courseTable.getColumnModel().getColumn(0)
        );

        // Configure the Show button.
        courseTable.getColumnModel()
                .getColumn(7)
                .setCellRenderer(new ShowButtonRenderer());

        courseTable.getColumnModel()
                .getColumn(7)
                .setCellEditor(new ShowButtonEditor(onShowCourse));

        JScrollPane scrollPane = new JScrollPane(courseTable);

        scrollPane.setBorder(
                new LineBorder(BORDER_GREEN, 1)
        );

        add(scrollPane, BorderLayout.CENTER);
    }

    public void clearCourses() {
        tableModel.setRowCount(0);
    }

    public void addCourse(Object[] course) {
        tableModel.addRow(course);
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}
