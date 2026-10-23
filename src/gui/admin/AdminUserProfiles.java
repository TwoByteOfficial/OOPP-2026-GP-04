package gui.admin;

import dao.admin.AdminUserDAO;
import gui.admin.UserProfileComponents.*;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class AdminUserProfiles extends JPanel {

    private final Color MINT = new Color(0xE8, 0xF5, 0xE9);

    private final AdminUserDAO userDAO;
    private List<User> allLoadedUsers = new ArrayList<>();

    private UserProfileTitleBar titleBar;
    private UserProfileFilterSet filterSet;
    private UserProfileActions actionToolbar;
    private UserProfileUserList userList;

    public AdminUserProfiles(long userId, String username, String fullName, String email, String role) {
        setLayout(new BorderLayout(0, 12));
        setBackground(MINT);
        setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

        userDAO = new AdminUserDAO();

        initUI();
        loadUsersFromDatabase();
    }

    private void initUI() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        titleBar = new UserProfileTitleBar(0);
        topPanel.add(titleBar);
        topPanel.add(Box.createVerticalStrut(10));

        filterSet = new UserProfileFilterSet(this::applyFilters);
        topPanel.add(filterSet);
        topPanel.add(Box.createVerticalStrut(10));

        actionToolbar = new UserProfileActions(
                this::openAddUserDialog,
                this::openViewDetailsDialog,
                this::openEditUserDialog,
                this::deleteSelectedUser,
                this::loadUsersFromDatabase
        );
        topPanel.add(actionToolbar);

        add(topPanel, BorderLayout.NORTH);

        userList = new UserProfileUserList();
        add(userList, BorderLayout.CENTER);
    }

    public void loadUsersFromDatabase() {
        try {
            allLoadedUsers = userDAO.getAllUsers();
            applyFilters();
        } catch (Exception e) {
            System.err.println("[AdminUserProfiles DB Error] Failed to fetch users.");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load user profiles from database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilters() {
        if (allLoadedUsers == null) return;

        String query = filterSet.getSearchQuery().toLowerCase();
        String roleFilter = filterSet.getSelectedRole();
        String statusFilter = filterSet.getSelectedStatus();

        List<User> filtered = new ArrayList<>();

        for (User u : allLoadedUsers) {
            if (!"All Roles".equalsIgnoreCase(roleFilter) && !roleFilter.equalsIgnoreCase(u.getRole())) {
                continue;
            }

            if (!"All Statuses".equalsIgnoreCase(statusFilter) && !statusFilter.equalsIgnoreCase(u.getUserStatus())) {
                continue;
            }

            if (!query.isEmpty()) {
                boolean matchName = u.getFullName() != null && u.getFullName().toLowerCase().contains(query);
                boolean matchUsername = u.getUsername() != null && u.getUsername().toLowerCase().contains(query);
                boolean matchEmail = u.getEmail() != null && u.getEmail().toLowerCase().contains(query);
                boolean matchNic = u.getNic() != null && u.getNic().toLowerCase().contains(query);

                if (!matchName && !matchUsername && !matchEmail && !matchNic) {
                    continue;
                }
            }

            filtered.add(u);
        }

        userList.updateData(filtered);
        titleBar.updateCount(filtered.size());
    }

    private void openAddUserDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        UserProfileForm form = new UserProfileForm(parentFrame, userDAO, null, this::loadUsersFromDatabase);
        form.setVisible(true);
    }

    private void openViewDetailsDialog() {
        User selected = userList.getSelectedUser();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a user profile row from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        UserProfileDetails details = new UserProfileDetails(parentFrame, selected);
        details.setVisible(true);
    }

    private void openEditUserDialog() {
        User selected = userList.getSelectedUser();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a user profile row to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        UserProfileForm form = new UserProfileForm(parentFrame, userDAO, selected, this::loadUsersFromDatabase);
        form.setVisible(true);
    }

    private void deleteSelectedUser() {
        User selected = userList.getSelectedUser();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a user profile row to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to permanently delete user '" + selected.getFullName() + "' (" + selected.getUsername() + ")?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = userDAO.deleteUser(selected.getUserId());
                if (success) {
                    JOptionPane.showMessageDialog(this, "User profile deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUsersFromDatabase();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete user account.", "Deletion Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
