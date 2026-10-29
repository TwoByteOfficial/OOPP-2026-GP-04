package gui.admin.TimetableComponents;

import dao.admin.AdminCourseDAO;
import dao.admin.AdminTimetableDAO;
import dao.admin.AdminUserDAO;
import gui.admin.AdminTheme;
import model.Course;
import model.Lecturer;
import model.TimetableEntry;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.Time;
import java.util.List;

public class ScheduleSlotComponent extends JButton {

    public ScheduleSlotComponent(Component parentComponent, AdminTimetableDAO timetableDAO, AdminCourseDAO courseDAO, AdminUserDAO userDAO, Runnable onSuccess) {
        super("+ Schedule Slot");
        setFont(AdminTheme.FONT_TINY);
        setBackground(AdminTheme.FOREST_GREEN);
        setForeground(AdminTheme.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(parentComponent);
            ScheduleSlotModal modal = new ScheduleSlotModal(owner, timetableDAO, courseDAO, userDAO, null, onSuccess);
            modal.setVisible(true);
        });
    }

    public static class ScheduleSlotModal extends JDialog {
        private AdminTimetableDAO timetableDAO;
        private AdminCourseDAO courseDAO;
        private AdminUserDAO userDAO;
        private TimetableEntry editEntry;
        private Runnable onSuccess;

        private JComboBox<String> cmbDepartment;
        private JComboBox<CourseComboItem> cmbCourse;
        private JComboBox<LecturerComboItem> cmbLecturer;
        private JTextField txtBatchYear;
        private JComboBox<String> cmbComponent;
        private JComboBox<String> cmbDay;
        private JTextField txtStartTime;
        private JTextField txtEndTime;
        private JTextField txtVenue;
        private JTextField txtAcademicYear;
        private JComboBox<String> cmbSemester;

        public static class CourseComboItem {
            private int courseId;
            private String displayText;
            public CourseComboItem(int courseId, String displayText) {
                this.courseId = courseId;
                this.displayText = displayText;
            }
            public int getCourseId() { return courseId; }
            @Override public String toString() { return displayText; }
        }

        public static class LecturerComboItem {
            private long userId;
            private String displayText;
            public LecturerComboItem(long userId, String displayText) {
                this.userId = userId;
                this.displayText = displayText;
            }
            public long getUserId() { return userId; }
            @Override public String toString() { return displayText; }
        }

        public ScheduleSlotModal(Frame parent, AdminTimetableDAO timetableDAO, AdminCourseDAO courseDAO, AdminUserDAO userDAO, TimetableEntry entryToEdit, Runnable onSuccess) {
            super(parent, (entryToEdit == null ? "Schedule Timetable Slot" : "Edit Timetable Entry"), true);
            this.timetableDAO = timetableDAO;
            this.courseDAO = courseDAO != null ? courseDAO : new AdminCourseDAO();
            this.userDAO = userDAO != null ? userDAO : new AdminUserDAO();
            this.editEntry = entryToEdit;
            this.onSuccess = onSuccess;

            setSize(740, 680);
            setLocationRelativeTo(parent);
            setResizable(false);
            getContentPane().setBackground(AdminTheme.MINT);
            setLayout(new BorderLayout());

            initUI();
            if (editEntry != null) {
                populateForm();
            }
        }

        private void initUI() {
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 16));
            headerPanel.setBackground(AdminTheme.DEEP_GREEN);

            JLabel lblHeader = new JLabel(editEntry == null ? "Schedule New Lecture / Practical Slot" : "Modify Scheduled Slot Details");
            lblHeader.setFont(AdminTheme.FONT_H3);
            lblHeader.setForeground(AdminTheme.WHITE);
            headerPanel.add(lblHeader);
            add(headerPanel, BorderLayout.NORTH);

            JPanel pnlForm = new JPanel(new GridLayout(11, 2, 14, 10));
            pnlForm.setBackground(AdminTheme.WHITE);
            pnlForm.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                    BorderFactory.createEmptyBorder(16, 24, 16, 24)
            ));

            pnlForm.add(createLabel("Department:"));
            cmbDepartment = new JComboBox<>(new String[]{ "1 - ICT", "2 - Engineering Technology", "3 - Bio Systems Technology" });
            styleCombo(cmbDepartment);
            pnlForm.add(cmbDepartment);

            pnlForm.add(createLabel("Select Course:"));
            cmbCourse = new JComboBox<>();
            styleCombo(cmbCourse);
            populateCourses();
            pnlForm.add(cmbCourse);

            pnlForm.add(createLabel("Select Lecturer:"));
            cmbLecturer = new JComboBox<>();
            styleCombo(cmbLecturer);
            populateLecturers();
            pnlForm.add(cmbLecturer);

            pnlForm.add(createLabel("Batch Year:"));
            txtBatchYear = createTextField("2024");
            pnlForm.add(txtBatchYear);

            pnlForm.add(createLabel("Component:"));
            cmbComponent = new JComboBox<>(new String[]{ "Theory", "Practical", "Tutorial", "Workshop" });
            styleCombo(cmbComponent);
            pnlForm.add(cmbComponent);

            pnlForm.add(createLabel("Day of Week:"));
            cmbDay = new JComboBox<>(new String[]{ "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" });
            styleCombo(cmbDay);
            pnlForm.add(cmbDay);

            pnlForm.add(createLabel("Start Time (HH:MM:SS):"));
            txtStartTime = createTextField("08:30:00");
            pnlForm.add(txtStartTime);

            pnlForm.add(createLabel("End Time (HH:MM:SS):"));
            txtEndTime = createTextField("10:30:00");
            pnlForm.add(txtEndTime);

            pnlForm.add(createLabel("Venue / Hall:"));
            txtVenue = createTextField("Auditorium");
            pnlForm.add(txtVenue);

            pnlForm.add(createLabel("Academic Year:"));
            txtAcademicYear = createTextField("2024/2025");
            pnlForm.add(txtAcademicYear);

            pnlForm.add(createLabel("Semester:"));
            cmbSemester = new JComboBox<>(new String[]{ "Semester 1", "Semester 2" });
            styleCombo(cmbSemester);
            pnlForm.add(cmbSemester);

            add(pnlForm, BorderLayout.CENTER);

            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
            footerPanel.setBackground(AdminTheme.WHITE);
            footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AdminTheme.PASTEL_GREEN));

            JButton btnSave = new JButton(editEntry == null ? "Schedule Slot" : "Update Slot");
            btnSave.setFont(AdminTheme.FONT_TINY);
            btnSave.setBackground(AdminTheme.FOREST_GREEN);
            btnSave.setForeground(AdminTheme.WHITE);
            btnSave.setFocusPainted(false);
            btnSave.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
            btnSave.addActionListener(e -> saveTimetable());

            JButton btnCancel = new JButton("Cancel");
            btnCancel.setFont(AdminTheme.FONT_TINY);
            btnCancel.setBackground(AdminTheme.PASTEL_GREEN);
            btnCancel.setForeground(AdminTheme.DEEP_GREEN);
            btnCancel.setFocusPainted(false);
            btnCancel.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
            btnCancel.addActionListener(e -> dispose());

            footerPanel.add(btnSave);
            footerPanel.add(btnCancel);
            add(footerPanel, BorderLayout.SOUTH);
        }

        private JLabel createLabel(String text) {
            JLabel l = new JLabel(text);
            l.setFont(AdminTheme.FONT_TINY);
            l.setForeground(AdminTheme.BLACK);
            return l;
        }

        private JTextField createTextField(String val) {
            JTextField tf = new JTextField(val);
            tf.setFont(AdminTheme.FONT_SMALL);
            tf.setBackground(AdminTheme.WHITE);
            tf.setForeground(AdminTheme.BLACK);
            tf.setCaretColor(AdminTheme.BLACK);
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
            return tf;
        }

        private void styleCombo(JComboBox<?> cb) {
            cb.setFont(AdminTheme.FONT_SMALL);
            cb.setBackground(AdminTheme.WHITE);
            cb.setForeground(AdminTheme.BLACK);
        }

        private void populateCourses() {
            try {
                List<Course> courses = courseDAO.getAllCourses();
                if (courses != null) {
                    for (Course c : courses) {
                        cmbCourse.addItem(new CourseComboItem(c.getCourseId(), c.getCourseCode() + " - " + c.getCourseName()));
                    }
                }
            } catch (Exception e) {
                System.err.println("[ScheduleSlotComponent] Could not populate courses: " + e.getMessage());
            }
        }

        private void populateLecturers() {
            try {
                List<User> lecturers = userDAO.getUsersByRole("Lecturer");
                if (lecturers != null) {
                    for (User u : lecturers) {
                        String title = (u instanceof Lecturer && ((Lecturer) u).getTitle() != null) ? ((Lecturer) u).getTitle() + " " : "";
                        cmbLecturer.addItem(new LecturerComboItem(u.getUserId(), title + u.getFullName()));
                    }
                }
            } catch (Exception e) {
                System.err.println("[ScheduleSlotComponent] Could not populate lecturers: " + e.getMessage());
            }
        }

        private void populateForm() {
            cmbDepartment.setSelectedIndex(Math.max(0, editEntry.getDepartmentId() - 1));
            txtBatchYear.setText(String.valueOf(editEntry.getBatchYear()));
            if (editEntry.getComponent() != null) cmbComponent.setSelectedItem(editEntry.getComponent());
            if (editEntry.getDayOfWeek() != null) cmbDay.setSelectedItem(editEntry.getDayOfWeek());
            if (editEntry.getStartTime() != null) txtStartTime.setText(editEntry.getStartTime().toString());
            if (editEntry.getEndTime() != null) txtEndTime.setText(editEntry.getEndTime().toString());
            if (editEntry.getVenue() != null) txtVenue.setText(editEntry.getVenue());
            if (editEntry.getAcademicYear() != null) txtAcademicYear.setText(editEntry.getAcademicYear());
            if (editEntry.getSemester() != null) cmbSemester.setSelectedItem(editEntry.getSemester());
        }

        private void saveTimetable() {
            try {
                int deptId = cmbDepartment.getSelectedIndex() + 1;

                CourseComboItem selectedCourse = (CourseComboItem) cmbCourse.getSelectedItem();
                if (selectedCourse == null) {
                    JOptionPane.showMessageDialog(this, "Please select a course module.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LecturerComboItem selectedLecturer = (LecturerComboItem) cmbLecturer.getSelectedItem();
                Long lecturerId = selectedLecturer != null ? selectedLecturer.getUserId() : null;

                int batchYear = Integer.parseInt(txtBatchYear.getText().trim());
                Time startTime = Time.valueOf(txtStartTime.getText().trim());
                Time endTime = Time.valueOf(txtEndTime.getText().trim());

                if (editEntry == null) {
                    TimetableEntry t = new TimetableEntry();
                    t.setDepartmentId(deptId);
                    t.setCourseId(selectedCourse.getCourseId());
                    t.setLecturerId(lecturerId);
                    t.setBatchYear(batchYear);
                    t.setComponent((String) cmbComponent.getSelectedItem());
                    t.setDayOfWeek((String) cmbDay.getSelectedItem());
                    t.setStartTime(startTime);
                    t.setEndTime(endTime);
                    t.setVenue(txtVenue.getText().trim());
                    t.setAcademicYear(txtAcademicYear.getText().trim());
                    t.setSemester((String) cmbSemester.getSelectedItem());

                    timetableDAO.createTimetableEntry(t);
                    JOptionPane.showMessageDialog(this, "Timetable slot scheduled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    editEntry.setDepartmentId(deptId);
                    editEntry.setCourseId(selectedCourse.getCourseId());
                    editEntry.setLecturerId(lecturerId);
                    editEntry.setBatchYear(batchYear);
                    editEntry.setComponent((String) cmbComponent.getSelectedItem());
                    editEntry.setDayOfWeek((String) cmbDay.getSelectedItem());
                    editEntry.setStartTime(startTime);
                    editEntry.setEndTime(endTime);
                    editEntry.setVenue(txtVenue.getText().trim());
                    editEntry.setAcademicYear(txtAcademicYear.getText().trim());
                    editEntry.setSemester((String) cmbSemester.getSelectedItem());

                    timetableDAO.updateTimetableEntry(editEntry);
                    JOptionPane.showMessageDialog(this, "Timetable slot updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }

                if (onSuccess != null) onSuccess.run();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving timetable slot: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
