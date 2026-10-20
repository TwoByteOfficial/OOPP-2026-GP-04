package gui.admin.UserProfileComponents;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Filter and Search toolbar panel for User Profiles page.
 * Explicitly styled to prevent OS Dark Theme background/foreground color overrides.
 */
public class UserProfileFilterSet extends JPanel {

    private final Color WHITE        = new Color(255, 255, 255);
    private final Color BLACK        = new Color(0, 0, 0);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);

    private JComboBox<String> cmbRoleFilter;
    private JComboBox<String> cmbStatusFilter;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnReset;

    private Runnable onFilterChange;

    public UserProfileFilterSet(Runnable onFilterChange) {
        this.onFilterChange = onFilterChange;

        setLayout(new FlowLayout(FlowLayout.LEFT, 16, 12));
        setBounds(0, 0, 1620, 65);
        setBackground(WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));

        // 1. Role Filter
        JLabel lblRole = createLabel("Filter by Role:");
        add(lblRole);

        String[] roles = { "All Roles", "Undergraduate", "Lecturer", "Technical Officer", "Admin" };
        cmbRoleFilter = createComboBox(roles);
        cmbRoleFilter.addActionListener(e -> triggerFilter());
        add(cmbRoleFilter);

        add(Box.createHorizontalStrut(15));

        // 2. Status Filter
        JLabel lblStatus = createLabel("Status:");
        add(lblStatus);

        String[] statuses = { "All Statuses", "Active", "Inactive" };
        cmbStatusFilter = createComboBox(statuses);
        cmbStatusFilter.addActionListener(e -> triggerFilter());
        add(cmbStatusFilter);

        add(Box.createHorizontalStrut(25));

        // 3. Search Box
        JLabel lblSearch = createLabel("Search:");
        add(lblSearch);

        txtSearch = new JTextField(22);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // 14 -> 16
        txtSearch.setBackground(WHITE);
        txtSearch.setForeground(BLACK);
        txtSearch.setCaretColor(BLACK);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        txtSearch.addActionListener(e -> triggerFilter());
        add(txtSearch);

        // Search Button
        btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 15)); // 13 -> 15
        btnSearch.setBackground(FOREST_GREEN);
        btnSearch.setForeground(WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setOpaque(true);
        btnSearch.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(e -> triggerFilter());
        add(btnSearch);

        // Reset Button
        btnReset = new JButton("Reset");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 15)); // 13 -> 15
        btnReset.setBackground(PASTEL_GREEN);
        btnReset.setForeground(DEEP_GREEN);
        btnReset.setFocusPainted(false);
        btnReset.setOpaque(true);
        btnReset.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> resetFilters());
        add(btnReset);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15)); // 13 -> 15
        l.setForeground(BLACK);
        return l;
    }

    private <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // 14 -> 16
        combo.setBackground(WHITE);
        combo.setForeground(BLACK);
        combo.setFocusable(false);

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    l.setBackground(PASTEL_GREEN);
                    l.setForeground(DEEP_GREEN);
                } else {
                    l.setBackground(WHITE);
                    l.setForeground(BLACK);
                }
                l.setOpaque(true);
                l.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                l.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return l;
            }
        });

        return combo;
    }

    private void triggerFilter() {
        if (onFilterChange != null) {
            onFilterChange.run();
        }
    }

    private void resetFilters() {
        cmbRoleFilter.setSelectedIndex(0);
        cmbStatusFilter.setSelectedIndex(0);
        txtSearch.setText("");
        triggerFilter();
    }

    public String getSelectedRole() {
        return (String) cmbRoleFilter.getSelectedItem();
    }

    public String getSelectedStatus() {
        return (String) cmbStatusFilter.getSelectedItem();
    }

    public String getSearchQuery() {
        return txtSearch.getText().trim();
    }
}
