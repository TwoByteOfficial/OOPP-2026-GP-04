package gui.admin.TimetableComponents;

import dao.admin.AdminTimetableDAO;
import gui.admin.AdminTheme;
import model.TimetableEntry;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class DeleteTimetableComponent extends JButton {

    public DeleteTimetableComponent(Component parent, AdminTimetableDAO dao, Supplier<TimetableEntry> entrySupplier, Runnable onSuccess) {
        super("Delete Entry");
        setFont(AdminTheme.FONT_BODY_BOLD);
        setBackground(AdminTheme.DANGER);
        setForeground(AdminTheme.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> {
            TimetableEntry selected = entrySupplier.get();
            if (selected == null) {
                JOptionPane.showMessageDialog(parent, "Please select an entry to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int choice = JOptionPane.showConfirmDialog(parent,
                    "Are you sure you want to delete slot TT-" + selected.getTimetableId() + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                try {
                    dao.deleteTimetableEntry(selected.getTimetableId());
                    JOptionPane.showMessageDialog(parent, "Slot deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    if (onSuccess != null) onSuccess.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parent, "Error deleting: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
