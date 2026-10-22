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
        cmbSemester = new JComboBox<>(new String[]{ "Semester 1", "Semester 2", "Semester I", "Semester II" });
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
        
        int deptIndex = Math.max(0, editCourse.getDepartmentId() - 1);
        if (deptIndex < cmbDepartment.getItemCount()) {
            cmbDepartment.setSelectedIndex(deptIndex);
        }
        
        if (editCourse.getAcademicYear() != null) txtAcademicYear.setText(editCourse.getAcademicYear());
        if (editCourse.getSemester() != null) {
            String sem = editCourse.getSemester();
            if ("Semester I".equalsIgnoreCase(sem)) sem = "Semester 1";
            else if ("Semester II".equalsIgnoreCase(sem)) sem = "Semester 2";
            cmbSemester.setSelectedItem(sem);
        }
        if (editCourse.getStatus() != null) cmbStatus.setSelectedItem(editCourse.getStatus());
    }

    private void saveCourse() {
        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();

        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Course Code (e.g., ICT1113).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            txtCode.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Course Name (e.g., Programming Fundamentals).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            txtName.requestFocus();
            return;
        }

        BigDecimal theory;
        try {
            theory = new BigDecimal(txtTheoryCredits.getText().trim());
            if (theory.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Theory Credits cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                txtTheoryCredits.requestFocus();
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Theory Credits must be a valid number (e.g., 2.0 or 0.0).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            txtTheoryCredits.requestFocus();
            return;
        }

        BigDecimal practical;
        try {
            practical = new BigDecimal(txtPracticalCredits.getText().trim());
            if (practical.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Practical Credits cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                txtPracticalCredits.requestFocus();
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Practical Credits must be a valid number (e.g., 1.0 or 0.0).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            txtPracticalCredits.requestFocus();
            return;
        }

        if (theory.add(practical).compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "Total Credits (Theory + Practical) must be greater than 0.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal ca;
        try {
            ca = new BigDecimal(txtCaWeight.getText().trim());
            if (ca.compareTo(BigDecimal.ZERO) < 0 || ca.compareTo(new BigDecimal("100")) > 0) {
                JOptionPane.showMessageDialog(this, "CA Weight must be between 0% and 100%.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                txtCaWeight.requestFocus();
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "CA Weight must be a valid numeric percentage (e.g., 30).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            txtCaWeight.requestFocus();
            return;
        }

        BigDecimal finalW;
        try {
            finalW = new BigDecimal(txtFinalWeight.getText().trim());
            if (finalW.compareTo(BigDecimal.ZERO) < 0 || finalW.compareTo(new BigDecimal("100")) > 0) {
                JOptionPane.showMessageDialog(this, "Final Exam Weight must be between 0% and 100%.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                txtFinalWeight.requestFocus();
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Final Exam Weight must be a valid numeric percentage (e.g., 70).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            txtFinalWeight.requestFocus();
            return;
        }

        if (ca.add(finalW).compareTo(new BigDecimal("100")) != 0) {
            JOptionPane.showMessageDialog(this, 
                "CA Weight (" + ca + "%) and Final Exam Weight (" + finalW + "%) must sum to 100%.Currently total weight is " + ca.add(finalW) + "%.",
                "Weight Allocation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedDeptStr = (String) cmbDepartment.getSelectedItem();
        int deptId = 1;
        if (selectedDeptStr != null && selectedDeptStr.contains("-")) {
            try {
                deptId = Integer.parseInt(selectedDeptStr.split("-")[0].trim());
            } catch (Exception ignored) {
                deptId = cmbDepartment.getSelectedIndex() + 1;
            }
        } else {
            deptId = cmbDepartment.getSelectedIndex() + 1;
        }

        String semSelected = (String) cmbSemester.getSelectedItem();
        String semesterFormatted = "Semester I";
        if (semSelected != null) {
            if (semSelected.contains("2") || semSelected.contains("II")) {
                semesterFormatted = "Semester II";
            }
        }

        BigDecimal totalCredits = theory.add(practical);

        try {
            if (editCourse == null) {
                Course c = new Course();
                c.setCourseCode(code);
                c.setCourseName(name);
                c.setTheoryCredits(theory);
                c.setPracticalCredits(practical);
                c.setTotalCredits(totalCredits);
                c.setCaWeight(ca);
                c.setFinalWeight(finalW);
                c.setDepartmentId(deptId);
                c.setAcademicYear(txtAcademicYear.getText().trim());
                c.setSemester(semesterFormatted);
                c.setStatus((String) cmbStatus.getSelectedItem());

                boolean success = courseDAO.createCourse(c);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Course '" + code + "' registered successfully!", "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Could not save course. Database operation returned false.", "Save Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                editCourse.setCourseCode(code);
                editCourse.setCourseName(name);
                editCourse.setTheoryCredits(theory);
                editCourse.setPracticalCredits(practical);
                editCourse.setTotalCredits(totalCredits);
                editCourse.setCaWeight(ca);
                editCourse.setFinalWeight(finalW);
                editCourse.setDepartmentId(deptId);
                editCourse.setAcademicYear(txtAcademicYear.getText().trim());
                editCourse.setSemester(semesterFormatted);
                editCourse.setStatus((String) cmbStatus.getSelectedItem());

                boolean success = courseDAO.updateCourse(editCourse);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Course '" + code + "' updated successfully!", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Could not update course. Record may no longer exist.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (SQLException ex) {
            String msg = ex.getMessage();
            if (msg != null && msg.toLowerCase().contains("duplicate")) {
                JOptionPane.showMessageDialog(this, 
                    "A course with Course Code '" + code + "' already exists in the system. Please enter a unique Course Code.",
                    "Duplicate Course Code", JOptionPane.WARNING_MESSAGE);
            } else if (msg != null && msg.toLowerCase().contains("foreign key")) {
                JOptionPane.showMessageDialog(this, 
                    "Department ID " + deptId + " does not exist in the database.Please select a valid Department.",
                    "Department Not Found", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Database error while saving course:" + msg,
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "An unexpected error occurred while saving the course:" + ex.getMessage(),
                "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
