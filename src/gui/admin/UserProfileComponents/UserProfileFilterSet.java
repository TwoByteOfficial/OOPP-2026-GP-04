package gui.admin.UserProfileComponents;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * UserProfileFilterSet - Consolidated single-file component containing:
 * - Refresh button (left side aligned)
 * - Search Bar text field
 * - Role Filter combo box
 * - Status Filter combo box
 * - Search Button
 * - Reset Filters button (right side aligned)
 *
 * Theme colors and typography are pulled dynamically from AdminTheme.
 */
public class UserProfileFilterSet extends JPanel {

    private final JButton btnRefresh;
    private final JTextField txtSearch;
    private final JComboBox<String> cmbRole;
    private final JComboBox<String> cmbStatus;
    private final JButton btnSearch;
    private final JButton btnReset;

    public UserProfileFilterSet(Runnable onFilterChanged, Runnable onRefresh) {
        setLayout(new BorderLayout(16, 0));
        setBackground(AdminTheme.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        // --- 1. Left Side Aligned Component: Refresh Button ---
        btnRefresh = new JButton("🔄 Refresh Data");
        btnRefresh.setFont(AdminTheme.font(Font.BOLD, 15));
        btnRefresh.setBackground(AdminTheme.PASTEL_GREEN);
        btnRefresh.setForeground(AdminTheme.DEEP_GREEN);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setPreferredSize(new Dimension(160, 38));
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> {
            if (onRefresh != null) onRefresh.run();
        });

        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlLeft.setOpaque(false);
        pnlLeft.add(btnRefresh);
        add(pnlLeft, BorderLayout.WEST);

        // --- 2. Center Component Group: SearchBar, Role Filter, Status Filter, Search Button ---
        JPanel pnlCenter = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        pnlCenter.setOpaque(false);

        // Search Bar Sub-Group
        JPanel pnlSearch = new JPanel(new BorderLayout(8, 0));
        pnlSearch.setOpaque(false);

        JLabel lblSearchIcon = new JLabel("🔍");
        lblSearchIcon.setFont(AdminTheme.font(Font.PLAIN, 16));

        txtSearch = new JTextField();
        txtSearch.setFont(AdminTheme.font(Font.PLAIN, 16));
        txtSearch.setBackground(AdminTheme.WHITE);
        txtSearch.setForeground(AdminTheme.BLACK);
        txtSearch.setCaretColor(AdminTheme.BLACK);
        txtSearch.setPreferredSize(new Dimension(260, 38));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        txtSearch.addActionListener(e -> {
            if (onFilterChanged != null) onFilterChanged.run();
        });

        pnlSearch.add(lblSearchIcon, BorderLayout.WEST);
        pnlSearch.add(txtSearch, BorderLayout.CENTER);
        pnlCenter.add(pnlSearch);

        // Role Filter Sub-Group
        JPanel pnlRole = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlRole.setOpaque(false);

        JLabel lblRole = new JLabel("Role:");
        lblRole.setFont(AdminTheme.font(Font.BOLD, 15));
        lblRole.setForeground(AdminTheme.DEEP_GREEN);

        String[] roles = { "All Roles", "Admin", "Lecturer", "Technical Officer", "Undergraduate" };
        cmbRole = new JComboBox<>(roles);
        cmbRole.setFont(AdminTheme.font(Font.PLAIN, 15));
        cmbRole.setBackground(AdminTheme.WHITE);
        cmbRole.setForeground(AdminTheme.BLACK);
        cmbRole.setPreferredSize(new Dimension(170, 38));
        cmbRole.setRenderer(createCustomCellRenderer());
        cmbRole.addActionListener(e -> {
            if (onFilterChanged != null) onFilterChanged.run();
        });

        pnlRole.add(lblRole);
        pnlRole.add(cmbRole);
        pnlCenter.add(pnlRole);

        // Status Filter Sub-Group
        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlStatus.setOpaque(false);

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(AdminTheme.font(Font.BOLD, 15));
        lblStatus.setForeground(AdminTheme.DEEP_GREEN);

        String[] statuses = { "All Statuses", "Active", "Inactive" };
        cmbStatus = new JComboBox<>(statuses);
        cmbStatus.setFont(AdminTheme.font(Font.PLAIN, 15));
        cmbStatus.setBackground(AdminTheme.WHITE);
        cmbStatus.setForeground(AdminTheme.BLACK);
        cmbStatus.setPreferredSize(new Dimension(150, 38));
        cmbStatus.setRenderer(createCustomCellRenderer());
        cmbStatus.addActionListener(e -> {
            if (onFilterChanged != null) onFilterChanged.run();
        });

        pnlStatus.add(lblStatus);
        pnlStatus.add(cmbStatus);
        pnlCenter.add(pnlStatus);

        // Search Button
        btnSearch = new JButton("Search");
        btnSearch.setFont(AdminTheme.font(Font.BOLD, 15));
        btnSearch.setBackground(AdminTheme.FOREST_GREEN);
        btnSearch.setForeground(AdminTheme.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(100, 38));
        btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(e -> {
            if (onFilterChanged != null) onFilterChanged.run();
        });

        pnlCenter.add(btnSearch);
        add(pnlCenter, BorderLayout.CENTER);

        // --- 3. Right Side Aligned Component: Reset Filter Button ---
        btnReset = new JButton("Reset Filters");
        btnReset.setFont(AdminTheme.font(Font.BOLD, 15));
        btnReset.setBackground(AdminTheme.PASTEL_GREEN);
        btnReset.setForeground(AdminTheme.DEEP_GREEN);
        btnReset.setFocusPainted(false);
        btnReset.setPreferredSize(new Dimension(130, 38));
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            resetFilters();
            if (onFilterChanged != null) onFilterChanged.run();
        });

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlRight.setOpaque(false);
        pnlRight.add(btnReset);
        add(pnlRight, BorderLayout.EAST);
    }

    public UserProfileFilterSet(Runnable onFilterChanged) {
        this(onFilterChanged, null);
    }

    private ListCellRenderer<Object> createCustomCellRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    l.setBackground(AdminTheme.PASTEL_GREEN);
                    l.setForeground(AdminTheme.DEEP_GREEN);
                } else {
                    l.setBackground(AdminTheme.WHITE);
                    l.setForeground(AdminTheme.BLACK);
                }
                l.setOpaque(true);
                l.setFont(AdminTheme.font(Font.PLAIN, 15));
                l.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return l;
            }
        };
    }

    public String getSearchQuery() {
        return txtSearch.getText().trim();
    }

    public String getSelectedRole() {
        return (String) cmbRole.getSelectedItem();
    }

    public String getSelectedStatus() {
        return (String) cmbStatus.getSelectedItem();
    }

    public void resetFilters() {
        txtSearch.setText("");
        cmbRole.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
    }

    public void reset() {
        resetFilters();
    }

    public JButton getBtnRefresh() { return btnRefresh; }
    public JButton getBtnSearch() { return btnSearch; }
    public JButton getBtnReset() { return btnReset; }
}
