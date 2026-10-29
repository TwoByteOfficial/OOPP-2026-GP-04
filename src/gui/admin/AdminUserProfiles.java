package gui.admin;

import dao.admin.AdminUserDAO;
import gui.admin.UserProfileComponents.*;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminUserProfiles - Primary User Profiles management view panel.
 * Restructured to integrate consolidated component files:
 * - UserProfileTitleBar
 * - UserProfileFilterSet (Search, Role Filter, Status Filter, Search Btn, Reset Btn on right, Refresh Btn on left)
 * - AddUserButton (Register User button + Modal Dialog)
 * - ViewUserButton (View Details button + Modal Dialog)
 * - EditUserButton (Edit User button + Modal Dialog)
 * - UserProfileTable (Single component data table)
 */
public class AdminUserProfiles extends JPanel {

    private final AdminUserDAO userDAO;
    private List<User> allLoadedUsers = new ArrayList<>();

    private UserProfileTitleBar titleBar;
    private UserProfileFilterSet filterSet;
    private AddUserButton btnAdd;
    private ViewUserButton btnView;
    private EditUserButton btnEdit;
    private UserProfileTable userTable;

    public AdminUserProfiles(long userId, String username, String fullName, String email, String role) {
        setLayout(new BorderLayout(0, 12));
        setBackground(AdminTheme.MINT);
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        userDAO = new AdminUserDAO();

        initUI();
        loadUsersFromDatabase();
    }

    private void initUI() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        // 1. Title Bar Component
        titleBar = new UserProfileTitleBar(0);
        topPanel.add(titleBar);
        topPanel.add(Box.createVerticalStrut(10));

        // 2. Filter Bar Component (Refresh on left, Search/Role/Status in center, Reset on right)
        filterSet = new UserProfileFilterSet(this::applyFilters, this::loadUsersFromDatabase);
        topPanel.add(filterSet);
        topPanel.add(Box.createVerticalStrut(10));

        // 3. Action Toolbar (Register New User, View Details, Edit User)
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        actionToolbar.setOpaque(false);
        actionToolbar.setPreferredSize(new Dimension(1620, 50));

        btnAdd = new AddUserButton(this, userDAO, this::loadUsersFromDatabase);
        btnView = new ViewUserButton(this, () -> userTable.getSelectedUser());
        btnEdit = new EditUserButton(this, userDAO, () -> userTable.getSelectedUser(), this::loadUsersFromDatabase);

        actionToolbar.add(btnAdd);
        actionToolbar.add(btnView);
        actionToolbar.add(btnEdit);

        topPanel.add(actionToolbar);

        add(topPanel, BorderLayout.NORTH);

        // 4. User Table Component
        userTable = new UserProfileTable();
        add(userTable, BorderLayout.CENTER);
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

        userTable.setUsersData(filtered);
        titleBar.updateCount(filtered.size());
    }
}