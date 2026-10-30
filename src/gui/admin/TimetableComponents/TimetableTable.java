package gui.admin.TimetableComponents;

import gui.admin.AdminTheme;
import model.TimetableEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class TimetableTable extends JTable {

    private DefaultTableModel model;
    private List<TimetableEntry> entryList = new ArrayList<>();

    public TimetableTable() {
        String[] columns = { "Slot ID", "Day", "Time Window", "Component", "Venue", "Department ID", "Batch Year" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        setModel(model);

        setFont(AdminTheme.FONT_SMALL);
        setRowHeight(44);
        setBackground(AdminTheme.WHITE);
        setSelectionBackground(AdminTheme.PASTEL_GREEN);
        setSelectionForeground(AdminTheme.DEEP_GREEN);
        setShowGrid(true);
        setGridColor(new Color(0xE0, 0xE0, 0xE0));
    }

    public void setTimetables(List<TimetableEntry> entries) {
        this.entryList = entries;
        model.setRowCount(0);
        for (TimetableEntry t : entries) {
            model.addRow(new Object[]{
                    "TT-" + t.getTimetableId(),
                    t.getDayOfWeek(),
                    t.getStartTime() + " - " + t.getEndTime(),
                    t.getComponent(),
                    t.getVenue(),
                    "Dept " + t.getDepartmentId(),
                    t.getBatchYear()
            });
        }
    }

    public TimetableEntry getSelectedTimetable() {
        int row = getSelectedRow();
        if (row >= 0 && row < entryList.size()) return entryList.get(row);
        return null;
    }
}
