
package gui.lecturer.LecturerMarksGrades.CASummaryPanel.CASummaryPanelComponents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class CASummaryTable extends JTable {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private final DefaultTableModel model;

    public CASummaryTable() {

        String[] columns = {
                "Registration No.",
                "Student Name",
                "CA Mark",
                "CA Weight",
                "CA Percentage",
                "CA Eligibility"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        setModel(model);
        setRowHeight(40);
        setFont(new Font("SansSerif", Font.PLAIN, 13));
        setSelectionBackground(PALE_GREEN);
        setSelectionForeground(Color.BLACK);
        setGridColor(new Color(230, 230, 230));
        setShowVerticalLines(false);
        setAutoCreateRowSorter(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(PALE_GREEN);
        header.setForeground(DEEP_GREEN);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
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
}
