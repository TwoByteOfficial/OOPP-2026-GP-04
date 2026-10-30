package gui.admin.TimetableComponents;

import dao.admin.AdminTimetableDAO;
import gui.admin.AdminTheme;
import model.TimetableEntry;

import javax.swing.*;
import java.awt.*;
import java.sql.Time;

public class ScheduleSlotComponent extends JButton {

    public ScheduleSlotComponent(Frame owner, AdminTimetableDAO dao, Runnable onSuccess) {
        super("+ Schedule Slot");
        setFont(AdminTheme.FONT_BODY_BOLD);
        setBackground(AdminTheme.FOREST_GREEN);
        setForeground(AdminTheme.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> {
            ScheduleSlotModal dialog = new ScheduleSlotModal(owner, dao, onSuccess);
            dialog.setVisible(true);
        });
    }

    public static class ScheduleSlotModal extends JDialog {
        private JComboBox<String> cmbDept;
        private JTextField txtBatch;
        private JComboBox<String> cmbComponent;
        private JComboBox<String> cmbDay;
        private JTextField txtStart;
        private JTextField txtEnd;
        private JTextField txtVenue;

        public ScheduleSlotModal(Frame owner, AdminTimetableDAO dao, Runnable onSuccess) {
            super(owner, "Schedule Timetable Slot", true);
            setSize(680, 560);
            setLocationRelativeTo(owner);
            setLayout(null);
            getContentPane().setBackground(AdminTheme.WHITE);

            JLabel lblHeader = new JLabel("Schedule New Slot");
            lblHeader.setFont(AdminTheme.FONT_H2); lblHeader.setForeground(AdminTheme.DEEP_GREEN);
            lblHeader.setBounds(24, 20, 400, 32); add(lblHeader);

            JLabel l1 = new JLabel("Department:"); l1.setFont(AdminTheme.FONT_TINY); l1.setBounds(24, 70, 120, 28); add(l1);
            cmbDept = new JComboBox<>(new String[]{ "1 - ICT", "2 - Engineering Tech", "3 - Bio Systems Tech" });
            cmbDept.setFont(AdminTheme.FONT_SMALL); cmbDept.setBounds(150, 70, 480, 32); add(cmbDept);

            JLabel l2 = new JLabel("Batch Year:"); l2.setFont(AdminTheme.FONT_TINY); l2.setBounds(24, 120, 120, 28); add(l2);
            txtBatch = new JTextField("2024"); txtBatch.setFont(AdminTheme.FONT_SMALL); txtBatch.setBounds(150, 120, 480, 32); add(txtBatch);

            JLabel l3 = new JLabel("Component:"); l3.setFont(AdminTheme.FONT_TINY); l3.setBounds(24, 170, 120, 28); add(l3);
            cmbComponent = new JComboBox<>(new String[]{ "Theory", "Practical", "Tutorial", "Workshop" });
            cmbComponent.setFont(AdminTheme.FONT_SMALL); cmbComponent.setBounds(150, 170, 480, 32); add(cmbComponent);

            JLabel l4 = new JLabel("Day of Week:"); l4.setFont(AdminTheme.FONT_TINY); l4.setBounds(24, 220, 120, 28); add(l4);
            cmbDay = new JComboBox<>(new String[]{ "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" });
            cmbDay.setFont(AdminTheme.FONT_SMALL); cmbDay.setBounds(150, 220, 480, 32); add(cmbDay);

            JLabel l5 = new JLabel("Start Time:"); l5.setFont(AdminTheme.FONT_TINY); l5.setBounds(24, 270, 120, 28); add(l5);
            txtStart = new JTextField("08:30:00"); txtStart.setFont(AdminTheme.FONT_SMALL); txtStart.setBounds(150, 270, 480, 32); add(txtStart);

            JLabel l6 = new JLabel("End Time:"); l6.setFont(AdminTheme.FONT_TINY); l6.setBounds(24, 320, 120, 28); add(l6);
            txtEnd = new JTextField("10:30:00"); txtEnd.setFont(AdminTheme.FONT_SMALL); txtEnd.setBounds(150, 320, 480, 32); add(txtEnd);

            JLabel l7 = new JLabel("Venue / Hall:"); l7.setFont(AdminTheme.FONT_TINY); l7.setBounds(24, 370, 120, 28); add(l7);
            txtVenue = new JTextField("Auditorium"); txtVenue.setFont(AdminTheme.FONT_SMALL); txtVenue.setBounds(150, 370, 480, 32); add(txtVenue);

            JButton btnSave = new JButton("Schedule");
            btnSave.setFont(AdminTheme.FONT_BODY_BOLD); btnSave.setBackground(AdminTheme.FOREST_GREEN); btnSave.setForeground(AdminTheme.WHITE);
            btnSave.setBounds(370, 450, 130, 40);
            btnSave.addActionListener(ev -> {
                try {
                    TimetableEntry t = new TimetableEntry();
                    t.setDepartmentId(cmbDept.getSelectedIndex() + 1);
                    t.setBatchYear(Integer.parseInt(txtBatch.getText().trim()));
                    t.setComponent((String) cmbComponent.getSelectedItem());
                    t.setDayOfWeek((String) cmbDay.getSelectedItem());
                    t.setStartTime(Time.valueOf(txtStart.getText().trim()));
                    t.setEndTime(Time.valueOf(txtEnd.getText().trim()));
                    t.setVenue(txtVenue.getText().trim());

                    dao.createTimetableEntry(t);
                    JOptionPane.showMessageDialog(this, "Slot scheduled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    if (onSuccess != null) onSuccess.run();
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            add(btnSave);

            JButton btnCancel = new JButton("Cancel");
            btnCancel.setFont(AdminTheme.FONT_BODY_BOLD); btnCancel.setBackground(AdminTheme.PASTEL_GREEN); btnCancel.setForeground(AdminTheme.DEEP_GREEN);
            btnCancel.setBounds(510, 450, 120, 40);
            btnCancel.addActionListener(ev -> dispose());
            add(btnCancel);
        }
    }
}
