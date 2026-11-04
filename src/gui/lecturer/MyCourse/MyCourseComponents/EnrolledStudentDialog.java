
package gui.lecturer.MyCourse.MyCourseComponents;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EnrolledStudentDialog {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private final EnrolledStudentLoader studentLoader =
            new EnrolledStudentLoader();
    public void show(
            Component parent,
            int courseId,
            String courseCode,
            String courseName
    ) {

        Window owner = SwingUtilities.getWindowAncestor(parent);

        JDialog dialog = new JDialog(
                owner,
                courseCode + " - Enrolled Undergraduates",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        dialog.setLayout(new BorderLayout(0, 15));
        dialog.getContentPane().setBackground(PALE_GREEN);

        // Dialog heading
        JPanel heading = new JPanel();

        heading.setLayout(
                new BoxLayout(heading, BoxLayout.Y_AXIS)
        );

        heading.setBackground(PALE_GREEN);
        heading.setBorder(
                new EmptyBorder(20, 20, 0, 20)
        );

        JLabel title = new JLabel(
                courseCode + " - Enrolled Undergraduates"
        );

        title.setFont(
                new Font("SansSerif", Font.BOLD, 22)
        );

        title.setForeground(DEEP_GREEN);

        JLabel subtitle = new JLabel(courseName);

        subtitle.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        heading.add(title);
        heading.add(Box.createVerticalStrut(8));
        heading.add(subtitle);

        // Student table
        EnrolledStudentTable studentTable =
                new EnrolledStudentTable();

        JScrollPane studentScrollPane =
                new JScrollPane(studentTable);

        studentScrollPane.setBorder(
                new EmptyBorder(0, 20, 0, 20)
        );

        // Student count
        JLabel studentCount = new JLabel(
                "Loading students..."
        );

        studentCount.setBorder(
                new EmptyBorder(0, 20, 0, 0)
        );

        // Load students
        try {

            List<Object[]> students =
                    studentLoader.loadStudents(courseId);

            for (Object[] student : students) {
                studentTable.addStudent(student);
            }

            studentCount.setText(
                    "Total enrolled undergraduates: "
                            + students.size()
            );

        } catch (SQLException exception) {

            studentCount.setText(
                    "Unable to load students."
            );

            JOptionPane.showMessageDialog(
                    dialog,
                    "Could not load enrolled students.\n"
                            + exception.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            exception.printStackTrace();
        }

        // Close button
        JButton closeButton = new JButton("Close");

        closeButton.setFocusPainted(false);
        closeButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        closeButton.addActionListener(
                event -> dialog.dispose()
        );

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 10)
        );

        buttons.setOpaque(false);
        buttons.setBorder(
                new EmptyBorder(0, 10, 10, 20)
        );

        buttons.add(closeButton);

        // Footer
        JPanel footer = new JPanel(
                new BorderLayout()
        );

        footer.setOpaque(false);
        footer.add(studentCount, BorderLayout.WEST);
        footer.add(buttons, BorderLayout.EAST);

        // Assemble dialog
        dialog.add(heading, BorderLayout.NORTH);
        dialog.add(studentScrollPane, BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);

        // Increased popup size
        dialog.setSize(1350, 950);

        dialog.setMinimumSize(
                new Dimension(1100, 700)
        );

        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
