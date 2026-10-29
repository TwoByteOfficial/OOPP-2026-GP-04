package gui.admin.CourseComponents;

import dao.admin.AdminCourseDAO;
import dao.admin.AdminUserDAO;
import gui.admin.AdminTheme;
import model.Course;
import model.Lecturer;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * AssignLecturerButton - Integrated component file containing the "Assign Lecturer" button
 * and its on-click modal dialog window for assigning faculty lecturers to course modules.
 */
public class AssignLecturerButton extends JButton {

    public AssignLecturerButton(CourseTable table, Frame parent, AdminCourseDAO courseDAO, AdminUserDAO userDAO, Runnable onSuccess) {
        super("👨‍🏫 Assign Lecturer");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setBackground(AdminTheme.FRESH_GREEN);
        setForeground(AdminTheme.DEEP_GREEN);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> {
            Course selected = table.getSelectedCourse();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a course module from the table to assign a lecturer.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            AssignLecturerDialog dialog = new AssignLecturerDialog(parent, courseDAO, userDAO, selected, onSuccess);
            dialog.setVisible(true);
        });
    }

    /**
     * AssignLecturerDialog - On-click popup window for assigning a lecturer to a course.
     */
    public static class AssignLecturerDialog extends JDialog {

        private final AdminCourseDAO courseDAO;
        private final AdminUserDAO userDAO;
        private final Course course;
        private final Runnable onSuccess;

        private JComboBox<LecturerComboItem> cmbLecturer;
        private JComboBox<String> cmbResponsibility;

        public AssignLecturerDialog(Frame parent, AdminCourseDAO courseDAO, AdminUserDAO userDAO, Course course, Runnable onSuccess) {
            super(parent, "Assign Academic Lecturer to Course Module", true);
            this.courseDAO = courseDAO;
            this.userDAO = userDAO;
            this.course = course;
            this.onSuccess = onSuccess;

            setSize(620, 320);
            setLocationRelativeTo(parent);
            setResizable(false);
            getContentPane().setBackground(AdminTheme.LIGHT_BG);
            setLayout(new BorderLayout());

            initUI();
        }

        private void initUI() {
            // Header
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
            headerPanel.setBackground(AdminTheme.DEEP_GREEN);

            JLabel lblHeader = new JLabel("Assign Lecturer: " + course.getCourseCode() + " - " + course.getCourseName());
            lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 17));
            lblHeader.setForeground(AdminTheme.WHITE);
            headerPanel.add(lblHeader);
            add(headerPanel, BorderLayout.NORTH);

            // Body Form
            JPanel body = new JPanel(new GridLayout(2, 2, 14, 14));
            body.setBackground(AdminTheme.WHITE);
            body.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                    BorderFactory.createEmptyBorder(20, 24, 20, 24)
            ));

            JLabel l1 = new JLabel("Select Lecturer:");
            l1.setFont(new Font("Segoe UI", Font.BOLD, 15));
            l1.setForeground(AdminTheme.DEEP_GREEN);
            body.add(l1);

            cmbLecturer = new JComboBox<>();
            cmbLecturer.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            cmbLecturer.setBackground(AdminTheme.WHITE);
            cmbLecturer.setForeground(AdminTheme.BLACK);
            populateLecturers();
            body.add(cmbLecturer);

            JLabel l2 = new JLabel("Responsibility:");
            l2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            l2.setForeground(AdminTheme.DEEP_GREEN);
            body.add(l2);

            cmbResponsibility = new JComboBox<>(new String[]{ "In-Charge", "Co-Lecturer", "Visiting Lecturer", "Practical Instructor" });
            cmbResponsibility.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            cmbResponsibility.setBackground(AdminTheme.WHITE);
            cmbResponsibility.setForeground(AdminTheme.BLACK);
            body.add(cmbResponsibility);

            add(body, BorderLayout.CENTER);

            // Footer
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
            footer.setBackground(AdminTheme.WHITE);
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AdminTheme.PASTEL_GREEN));

            JButton btnAssign = new JButton("Assign Lecturer");
            btnAssign.setFont(new Font("Segoe UI", Font.BOLD, 15));
            btnAssign.setBackground(AdminTheme.FOREST_GREEN);
            btnAssign.setForeground(AdminTheme.WHITE);
            btnAssign.setFocusPainted(false);
            btnAssign.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnAssign.addActionListener(e -> {
                LecturerComboItem selected = (LecturerComboItem) cmbLecturer.getSelectedItem();
                if (selected == null) {
                    JOptionPane.showMessageDialog(this, "Please select a lecturer to assign.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    String resp = (String) cmbResponsibility.getSelectedItem();
                    courseDAO.assignLecturerToCourse(course.getCourseId(), selected.getUserId(), resp);
                    JOptionPane.showMessageDialog(this, "Lecturer assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    if (onSuccess != null) onSuccess.run();
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JButton btnCancel = new JButton("Cancel");
            btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            btnCancel.setBackground(AdminTheme.PASTEL_GREEN);
            btnCancel.setForeground(AdminTheme.DEEP_GREEN);
            btnCancel.setFocusPainted(false);
            btnCancel.addActionListener(e -> dispose());

            footer.add(btnAssign);
            footer.add(btnCancel);
            add(footer, BorderLayout.SOUTH);
        }

        private void populateLecturers() {
            try {
                if (userDAO != null) {
                    List<User> lecturers = userDAO.getUsersByRole("Lecturer");
                    if (lecturers != null) {
                        for (User u : lecturers) {
                            String title = (u instanceof Lecturer && ((Lecturer) u).getTitle() != null) ? ((Lecturer) u).getTitle() + " " : "";
                            String code = (u instanceof Lecturer && ((Lecturer) u).getLecturerCode() != null) ? " (" + ((Lecturer) u).getLecturerCode() + ")" : "";
                            cmbLecturer.addItem(new LecturerComboItem(u.getUserId(), title + u.getFullName() + code));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("[AssignLecturerDialog] Could not populate lecturers: " + e.getMessage());
            }
        }

        private static class LecturerComboItem {
            private final long userId;
            private final String displayName;

            public LecturerComboItem(long userId, String displayName) {
                this.userId = userId;
                this.displayName = displayName;
            }

            public long getUserId() { return userId; }

            @Override
            public String toString() { return displayName; }
        }
    }
}
