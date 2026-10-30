package gui.admin.CourseComponents;

import javax.swing.*;
import java.awt.*;

public class CourseActions extends JPanel {

    private AddCourseButton btnAdd;
    private ViewCourseButton btnView;
    private EditCourseButton btnEdit;
    private DeleteCourseButton btnDelete;
    private AssignLecturerButton btnAssign;
    private RefreshCourseButton btnRefresh;

    public CourseActions(Runnable onAdd, Runnable onView, Runnable onEdit, Runnable onDelete, Runnable onAssign, Runnable onRefresh) {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        setBackground(Color.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));

        btnAdd = new AddCourseButton(onAdd);
        btnView = new ViewCourseButton(onView);
        btnEdit = new EditCourseButton(onEdit);
        btnDelete = new DeleteCourseButton(onDelete);
        btnAssign = new AssignLecturerButton(onAssign);
        btnRefresh = new RefreshCourseButton(onRefresh);

        add(btnAdd);
        add(btnView);
        add(btnEdit);
        add(btnAssign);
        add(btnDelete);
        add(btnRefresh);
    }
}
