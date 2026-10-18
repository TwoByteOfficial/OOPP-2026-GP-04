
package gui.technical.dashboard.bottom;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DepartmentTimetablePanel extends JPanel {

    private final Color NAVY = new Color(24, 58, 99);
    private final Color MUTED = new Color(105, 118, 135);

    public DepartmentTimetablePanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 230, 238)),
                new EmptyBorder(18, 18, 18, 18)));

        JLabel title = new JLabel("Today's Department Timetable");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(NAVY);

        String[] columns = {
                "Time", "Course", "Component", "Session"
        };

        Object[][] data = {};

        JTable table = new JTable(data, columns);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 12));
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JLabel note = new JLabel(
                "Today's timetable data will appear here.");
        note.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        note.setForeground(MUTED);

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);
        content.add(note, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);

        add(title, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }
}
