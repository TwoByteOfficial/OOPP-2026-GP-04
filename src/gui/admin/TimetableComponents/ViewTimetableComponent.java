package gui.admin.TimetableComponents;

import gui.admin.AdminTheme;
import model.TimetableEntry;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class ViewTimetableComponent extends JButton {

    public ViewTimetableComponent(Component parentComponent, Supplier<TimetableEntry> selectedSupplier) {
        super("View Entry");
        setFont(AdminTheme.FONT_TINY);
        setBackground(AdminTheme.PASTEL_GREEN);
        setForeground(AdminTheme.DEEP_GREEN);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> {
            TimetableEntry selected = selectedSupplier.get();
            if (selected == null) {
                JOptionPane.showMessageDialog(parentComponent, "Please select a timetable slot from the table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(parentComponent);
            ViewTimetableModal details = new ViewTimetableModal(owner, selected);
            details.setVisible(true);
        });
    }

    public static class ViewTimetableModal extends JDialog {
        public ViewTimetableModal(Frame parent, TimetableEntry entry) {
            super(parent, "Scheduled Timetable Slot Details", true);
            setSize(620, 500);
            setLocationRelativeTo(parent);
            setResizable(false);
            getContentPane().setBackground(AdminTheme.WHITE);
            setLayout(new BorderLayout());

            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
            headerPanel.setBackground(AdminTheme.DEEP_GREEN);
            JLabel lblHeader = new JLabel("Timetable Entry: TT-" + entry.getTimetableId());
            lblHeader.setFont(AdminTheme.FONT_H3);
            lblHeader.setForeground(AdminTheme.WHITE);
            headerPanel.add(lblHeader);
            add(headerPanel, BorderLayout.NORTH);

            JPanel pnlContent = new JPanel(new GridLayout(9, 2, 14, 12));
            pnlContent.setBackground(AdminTheme.WHITE);
            pnlContent.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

            addDetailRow(pnlContent, "Timetable Slot ID:", "TT-" + entry.getTimetableId());
            addDetailRow(pnlContent, "Course ID:", "CID-" + entry.getCourseId());
            addDetailRow(pnlContent, "Day & Time:", entry.getDayOfWeek() + " (" + entry.getStartTime() + " - " + entry.getEndTime() + ")");
            addDetailRow(pnlContent, "Component:", entry.getComponent() != null ? entry.getComponent() : "Lecture");
            addDetailRow(pnlContent, "Venue / Hall:", entry.getVenue() != null ? entry.getVenue() : "Unassigned");
            addDetailRow(pnlContent, "Department:", "Department " + entry.getDepartmentId());
            addDetailRow(pnlContent, "Batch Year:", String.valueOf(entry.getBatchYear()));
            addDetailRow(pnlContent, "Lecturer ID:", entry.getLecturerId() != null ? "L-" + entry.getLecturerId() : "Unassigned");
            addDetailRow(pnlContent, "Academic Year / Sem:", entry.getAcademicYear() + " (" + entry.getSemester() + ")");

            add(pnlContent, BorderLayout.CENTER);

            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
            footerPanel.setBackground(AdminTheme.WHITE);
            JButton btnClose = new JButton("Close");
            btnClose.setFont(AdminTheme.FONT_TINY);
            btnClose.setBackground(AdminTheme.FOREST_GREEN);
            btnClose.setForeground(AdminTheme.WHITE);
            btnClose.setFocusPainted(false);
            btnClose.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
            btnClose.addActionListener(e -> dispose());
            footerPanel.add(btnClose);
            add(footerPanel, BorderLayout.SOUTH);
        }

        private void addDetailRow(JPanel panel, String label, String value) {
            JLabel l = new JLabel(label);
            l.setFont(AdminTheme.FONT_TINY);
            l.setForeground(AdminTheme.BLACK);

            JLabel v = new JLabel(value);
            v.setFont(AdminTheme.FONT_SMALL);
            v.setForeground(AdminTheme.DEEP_GREEN);

            panel.add(l);
            panel.add(v);
        }
    }
}
