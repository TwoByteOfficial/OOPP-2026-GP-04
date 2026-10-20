package gui.admin.UserProfileComponents;

import model.User;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * UserProfileUserList - Container component wrapping UserProfileTable inside a styled JScrollPane.
 */
public class UserProfileUserList extends JPanel {

    private final Color WHITE        = Color.WHITE;
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);

    private final UserProfileTable table;
    private final JScrollPane scrollPane;

    public UserProfileUserList() {
        setLayout(new BorderLayout());
        setBounds(0, 0, 1620, 680);
        setBackground(WHITE);

        table = new UserProfileTable();

        scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(WHITE);
        scrollPane.setBackground(WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(PASTEL_GREEN, 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateData(List<User> users) {
        table.setUsersData(users);
    }

    public User getSelectedUser() {
        return table.getSelectedUser();
    }

    public UserProfileTable getTable() {
        return table;
    }
}
