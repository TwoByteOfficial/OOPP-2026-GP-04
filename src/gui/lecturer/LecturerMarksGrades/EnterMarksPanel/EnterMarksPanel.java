
package gui.lecturer.LecturerMarksGrades.EnterMarksPanel;

import dao.Lecturer.MarksGradesDAO;
import gui.lecturer.LecturerMarksGrades.EnterMarksPanel.EnterMarksPanelComponents.MarksAssessmentSelector;
import gui.lecturer.LecturerMarksGrades.EnterMarksPanel.EnterMarksPanelComponents.MarksCourseSelector;
import gui.lecturer.LecturerMarksGrades.EnterMarksPanel.EnterMarksPanelComponents.MarksStudentsTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class EnterMarksPanel extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);

    private final long lecturerUserId;
    private final MarksGradesDAO dao = new MarksGradesDAO();

    private final MarksCourseSelector courseSelector =
            new MarksCourseSelector();

    private final MarksAssessmentSelector assessmentSelector =
            new MarksAssessmentSelector();

    private final MarksStudentsTable studentsTable =
            new MarksStudentsTable();

    private final JLabel messageLabel = new JLabel("Ready");

    private final JButton refreshButton = new JButton("Refresh");
    private final JButton saveButton = new JButton("Save Marks");

    private boolean loadingAssessments;

    public EnterMarksPanel(long lecturerUserId) {
        this.lecturerUserId = lecturerUserId;

        setLayout(new BorderLayout(0, 18));
        setBorder(new EmptyBorder(20, 5, 5, 5));
        setBackground(Color.WHITE);

        add(createTopPanel(), BorderLayout.NORTH);
        add(studentsTable, BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        courseSelector.addSelectionListener(e -> loadAssessments());

        assessmentSelector.addSelectionListener(e -> {
            if (!loadingAssessments) {
                loadStudents();
            }
        });

        styleButton(refreshButton, false);
        styleButton(saveButton, true);

        loadCourses();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JLabel title = new JLabel("Enter Assessment Marks");
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        title.setForeground(DEEP_GREEN);

        JPanel selectors = new JPanel(new GridLayout(1, 2, 18, 0));
        selectors.setOpaque(false);
        selectors.add(courseSelector);
        selectors.add(assessmentSelector);

        panel.add(title, BorderLayout.NORTH);
        panel.add(selectors, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);

        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        messageLabel.setForeground(new Color(90, 90, 90));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(refreshButton);
        buttons.add(saveButton);

        refreshButton.addActionListener(e -> {
            if (courseSelector.getSelectedCourseId() >= 0
                    && assessmentSelector.getSelectedAssessmentId() >= 0) {
                loadStudents();
            } else {
                loadCourses();
            }
        });

        saveButton.addActionListener(e -> saveMarks());

        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.EAST);

        return panel;
    }

    private void styleButton(JButton button, boolean primary) {
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(primary ? 125 : 95, 36));

        if (primary) {
            button.setBackground(DEEP_GREEN);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(new Color(232, 245, 233));
            button.setForeground(DEEP_GREEN);
        }

        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    private void loadCourses() {
        try {
            List<Object[]> courses = dao.getAssignedCourses(lecturerUserId);

            loadingAssessments = true;
            assessmentSelector.setAssessments(Collections.emptyList());
            loadingAssessments = false;

            studentsTable.setStudents(Collections.emptyList());
            courseSelector.setCourses(courses);

            if (courses.isEmpty()) {
                messageLabel.setText("No assigned courses were found.");
                return;
            }

            loadAssessments();

        } catch (SQLException ex) {
            loadingAssessments = false;
            showError("Could not load courses.", ex);
        }
    }

    private void loadAssessments() {
        int courseId = courseSelector.getSelectedCourseId();

        if (courseId < 0) {
            loadingAssessments = true;
            assessmentSelector.setAssessments(Collections.emptyList());
            loadingAssessments = false;
            studentsTable.setStudents(Collections.emptyList());
            messageLabel.setText("Please select a course.");
            return;
        }

        try {
            loadingAssessments = true;

            List<Object[]> assessments =
                    dao.getAssessments(lecturerUserId, courseId);

            assessmentSelector.setAssessments(assessments);
            loadingAssessments = false;

            if (assessments.isEmpty()) {
                studentsTable.setStudents(Collections.emptyList());
                messageLabel.setText(
                        "No assessments found for this course."
                );
                return;
            }

            loadStudents();

        } catch (SQLException ex) {
            loadingAssessments = false;
            studentsTable.setStudents(Collections.emptyList());
            showError("Could not load assessments.", ex);
        }
    }

    private void loadStudents() {
        int courseId = courseSelector.getSelectedCourseId();
        long assessmentId = assessmentSelector.getSelectedAssessmentId();

        if (courseId < 0 || assessmentId < 0) {
            studentsTable.setStudents(Collections.emptyList());
            return;
        }

        try {
            List<Object[]> students = dao.getStudentsForAssessment(
                    lecturerUserId, courseId, assessmentId
            );

            studentsTable.setStudents(students);
            messageLabel.setText(students.size() + " student(s) loaded.");

        } catch (SQLException ex) {
            showError("Could not load students.", ex);
        }
    }

    private void saveMarks() {
        studentsTable.stopEditing();

        int courseId = courseSelector.getSelectedCourseId();
        long assessmentId = assessmentSelector.getSelectedAssessmentId();

        if (courseId < 0 || assessmentId < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a course and assessment.",
                    "Missing Selection",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int savedCount = 0;
        int skippedCount = 0;

        try {
            for (int row = 0; row < studentsTable.getStudentCount(); row++) {
                Object value = studentsTable.getMarkAt(row);

                if (value == null || value.toString().trim().isEmpty()) {
                    skippedCount++;
                    continue;
                }

                BigDecimal mark;

                try {
                    mark = new BigDecimal(value.toString().trim());
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException(
                            "Invalid mark in row " + (row + 1)
                                    + ". Please enter a number."
                    );
                }

                if (mark.compareTo(BigDecimal.ZERO) < 0
                        || mark.compareTo(new BigDecimal("100")) > 0) {
                    throw new IllegalArgumentException(
                            "Marks must be between 0 and 100. "
                                    + "Check row " + (row + 1) + "."
                    );
                }

                long studentId = studentsTable.getStudentIdAt(row);

                dao.saveMark(
                        lecturerUserId,
                        courseId,
                        assessmentId,
                        studentId,
                        mark
                );

                studentsTable.setStatusAt(row, "Saved");
                savedCount++;
            }

            loadStudents();

            JOptionPane.showMessageDialog(
                    this,
                    "Marks saved: " + savedCount
                            + "\nBlank rows skipped: " + skippedCount,
                    "Save Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Invalid Mark",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException ex) {
            showError(
                    "A database error occurred while saving marks. "
                            + "Some earlier rows may already have been saved.",
                    ex
            );
        }
    }

    private void showError(String message, Exception ex) {
        ex.printStackTrace();
        messageLabel.setText(message);

        JOptionPane.showMessageDialog(
                this,
                message + "\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
