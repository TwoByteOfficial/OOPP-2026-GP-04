
package gui.technical.dashboard.bottom;

import gui.theme.UniMisTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DepartmentTimetablePanel extends JPanel {

    public DepartmentTimetablePanel() {

        setLayout(new BorderLayout(0, 14));
        setBackground(UniMisTheme.WHITE);
        setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel heading = new JLabel("Department Timetable");
        heading.setFont(UniMisTheme.FONT_H3);
        heading.setForeground(UniMisTheme.TEXT);

        add(heading, BorderLayout.NORTH);

        String[] columns = {
                "Time", "Course", "Component", "Venue"
        };

        DefaultTableModel model = new DefaultTableModel(
                columns, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(UniMisTheme.FONT_TINY);
        table.setRowHeight(30);
        table.setForeground(UniMisTheme.TEXT);
        table.setBackground(UniMisTheme.WHITE);
        table.setGridColor(UniMisTheme.BORDER);
        table.setSelectionBackground(UniMisTheme.PASTEL_GREEN);
        table.setSelectionForeground(UniMisTheme.TEXT);

        table.getTableHeader().setFont(UniMisTheme.FONT_BODY_BOLD);
        table.getTableHeader().setBackground(UniMisTheme.MINT);
        table.getTableHeader().setForeground(UniMisTheme.TEXT);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(UniMisTheme.BORDER));

        add(scrollPane, BorderLayout.CENTER);
    }
}
