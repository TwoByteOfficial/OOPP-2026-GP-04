
package gui.lecturer.LecturerMarksGrades.CASummaryPanel;

import dao.Lecturer.CASummaryDAO;
import dao.Lecturer.MarksGradesDAO;
import gui.lecturer.LecturerMarksGrades.CASummaryPanel.CASummaryPanelComponents.CASummaryTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CASummaryPanel extends JPanel {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private static final Color BORDER_GREEN =
            new Color(200, 230, 201);

    private final long lecturerUserId;

    private final MarksGradesDAO marksGradesDAO =
            new MarksGradesDAO();

    private final CASummaryDAO caSummaryDAO =
            new CASummaryDAO();

    private final JComboBox<CourseItem> courseSelector =
            new JComboBox<>();

    private final CASummaryTable summaryTable =
            new CASummaryTable();

    private final JLabel statusLabel =
            new JLabel("Select a course to view CA results.");

    private final JButton refreshButton =
            new JButton("Refresh");

    private boolean loadingCourses = false;

    public CASummaryPanel(long lecturerUserId) {

        this.lecturerUserId = lecturerUserId;

        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(20, 5, 5, 5));
        setBackground(Color.WHITE);

        add(createHeader(), BorderLayout.NORTH);
        add(createTableSection(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        loadCourses();
    }

    private JPanel createHeader() {

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);

        JLabel title =
                new JLabel("Continuous Assessment Summary");

        title.setFont(new Font("SansSerif", Font.BOLD, 19));
        title.setForeground(DEEP_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel description = new JLabel(
                "View student CA contributions, percentages, and eligibility."
        );

        description.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        description.setForeground(Color.DARK_GRAY);
        description.setBorder(new EmptyBorder(7, 0, 15, 0));
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel controls = new JPanel(new FlowLayout(
                FlowLayout.LEFT, 10, 5
        ));

        controls.setBackground(Color.WHITE);
        controls.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        courseSelector.setPreferredSize(new Dimension(300, 36));
        courseSelector.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        refreshButton.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        refreshButton.setForeground(DEEP_GREEN);
        refreshButton.setBackground(PALE_GREEN);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_GREEN),
                        new EmptyBorder(8, 15, 8, 15)
                )
        );
        refreshButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        controls.add(courseLabel);
        controls.add(courseSelector);
        controls.add(refreshButton);

        header.add(title);
        header.add(description);
        header.add(controls);

        courseSelector.addActionListener(e -> {
            if (!loadingCourses) {
                loadSelectedCourse();
            }
        });

        refreshButton.addActionListener(e -> {
            loadSelectedCourse();
        });

        return header;
    }

    private JPanel createTableSection() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(
                BorderFactory.createLineBorder(BORDER_GREEN)
        );

        JScrollPane scrollPane = new JScrollPane(summaryTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFooter() {

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(5, 2, 2, 2));

        statusLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );
        statusLabel.setForeground(DEEP_GREEN);

        footer.add(statusLabel, BorderLayout.WEST);

        return footer;
    }

    private void loadCourses() {

        loadingCourses = true;
        courseSelector.removeAllItems();
        summaryTable.clearRows();

        try {
            List<Object[]> courses =
                    marksGradesDAO.getAssignedCourses(lecturerUserId);

            for (Object[] course : courses) {
                int courseId =
                        ((Number) course[0]).intValue();

                String courseCode = String.valueOf(course[1]);
                String courseName = String.valueOf(course[2]);

                courseSelector.addItem(
                        new CourseItem(
                                courseId,
                                courseCode,
                                courseName
                        )
                );
            }

            if (courses.isEmpty()) {
                statusLabel.setText(
                        "No assigned courses were found."
                );
            }

        } catch (SQLException ex) {
            statusLabel.setText("Unable to load courses.");
            showDatabaseError(ex);
        } finally {
            loadingCourses = false;
        }

        if (courseSelector.getItemCount() > 0) {
            courseSelector.setSelectedIndex(0);
            loadSelectedCourse();
        }
    }

    private void loadSelectedCourse() {

        CourseItem selected =
                (CourseItem) courseSelector.getSelectedItem();

        if (selected == null) {
            summaryTable.clearRows();
            return;
        }

        refreshButton.setEnabled(false);
        statusLabel.setText("Loading CA summary...");

        try {
            List<Object[]> rows =
                    caSummaryDAO.getCASummary(selected.courseId);

            summaryTable.setRows(rows);

            statusLabel.setText(
                    "Students: " + rows.size()
                            + "  |  Course: " + selected.courseCode
            );

        } catch (SQLException ex) {
            summaryTable.clearRows();
            statusLabel.setText(
                    "Unable to load CA summary."
            );
            showDatabaseError(ex);
        } finally {
            refreshButton.setEnabled(true);
        }
    }

    private void showDatabaseError(SQLException ex) {

        ex.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                "Could not load the CA summary from the database.\n"
                        + "Please check the database connection and try again.",
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
