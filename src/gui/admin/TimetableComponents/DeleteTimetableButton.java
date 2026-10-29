package gui.admin.TimetableComponents;

import dao.admin.AdminTimetableDAO;
import model.TimetableEntry;
import java.awt.Component;
import java.util.function.Supplier;

public class DeleteTimetableButton extends DeleteTimetableComponent {
    public DeleteTimetableButton(Component parentComponent, AdminTimetableDAO timetableDAO, Supplier<TimetableEntry> selectedSupplier, Runnable onSuccess) {
        super(parentComponent, timetableDAO, selectedSupplier, onSuccess);
    }
}
