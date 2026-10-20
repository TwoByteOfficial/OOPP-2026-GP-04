package gui.admin.TimetableComponents;

import model.TimetableEntry;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TimetableView extends JPanel {

    private TimetableTable table;

    public TimetableView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        table = new TimetableTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setTimetables(List<TimetableEntry> entries) { table.setTimetables(entries); }
    public TimetableEntry getSelectedTimetable() { return table.getSelectedTimetable(); }
    public TimetableTable getTable() { return table; }
}
