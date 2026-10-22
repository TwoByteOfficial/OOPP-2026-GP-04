
package gui.lecturer.LecturerMarksGrades.EnterMarksPanel.EnterMarksPanelComponents;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class MarksCourseSelector extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color BORDER_GREEN = new Color(200, 230, 201);

    private final JComboBox<CourseOption> courseCombo;

    public MarksCourseSelector() {
        setLayout(new BorderLayout(0, 8));
        setOpaque(false);

        JLabel label = new JLabel("Select Course");
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(DEEP_GREEN);

        courseCombo = new JComboBox<>();
        courseCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        courseCombo.setBackground(Color.WHITE);
        courseCombo.setPreferredSize(new Dimension(320, 36));
        courseCombo.setBorder(BorderFactory.createLineBorder(BORDER_GREEN));

        add(label, BorderLayout.NORTH);
        add(courseCombo, BorderLayout.CENTER);
    }

    public void setCourses(List<Object[]> courses) {
        courseCombo.removeAllItems();

        for (Object[] course : courses) {
            int courseId = ((Number) course[0]).intValue();
            String courseCode = String.valueOf(course[1]);
            String courseName = String.valueOf(course[2]);

            courseCombo.addItem(
                    new CourseOption(courseId, courseCode, courseName)
            );
        }
    }

    public int getSelectedCourseId() {
        CourseOption selected =
                (CourseOption) courseCombo.getSelectedItem();

        return selected == null ? -1 : selected.courseId;
    }

    public void addSelectionListener(ActionListener listener) {
        courseCombo.addActionListener(listener);
    }

    private static class CourseOption {
        private final int courseId;
        private final String courseCode;
        private final String courseName;

        private CourseOption(int courseId, String courseCode,
                             String courseName) {
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
