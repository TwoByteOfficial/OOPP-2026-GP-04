package gui.admin.TimetableComponents;

import gui.admin.AdminTheme;
import model.TimetableEntry;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class ViewTimetableComponent extends JButton {

    public ViewTimetableComponent(Frame owner, Supplier<TimetableEntry> entrySupplier) {
        super("View Entry");
        setFont(AdminTheme.FONT_BODY_BOLD);
        setBackground(AdminTheme.PASTEL_GREEN);
        setForeground(AdminTheme.DEEP_GREEN);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> {
            TimetableEntry selected = entrySupplier.get();
            if (selected == null) {
                JOptionPane.showMessageDialog(owner, "Please select a timetable entry to view.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ViewTimetableModal dialog = new ViewTimetableModal(owner, selected);
            dialog.setVisible(true);
        });
    }

    public static class ViewTimetableModal extends JDialog {
        public ViewTimetableModal(Frame owner, TimetableEntry entry) {
            super(owner, "Timetable Entry Details", true);
            setSize(580, 420);
            setLocationRelativeTo(owner);
            setLayout(null);
            getContentPane().setBackground(AdminTheme.WHITE);

            JLabel lblTitle = new JLabel("Slot ID: TT-" + entry.getTimetableId());
            lblTitle.setFont(AdminTheme.FONT_H3); lblTitle.setForeground(AdminTheme.DEEP_GREEN);
            lblTitle.setBounds(24, 20, 500, 30); add(lblTitle);

            JLabel l1 = new JLabel("Day & Time: " + entry.getDayOfWeek() + " (" + entry.getStartTime() + " - " + entry.getEndTime() + ")");
            l1.setFont(AdminTheme.FONT_SMALL); l1.setBounds(24, 70, 500, 26); add(l1);

            JLabel l2 = new JLabel("Component: " + entry.getComponent());
            l2.setFont(AdminTheme.FONT_SMALL); l2.setBounds(24, 110, 500, 26); add(l2);

            JLabel l3 = new JLabel("Venue / Hall: " + entry.getVenue());
            l3.setFont(AdminTheme.FONT_SMALL); l3.setBounds(24, 150, 500, 26); add(l3);

            JLabel l4 = new JLabel("Department ID: " + entry.getDepartmentId() + " | Batch: " + entry.getBatchYear());
            l4.setFont(AdminTheme.FONT_SMALL); l4.setBounds(24, 190, 500, 26); add(l4);

            JButton btnClose = new JButton("Close");
            btnClose.setFont(AdminTheme.FONT_BODY_BOLD); btnClose.setBackground(AdminTheme.FOREST_GREEN); btnClose.setForeground(AdminTheme.WHITE);
            btnClose.setBounds(420, 320, 120, 38);
            btnClose.addActionListener(ev -> dispose());
            add(btnClose);
        }
    }
}
