package gui.admin.CourseComponents;

import dao.admin.AdminCourseDAO;
import model.Course;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

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
    private JTextArea txtDescription;
    private JTextField txtTheoryCredits;
    private JTextField txtPracticalCredits;
    private JTextField txtCaWeight;
    private JTextField txtFinalWeight;
    private JComboBox<String> cmbDepartment;
    private JTextField txtAcademicYear;
    private JComboBox<String> cmbSemester;
    private JComboBox<String> cmbStatus;

    public CourseForm(Frame parent, AdminCourseDAO dao, Course courseToEdit, Runnable onSuccess) {
        super(parent, (courseToEdit == null ? "Add New Course Module" : "Edit Course Module"), true);
        this.courseDAO = dao;
        this.editCourse = courseToEdit;
        this.onSuccess = onSuccess;

        setSize(780, 680);
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

        JPanel pnlForm = new JPanel(new GridLayout(10, 2, 14, 12));
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

        pnlForm.add(createLabel("CA Weight (%):"));
        txtCaWeight = createTextField("30");
        pnlForm.add(txtCaWeight);

        pnlForm.add(createLabel("Final Exam Weight (%):"));
        txtFinalWeight = createTextField("70");
        pnlForm.add(txtFinalWeight);

        pnlForm.add(createLabel("Department:"));
        cmbDepartment = new JComboBox<>(new String[]{ "1 - ICT", "2 - Engineering Technology", "3 - Bio Systems Technology" });
        styleCombo(cmbDepartment);
        pnlForm.add(cmbDepartment);

        pnlForm.add(createLabel("Academic Year:"));
        txtAcademicYear = createTextField("2024/2025");
        pnlForm.add(txtAcademicYear);

        pnlForm.add(createLabel("Semester:"));
        cmbSemester = new JComboBox<>(new String[]{ "Semester 1", "Semester 2" });
        styleCombo(cmbSemester);
        pnlForm.add(cmbSemester);

        pnlForm.add(createLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{ "Active", "Inactive" });
        styleCombo(cmbStatus);
        pnlForm.add(cmbStatus);

        add(pnlForm, BorderLayout.CENTER);

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
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(BLACK);
        return l;
    }

    private JTextField createTextField(String val) {
        JTextField tf = new JTextField(val);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tf.setBackground(WHITE);
        tf.setForeground(BLACK);
        tf.setCaretColor(BLACK);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cb.setBackground(WHITE);
        cb.setForeground(BLACK);
    }

    private void populateForm() {
        txtCode.setText(editCourse.getCourseCode());
        txtName.setText(editCourse.getCourseName());
        if (editCourse.getTheoryCredits() != null) txtTheoryCredits.setText(editCourse.getTheoryCredits().toString());
        if (editCourse.getPracticalCredits() != null) txtPracticalCredits.setText(editCourse.getPracticalCredits().toString());
        if (editCourse.getCaWeight() != null) txtCaWeight.setText(editCourse.getCaWeight().toString());
        if (editCourse.getFinalWeight() != null) txtFinalWeight.setText(editCourse.getFinalWeight().toString());
        cmbDepartment.setSelectedIndex(Math.max(0, editCourse.getDepartmentId() - 1));
        if (editCourse.getAcademicYear() != null) txtAcademicYear.setText(editCourse.getAcademicYear());
        if (editCourse.getSemester() != null) cmbSemester.setSelectedItem(editCourse.getSemester());
        if (editCourse.getStatus() != null) cmbStatus.setSelectedItem(editCourse.getStatus());
    }

    private void saveCourse() {
        try {
            String code = txtCode.getText().trim();
            String name = txtName.getText().trim();
            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course Code and Name are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal theory = new BigDecimal(txtTheoryCredits.getText().trim());
            BigDecimal practical = new BigDecimal(txtPracticalCredits.getText().trim());
            BigDecimal total = theory.add(practical);
            BigDecimal ca = new BigDecimal(txtCaWeight.getText().trim());
            BigDecimal finalW = new BigDecimal(txtFinalWeight.getText().trim());
            int deptId = cmbDepartment.getSelectedIndex() + 1;

            if (editCourse == null) {
                Course c = new Course();
                c.setCourseCode(code);
                c.setCourseName(name);
                c.setTheoryCredits(theory);
                c.setPracticalCredits(practical);
                c.setTotalCredits(total);
                c.setCaWeight(ca);
                c.setFinalWeight(finalW);
                c.setDepartmentId(deptId);
                c.setAcademicYear(txtAcademicYear.getText().trim());
                c.setSemester((String) cmbSemester.getSelectedItem());
                c.setStatus((String) cmbStatus.getSelectedItem());

                courseDAO.createCourse(c);
                JOptionPane.showMessageDialog(this, "Course registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                editCourse.setCourseCode(code);
                editCourse.setCourseName(name);
                editCourse.setTheoryCredits(theory);
                editCourse.setPracticalCredits(practical);
                editCourse.setTotalCredits(total);
                editCourse.setCaWeight(ca);
                editCourse.setFinalWeight(finalW);
                editCourse.setDepartmentId(deptId);
                editCourse.setAcademicYear(txtAcademicYear.getText().trim());
                editCourse.setSemester((String) cmbSemester.getSelectedItem());
                editCourse.setStatus((String) cmbStatus.getSelectedItem());

                courseDAO.updateCourse(editCourse);
                JOptionPane.showMessageDialog(this, "Course updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving course: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
