package gui.admin.UserProfileComponents;

import javax.swing.*;
import java.awt.*;

/**
 * UserProfileActions - Composite action toolbar aggregating dedicated button components:
 * AddUserButton, ViewUserButton, EditUserButton, and RefreshUserButton.
 * DeleteUserButton has been removed.
 */
public class UserProfileActions extends JPanel {

    private final AddUserButton btnAdd;
    private final ViewUserButton btnView;
    private final EditUserButton btnEdit;
    private final RefreshUserButton btnRefresh;

    public UserProfileActions(Runnable onAdd, Runnable onView, Runnable onEdit, Runnable onRefresh) {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        setOpaque(false);
        setPreferredSize(new Dimension(1620, 50));

        btnAdd = new AddUserButton(onAdd);
        btnView = new ViewUserButton(onView);
        btnEdit = new EditUserButton(onEdit);
        btnRefresh = new RefreshUserButton(onRefresh);

        add(btnAdd);
        add(btnView);
        add(btnEdit);
        add(btnRefresh);
    }

    public UserProfileActions(Runnable onAdd, Runnable onView, Runnable onEdit, Runnable onDelete, Runnable onRefresh) {
        this(onAdd, onView, onEdit, onRefresh);
    }
}