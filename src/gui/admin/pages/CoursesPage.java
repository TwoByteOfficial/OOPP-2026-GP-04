package gui.admin.pages;

import dao.CourseDAO;
import dao.DepartmentDAO;
import gui.admin.AdminDashboard;
import gui.admin.AdminTheme;
import model.Course;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class CoursesPage extends JPanel {

    private final CourseDAO dao = new CourseDAO();
    private final JPanel tableHost = new JPanel(new BorderLayout());

    public CoursesPage(AdminDashboard dash) {
        setLayout(new BorderLayout());
        setBackground(AdminTheme.LIGHT);
        setBorder(new EmptyBorder(24, 32, 24, 32));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(AdminTheme.label("Course Management", AdminTheme.F_H2, AdminTheme.PRIMARY));
        left.add(AdminTheme.label("Create and maintain course modules.", AdminTheme.F_SMALL, AdminTheme.MUTED));
        header.add(left, BorderLayout.WEST);

        JButton add = AdminTheme.button("+ Add Course", AdminTheme.PRIMARY);
        add.addActionListener(e -> openModal(null));
        header.add(add, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        add(tableHost, BorderLayout.CENTER);
        reload();
    }

    private void reload() {
        try {
            List<Course> list = dao.getAllCourses();
            Object[][] rows = new Object[list.size()][7];
            for (int i = 0; i < list.size(); i++) {
                Course c = list.get(i);
                rows[i] = new Object[]{
                        c.getCourseCode(), c.getCourseName(),
                        c.getTheoryCredits(), c.getPracticalCredits(),
                        c.getTotalCredits(), c.getCaWeight(), "Edit / Delete"
                };
            }
            JPanel wrap = AdminTheme.card();
            wrap.setLayout(new BorderLayout());
            wrap.add(AdminTheme.table(rows,
                            new String[]{"Code","Name","Theory Cr","Practical Cr","Total Cr","CA Weight","Actions"}),
                    BorderLayout.CENTER);
            tableHost.removeAll();
            tableHost.add(wrap, BorderLayout.CENTER);
            tableHost.revalidate();
            tableHost.repaint();
        } catch (Exception ex) {
            AdminTheme.showError(this, "Could not load courses:\n" + ex.getMessage());
        }
    }

    private void openModal(Course c) { /* build JDialog similar to UsersPage */ }
}