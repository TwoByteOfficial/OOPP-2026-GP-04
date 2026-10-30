package gui.admin.CourseComponents;

import dao.admin.AdminCourseDAO;
import model.Course;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;

public class CourseForm extends JDialog {

    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color WHITE        = Color.WHITE;
    private final Color BLACK        = Color.BLACK;

    private AdminCourseDAO courseDAO;
    private Course editCourse;
    private Runnable onSuccess;

    private JTextField txtCode;
    private JTextField txtName;
    private JTextField txtTheoryCredits;
    private JTextField txtPracticalCredits;

    // CA Sub-weight Fields
    private JTextField txtQuizWeight;
    private JTextField txtAssignmentWeight;
    private JTextField txtMidTheoryWeight;
    private JTextField txtMidPracticalWeight;

    // End Exam Sub-weight Fields
    private JTextField txtEndTheoryWeight;
    private JTextField txtEndPracticalWeight;

    private JComboBox<String> cmbDepartment;
    private JTextField txtAcademicYear;
    private JComboBox<String> cmbSemester;
    private JComboBox<String> cmbStatus;

    public CourseForm(Frame parent, AdminCourseDAO dao, Course courseToEdit, Runnable onSuccess) {
        super(parent, (courseToEdit == null ? "Add New Course Module" : "Edit Course Module"), true);
        this.courseDAO = dao;
        this.editCourse = courseToEdit;
        this.onSuccess = onSuccess;

        setSize(820, 780);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(new Color(0xE8, 0xF5, 0xE9));
        setLayout(new BorderLayout());

        initUI();

        if (editCourse != null) {
            populateForm();
        }
    }

    private void initUI() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 16));
        headerPanel.setBackground(DEEP_GREEN);

        JLabel lblHeader = new JLabel(editCourse == null ? "Register New Academic Course Module" : "Modify Course Module Details");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(WHITE);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        JPanel pnlForm = new JPanel(new GridLayout(14, 2, 12, 8));
        pnlForm.setBackground(WHITE);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        pnlForm.add(createLabel("Course Code:"));
        txtCode = createTextField("ICT1113");
        pnlForm.add(txtCode);

        pnlForm.add(createLabel("Course Name:"));
        txtName = createTextField("Programming Fundamentals");
        pnlForm.add(txtName);

        pnlForm.add(createLabel("Theory Credits:"));
        txtTheoryCredits = createTextField("2.0");
        pnlForm.add(txtTheoryCredits);

        pnlForm.add(createLabel("Practical Credits:"));
        txtPracticalCredits = createTextField("1.0");
        pnlForm.add(txtPracticalCredits);

        // Continuous Assessment Breakdown
        pnlForm.add(createSectionHeader("--- CA Breakdown Weights ---"));
        pnlForm.add(new JLabel(""));

        pnlForm.add(createLabel("Max Quiz 2 Weight (%):"));
        txtQuizWeight = createTextField("10.0");
        pnlForm.add(txtQuizWeight);

        pnlForm.add(createLabel("Assignment Weight (%):"));
        txtAssignmentWeight = createTextField("10.0");
        pnlForm.add(txtAssignmentWeight);

        pnlForm.add(createLabel("Mid Exam Theory Weight (%):"));
        txtMidTheoryWeight = createTextField("10.0");
        pnlForm.add(txtMidTheoryWeight);

        pnlForm.add(createLabel("Mid Exam Practical Weight (%):"));
        txtMidPracticalWeight = createTextField("0.0");
        pnlForm.add(txtMidPracticalWeight);

        // End Semester Examination Breakdown
        pnlForm.add(createSectionHeader("--- End Exam Breakdown Weights ---"));
        pnlForm.add(new JLabel(""));

        pnlForm.add(createLabel("End Exam Theory Weight (%):"));
        txtEndTheoryWeight = createTextField("70.0");
        pnlForm.add(txtEndTheoryWeight);

        pnlForm.add(createLabel("End Exam Practical Weight (%):"));
        txtEndPracticalWeight = createTextField("0.0");
        pnlForm.add(txtEndPracticalWeight);

        // General Attributes
        pnlForm.add(createLabel("Department:"));
        cmbDepartment = new JComboBox<>(new String[]{ "1 - ICT", "2 - Engineering Technology", "3 - Bio Systems Technology" });
        styleCombo(cmbDepartment);
        pnlForm.add(cmbDepartment);

        pnlForm.add(createLabel("Academic Year:"));
        txtAcademicYear = createTextField("2024/2025");
        pnlForm.add(txtAcademicYear);

        pnlForm.add(createLabel("Semester:"));
        cmbSemester = new JComboBox<>(new String[]{ "Semester 1", "Semester 2", "Semester I", "Semester II" });
        styleCombo(cmbSemester);
        pnlForm.add(cmbSemester);

        pnlForm.add(createLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{ "Active", "Inactive" });
        styleCombo(cmbStatus);
        pnlForm.add(cmbStatus);

        add(new JScrollPane(pnlForm), BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        footerPanel.setBackground(WHITE);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, PASTEL_GREEN));

        JButton btnSave = new JButton(editCourse == null ? "Save Course" : "Update Course");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSave.setBackground(FOREST_GREEN);
        btnSave.setForeground(WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btnSave.addActionListener(e -> saveCourse());

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCancel.setBackground(PASTEL_GREEN);
        btnCancel.setForeground(DEEP_GREEN);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btnCancel.addActionListener(e -> dispose());

        footerPanel.add(btnSave);
        footerPanel.add(btnCancel);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(BLACK);
        return l;
    }

    private JLabel createSectionHeader(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(DEEP_GREEN);
        return l;
    }

    private JTextField createTextField(String val) {
        JTextField tf = new JTextField(val);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tf.setBackground(WHITE);
        tf.setForeground(BLACK);
        tf.setCaretColor(BLACK);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cb.setBackground(WHITE);
        cb.setForeground(BLACK);
    }

    private void populateForm() {
        txtCode.setText(editCourse.getCourseCode());
        txtName.setText(editCourse.getCourseName());
        if (editCourse.getTheoryCredits() != null) txtTheoryCredits.setText(editCourse.getTheoryCredits().toString());
        if (editCourse.getPracticalCredits() != null) txtPracticalCredits.setText(editCourse.getPracticalCredits().toString());

        if (editCourse.getQuizWeight() != null) txtQuizWeight.setText(editCourse.getQuizWeight().toString());
        if (editCourse.getAssignmentWeight() != null) txtAssignmentWeight.setText(editCourse.getAssignmentWeight().toString());
        if (editCourse.getMidTheoryWeight() != null) txtMidTheoryWeight.setText(editCourse.getMidTheoryWeight().toString());
        if (editCourse.getMidPracticalWeight() != null) txtMidPracticalWeight.setText(editCourse.getMidPracticalWeight().toString());

        if (editCourse.getEndTheoryWeight() != null) txtEndTheoryWeight.setText(editCourse.getEndTheoryWeight().toString());
        if (editCourse.getEndPracticalWeight() != null) txtEndPracticalWeight.setText(editCourse.getEndPracticalWeight().toString());

        int deptIndex = Math.max(0, editCourse.getDepartmentId() - 1);
        if (deptIndex < cmbDepartment.getItemCount()) cmbDepartment.setSelectedIndex(deptIndex);

        if (editCourse.getAcademicYear() != null) txtAcademicYear.setText(editCourse.getAcademicYear());
        if (editCourse.getSemester() != null) {
            String sem = editCourse.getSemester();
            if ("Semester I".equalsIgnoreCase(sem)) sem = "Semester 1";
            else if ("Semester II".equalsIgnoreCase(sem)) sem = "Semester 2";
            cmbSemester.setSelectedItem(sem);
        }
        if (editCourse.getStatus() != null) cmbStatus.setSelectedItem(editCourse.getStatus());
    }

    private BigDecimal parseNonNegativeBigDecimal(JTextField tf, String fieldName) throws Exception {
        BigDecimal val = new BigDecimal(tf.getText().trim());
        if (val.compareTo(BigDecimal.ZERO) < 0) {
            JOptionPane.showMessageDialog(this, fieldName + " cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            tf.requestFocus();
            throw new IllegalArgumentException(fieldName + " negative");
        }
        return val;
    }

    private void saveCourse() {
        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();

        if (code.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course Code and Name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal theory, practical, quiz, assignment, midTheory, midPractical, endTheory, endPractical;
        try {
            theory = parseNonNegativeBigDecimal(txtTheoryCredits, "Theory Credits");
            practical = parseNonNegativeBigDecimal(txtPracticalCredits, "Practical Credits");
            quiz = parseNonNegativeBigDecimal(txtQuizWeight, "Quiz Weight");
            assignment = parseNonNegativeBigDecimal(txtAssignmentWeight, "Assignment Weight");
            midTheory = parseNonNegativeBigDecimal(txtMidTheoryWeight, "Mid Exam Theory Weight");
            midPractical = parseNonNegativeBigDecimal(txtMidPracticalWeight, "Mid Exam Practical Weight");
            endTheory = parseNonNegativeBigDecimal(txtEndTheoryWeight, "End Exam Theory Weight");
            endPractical = parseNonNegativeBigDecimal(txtEndPracticalWeight, "End Exam Practical Weight");
        } catch (Exception ex) {
            return;
        }

        BigDecimal caTotal = quiz.add(assignment).add(midTheory).add(midPractical);
        BigDecimal endTotal = endTheory.add(endPractical);
        BigDecimal grandTotal = caTotal.add(endTotal);

        if (grandTotal.compareTo(new BigDecimal("100.00")) != 0) {
            JOptionPane.showMessageDialog(this, "Total Course Assessment Weight must equal 100%. " + "Current CA Weight: " + caTotal + "% " + "Current End Exam Weight: " + endTotal + "% " + "Total: " + grandTotal + "%", "Weight Allocation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int deptId = cmbDepartment.getSelectedIndex() + 1;
        String semSelected = (String) cmbSemester.getSelectedItem();
        String semesterFormatted = (semSelected != null && (semSelected.contains("2") || semSelected.contains("II"))) ? "Semester II" : "Semester I";

        try {
            Course c = (editCourse == null) ? new Course() : editCourse;
            c.setCourseCode(code);
            c.setCourseName(name);
            c.setTheoryCredits(theory);
            c.setPracticalCredits(practical);
            c.setTotalCredits(theory.add(practical));

            c.setQuizWeight(quiz);
            c.setAssignmentWeight(assignment);
            c.setMidTheoryWeight(midTheory);
            c.setMidPracticalWeight(midPractical);
            c.setCaWeight(caTotal);

            c.setEndTheoryWeight(endTheory);
            c.setEndPracticalWeight(endPractical);
            c.setFinalWeight(endTotal);

            c.setDepartmentId(deptId);
            c.setAcademicYear(txtAcademicYear.getText().trim());
            c.setSemester(semesterFormatted);
            c.setStatus((String) cmbStatus.getSelectedItem());

            boolean success = (editCourse == null) ? courseDAO.createCourse(c) : courseDAO.updateCourse(c);
            if (success) {
                JOptionPane.showMessageDialog(this, "Course '" + code + "' saved successfully!", "Operation Successful", JOptionPane.INFORMATION_MESSAGE);
                if (onSuccess != null) onSuccess.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Could not save course in database.", "Save Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}