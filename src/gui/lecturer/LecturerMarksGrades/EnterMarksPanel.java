package gui.lecturer.LecturerMarksGrades;

import dao.Lecturer.MarksGradesDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnterMarksPanel extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color PALE_GREEN = new Color(232, 245, 233);
    private static final Color BORDER_GREEN = new Color(200, 230, 201);
    private static final Color TEXT_GRAY = new Color(96, 112, 100);

    private final long lecturerId;
    private final MarksGradesDAO marksDAO = new MarksGradesDAO();

    private final JComboBox<String> courseCombo = new JComboBox<>();
    private final Map<String, Integer> courseMap = new HashMap<>();

    private final JComboBox<String> assessmentCombo = new JComboBox<>();
    private final Map<String, Long> assessmentMap = new HashMap<>();

    private final JLabel assessmentInfoLabel = new JLabel("Select a course and assessment to view marks.");
    private final DefaultTableModel tableModel;
    private final JTable studentTable;
    private final JLabel statusLabel = new JLabel("Ready");

    private boolean isUpdatingCombo = false;

    public EnterMarksPanel(long lecturerId) {
        this.lecturerId = lecturerId;

        setLayout(new BorderLayout(0, 15));
        setBackground(PALE_GREEN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Selection panel
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        selectorPanel.setOpaque(false);

        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        courseLabel.setForeground(DEEP_GREEN);

        courseCombo.setPreferredSize(new Dimension(280, 34));
        courseCombo.addActionListener(e -> {
            if (!isUpdatingCombo) {
                onCourseSelected();
            }
        });

        JLabel assessmentLabel = new JLabel("Select Assessment:");
        assessmentLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        assessmentLabel.setForeground(DEEP_GREEN);

        assessmentCombo.setPreferredSize(new Dimension(280, 34));
        assessmentCombo.addActionListener(e -> {
            if (!isUpdatingCombo) {
                loadStudentMarks();
            }
        });

        JButton loadBtn = new JButton("Load Students");
        loadBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        loadBtn.addActionListener(e -> loadStudentMarks());

        selectorPanel.add(courseLabel);
        selectorPanel.add(courseCombo);
        selectorPanel.add(assessmentLabel);
        selectorPanel.add(assessmentCombo);
        selectorPanel.add(loadBtn);

        assessmentInfoLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        assessmentInfoLabel.setForeground(TEXT_GRAY);

        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));
        headerPanel.setOpaque(false);
        headerPanel.add(selectorPanel, BorderLayout.NORTH);
        headerPanel.add(assessmentInfoLabel, BorderLayout.SOUTH);

        // Student Marks Table
        String[] columns = {"Student ID", "Reg. No.", "Full Name", "Mark", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only Mark column is editable
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(34);
        studentTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        studentTable.setSelectionBackground(PALE_GREEN);
        studentTable.setSelectionForeground(DEEP_GREEN);
        studentTable.setGridColor(BORDER_GREEN);
        studentTable.setFillsViewportHeight(true);

        studentTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        studentTable.getTableHeader().setBackground(PALE_GREEN);
        studentTable.getTableHeader().setForeground(DEEP_GREEN);

        // Hide Student ID column from view while retaining in model
        studentTable.removeColumn(studentTable.getColumnModel().getColumn(0));

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(new LineBorder(BORDER_GREEN, 1));

        // Footer & Save buttons
        JButton saveSelectedBtn = new JButton("Save Selected Row Mark");
        saveSelectedBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        saveSelectedBtn.setBackground(DEEP_GREEN);
        saveSelectedBtn.setForeground(Color.WHITE);
        saveSelectedBtn.setFocusPainted(false);
        saveSelectedBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveSelectedBtn.addActionListener(e -> saveSelectedMark());

        JButton saveAllBtn = new JButton("Save All Entered Marks");
        saveAllBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        saveAllBtn.setBackground(DEEP_GREEN);
        saveAllBtn.setForeground(Color.WHITE);
        saveAllBtn.setFocusPainted(false);
        saveAllBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveAllBtn.addActionListener(e -> saveAllMarks());

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);
        actionButtons.add(saveSelectedBtn);
        actionButtons.add(saveAllBtn);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(actionButtons, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        loadAssignedCourses();
    }

    private void loadAssignedCourses() {
        isUpdatingCombo = true;
        try {
            courseCombo.removeAllItems();
            courseMap.clear();

            List<Object[]> courses = marksDAO.getAssignedCourses(lecturerId);
            for (Object[] c : courses) {
                int courseId = (int) c[0];
                String courseCode = (String) c[1];
                String courseName = (String) c[2];
                String label = courseCode + " - " + courseName;
                courseCombo.addItem(label);
                courseMap.put(label, courseId);
            }
        } catch (SQLException e) {
            statusLabel.setText("Failed to load courses.");
            JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            isUpdatingCombo = false;
        }

        if (courseCombo.getItemCount() > 0) {
            onCourseSelected();
        }
    }

    private void onCourseSelected() {
        String selectedCourse = (String) courseCombo.getSelectedItem();
        Integer courseId = courseMap.get(selectedCourse);

        isUpdatingCombo = true;
        try {
            assessmentCombo.removeAllItems();
            assessmentMap.clear();
        } finally {
            isUpdatingCombo = false;
        }

        if (courseId == null) return;

        try {
            List<Object[]> assessments = marksDAO.getAssessments(lecturerId, courseId);
            isUpdatingCombo = true;
            try {
                for (Object[] a : assessments) {
                    long aId = (long) a[0];
                    String name = (String) a[1];
                    String category = (String) a[2];
                    BigDecimal maxMark = (BigDecimal) a[4];

                    String label = name + " [" + category + "] (Max: " + (maxMark != null ? maxMark.toPlainString() : "100") + ")";
                    assessmentCombo.addItem(label);
                    assessmentMap.put(label, aId);
                }
            } finally {
                isUpdatingCombo = false;
            }

            if (assessmentCombo.getItemCount() > 0) {
                loadStudentMarks();
            } else {
                tableModel.setRowCount(0);
                assessmentInfoLabel.setText("No assessment definitions found for this course.");
            }
        } catch (SQLException e) {
            statusLabel.setText("Failed to load assessments.");
            System.err.println("Error loading assessments: " + e.getMessage());
        }
    }

    private void loadStudentMarks() {
        String selectedCourse = (String) courseCombo.getSelectedItem();
        Integer courseId = courseMap.get(selectedCourse);

        String selectedAssessment = (String) assessmentCombo.getSelectedItem();
        Long assessmentId = assessmentMap.get(selectedAssessment);

        tableModel.setRowCount(0);

        if (courseId == null || assessmentId == null) return;

        assessmentInfoLabel.setText("Entering marks for: " + selectedAssessment);
        statusLabel.setText("Loading enrolled students...");

        try {
            List<Object[]> students = marksDAO.getStudentsForAssessment(lecturerId, courseId, assessmentId);
            for (Object[] s : students) {
                tableModel.addRow(new Object[]{
                        s[0], // student user_id
                        s[1], // reg_no
                        s[2], // full_name
                        s[3] == null ? "" : s[3].toString(), // mark
                        s[4]  // status
                });
            }
            statusLabel.setText("Loaded " + students.size() + " students.");
        } catch (SQLException e) {
            statusLabel.setText("Failed to load student marks.");
            JOptionPane.showMessageDialog(this, "Error loading student marks: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveSelectedMark() {
        int viewRow = studentTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student row first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert view index to model index to prevent sorting/filtering bugs
        int modelRow = studentTable.convertRowIndexToModel(viewRow);
        saveMarkForModelRow(modelRow, true);
    }

    private void saveAllMarks() {
        int successCount = 0;
        int errorCount = 0;
        StringBuilder errorDetails = new StringBuilder();

        for (int modelRow = 0; modelRow < tableModel.getRowCount(); modelRow++) {
            Object markVal = tableModel.getValueAt(modelRow, 3);
            if (markVal == null || markVal.toString().trim().isEmpty()) {
                continue; // Skip empty rows silently during batch save
            }

            try {
                if (saveMarkForModelRow(modelRow, false)) {
                    successCount++;
                } else {
                    errorCount++;
                }
            } catch (Exception e) {
                errorCount++;
                String regNo = (String) tableModel.getValueAt(modelRow, 1);
                errorDetails.append(regNo).append(": ").append(e.getMessage()).append("\n");
            }
        }

        if (errorCount > 0) {
            JOptionPane.showMessageDialog(this,
                    "Saved " + successCount + " marks successfully.\nFailed/skipped " + errorCount + " marks.\n" + errorDetails.toString(),
                    "Batch Save Results", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Processed and saved marks for " + successCount + " students.", "Save Successful", JOptionPane.INFORMATION_MESSAGE);
        }
        loadStudentMarks();
    }

    private boolean saveMarkForModelRow(int modelRow, boolean showSingleErrorDialog) {
        String selectedCourse = (String) courseCombo.getSelectedItem();
        Integer courseId = courseMap.get(selectedCourse);

        String selectedAssessment = (String) assessmentCombo.getSelectedItem();
        Long assessmentId = assessmentMap.get(selectedAssessment);

        if (courseId == null || assessmentId == null) return false;

        Object rawStudentId = tableModel.getValueAt(modelRow, 0);
        if (rawStudentId == null) return false;

        // Safe conversion of Object to primitive long
        long studentId;
        try {
            studentId = Long.parseLong(rawStudentId.toString());
        } catch (NumberFormatException e) {
            if (showSingleErrorDialog) {
                JOptionPane.showMessageDialog(this, "Invalid Student ID format.", "Data Error", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }

        String regNo = (String) tableModel.getValueAt(modelRow, 1);
        Object markVal = tableModel.getValueAt(modelRow, 3);

        if (markVal == null || markVal.toString().trim().isEmpty()) {
            return false;
        }

        try {
            BigDecimal mark = new BigDecimal(markVal.toString().trim());
            marksDAO.saveMark(lecturerId, courseId, assessmentId, studentId, mark);
            tableModel.setValueAt("Saved", modelRow, 4);
            statusLabel.setText("Saved mark for " + regNo);
            return true;
        } catch (NumberFormatException e) {
            if (showSingleErrorDialog) {
                JOptionPane.showMessageDialog(this, "Invalid mark format for student " + regNo + ". Must be a number.", "Format Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            if (showSingleErrorDialog) {
                JOptionPane.showMessageDialog(this, "Error saving mark for " + regNo + ": " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }
}