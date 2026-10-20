package gui.admin.CourseComponents;

import model.Course;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CourseList extends JPanel {

    private CourseTable table;

    public CourseList() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        table = new CourseTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setCourses(List<Course> courses) { table.setCourses(courses); }
    public Course getSelectedCourse() { return table.getSelectedCourse(); }
    public CourseTable getTable() { return table; }
}
