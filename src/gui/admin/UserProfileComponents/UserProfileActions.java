package gui.admin.UserProfileComponents;

import javax.swing.*;
import java.awt.*;

/**
 * Action toolbar panel providing CRUD operation triggers for User Profiles management.
 */
public class UserProfileActions extends JPanel {

    private final Color WHITE        = new Color(255, 255, 255);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color DANGER       = new Color(0xDC, 0x35, 0x45);

    private JButton btnAddUser;
    private JButton btnViewUser;
    private JButton btnEditUser;
    private JButton btnDeleteUser;
    private JButton btnRefresh;

    public UserProfileActions(Runnable onAdd, Runnable onView, Runnable onEdit, Runnable onDelete, Runnable onRefresh) {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        setBounds(0, 0, 1620, 60);
        setBackground(WHITE);
        setOpaque(true);

        btnAddUser = createActionButton("+ Register New User", FOREST_GREEN, WHITE);
        btnAddUser.addActionListener(e -> { if (onAdd != null) onAdd.run(); });
        add(btnAddUser);

        btnViewUser = createActionButton("View Details", PASTEL_GREEN, DEEP_GREEN);
        btnViewUser.addActionListener(e -> { if (onView != null) onView.run(); });
        add(btnViewUser);

        btnEditUser = createActionButton("Edit User", PASTEL_GREEN, DEEP_GREEN);
        btnEditUser.addActionListener(e -> { if (onEdit != null) onEdit.run(); });
        add(btnEditUser);

        btnDeleteUser = createActionButton("Delete User", DANGER, WHITE);
        btnDeleteUser.addActionListener(e -> { if (onDelete != null) onDelete.run(); });
        add(btnDeleteUser);

        btnRefresh = createActionButton("Refresh List", PASTEL_GREEN, DEEP_GREEN);
        btnRefresh.addActionListener(e -> { if (onRefresh != null) onRefresh.run(); });
        add(btnRefresh);
    }

    private JButton createActionButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16)); // 14 -> 16
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
