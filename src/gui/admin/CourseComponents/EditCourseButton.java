package gui.admin.CourseComponents;

import dao.admin.AdminCourseDAO;
import gui.admin.AdminTheme;
import model.Course;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Map;

/**
 * EditCourseButton - Integrated component file containing the "Edit Course" button
 * and its on-click modal dialog window for modifying course specifications and CA/END assessment weights.
 */
public class EditCourseButton extends JButton {

    public EditCourseButton(CourseTable table, Frame parent, AdminCourseDAO courseDAO, Runnable onSuccess) {
        super("✏️ Edit Course");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setBackground(AdminTheme.PASTEL_GREEN);
        setForeground(AdminTheme.DEEP_GREEN);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> {
            Course selected = table.getSelectedCourse();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a course module from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            EditCourseDialog dialog = new EditCourseDialog(parent, selected, courseDAO, onSuccess);
            dialog.setVisible(true);
        });
    }

    /**
     * EditCourseDialog - On-click popup window for modifying an existing course module.
     */
    public static class EditCourseDialog extends JDialog {

        private final Course courseToEdit;
        private final AdminCourseDAO courseDAO;
        private final Runnable onSuccess;

        private JTextField txtCode;
        private JTextField txtName;
        private JTextField txtTheoryCredits;
        private JTextField txtPracticalCredits;
        private JComboBox<String> cmbDepartment;
        private JTextField txtAcademicYear;
        private JComboBox<String> cmbSemester;
        private JComboBox<String> cmbStatus;

        // CA Weight Fields (%)
        private JTextField txtMax2QuizWeight;
        private JTextField txtAssignWeight;
        private JTextField txtMidTheoryWeight;
        private JTextField txtMidPracticalWeight;

        // END Weight Fields (%)
        private JTextField txtEndTheoryWeight;
        private JTextField txtEndPracticalWeight;

        // Computed Summary Labels
        private JLabel lblComputedCaWeight;
        private JLabel lblComputedFinalWeight;
        private JLabel lblComputedTotalWeight;

        public EditCourseDialog(Frame parent, Course course, AdminCourseDAO dao, Runnable onSuccess) {
            super(parent, "Modify Course Module Details: " + course.getCourseCode(), true);
            this.courseToEdit = course;
            this.courseDAO = dao;
            this.onSuccess = onSuccess;

            setSize(880, 760);
            setLocationRelativeTo(parent);
            setResizable(false);
            getContentPane().setBackground(AdminTheme.LIGHT_BG);
            setLayout(new BorderLayout());

            initUI();
            populateForm();
        }

        private void initUI() {
            // Header Panel
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 16));
            headerPanel.setBackground(AdminTheme.DEEP_GREEN);

            JLabel lblHeader = new JLabel("Modify Course Module Details");
            lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblHeader.setForeground(AdminTheme.WHITE);
            headerPanel.add(lblHeader);
            add(headerPanel, BorderLayout.NORTH);

            // Scrollable Content Panel
            JPanel mainContent = new JPanel();
            mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
            mainContent.setBackground(AdminTheme.LIGHT_BG);
            mainContent.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

            // Section 1: Base Module Details
            JPanel pnlBase = new JPanel(new GridBagLayout());
            pnlBase.setBackground(AdminTheme.WHITE);
            pnlBase.setBorder(createTitledBorder("1. Basic Module Specifications"));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 10, 6, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Row 0
            addGridRow(pnlBase, gbc, 0, "Course Code:", txtCode = createTextField(""), "Course Name:", txtName = createTextField(""));
            // Row 1
            addGridRow(pnlBase, gbc, 1, "Theory Credits:", txtTheoryCredits = createTextField(""), "Practical Credits:", txtPracticalCredits = createTextField(""));
            // Row 2
            cmbDepartment = new JComboBox<>(new String[]{ "1 - ICT", "2 - Engineering Technology", "3 - Bio Systems Technology" });
            styleCombo(cmbDepartment);
            cmbSemester = new JComboBox<>(new String[]{ "Semester I", "Semester II" });
            styleCombo(cmbSemester);
            addGridRow(pnlBase, gbc, 2, "Department:", cmbDepartment, "Semester:", cmbSemester);
            // Row 3
            cmbStatus = new JComboBox<>(new String[]{ "Active", "Inactive" });
            styleCombo(cmbStatus);
            addGridRow(pnlBase, gbc, 3, "Academic Year:", txtAcademicYear = createTextField(""), "Status:", cmbStatus);

            mainContent.add(pnlBase);
            mainContent.add(Box.createVerticalStrut(14));

            // Section 2: Assessment Weight Breakdown (%)
            JPanel pnlWeights = new JPanel(new GridBagLayout());
            pnlWeights.setBackground(AdminTheme.WHITE);
            pnlWeights.setBorder(createTitledBorder("2. Course Assessment Structure & Weight Breakdown (%)"));

            DocumentListener calcListener = new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { recalculateWeights(); }
                public void removeUpdate(DocumentEvent e) { recalculateWeights(); }
                public void changedUpdate(DocumentEvent e) { recalculateWeights(); }
            };

            // CA Weight Fields (Max 2 Quiz, Assignment, Mid Theory, Mid Practical)
            txtMax2QuizWeight = createWeightField("10.0", calcListener);
            txtAssignWeight = createWeightField("10.0", calcListener);
            txtMidTheoryWeight = createWeightField("10.0", calcListener);
            txtMidPracticalWeight = createWeightField("10.0", calcListener);

            // END Weight Fields (End Exam Theory, End Exam Practical)
            txtEndTheoryWeight = createWeightField("40.0", calcListener);
            txtEndPracticalWeight = createWeightField("20.0", calcListener);

            // Row 0: CA Weights - Max 2 Quiz & Assignment
            addGridRow(pnlWeights, gbc, 0, "[CA] Max 2 Quiz Weight (%):", txtMax2QuizWeight, "[CA] Assignment Weight (%):", txtAssignWeight);
            // Row 1: CA Weights - Mid Exams
            addGridRow(pnlWeights, gbc, 1, "[CA] Mid Exam Theory (%):", txtMidTheoryWeight, "[CA] Mid Exam Practical (%):", txtMidPracticalWeight);
            // Row 2: END Weights - End Exams
            addGridRow(pnlWeights, gbc, 2, "[END] End Exam Theory (%):", txtEndTheoryWeight, "[END] End Exam Practical (%):", txtEndPracticalWeight);

            // Summary Breakdown Bar
            JPanel pnlSummary = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
            pnlSummary.setBackground(AdminTheme.MINT);
            pnlSummary.setBorder(BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1));

            lblComputedCaWeight = new JLabel("CA Weight: 40.0%");
            lblComputedCaWeight.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblComputedCaWeight.setForeground(AdminTheme.DEEP_GREEN);

            lblComputedFinalWeight = new JLabel("END Weight: 60.0%");
            lblComputedFinalWeight.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblComputedFinalWeight.setForeground(AdminTheme.DEEP_GREEN);

            lblComputedTotalWeight = new JLabel("Total Weight: 100.0%");
            lblComputedTotalWeight.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblComputedTotalWeight.setForeground(AdminTheme.FOREST_GREEN);

            pnlSummary.add(lblComputedCaWeight);
            pnlSummary.add(new JLabel(" | "));
            pnlSummary.add(lblComputedFinalWeight);
            pnlSummary.add(new JLabel(" | "));
            pnlSummary.add(lblComputedTotalWeight);

            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
            pnlWeights.add(pnlSummary, gbc);

            mainContent.add(pnlWeights);

            JScrollPane scrollPane = new JScrollPane(mainContent);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(12);
            add(scrollPane, BorderLayout.CENTER);

            // Footer Panel
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 14));
            footer.setBackground(AdminTheme.WHITE);
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AdminTheme.PASTEL_GREEN));

            JButton btnUpdate = new JButton("Update Course Module");
            btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btnUpdate.setBackground(AdminTheme.FOREST_GREEN);
            btnUpdate.setForeground(AdminTheme.WHITE);
            btnUpdate.setFocusPainted(false);
            btnUpdate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnUpdate.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
            btnUpdate.addActionListener(e -> updateCourse());

            JButton btnCancel = new JButton("Cancel");
            btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btnCancel.setBackground(AdminTheme.PASTEL_GREEN);
            btnCancel.setForeground(AdminTheme.DEEP_GREEN);
            btnCancel.setFocusPainted(false);
            btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnCancel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            btnCancel.addActionListener(e -> dispose());

            footer.add(btnUpdate);
            footer.add(btnCancel);
            add(footer, BorderLayout.SOUTH);
        }

        private void populateForm() {
            if (courseToEdit == null) return;

            txtCode.setText(courseToEdit.getCourseCode());
            txtName.setText(courseToEdit.getCourseName());
            txtTheoryCredits.setText(String.valueOf(courseToEdit.getTheoryCredits()));
            txtPracticalCredits.setText(String.valueOf(courseToEdit.getPracticalCredits()));
            txtAcademicYear.setText(courseToEdit.getAcademicYear());

            int deptIdx = courseToEdit.getDepartmentId() - 1;
            if (deptIdx >= 0 && deptIdx < cmbDepartment.getItemCount()) {
                cmbDepartment.setSelectedIndex(deptIdx);
            }

            if ("Semester II".equalsIgnoreCase(courseToEdit.getSemester()) || "Semester 2".equalsIgnoreCase(courseToEdit.getSemester())) {
                cmbSemester.setSelectedIndex(1);
            } else {
                cmbSemester.setSelectedIndex(0);
            }

            if ("Inactive".equalsIgnoreCase(courseToEdit.getStatus())) {
                cmbStatus.setSelectedIndex(1);
            } else {
                cmbStatus.setSelectedIndex(0);
            }

            // Populate Assessment Weights from DB
            if (courseDAO != null) {
                Map<String, BigDecimal> weights = courseDAO.getCourseAssessmentWeights(courseToEdit.getCourseId());
                txtMax2QuizWeight.setText(getWeightStr(weights, "quiz"));
                txtAssignWeight.setText(getWeightStr(weights, "assignment"));
                txtMidTheoryWeight.setText(getWeightStr(weights, "midTheory"));
                txtMidPracticalWeight.setText(getWeightStr(weights, "midPractical"));
                txtEndTheoryWeight.setText(getWeightStr(weights, "endTheory"));
                txtEndPracticalWeight.setText(getWeightStr(weights, "endPractical"));
            }

            recalculateWeights();
        }

        private String getWeightStr(Map<String, BigDecimal> map, String key) {
            if (map != null && map.containsKey(key)) {
                return map.get(key).toString();
            }
            return "0.0";
        }

        private JTextField createWeightField(String defaultVal, DocumentListener listener) {
            JTextField tf = createTextField(defaultVal);
            tf.getDocument().addDocumentListener(listener);
            return tf;
        }

        private void recalculateWeights() {
            try {
                BigDecimal q = parseDecimal(txtMax2QuizWeight.getText());
                BigDecimal a = parseDecimal(txtAssignWeight.getText());
                BigDecimal mt = parseDecimal(txtMidTheoryWeight.getText());
                BigDecimal mp = parseDecimal(txtMidPracticalWeight.getText());
                BigDecimal et = parseDecimal(txtEndTheoryWeight.getText());
                BigDecimal ep = parseDecimal(txtEndPracticalWeight.getText());

                // CA Weight = Max 2 Quiz + Assignment + Mid Theory + Mid Practical
                BigDecimal ca = q.add(a).add(mt).add(mp);
                // END Weight = End Exam Theory + End Exam Practical
                BigDecimal end = et.add(ep);
                BigDecimal total = ca.add(end);

                lblComputedCaWeight.setText("CA Weight: " + ca + "%");
                lblComputedFinalWeight.setText("END Weight: " + end + "%");
                lblComputedTotalWeight.setText("Total Weight: " + total + "%");

                if (total.compareTo(new BigDecimal("100.00")) == 0) {
                    lblComputedTotalWeight.setForeground(AdminTheme.FOREST_GREEN);
                } else {
                    lblComputedTotalWeight.setForeground(AdminTheme.DANGER);
                }
            } catch (Exception ignored) {
            }
        }

        private BigDecimal parseDecimal(String text) {
            try {
                if (text == null || text.trim().isEmpty()) return BigDecimal.ZERO;
                return new BigDecimal(text.trim());
            } catch (Exception e) {
                return BigDecimal.ZERO;
            }
        }

        private void updateCourse() {
            String code = txtCode.getText().trim();
            String name = txtName.getText().trim();

            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course Code and Course Name are required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                BigDecimal theory = parseDecimal(txtTheoryCredits.getText());
                BigDecimal practical = parseDecimal(txtPracticalCredits.getText());
                BigDecimal totalCredits = theory.add(practical);

                BigDecimal quizW = parseDecimal(txtMax2QuizWeight.getText());
                BigDecimal assignW = parseDecimal(txtAssignWeight.getText());
                BigDecimal midTheoryW = parseDecimal(txtMidTheoryWeight.getText());
                BigDecimal midPracticalW = parseDecimal(txtMidPracticalWeight.getText());
                BigDecimal endTheoryW = parseDecimal(txtEndTheoryWeight.getText());
                BigDecimal endPracticalW = parseDecimal(txtEndPracticalWeight.getText());

                // CA Weight = Max 2 Quiz + Assignment + Mid Theory + Mid Practical
                BigDecimal caWeight = quizW.add(assignW).add(midTheoryW).add(midPracticalW);
                // END Weight = End Exam Theory + End Exam Practical
                BigDecimal finalWeight = endTheoryW.add(endPracticalW);

                int deptId = cmbDepartment.getSelectedIndex() + 1;
                String semester = (String) cmbSemester.getSelectedItem();
                String status = (String) cmbStatus.getSelectedItem();

                courseToEdit.setCourseCode(code);
                courseToEdit.setCourseName(name);
                courseToEdit.setTheoryCredits(theory);
                courseToEdit.setPracticalCredits(practical);
                courseToEdit.setTotalCredits(totalCredits);
                courseToEdit.setCaWeight(caWeight);
                courseToEdit.setFinalWeight(finalWeight);
                courseToEdit.setDepartmentId(deptId);
                courseToEdit.setAcademicYear(txtAcademicYear.getText().trim());
                courseToEdit.setSemester(semester);
                courseToEdit.setStatus(status);

                boolean success = courseDAO.updateCourse(courseToEdit, quizW, assignW, midTheoryW, midPracticalW, endTheoryW, endPracticalW);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Course '" + code + "' updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    if (onSuccess != null) onSuccess.run();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update course module in database.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating course: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void addGridRow(JPanel pnl, GridBagConstraints gbc, int row, String lbl1, Component c1, String lbl2, Component c2) {
            gbc.gridy = row;

            gbc.gridx = 0; gbc.weightx = 0.15;
            JLabel l1 = new JLabel(lbl1);
            l1.setFont(new Font("Segoe UI", Font.BOLD, 14));
            l1.setForeground(AdminTheme.DEEP_GREEN);
            pnl.add(l1, gbc);

            gbc.gridx = 1; gbc.weightx = 0.35;
            pnl.add(c1, gbc);

            gbc.gridx = 2; gbc.weightx = 0.15;
            JLabel l2 = new JLabel(lbl2);
            l2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            l2.setForeground(AdminTheme.DEEP_GREEN);
            pnl.add(l2, gbc);

            gbc.gridx = 3; gbc.weightx = 0.35;
            pnl.add(c2, gbc);
        }

        private JTextField createTextField(String defaultVal) {
            JTextField tf = new JTextField(defaultVal);
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            tf.setPreferredSize(new Dimension(200, 36));
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            return tf;
        }

        private void styleCombo(JComboBox<String> combo) {
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            combo.setPreferredSize(new Dimension(200, 36));
            combo.setBackground(AdminTheme.WHITE);
        }

        private TitledBorder createTitledBorder(String title) {
            TitledBorder tb = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1), title);
            tb.setTitleFont(new Font("Segoe UI", Font.BOLD, 15));
            tb.setTitleColor(AdminTheme.DEEP_GREEN);
            return tb;
        }
    }
}
