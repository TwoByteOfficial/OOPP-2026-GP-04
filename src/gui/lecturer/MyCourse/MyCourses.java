
package gui.lecturer.MyCourse;

import gui.lecturer.MyCourse.MyCourseComponents.EnrolledStudentDialog;
import gui.lecturer.MyCourse.MyCourseComponents.MyCourseTable;
import gui.lecturer.MyCourse.MyCourseComponents.MyCourseTitleBar;
import gui.lecturer.MyCourse.MyCourseComponents.MyCourseLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MyCourses extends JPanel {

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private final long lecturerUserId;

    private final MyCourseLoader courseLoader = new MyCourseLoader();

    private final MyCourseTable courseTable;

    private final JLabel statusLabel;

    public MyCourses(long lecturerUserId) {

        this.lecturerUserId = lecturerUserId;

        setLayout(new BorderLayout(0, 20));
        setBackground(PALE_GREEN);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Page heading
        MyCourseTitleBar heading = new MyCourseTitleBar();

        // Course table
        courseTable = new MyCourseTable(
                this::showEnrolledStudents
        );

        // Status label
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        refreshButton.addActionListener(
                event -> loadCourses()
        );

        // Footer
        JPanel footer = new JPanel(
                new BorderLayout(10, 0)
        );

        footer.setOpaque(false);
        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(refreshButton, BorderLayout.EAST);

        // Main page layout
        add(heading, BorderLayout.NORTH);
        add(courseTable, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        loadCourses();
    }

    // Load assigned courses from the database.
    private void loadCourses() {

        statusLabel.setText("Loading courses...");
        courseTable.clearCourses();

        try {

            List<Object[]> courses =
                    courseLoader.loadCourses(lecturerUserId);

            for (Object[] course : courses) {

                courseTable.addCourse(new Object[]{
                        course[0],             // Hidden course ID
                        course[1],             // Course code
                        course[2],             // Course name
                        course[3],             // Total credits
                        course[4],             // Theory credits
                        course[5],             // Practical credits
                        course[6] == null
                                ? "N/A"
                                : course[6] + "%",
                        course[7],             // Enrolled students
                        "Show Details"                 // Action button
                });
            }

            statusLabel.setText(
                    "Total assigned courses: " + courses.size()
            );

        } catch (SQLException exception) {

            statusLabel.setText("Unable to load courses.");

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load your assigned courses.\n"
                            + exception.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            exception.printStackTrace();
        }
    }

    // Open the enrolled-students dialog.
    private void showEnrolledStudents(int modelRow) {

        DefaultTableModel tableModel =
                courseTable.getTableModel();

        int courseId = ((Number) tableModel.getValueAt(
                modelRow, 0
        )).intValue();

        String courseCode = tableModel.getValueAt(
                modelRow, 1
        ).toString();

        String courseName = tableModel.getValueAt(
                modelRow, 2
        ).toString();

        EnrolledStudentDialog studentDialog =
                new EnrolledStudentDialog();

        studentDialog.show(
                this,
                courseId,
                courseCode,
                courseName
        );
    }
}