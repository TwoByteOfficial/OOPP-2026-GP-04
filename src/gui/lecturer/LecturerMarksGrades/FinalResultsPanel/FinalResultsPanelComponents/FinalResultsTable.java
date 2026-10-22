
package gui.lecturer.LecturerMarksGrades.FinalResultsPanel.FinalResultsPanelComponents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class FinalResultsTable extends JPanel {

    private final DefaultTableModel model;
    private final JTable table;

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private static final Color BORDER_GREEN =
            new Color(200, 230, 201);

    public FinalResultsTable() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(BORDER_GREEN));

        String[] columns = {
                "Registration No.",
                "Student Name",
                "Credits",
                "CA Mark",
                "Final Exam",
                "Final Mark",
                "Attendance %",
                "Eligibility",
                "Grade",
                "Grade Point",
                "SGPA",
                "Semester Credits",
                "CGPA"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setGridColor(BORDER_GREEN);
        table.setSelectionBackground(PALE_GREEN);
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setBackground(PALE_GREEN);
        header.setForeground(DEEP_GREEN);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(100, 42));

        int[] widths = {
                105, 145, 60, 75, 85, 80, 90,
                100, 60, 80, 65, 100, 65
        };

        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void setRows(List<Object[]> rows) {
        model.setRowCount(0);

        for (Object[] row : rows) {
            model.addRow(row);
        }
    }

    public void clearRows() {
        model.setRowCount(0);
    }

    public int getRowCount() {
        return model.getRowCount();
    }
}
