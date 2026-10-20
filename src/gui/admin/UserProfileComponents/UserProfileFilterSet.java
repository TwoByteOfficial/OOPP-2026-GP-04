package gui.admin.UserProfileComponents;

import javax.swing.*;
import java.awt.*;

/**
 * UserProfileFilterSet - Composite filter panel holding SearchBar, RoleFilter, StatusFilter, and Reset button.
 */
public class UserProfileFilterSet extends JPanel {

    private final Color WHITE        = Color.WHITE;
    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);

    private final UserProfileSearchBar searchBar;
    private final UserProfileRoleFilter roleFilter;
    private final UserProfileStatusFilter statusFilter;
    private final JButton btnReset;

    public UserProfileFilterSet(Runnable onFilterChanged) {
        setLayout(null);
        setBounds(0, 0, 1620, 60);
        setBackground(WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        searchBar = new UserProfileSearchBar(onFilterChanged);
        searchBar.setBounds(15, 10, 380, 40);
        add(searchBar);

        roleFilter = new UserProfileRoleFilter(onFilterChanged);
        roleFilter.setBounds(420, 10, 240, 40);
        add(roleFilter);

        statusFilter = new UserProfileStatusFilter(onFilterChanged);
        statusFilter.setBounds(680, 10, 210, 40);
        add(statusFilter);

        JButton btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSearch.setBackground(new Color(0x38, 0x8E, 0x3C));
        btnSearch.setForeground(WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setBounds(910, 11, 100, 38);
        btnSearch.addActionListener(e -> {
            if (onFilterChanged != null) onFilterChanged.run();
        });
        add(btnSearch);

        btnReset = new JButton("Reset Filters");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnReset.setBackground(PASTEL_GREEN);
        btnReset.setForeground(DEEP_GREEN);
        btnReset.setFocusPainted(false);
        btnReset.setBounds(1020, 11, 130, 38);
        btnReset.addActionListener(e -> {
            searchBar.clear();
            roleFilter.reset();
            statusFilter.reset();
            if (onFilterChanged != null) onFilterChanged.run();
        });
        add(btnReset);
    }

    public String getSearchQuery() { return searchBar.getSearchQuery(); }
    public String getSelectedRole() { return roleFilter.getSelectedRole(); }
    public String getSelectedStatus() { return statusFilter.getSelectedStatus(); }
}
