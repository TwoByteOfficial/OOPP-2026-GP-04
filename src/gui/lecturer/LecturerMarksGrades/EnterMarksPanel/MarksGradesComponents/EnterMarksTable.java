package gui.lecturer.LecturerMarksGrades.EnterMarksPanel.EnterMarksPanelComponents;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EnterMarksTable extends JPanel {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private static final Color BORDER_GREEN =
            new Color(200, 230, 201);

    private final DefaultTableModel tableModel;
    private final JTable marksTable;

    public EnterMarksTable() {

        setLayout(new BorderLayout());
        setOpaque(false);

        String[] columns = {
                "Student ID",
                "Registration No.",
                "Student Name",
                "Mark",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {

                // Only the Mark column is editable.
                return column == 3;
            }

            @Override
            public Class<?> getColumnClass(int column) {

                if (column == 0) {
                    return Long.class;
                }

                return Object.class;
            }
        };

        marksTable = new JTable(tableModel);

        marksTable.setRowHeight(40);

        marksTable.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        marksTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        marksTable.setSelectionBackground(PALE_GREEN);
        marksTable.setSelectionForeground(DEEP_GREEN);
        marksTable.setGridColor(BORDER_GREEN);
        marksTable.setFillsViewportHeight(true);
        marksTable.setAutoCreateRowSorter(true);

        marksTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        marksTable.getTableHeader().setBackground(PALE_GREEN);
        marksTable.getTableHeader().setForeground(DEEP_GREEN);
        marksTable.getTableHeader().setReorderingAllowed(false);

        // Hide the internal Student ID column.
        marksTable.removeColumn(
                marksTable.getColumnModel().getColumn(0)
        );

        JScrollPane scrollPane = new JScrollPane(marksTable);

        scrollPane.setBorder(
                new LineBorder(BORDER_GREEN, 1)
        );

        add(scrollPane, BorderLayout.CENTER);
    }

    public void clearStudents() {
        tableModel.setRowCount(0);
    }

    public void addStudent(Object[] student) {
        tableModel.addRow(student);
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTable getMarksTable() {
        return marksTable;
    }
}