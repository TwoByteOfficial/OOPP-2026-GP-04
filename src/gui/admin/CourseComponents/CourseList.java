package gui.admin.CourseComponents;

import model.Course;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CourseList extends JPanel {

    private CourseTable table;

    public CourseList() {
        this(new CourseTable());
    }

    public CourseList(CourseTable table) {
        this.table = table != null ? table : new CourseTable();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setCourses(List<Course> courses) {
        table.setCourses(courses);
    }

    public Course getSelectedCourse() {
        return table.getSelectedCourse();
    }

    public CourseTable getTable() {
        return table;
    }
}
