package gui.admin.UserProfileComponents;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UserProfileUserList extends JPanel {

    private UserProfileTable table;

    public UserProfileUserList() {
        this(new UserProfileTable());
    }

    public UserProfileUserList(UserProfileTable table) {
        this.table = table != null ? table : new UserProfileTable();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateData(List<User> users) {
        table.setUsersData(users);
    }

    public void setUsersData(List<User> users) {
        table.setUsersData(users);
    }

    public User getSelectedUser() {
        return table.getSelectedUser();
    }

    public UserProfileTable getTable() {
        return table;
    }
}
