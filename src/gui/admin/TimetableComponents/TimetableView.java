package gui.admin.TimetableComponents;

import gui.admin.AdminTheme;
import model.TimetableEntry;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TimetableView extends JPanel {
    private TimetableTable table;

    public TimetableView(TimetableTable table) {
        this.table = table != null ? table : new TimetableTable();
        setLayout(null);
        setBackground(AdminTheme.WHITE);

        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.setBounds(0, 0, 1620, 680);
        scrollPane.getViewport().setBackground(AdminTheme.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1));
        add(scrollPane);
    }

    public void setTimetables(List<TimetableEntry> entries) { table.setTimetables(entries); }
    public TimetableEntry getSelectedTimetable() { return table.getSelectedTimetable(); }
}
