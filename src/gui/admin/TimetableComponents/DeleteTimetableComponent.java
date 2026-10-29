package gui.admin.TimetableComponents;

import dao.admin.AdminTimetableDAO;
import gui.admin.AdminTheme;
import model.TimetableEntry;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class DeleteTimetableComponent extends JButton {

    public DeleteTimetableComponent(Component parentComponent, AdminTimetableDAO timetableDAO, Supplier<TimetableEntry> selectedSupplier, Runnable onSuccess) {
        super("Delete Entry");
        setFont(AdminTheme.FONT_TINY);
        setBackground(AdminTheme.DANGER);
        setForeground(AdminTheme.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> {
            TimetableEntry selected = selectedSupplier.get();
            if (selected == null) {
                JOptionPane.showMessageDialog(parentComponent, "Please select a timetable slot from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int choice = JOptionPane.showConfirmDialog(parentComponent,
                    "Are you sure you want to delete Slot TT-" + selected.getTimetableId() + "?",
                    "Delete Timetable Slot", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                try {
                    timetableDAO.deleteTimetableEntry(selected.getTimetableId());
                    JOptionPane.showMessageDialog(parentComponent, "Slot deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    if (onSuccess != null) onSuccess.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentComponent, "Error deleting slot: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
