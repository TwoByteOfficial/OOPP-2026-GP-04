package gui.admin.EnrollmentComponents;

import dao.admin.AdminCourseDAO;
import dao.admin.AdminEnrollDAO;
import model.Course;

import javax.swing.*;
import java.awt.*;

import java.util.List;

/**
 * EnrollmentForm - Form dialog for submitting new student enrollment requests.
 */
public class EnrollmentForm extends JDialog {

    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color WHITE        = Color.WHITE;
    private final Color MINT         = new Color(0xE8, 0xF5, 0xE9);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);

    private final JTextField txtStudentId;
    private final JComboBox<String> cmbCourses;
    private final JComboBox<String> cmbAcademicYear;
    private final JComboBox<String> cmbSemester;
    private final JComboBox<String> cmbEnrollmentType;
    private final JTextArea txtRequestNote;

    private final AdminEnrollDAO enrollDAO;
    private final List<Course> courseList;
    private final Runnable onSuccess;

    public EnrollmentForm(Frame owner, AdminEnrollDAO enrollDAO, AdminCourseDAO courseDAO, Runnable onSuccess) {
        super(owner, "Create New Course Enrollment Request", true);

        this.enrollDAO = enrollDAO;
        this.onSuccess = onSuccess;

        setSize(560, 520);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DEEP_GREEN);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel titleLabel = new JLabel("Create New Course Enrollment Request");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Student ID
        addLabel(formPanel, gbc, row, "Student User ID:");
        txtStudentId = new JTextField();
        txtStudentId.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtStudentId.setToolTipText("Enter numerical User ID of student (e.g. 5)");
        addControl(formPanel, gbc, row++, txtStudentId);

        // Course Dropdown
        addLabel(formPanel, gbc, row, "Select Course:");
        cmbCourses = new JComboBox<>();
        cmbCourses.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        List<Course> loadedList = null;
        try {
            loadedList = courseDAO.getAllCourses();
            for (Course c : loadedList) {
                cmbCourses.addItem(c.getCourseCode() + " - " + c.getCourseName() + " (ID: " + c.getCourseId() + ")");
            }
        } catch (Exception e) {
            cmbCourses.addItem("Error loading courses");
        }
        this.courseList = loadedList;
        addControl(formPanel, gbc, row++, cmbCourses);

        // Academic Year
        addLabel(formPanel, gbc, row, "Academic Year:");
        cmbAcademicYear = new JComboBox<>(new String[]{ "2026", "2025/2026", "2024/2025" });
        cmbAcademicYear.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        addControl(formPanel, gbc, row++, cmbAcademicYear);

        // Semester
        addLabel(formPanel, gbc, row, "Semester:");
        cmbSemester = new JComboBox<>(new String[]{ "Semester I", "Semester II" });
        cmbSemester.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        addControl(formPanel, gbc, row++, cmbSemester);

        // Enrollment Type
        addLabel(formPanel, gbc, row, "Enrollment Type:");
        cmbEnrollmentType = new JComboBox<>(new String[]{ "Normal", "Repeat", "Batch Missed" });
        cmbEnrollmentType.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        addControl(formPanel, gbc, row++, cmbEnrollmentType);

        // Request Note
        addLabel(formPanel, gbc, row, "Request Note:");
        txtRequestNote = new JTextArea(3, 20);
        txtRequestNote.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtRequestNote.setLineWrap(true);
        txtRequestNote.setWrapStyleWord(true);
        JScrollPane scrollNote = new JScrollPane(txtRequestNote);
        scrollNote.setBorder(BorderFactory.createLineBorder(PASTEL_GREEN, 1));

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.7;
        formPanel.add(scrollNote, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        footerPanel.setBackground(MINT);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> dispose());

        JButton btnSubmit = new JButton("Submit Request");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubmit.setBackground(FOREST_GREEN);
        btnSubmit.setForeground(WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.addActionListener(e -> submitForm());

        footerPanel.add(btnCancel);
        footerPanel.add(btnSubmit);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void addLabel(JPanel panel, GridBagConstraints gbc, int row, String text) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(DEEP_GREEN);
        panel.add(lbl, gbc);
    }

    private void addControl(JPanel panel, GridBagConstraints gbc, int row, JComponent comp) {
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.7;
        panel.add(comp, gbc);
    }

    private void submitForm() {
        String studentIdStr = txtStudentId.getText().trim();
        if (studentIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Student User ID.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long studentId;
        try {
            studentId = Long.parseLong(studentIdStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Student User ID must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (courseList == null || courseList.isEmpty() || cmbCourses.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course module.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Course selectedCourse = courseList.get(cmbCourses.getSelectedIndex());
        String academicYear = (String) cmbAcademicYear.getSelectedItem();
        String semester = (String) cmbSemester.getSelectedItem();
        String enrollmentType = (String) cmbEnrollmentType.getSelectedItem();
        String note = txtRequestNote.getText().trim();

        try {
            boolean created = enrollDAO.createEnrollmentRequest(studentId, selectedCourse.getCourseId(), academicYear, semester, enrollmentType, note);
            if (created) {
                JOptionPane.showMessageDialog(this, "Enrollment request created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (onSuccess != null) onSuccess.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create enrollment request.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
