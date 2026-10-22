
package gui.lecturer.LecturerMarksGrades.EligibilityPanel;

import dao.Lecturer.EligibilityDAO;
import dao.Lecturer.MarksGradesDAO;
import gui.lecturer.LecturerMarksGrades.EligibilityPanel.EligibilityPanelComponents.EligibilityTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EligibilityPanel extends JPanel {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private static final Color BORDER_GREEN =
            new Color(200, 230, 201);

    private final long lecturerUserId;

    private final JComboBox<CourseItem> courseCombo;
    private final JLabel statusLabel;
    private final EligibilityTable eligibilityTable;

    private final MarksGradesDAO marksGradesDAO;
    private final EligibilityDAO eligibilityDAO;

    public EligibilityPanel(long lecturerUserId) {
        this.lecturerUserId = lecturerUserId;

        marksGradesDAO = new MarksGradesDAO();
        eligibilityDAO = new EligibilityDAO();

        setLayout(new BorderLayout(0, 18));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // Page heading
        JLabel title = new JLabel("Exam Eligibility");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(DEEP_GREEN);

        JLabel description = new JLabel(
                "Check student attendance, CA percentage, and final exam eligibility."
        );
        description.setFont(new Font("SansSerif", Font.PLAIN, 13));
        description.setForeground(Color.DARK_GRAY);

        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(Color.WHITE);
        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(description);

        // Course selection
        courseCombo = new JComboBox<>();
        courseCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        courseCombo.setPreferredSize(new Dimension(320, 38));
        courseCombo.setBackground(Color.WHITE);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFocusPainted(false);
        refreshButton.setBackground(PALE_GREEN);
        refreshButton.setForeground(DEEP_GREEN);
        refreshButton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_GREEN),
                        new EmptyBorder(8, 18, 8, 18)
                )
        );

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filters.setBackground(Color.WHITE);
        filters.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_GREEN),
                        new EmptyBorder(8, 10, 8, 10)
                )
        );

        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setForeground(DEEP_GREEN);
        courseLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        filters.add(courseLabel);
        filters.add(courseCombo);
        filters.add(refreshButton);

        // Requirements information
        JLabel requirements = new JLabel(
                "Requirements: Attendance ≥ 80%  |  CA Percentage ≥ 40%"
        );
        requirements.setFont(new Font("SansSerif", Font.BOLD, 13));
        requirements.setForeground(DEEP_GREEN);
        requirements.setOpaque(true);
        requirements.setBackground(PALE_GREEN);
        requirements.setBorder(new EmptyBorder(12, 14, 12, 14));

        statusLabel = new JLabel("Select a course to view eligibility.");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        eligibilityTable = new EligibilityTable();

        JPanel tableSection = new JPanel(new BorderLayout(0, 8));
        tableSection.setBackground(Color.WHITE);
        tableSection.add(requirements, BorderLayout.NORTH);
        tableSection.add(eligibilityTable, BorderLayout.CENTER);

        JPanel topSection = new JPanel(new BorderLayout(0, 14));
        topSection.setBackground(Color.WHITE);
        topSection.add(heading, BorderLayout.NORTH);

        JPanel filterSection = new JPanel(new BorderLayout(0, 8));
        filterSection.setBackground(Color.WHITE);
        filterSection.add(filters, BorderLayout.NORTH);
        filterSection.add(statusLabel, BorderLayout.SOUTH);

        topSection.add(filterSection, BorderLayout.CENTER);

        add(topSection, BorderLayout.NORTH);
        add(tableSection, BorderLayout.CENTER);

        courseCombo.addActionListener(e -> loadEligibility());

        refreshButton.addActionListener(e -> {
            if (courseCombo.getSelectedItem() == null) {
                loadCourses();
            } else {
                loadEligibility();
            }
        });

        loadCourses();
    }

    private void loadCourses() {
        courseCombo.removeAllItems();
        eligibilityTable.clearRows();

        try {
            List<Object[]> courses =
                    marksGradesDAO.getAssignedCourses(lecturerUserId);

            for (Object[] course : courses) {
                int courseId = ((Number) course[0]).intValue();
                String courseCode = String.valueOf(course[1]);
                String courseName = String.valueOf(course[2]);

                courseCombo.addItem(
                        new CourseItem(courseId, courseCode, courseName)
                );
            }

            if (courseCombo.getItemCount() == 0) {
                statusLabel.setText("No assigned courses found.");
            }

        } catch (SQLException ex) {
            showError("Could not load assigned courses.", ex);
        }
    }

    private void loadEligibility() {
        CourseItem selected =
                (CourseItem) courseCombo.getSelectedItem();

        if (selected == null) {
            eligibilityTable.clearRows();
            return;
        }

        try {
            List<Object[]> rows =
                    eligibilityDAO.getEligibilityRows(selected.courseId);

            eligibilityTable.setRows(rows);

            statusLabel.setText(
                    rows.size() + " student record(s) found for "
                            + selected.courseCode + "."
            );

            if (rows.isEmpty()) {
                statusLabel.setText(
                        "No eligibility records found for this course."
                );
            }

        } catch (SQLException ex) {
            eligibilityTable.clearRows();
            showError("Could not load eligibility records.", ex);
        }
    }

    private void showError(String message, Exception ex) {
        statusLabel.setText(message);
        ex.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                message + "\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private static class CourseItem {
        private final int courseId;
        private final String courseCode;
        private final String courseName;

        private CourseItem(
                int courseId,
                String courseCode,
                String courseName
        ) {
            this.courseId = courseId;
            this.courseCode = courseCode;
            this.courseName = courseName;
        }

        @Override
        public String toString() {
            return courseCode + " - " + courseName;
        }
    }
}
