
package gui.lecturer.LecturerMarksGrades.EnterMarksPanel.EnterMarksPanelComponents;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class MarksStudentsTable extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color PALE_GREEN = new Color(232, 245, 233);
    private static final Color BORDER_GREEN = new Color(200, 230, 201);

    private final DefaultTableModel tableModel;
    private final JTable studentsTable;

    public MarksStudentsTable() {
        setLayout(new BorderLayout());
        setOpaque(false);

        String[] columns = {
                "Student ID",
                "Registration No",
                "Student Name",
                "Mark",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        studentsTable = new JTable(tableModel);
        studentsTable.setRowHeight(42);
        studentsTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        studentsTable.setFillsViewportHeight(true);
        studentsTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        studentsTable.setSelectionBackground(PALE_GREEN);
        studentsTable.setSelectionForeground(DEEP_GREEN);
        studentsTable.setGridColor(BORDER_GREEN);
        studentsTable.setShowVerticalLines(false);
        studentsTable.setAutoCreateRowSorter(true);

        studentsTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        studentsTable.getTableHeader().setBackground(PALE_GREEN);
        studentsTable.getTableHeader().setForeground(DEEP_GREEN);
        studentsTable.getTableHeader().setPreferredSize(
                new Dimension(0, 40)
        );
        studentsTable.getTableHeader().setReorderingAllowed(false);

        studentsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        studentsTable.getColumnModel().getColumn(4).setPreferredWidth(110);

        // Keep Student ID in the model, but hide it from the screen.
        studentsTable.removeColumn(
                studentsTable.getColumnModel().getColumn(0)
        );

        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(new LineBorder(BORDER_GREEN, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setStudents(List<Object[]> students) {
        tableModel.setRowCount(0);

        for (Object[] student : students) {
            Object mark = student[3];

            tableModel.addRow(new Object[]{
                    student[0],
                    student[1],
                    student[2],
                    mark == null ? "" : mark,
                    student[4] == null ? "" : student[4]
            });
        }
    }

    public int getStudentCount() {
        return studentsTable.getRowCount();
    }

    public long getStudentIdAt(int viewRow) {
        int modelRow = studentsTable.convertRowIndexToModel(viewRow);
        return ((Number) tableModel.getValueAt(modelRow, 0)).longValue();
    }

    public Object getMarkAt(int viewRow) {
        int modelRow = studentsTable.convertRowIndexToModel(viewRow);
        return tableModel.getValueAt(modelRow, 3);
    }

    public void setStatusAt(int viewRow, String status) {
        int modelRow = studentsTable.convertRowIndexToModel(viewRow);
        tableModel.setValueAt(status, modelRow, 4);
    }

    public void stopEditing() {
        if (studentsTable.isEditing()) {
            studentsTable.getCellEditor().stopCellEditing();
        }
    }

    public BigDecimal getNumericMarkAt(int viewRow) {
        Object value = getMarkAt(viewRow);

        if (value == null || value.toString().trim().isEmpty()) {
            return null;
        }

        return new BigDecimal(value.toString().trim());
    }
}
