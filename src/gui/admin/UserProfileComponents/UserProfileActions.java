package gui.admin.UserProfileComponents;

import javax.swing.*;
import java.awt.*;

/**
 * UserProfileActions - Composite action toolbar aggregating dedicated button components:
 * AddUserButton, ViewUserButton, EditUserButton, DeleteUserButton, and RefreshUserButton.
 */
public class UserProfileActions extends JPanel {

    private final AddUserButton btnAdd;
    private final ViewUserButton btnView;
    private final EditUserButton btnEdit;
    private final DeleteUserButton btnDelete;
    private final RefreshUserButton btnRefresh;

    public UserProfileActions(Runnable onAdd, Runnable onView, Runnable onEdit, Runnable onDelete, Runnable onRefresh) {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        setOpaque(false);
        setBounds(0, 0, 1620, 50);

        btnAdd = new AddUserButton(onAdd);
        btnView = new ViewUserButton(onView);
        btnEdit = new EditUserButton(onEdit);
        btnDelete = new DeleteUserButton(onDelete);
        btnRefresh = new RefreshUserButton(onRefresh);

        add(btnAdd);
        add(btnView);
        add(btnEdit);
        add(btnDelete);
        add(btnRefresh);
    }
}
