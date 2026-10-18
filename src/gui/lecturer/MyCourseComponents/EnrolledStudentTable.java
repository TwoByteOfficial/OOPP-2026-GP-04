
package gui.lecturer.MyCourseComponents;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EnrolledStudentTable extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color BORDER_GREEN = new Color(200, 230, 201);

    private final DefaultTableModel tableModel;
    private final JTable table;

    public EnrolledStudentTable() {
        setLayout(new BorderLayout());
        setOpaque(false);

        String[] columns = {
                "Reg. No.",
                "Name",
                "Student Type",
                "Attendance",
                "CA Eligibility"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 245, 233));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(BORDER_GREEN);
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);

        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        table.getTableHeader().setForeground(DEEP_GREEN);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(BORDER_GREEN, 1));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void clearStudents() {
        tableModel.setRowCount(0);
    }

    public void addStudent(Object[] student) {
        tableModel.addRow(student);
    }

    public int getStudentCount() {
        return tableModel.getRowCount();
    }

    public JTable getTable() {
        return table;
    }
}
