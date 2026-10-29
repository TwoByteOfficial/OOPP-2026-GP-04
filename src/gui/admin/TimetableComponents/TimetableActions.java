package gui.admin.TimetableComponents;

import dao.admin.AdminCourseDAO;
import dao.admin.AdminTimetableDAO;
import dao.admin.AdminUserDAO;
import gui.admin.AdminTheme;
import model.TimetableEntry;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class TimetableActions extends JPanel {

    private ScheduleSlotComponent btnSchedule;
    private ViewTimetableComponent btnView;
    private EditTimetableComponent btnEdit;
    private DeleteTimetableComponent btnDelete;

    public TimetableActions(Component parentComponent, AdminTimetableDAO timetableDAO, AdminCourseDAO courseDAO, AdminUserDAO userDAO, Supplier<TimetableEntry> selectedSupplier, Runnable onSuccess) {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        setBackground(AdminTheme.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));

        btnSchedule = new ScheduleSlotComponent(parentComponent, timetableDAO, courseDAO, userDAO, onSuccess);
        btnView = new ViewTimetableComponent(parentComponent, selectedSupplier);
        btnEdit = new EditTimetableComponent(parentComponent, timetableDAO, courseDAO, userDAO, selectedSupplier, onSuccess);
        btnDelete = new DeleteTimetableComponent(parentComponent, timetableDAO, selectedSupplier, onSuccess);

        add(btnSchedule);
        add(btnView);
        add(btnEdit);
        add(btnDelete);
    }
}
