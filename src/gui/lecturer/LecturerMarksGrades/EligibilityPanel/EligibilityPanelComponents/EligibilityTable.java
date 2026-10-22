
package gui.lecturer.LecturerMarksGrades.EligibilityPanel.EligibilityPanelComponents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class EligibilityTable extends JPanel {

    private final DefaultTableModel model;
    private final JTable table;

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private static final Color BORDER_GREEN =
            new Color(200, 230, 201);

    public EligibilityTable() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(BORDER_GREEN));

        String[] columns = {
                "Registration No.",
                "Student Name",
                "Attendance %",
                "CA Mark %",
                "Attendance Requirement",
                "CA Requirement",
                "Final Exam Eligibility"
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
        table.setSelectionBackground(PALE_GREEN);
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(BORDER_GREEN);
        table.setShowGrid(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setBackground(PALE_GREEN);
        header.setForeground(DEEP_GREEN);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(100, 42));

        int[] widths = {110, 160, 90, 85, 160, 140, 155};

        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i)
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
