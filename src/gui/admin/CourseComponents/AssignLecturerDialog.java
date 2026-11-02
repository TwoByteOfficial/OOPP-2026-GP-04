package gui.admin.CourseComponents;

import dao.admin.AdminCourseDAO;
import dao.admin.AdminUserDAO;
import model.Course;
import model.Lecturer;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AssignLecturerDialog extends JDialog {

    private AdminCourseDAO courseDAO;
    private AdminUserDAO userDAO;
    private Course course;
    private JComboBox<LecturerComboItem> cmbLecturer;
    private JComboBox<String> cmbResponsibility;

    public static class LecturerComboItem {
        private long userId;
        private String displayText;

        public LecturerComboItem(long userId, String displayText) {
            this.userId = userId;
            this.displayText = displayText;
        }

        public long getUserId() { return userId; }

        @Override
        public String toString() { return displayText; }
    }

    public AssignLecturerDialog(Frame parent, AdminCourseDAO dao, Course course, Runnable onSuccess) {
        this(parent, dao, new AdminUserDAO(), course, onSuccess);
    }

    public AssignLecturerDialog(Frame parent, AdminCourseDAO courseDAO, AdminUserDAO userDAO, Course course, Runnable onSuccess) {
        super(parent, "Assign Lecturer to " + course.getCourseCode(), true);
        this.courseDAO = courseDAO;
        this.userDAO = userDAO;
        this.course = course;

        setSize(540, 320);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setBackground(new Color(0x1B, 0x5E, 0x20));
        JLabel lblHeader = new JLabel("Assign Lecturer to: " + course.getCourseCode() + " - " + course.getCourseName());
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(2, 2, 14, 14));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel l1 = new JLabel("Select Lecturer:");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l1.setForeground(Color.BLACK);
        body.add(l1);

        cmbLecturer = new JComboBox<>();
        cmbLecturer.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cmbLecturer.setBackground(Color.WHITE);
        cmbLecturer.setForeground(Color.BLACK);
        populateLecturers();
        body.add(cmbLecturer);

        JLabel l2 = new JLabel("Responsibility:");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l2.setForeground(Color.BLACK);
        body.add(l2);

        cmbResponsibility = new JComboBox<>(new String[]{ "Coordinator", "Lecturer", "Practical Instructor" });
        cmbResponsibility.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cmbResponsibility.setBackground(Color.WHITE);
        cmbResponsibility.setForeground(Color.BLACK);
        body.add(cmbResponsibility);

        add(body, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        footer.setBackground(Color.WHITE);

        JButton btnAssign = new JButton("Assign Lecturer");
        btnAssign.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnAssign.setBackground(new Color(0x38, 0x8E, 0x3C));
        btnAssign.setForeground(Color.WHITE);
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
        btnCancel.addActionListener(e -> dispose());

        footer.add(btnAssign);
        footer.add(btnCancel);
        add(footer, BorderLayout.SOUTH);
    }

    private void populateLecturers() {
        try {
            List<User> lecturers = userDAO.getUsersByRole("Lecturer");
            if (lecturers != null) {
                for (User u : lecturers) {
                    String title = (u instanceof Lecturer && ((Lecturer) u).getTitle() != null) ? ((Lecturer) u).getTitle() + " " : "";
                    String code = (u instanceof Lecturer && ((Lecturer) u).getLecturerCode() != null) ? " (" + ((Lecturer) u).getLecturerCode() + ")" : "";
                    cmbLecturer.addItem(new LecturerComboItem(u.getUserId(), title + u.getFullName() + code));
                }
            }
        } catch (Exception e) {
            System.err.println("[AssignLecturerDialog] Could not populate lecturers: " + e.getMessage());
        }
    }
}
