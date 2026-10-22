
package gui.lecturer.LecturerMarksGrades.FinalResultsPanel;

import dao.Lecturer.FinalResultsDAO;
import dao.Lecturer.MarksGradesDAO;
import gui.lecturer.LecturerMarksGrades.FinalResultsPanel.FinalResultsPanelComponents.FinalResultsTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class FinalResultsPanel extends JPanel {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private static final Color BORDER_GREEN =
            new Color(200, 230, 201);

    private final long lecturerUserId;

    private final JComboBox<CourseItem> courseCombo;
    private final JLabel statusLabel;
    private final JLabel totalStudentsLabel;
    private final JLabel eligibleLabel;
    private final JLabel notEligibleLabel;

    private final FinalResultsTable resultsTable;

    private final MarksGradesDAO marksGradesDAO;
    private final FinalResultsDAO finalResultsDAO;

    public FinalResultsPanel(long lecturerUserId) {
        this.lecturerUserId = lecturerUserId;

        marksGradesDAO = new MarksGradesDAO();
        finalResultsDAO = new FinalResultsDAO();

        setLayout(new BorderLayout(0, 16));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // Heading
        JLabel title = new JLabel("Final Results & GPA");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(DEEP_GREEN);

        JLabel subtitle = new JLabel(
                "View final marks, grades, SGPA and CGPA."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(Color.DARK_GRAY);

        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(Color.WHITE);
        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        // Course selector
        courseCombo = new JComboBox<>();
        courseCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        courseCombo.setPreferredSize(new Dimension(330, 38));
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

        JPanel filterPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_GREEN),
                        new EmptyBorder(8, 10, 8, 10)
                )
        );

        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        courseLabel.setForeground(DEEP_GREEN);

        filterPanel.add(courseLabel);
        filterPanel.add(courseCombo);
        filterPanel.add(refreshButton);

        // Summary cards
        totalStudentsLabel = new JLabel("Students: 0");
        eligibleLabel = new JLabel("Eligible: 0");
        notEligibleLabel = new JLabel("Not Eligible: 0");

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.add(createSummaryCard(totalStudentsLabel));
        summaryPanel.add(createSummaryCard(eligibleLabel));
        summaryPanel.add(createSummaryCard(notEligibleLabel));

        statusLabel = new JLabel("Select a course to view results.");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        statusLabel.setForeground(Color.DARK_GRAY);

        resultsTable = new FinalResultsTable();

        JPanel tableSection = new JPanel(new BorderLayout(0, 8));
        tableSection.setBackground(Color.WHITE);
        tableSection.add(statusLabel, BorderLayout.NORTH);
        tableSection.add(resultsTable, BorderLayout.CENTER);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);

        topPanel.add(heading);
        topPanel.add(Box.createVerticalStrut(16));
        topPanel.add(filterPanel);
        topPanel.add(Box.createVerticalStrut(12));
        topPanel.add(summaryPanel);

        add(topPanel, BorderLayout.NORTH);
        add(tableSection, BorderLayout.CENTER);

        courseCombo.addActionListener(e -> loadResults());

        refreshButton.addActionListener(e -> {
            if (courseCombo.getSelectedItem() == null) {
                loadCourses();
            } else {
                loadResults();
            }
        });

        loadCourses();
    }

    private JPanel createSummaryCard(JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PALE_GREEN);
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_GREEN),
                        new EmptyBorder(14, 12, 14, 12)
                )
        );

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        valueLabel.setForeground(DEEP_GREEN);

        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void loadCourses() {
        courseCombo.removeAllItems();
        resultsTable.clearRows();
        updateSummary(0, 0, 0);

        try {
            List<Object[]> courses =
                    marksGradesDAO.getAssignedCourses(lecturerUserId);

            for (Object[] course : courses) {
                int courseId =
                        ((Number) course[0]).intValue();

                String courseCode = String.valueOf(course[1]);
                String courseName = String.valueOf(course[2]);

                courseCombo.addItem(
                        new CourseItem(
                                courseId,
                                courseCode,
                                courseName
                        )
                );
            }

            if (courseCombo.getItemCount() == 0) {
                statusLabel.setText("No assigned courses found.");
            }

        } catch (SQLException ex) {
            showError("Could not load assigned courses.", ex);
        }
    }

    private void loadResults() {
        CourseItem selected =
                (CourseItem) courseCombo.getSelectedItem();

        if (selected == null) {
            resultsTable.clearRows();
            updateSummary(0, 0, 0);
            return;
        }

        try {
            List<Object[]> rows =
                    finalResultsDAO.getFinalResults(selected.courseId);

            resultsTable.setRows(rows);

            int eligibleCount = 0;
            int notEligibleCount = 0;

            for (Object[] row : rows) {
                String eligibility = String.valueOf(row[7]);

                if ("Eligible".equalsIgnoreCase(eligibility)) {
                    eligibleCount++;
                } else {
                    notEligibleCount++;
                }
            }

            updateSummary(
                    rows.size(),
                    eligibleCount,
                    notEligibleCount
            );

            statusLabel.setText(
                    rows.size() + " student result(s) for "
                            + selected.courseCode + "."
            );

            if (rows.isEmpty()) {
                statusLabel.setText(
                        "No final result records found for this course."
                );
            }

        } catch (SQLException ex) {
            resultsTable.clearRows();
            updateSummary(0, 0, 0);
            showError("Could not load final results.", ex);
        }
    }

    private void updateSummary(
            int total,
            int eligible,
            int notEligible
    ) {
        totalStudentsLabel.setText("Students: " + total);
        eligibleLabel.setText("Eligible: " + eligible);
        notEligibleLabel.setText("Not Eligible: " + notEligible);
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
