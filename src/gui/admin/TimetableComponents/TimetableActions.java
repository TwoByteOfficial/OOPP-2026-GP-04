package gui.admin.TimetableComponents;

import javax.swing.*;
import java.awt.*;

public class TimetableActions extends JPanel {

    private AddTimetableButton btnAdd;
    private ViewTimetableButton btnView;
    private EditTimetableButton btnEdit;
    private DeleteTimetableButton btnDelete;
    private RefreshTimetableButton btnRefresh;

    public TimetableActions(Runnable onAdd, Runnable onView, Runnable onEdit, Runnable onDelete, Runnable onRefresh) {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        setBackground(Color.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));

        btnAdd = new AddTimetableButton(onAdd);
        btnView = new ViewTimetableButton(onView);
        btnEdit = new EditTimetableButton(onEdit);
        btnDelete = new DeleteTimetableButton(onDelete);
        btnRefresh = new RefreshTimetableButton(onRefresh);

        add(btnAdd);
        add(btnView);
        add(btnEdit);
        add(btnDelete);
        add(btnRefresh);
    }
}
