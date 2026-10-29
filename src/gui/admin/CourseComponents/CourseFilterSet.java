package gui.admin.CourseComponents;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

public class CourseFilterSet extends JPanel {

    private final JTextField txtSearch;
    private final JComboBox<String> cmbDepartment;
    private final JComboBox<String> cmbSemester;
    private final JButton btnSearch;
    private final JButton btnReset;
    private final JButton btnRefresh;

    public CourseFilterSet(Runnable onFilterChanged) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 14, 8));
        setBackground(AdminTheme.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        // Refresh button (left aligned)
        btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnRefresh.setBackground(AdminTheme.MINT);
        btnRefresh.setForeground(AdminTheme.DEEP_GREEN);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setPreferredSize(new Dimension(110, 38));
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> {
            if (onFilterChanged != null) onFilterChanged.run();
        });
        add(btnRefresh);

        // Search Bar
        JLabel lblIcon = new JLabel("🔍");
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(lblIcon);

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtSearch.setBackground(AdminTheme.WHITE);
        txtSearch.setForeground(AdminTheme.BLACK);
        txtSearch.setPreferredSize(new Dimension(240, 38));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        txtSearch.addActionListener(e -> {
            if (onFilterChanged != null) onFilterChanged.run();
        });
        add(txtSearch);

        // Department Filter
        JLabel lblDept = new JLabel("Department:");
        lblDept.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblDept.setForeground(AdminTheme.DEEP_GREEN);
        add(lblDept);

        String[] depts = { "All Departments", "1 - ICT", "2 - Engineering Technology", "3 - Bio Systems Technology" };
        cmbDepartment = new JComboBox<>(depts);
        styleCombo(cmbDepartment);
        cmbDepartment.setPreferredSize(new Dimension(210, 38));
        cmbDepartment.addActionListener(e -> {
            if (onFilterChanged != null) onFilterChanged.run();
        });
        add(cmbDepartment);

        // Semester Filter
        JLabel lblSem = new JLabel("Semester:");
        lblSem.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSem.setForeground(AdminTheme.DEEP_GREEN);
        add(lblSem);

        String[] sems = { "All Semesters", "Semester 1", "Semester 2" };
        cmbSemester = new JComboBox<>(sems);
        styleCombo(cmbSemester);
        cmbSemester.setPreferredSize(new Dimension(150, 38));
        cmbSemester.addActionListener(e -> {
            if (onFilterChanged != null) onFilterChanged.run();
        });
        add(cmbSemester);

        // Search Button
        btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSearch.setBackground(AdminTheme.FOREST_GREEN);
        btnSearch.setForeground(AdminTheme.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(100, 38));
        btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(e -> {
            if (onFilterChanged != null) onFilterChanged.run();
        });
        add(btnSearch);

        // Reset Button
        btnReset = new JButton("Reset Filters");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnReset.setBackground(AdminTheme.PASTEL_GREEN);
        btnReset.setForeground(AdminTheme.DEEP_GREEN);
        btnReset.setFocusPainted(false);
        btnReset.setPreferredSize(new Dimension(130, 38));
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cmbDepartment.setSelectedIndex(0);
            cmbSemester.setSelectedIndex(0);
            if (onFilterChanged != null) onFilterChanged.run();
        });
        add(btnReset);
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        combo.setBackground(AdminTheme.WHITE);
        combo.setForeground(AdminTheme.BLACK);
        combo.setRenderer(new DefaultListCellRenderer() {
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
                l.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                l.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return l;
            }
        });
    }

    public String getSearchQuery() {
        return txtSearch.getText().trim();
    }

    public int getSelectedDepartmentId() {
        int idx = cmbDepartment.getSelectedIndex();
        return idx; // 0 = All, 1 = Dept 1, 2 = Dept 2, 3 = Dept 3
    }

    public String getSelectedSemester() {
        return (String) cmbSemester.getSelectedItem();
    }

    public void clear() {
        txtSearch.setText("");
        cmbDepartment.setSelectedIndex(0);
        cmbSemester.setSelectedIndex(0);
    }
}
