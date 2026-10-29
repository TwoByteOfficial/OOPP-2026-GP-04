package gui.admin.NoticeComponents;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

public class NoticeFilterBar extends JPanel {

    private final JTextField txtSearch;
    private final JComboBox<String> cmbType;
    private final JComboBox<String> cmbRole;
    private final JButton btnReset;
    private final JButton btnRefresh;

    public NoticeFilterBar(Runnable onFilterTrigger, Runnable onRefreshTrigger) {
        setLayout(new BorderLayout());
        setBackground(AdminTheme.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));

        // Left Panel: Search notices, Notice type filter, Target role filter, Reset filter button
        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        pnlLeft.setOpaque(false);

        // Search Bar
        JLabel lblSearch = new JLabel("Search Notices:");
        lblSearch.setFont(AdminTheme.FONT_BODY_BOLD);
        lblSearch.setForeground(AdminTheme.BLACK);
        pnlLeft.add(lblSearch);

        txtSearch = new JTextField(16);
        txtSearch.setFont(AdminTheme.FONT_BODY);
        txtSearch.setBackground(AdminTheme.WHITE);
        txtSearch.setForeground(AdminTheme.BLACK);
        txtSearch.setCaretColor(AdminTheme.BLACK);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        txtSearch.addActionListener(e -> onFilterTrigger.run());
        pnlLeft.add(txtSearch);

        // Notice Type Filter
        JLabel lblType = new JLabel("Notice Type:");
        lblType.setFont(AdminTheme.FONT_BODY_BOLD);
        lblType.setForeground(AdminTheme.BLACK);
        pnlLeft.add(lblType);

        String[] types = { "All Types", "General", "Academic", "Exam", "Emergency" };
        cmbType = new JComboBox<>(types);
        cmbType.setFont(AdminTheme.FONT_BODY);
        cmbType.setBackground(AdminTheme.WHITE);
        cmbType.setForeground(AdminTheme.BLACK);
        cmbType.setFocusable(false);
        cmbType.addActionListener(e -> onFilterTrigger.run());
        pnlLeft.add(cmbType);

        // Target Role Filter
        JLabel lblRole = new JLabel("Target Role:");
        lblRole.setFont(AdminTheme.FONT_BODY_BOLD);
        lblRole.setForeground(AdminTheme.BLACK);
        pnlLeft.add(lblRole);

        String[] roles = { "All Roles", "Undergraduate", "Lecturer", "Technical Officer", "Admin" };
        cmbRole = new JComboBox<>(roles);
        cmbRole.setFont(AdminTheme.FONT_BODY);
        cmbRole.setBackground(AdminTheme.WHITE);
        cmbRole.setForeground(AdminTheme.BLACK);
        cmbRole.setFocusable(false);
        cmbRole.addActionListener(e -> onFilterTrigger.run());
        pnlLeft.add(cmbRole);

        // Reset Filter Button (Left Aligned)
        btnReset = new JButton("Reset Filters");
        btnReset.setFont(AdminTheme.FONT_BODY_BOLD);
        btnReset.setBackground(AdminTheme.PASTEL_GREEN);
        btnReset.setForeground(AdminTheme.DEEP_GREEN);
        btnReset.setFocusPainted(false);
        btnReset.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            resetFilters();
            onFilterTrigger.run();
        });
        pnlLeft.add(btnReset);

        add(pnlLeft, BorderLayout.WEST);

        // Right Panel: Refresh Button (Right Aligned)
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        pnlRight.setOpaque(false);

        btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(AdminTheme.FONT_BODY_BOLD);
        btnRefresh.setBackground(AdminTheme.MINT);
        btnRefresh.setForeground(AdminTheme.DEEP_GREEN);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> onRefreshTrigger.run());

        pnlRight.add(btnRefresh);

        add(pnlRight, BorderLayout.EAST);
    }

    public String getSearchQuery() {
        return txtSearch.getText().trim();
    }

    public String getSelectedType() {
        return (String) cmbType.getSelectedItem();
    }

    public String getSelectedRole() {
        return (String) cmbRole.getSelectedItem();
    }

    public void resetFilters() {
        txtSearch.setText("");
        cmbType.setSelectedIndex(0);
        cmbRole.setSelectedIndex(0);
    }
}
