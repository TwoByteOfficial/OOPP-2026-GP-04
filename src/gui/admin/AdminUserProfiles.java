package gui.admin;

import gui.admin.UserProfileComponents.*;
import dao.admin.AdminUserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * AdminUserProfiles - Primary User Management View Panel.
 * Synchronizes UserProfileTitleBar, UserProfileFilterSet, UserProfileActions, and UserProfileTable.
 */
public class AdminUserProfiles extends JPanel {

    private final Color MINT = new Color(0xE8, 0xF5, 0xE9);

    private UserProfileTitleBar titleBar;
    private UserProfileFilterSet filterSet;
    private UserProfileActions actionToolbar;
    private UserProfileTable userTable;

    private AdminUserDAO userDAO;
    private List<User> allFetchedUsers = new ArrayList<>();

    private final long currentAdminUserId;
    private final String currentAdminFullName;

    public AdminUserProfiles(long userId, String username, String fullName, String email, String role) {
        this.currentAdminUserId = userId;
        this.currentAdminFullName = fullName;

        setLayout(null);
        setBounds(0, 0, 1660, 1080);
        setBackground(MINT);
        setOpaque(true);

        userDAO = new AdminUserDAO();

        initUI();
        loadUsersDataFromDB();
    }

    private void initUI() {
        // 1. Title Bar
        titleBar = new UserProfileTitleBar(0);
        titleBar.setLocation(20, 20);
        add(titleBar);

        // 2. Filter & Search Bar
        filterSet = new UserProfileFilterSet(this::applyFiltersAndSearch);
        filterSet.setLocation(20, 120);
        add(filterSet);

        // 3. Action Toolbar
        actionToolbar = new UserProfileActions(
                this::openAddUserDialog,
                this::openViewDetailsDialog,
                this::openEditUserDialog,
                this::deleteSelectedUser,
                this::loadUsersDataFromDB
        );
        actionToolbar.setLocation(20, 200);
        add(actionToolbar);

        // 4. Data Table
        userTable = new UserProfileTable();
        userTable.setLocation(20, 285);
        add(userTable);
    }

    private void loadUsersDataFromDB() {
        try {
            allFetchedUsers = userDAO.getAllUsers();
            applyFiltersAndSearch();
        } catch (Exception e) {
            System.err.println("[UserProfiles DB Error] Failed to fetch users list.");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load users list from database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFiltersAndSearch() {
        String role = filterSet.getSelectedRole();
        String status = filterSet.getSelectedStatus();
        String query = filterSet.getSearchQuery().toLowerCase();

        List<User> filteredList = new ArrayList<>();

        for (User u : allFetchedUsers) {
            // Role Match
            boolean matchesRole = "All Roles".equalsIgnoreCase(role) || role.equalsIgnoreCase(u.getRole());

            // Status Match
            boolean matchesStatus = "All Statuses".equalsIgnoreCase(status)
                    || (status.equalsIgnoreCase(u.getUserStatus() != null ? u.getUserStatus() : "Active"));

            // Query Match
            boolean matchesQuery = query.isEmpty()
                    || (u.getFullName() != null && u.getFullName().toLowerCase().contains(query))
                    || (u.getUsername() != null && u.getUsername().toLowerCase().contains(query))
                    || (u.getEmail() != null && u.getEmail().toLowerCase().contains(query))
                    || (u.getNic() != null && u.getNic().toLowerCase().contains(query));

            if (matchesRole && matchesStatus && matchesQuery) {
                filteredList.add(u);
            }
        }

        userTable.setUsersData(filteredList);
        titleBar.updateUserCount(filteredList.size());
    }

    private void openAddUserDialog() {
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        UserProfileForm form = new UserProfileForm(topFrame, userDAO, null, this::loadUsersDataFromDB);
        form.setVisible(true);
    }

    private void openViewDetailsDialog() {
        User selected = userTable.getSelectedUser();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a user row from the directory table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        UserProfileDetails detailsDialog = new UserProfileDetails(topFrame, selected);
        detailsDialog.setVisible(true);
    }

    private void openEditUserDialog() {
        User selected = userTable.getSelectedUser();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a user row from the directory table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        UserProfileForm form = new UserProfileForm(topFrame, userDAO, selected, this::loadUsersDataFromDB);
        form.setVisible(true);
    }

    private void deleteSelectedUser() {
        User selected = userTable.getSelectedUser();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a user row from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete user account: " + selected.getFullName() + " (" + selected.getUsername() + ")?\nThis action will remove all role records permanently.",
                "Confirm Delete User",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = userDAO.deleteUser(selected.getUserId());
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "User profile deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUsersDataFromDB();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete user profile.", "Delete Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting user profile: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
