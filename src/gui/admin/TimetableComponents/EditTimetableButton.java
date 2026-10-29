package gui.admin.TimetableComponents;

import dao.admin.AdminCourseDAO;
import dao.admin.AdminTimetableDAO;
import dao.admin.AdminUserDAO;
import model.TimetableEntry;
import java.awt.Component;
import java.util.function.Supplier;

public class EditTimetableButton extends EditTimetableComponent {
    public EditTimetableButton(Component parentComponent, AdminTimetableDAO timetableDAO, AdminCourseDAO courseDAO, AdminUserDAO userDAO, Supplier<TimetableEntry> selectedSupplier, Runnable onSuccess) {
        super(parentComponent, timetableDAO, courseDAO, userDAO, selectedSupplier, onSuccess);
    }
}
