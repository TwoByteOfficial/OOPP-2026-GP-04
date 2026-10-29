package gui.admin.TimetableComponents;

import model.TimetableEntry;
import java.awt.Component;
import java.util.function.Supplier;

public class ViewTimetableButton extends ViewTimetableComponent {
    public ViewTimetableButton(Component parentComponent, Supplier<TimetableEntry> selectedSupplier) {
        super(parentComponent, selectedSupplier);
    }
}
