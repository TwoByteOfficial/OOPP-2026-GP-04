package gui.admin.CourseComponents;

import dao.admin.AdminCourseDAO;
import dao.admin.AdminUserDAO;
import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * CourseActions - Action toolbar component aggregating button components:
 * AddCourseButton, ViewCourseButton, EditCourseButton, AssignLecturerButton, and DeleteCourseButton.
 */
public class CourseActions extends JPanel {

    private final AddCourseButton btnAdd;
    private final ViewCourseButton btnView;
    private final EditCourseButton btnEdit;
    private final AssignLecturerButton btnAssign;
    private final DeleteCourseButton btnDelete;

    public CourseActions(CourseTable table, Frame parent, AdminCourseDAO courseDAO, AdminUserDAO userDAO, Runnable onSuccess) {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        setBackground(AdminTheme.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));

        btnAdd = new AddCourseButton(parent, courseDAO, onSuccess);
        btnView = new ViewCourseButton(table, courseDAO);
        btnEdit = new EditCourseButton(table, parent, courseDAO, onSuccess);
        btnAssign = new AssignLecturerButton(table, parent, courseDAO, userDAO, onSuccess);
        btnDelete = new DeleteCourseButton(table, parent, courseDAO, onSuccess);

        add(btnAdd);
        add(btnView);
        add(btnEdit);
        add(btnAssign);
        add(btnDelete);
    }
}
