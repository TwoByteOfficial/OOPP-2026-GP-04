package gui.admin.TimetableComponents;

import dao.admin.AdminCourseDAO;
import dao.admin.AdminTimetableDAO;
import dao.admin.AdminUserDAO;
import java.awt.Component;

public class AddTimetableButton extends ScheduleSlotComponent {
    public AddTimetableButton(Component parentComponent, AdminTimetableDAO timetableDAO, AdminCourseDAO courseDAO, AdminUserDAO userDAO, Runnable onSuccess) {
        super(parentComponent, timetableDAO, courseDAO, userDAO, onSuccess);
    }
}
