package gui.admin.TimetableComponents;

import dao.admin.AdminCourseDAO;
import dao.admin.AdminUserDAO;
import gui.admin.AdminTheme;
import model.TimetableEntry;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TimetableView extends JPanel {

    private TimetableTable table;

    public TimetableView() {
        this(new TimetableTable());
    }

    public TimetableView(TimetableTable table) {
        this.table = table != null ? table : new TimetableTable();
        setLayout(new BorderLayout());
        setBackground(AdminTheme.WHITE);

        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.getViewport().setBackground(AdminTheme.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setTimetables(List<TimetableEntry> entries) {
        table.setTimetables(entries);
    }

    public void setTimetables(List<TimetableEntry> entries, AdminCourseDAO courseDAO, AdminUserDAO userDAO) {
        table.setTimetables(entries, courseDAO, userDAO);
    }

    public TimetableEntry getSelectedTimetable() {
        return table.getSelectedTimetable();
    }

    public TimetableTable getTable() {
        return table;
    }
}
