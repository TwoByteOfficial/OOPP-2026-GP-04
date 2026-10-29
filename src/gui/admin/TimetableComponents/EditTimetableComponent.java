package gui.admin.TimetableComponents;

import dao.admin.AdminCourseDAO;
import dao.admin.AdminTimetableDAO;
import dao.admin.AdminUserDAO;
import gui.admin.AdminTheme;
import model.TimetableEntry;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class EditTimetableComponent extends JButton {

    public EditTimetableComponent(Component parentComponent, AdminTimetableDAO timetableDAO, AdminCourseDAO courseDAO, AdminUserDAO userDAO, Supplier<TimetableEntry> selectedSupplier, Runnable onSuccess) {
        super("Edit Entry");
        setFont(AdminTheme.FONT_TINY);
        setBackground(AdminTheme.PASTEL_GREEN);
        setForeground(AdminTheme.DEEP_GREEN);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> {
            TimetableEntry selected = selectedSupplier.get();
            if (selected == null) {
                JOptionPane.showMessageDialog(parentComponent, "Please select a timetable slot from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(parentComponent);
            ScheduleSlotComponent.ScheduleSlotModal form = new ScheduleSlotComponent.ScheduleSlotModal(owner, timetableDAO, courseDAO, userDAO, selected, onSuccess);
            form.setVisible(true);
        });
    }
}
