package gui.admin.NoticeComponents;

import javax.swing.*;
import java.awt.*;

public class NoticeFilterSet extends JPanel {

    private NoticeSearchBar searchBar;
    private NoticeTypeFilter typeFilter;
    private NoticeTargetRoleFilter roleFilter;

    public NoticeFilterSet(Runnable onFilterTrigger) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 16, 12));
        setBackground(Color.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));

        searchBar = new NoticeSearchBar(query -> onFilterTrigger.run());
        typeFilter = new NoticeTypeFilter(t -> onFilterTrigger.run());
        roleFilter = new NoticeTargetRoleFilter(r -> onFilterTrigger.run());

        add(searchBar);
        add(typeFilter);
        add(roleFilter);

        JButton btnReset = new JButton("Reset Filters");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnReset.setBackground(new Color(0xC8, 0xE6, 0xC9));
        btnReset.setForeground(new Color(0x1B, 0x5E, 0x20));
        btnReset.setFocusPainted(false);
        btnReset.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            searchBar.reset();
            typeFilter.reset();
            roleFilter.reset();
            onFilterTrigger.run();
        });
        add(btnReset);
    }

    public String getSearchQuery() { return searchBar.getSearchQuery(); }
    public String getSelectedType() { return typeFilter.getSelectedType(); }
    public String getSelectedRole() { return roleFilter.getSelectedRole(); }
}
