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
    private final JButton btnSearch;
    private final JButton btnReset;

    public UserProfileFilterSet(Runnable onFilterChanged) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 16, 10));
        setBackground(WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));

        searchBar = new UserProfileSearchBar(onFilterChanged);
        roleFilter = new UserProfileRoleFilter(onFilterChanged);
        statusFilter = new UserProfileStatusFilter(onFilterChanged);

        btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSearch.setBackground(new Color(0x38, 0x8E, 0x3C));
        btnSearch.setForeground(WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(100, 38));
        btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(e -> {
            if (onFilterChanged != null) onFilterChanged.run();
        });

        btnReset = new JButton("Reset Filters");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnReset.setBackground(PASTEL_GREEN);
        btnReset.setForeground(DEEP_GREEN);
        btnReset.setFocusPainted(false);
        btnReset.setPreferredSize(new Dimension(130, 38));
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            searchBar.clear();
            roleFilter.reset();
            statusFilter.reset();
            if (onFilterChanged != null) onFilterChanged.run();
        });

        add(searchBar);
        add(roleFilter);
        add(statusFilter);
        add(btnSearch);
        add(btnReset);
    }

    public String getSearchQuery() { return searchBar.getSearchQuery(); }
    public String getSelectedRole() { return roleFilter.getSelectedRole(); }
    public String getSelectedStatus() { return statusFilter.getSelectedStatus(); }
}
