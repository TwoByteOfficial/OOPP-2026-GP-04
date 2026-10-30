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

        setSize(780, 740);
        setLocationRelativeTo(parent);
        setResizable(true);
        getContentPane().setBackground(WHITE);
        setLayout(new BorderLayout());

        initUI();

        if (editCourse != null) {
            populateForm();
        }
    }

    private void initUI() {
        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 16));
        headerPanel.setBackground(DEEP_GREEN);

        JLabel lblHeader = new JLabel(editCourse == null ? "Register New Academic Course Module" : "Modify Course Module Details");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(WHITE);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        // Main Form Panel with GridBagLayout for pristine alignment
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(WHITE);
        pnlForm.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        int row = 0;

        // Basic Information Section
        row = addSectionHeader(pnlForm, gbc, row, "Basic Module Information");
        row = addFormField(pnlForm, gbc, row, "Course Code:", txtCode = createTextField("ICT1113"));
        row = addFormField(pnlForm, gbc, row, "Course Name:", txtName = createTextField("Programming Fundamentals"));
        row = addFormField(pnlForm, gbc, row, "Theory Credits:", txtTheoryCredits = createTextField("2.0"));
        row = addFormField(pnlForm, gbc, row, "Practical Credits:", txtPracticalCredits = createTextField("1.0"));

        // Continuous Assessment (CA) Breakdown Section
        row = addSectionHeader(pnlForm, gbc, row, "Continuous Assessment (CA) Weights");
        row = addFormField(pnlForm, gbc, row, "Max Quiz 2 Weight (%):", txtQuizWeight = createTextField("10.0"));
        row = addFormField(pnlForm, gbc, row, "Assignment Weight (%):", txtAssignmentWeight = createTextField("10.0"));
        row = addFormField(pnlForm, gbc, row, "Mid Exam Theory Weight (%):", txtMidTheoryWeight = createTextField("10.0"));
        row = addFormField(pnlForm, gbc, row, "Mid Exam Practical Weight (%):", txtMidPracticalWeight = createTextField("0.0"));

        // End Semester Examination Section
        row = addSectionHeader(pnlForm, gbc, row, "End Semester Examination Weights");
        row = addFormField(pnlForm, gbc, row, "End Exam Theory Weight (%):", txtEndTheoryWeight = createTextField("70.0"));
        row = addFormField(pnlForm, gbc, row, "End Exam Practical Weight (%):", txtEndPracticalWeight = createTextField("0.0"));

        // General Module Settings Section
        row = addSectionHeader(pnlForm, gbc, row, "Academic & Department Settings");

        cmbDepartment = new JComboBox<>(new String[]{ "1 - ICT", "2 - Engineering Technology", "3 - Bio Systems Technology" });
        styleCombo(cmbDepartment);
        row = addFormField(pnlForm, gbc, row, "Department:", cmbDepartment);

        row = addFormField(pnlForm, gbc, row, "Academic Year:", txtAcademicYear = createTextField("2024/2025"));

        cmbSemester = new JComboBox<>(new String[]{ "Semester 1", "Semester 2", "Semester I", "Semester II" });
        styleCombo(cmbSemester);
        row = addFormField(pnlForm, gbc, row, "Semester:", cmbSemester);

        cmbStatus = new JComboBox<>(new String[]{ "Active", "Inactive" });
        styleCombo(cmbStatus);
        row = addFormField(pnlForm, gbc, row, "Status:", cmbStatus);

        // Vertical glue filler to push everything neatly to top
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        pnlForm.add(Box.createGlue(), gbc);

        JScrollPane scrollPane = new JScrollPane(pnlForm);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Footer Action Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        footerPanel.setBackground(WHITE);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, PASTEL_GREEN));

        JButton btnSave = new JButton(editCourse == null ? "Save Course" : "Update Course");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSave.setBackground(FOREST_GREEN);
        btnSave.setForeground(WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSave.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        btnSave.addActionListener(e -> saveCourse());

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCancel.setBackground(PASTEL_GREEN);
        btnCancel.setForeground(DEEP_GREEN);
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        btnCancel.addActionListener(e -> dispose());

        footerPanel.add(btnSave);
        footerPanel.add(btnCancel);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private int addSectionHeader(JPanel panel, GridBagConstraints gbc, int row, String title) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(16, 12, 8, 12);

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(DEEP_GREEN);

        JSeparator line = new JSeparator();
        line.setForeground(PASTEL_GREEN);

        pnlHeader.add(lbl, BorderLayout.NORTH);
        pnlHeader.add(line, BorderLayout.SOUTH);

        panel.add(pnlHeader, gbc);
        return row + 1;
    }

    private int addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        // Label constraint
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.30;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(6, 16, 6, 12);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(BLACK);
        panel.add(lbl, gbc);

        // Field constraint
        gbc.gridx = 1;
        gbc.weightx = 0.70;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 16);

        panel.add(field, gbc);

        return row + 1;
    }

    private JTextField createTextField(String val) {
        JTextField tf = new JTextField(val);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tf.setPreferredSize(new Dimension(300, 36));
        tf.setBackground(WHITE);
        tf.setForeground(BLACK);
        tf.setCaretColor(BLACK);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        return tf;
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cb.setPreferredSize(new Dimension(300, 36));
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