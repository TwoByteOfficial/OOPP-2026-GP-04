package gui.admin.TimetableComponents;

import gui.admin.AdminTheme;
import javax.swing.*;
import java.awt.*;

public class TimetableFilterBar extends JPanel {

    private JComboBox<String> cmbDept;
    private JComboBox<String> cmbBatch;
    private JComboBox<String> cmbSemester;
    private JButton btnReset;
    private JButton btnRefresh;

    public TimetableFilterBar(Runnable onFilterTrigger, Runnable onRefresh) {
        setLayout(new BorderLayout());
        setBackground(AdminTheme.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));

        // Left-aligned Filter controls and Reset button
        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        pnlLeft.setOpaque(false);

        JLabel lblDept = new JLabel("Department:");
        lblDept.setFont(AdminTheme.FONT_TINY);
        lblDept.setForeground(AdminTheme.BLACK);
        pnlLeft.add(lblDept);

        String[] departments = { "All Departments", "1 - ICT", "2 - Engineering Technology", "3 - Bio Systems Technology" };
        cmbDept = new JComboBox<>(departments);
        cmbDept.setFont(AdminTheme.FONT_SMALL);
        cmbDept.setBackground(AdminTheme.WHITE);
        cmbDept.setForeground(AdminTheme.BLACK);
        cmbDept.setFocusable(false);
        cmbDept.addActionListener(e -> onFilterTrigger.run());
        pnlLeft.add(cmbDept);

        JLabel lblBatch = new JLabel("Batch Year:");
        lblBatch.setFont(AdminTheme.FONT_TINY);
        lblBatch.setForeground(AdminTheme.BLACK);
        pnlLeft.add(lblBatch);

        String[] batches = { "All Batches", "2021", "2022", "2023", "2024" };
        cmbBatch = new JComboBox<>(batches);
        cmbBatch.setFont(AdminTheme.FONT_SMALL);
        cmbBatch.setBackground(AdminTheme.WHITE);
        cmbBatch.setForeground(AdminTheme.BLACK);
        cmbBatch.setFocusable(false);
        cmbBatch.addActionListener(e -> onFilterTrigger.run());
        pnlLeft.add(cmbBatch);

        JLabel lblSem = new JLabel("Semester:");
        lblSem.setFont(AdminTheme.FONT_TINY);
        lblSem.setForeground(AdminTheme.BLACK);
        pnlLeft.add(lblSem);

        String[] semesters = { "All Semesters", "Semester 1", "Semester 2" };
        cmbSemester = new JComboBox<>(semesters);
        cmbSemester.setFont(AdminTheme.FONT_SMALL);
        cmbSemester.setBackground(AdminTheme.WHITE);
        cmbSemester.setForeground(AdminTheme.BLACK);
        cmbSemester.setFocusable(false);
        cmbSemester.addActionListener(e -> onFilterTrigger.run());
        pnlLeft.add(cmbSemester);

        btnReset = new JButton("Reset Filters");
        btnReset.setFont(AdminTheme.FONT_TINY);
        btnReset.setBackground(AdminTheme.PASTEL_GREEN);
        btnReset.setForeground(AdminTheme.DEEP_GREEN);
        btnReset.setFocusPainted(false);
        btnReset.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            cmbDept.setSelectedIndex(0);
            cmbBatch.setSelectedIndex(0);
            cmbSemester.setSelectedIndex(0);
            onFilterTrigger.run();
        });
        pnlLeft.add(btnReset);

        add(pnlLeft, BorderLayout.WEST);

        // Right-aligned Refresh button
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        pnlRight.setOpaque(false);

        btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(AdminTheme.FONT_TINY);
        btnRefresh.setBackground(AdminTheme.MINT);
        btnRefresh.setForeground(AdminTheme.DEEP_GREEN);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> onRefresh.run());
        pnlRight.add(btnRefresh);

        add(pnlRight, BorderLayout.EAST);
    }

    public String getSelectedDepartment() { return (String) cmbDept.getSelectedItem(); }
    public String getSelectedBatch() { return (String) cmbBatch.getSelectedItem(); }
    public String getSelectedSemester() { return (String) cmbSemester.getSelectedItem(); }

    public int getSelectedDepartmentId() {
        String dept = getSelectedDepartment();
        if (dept == null || dept.startsWith("All")) return 0;
        if (dept.startsWith("1")) return 1;
        if (dept.startsWith("2")) return 2;
        if (dept.startsWith("3")) return 3;
        return 0;
    }

    public int getSelectedBatchYear() {
        String b = getSelectedBatch();
        if (b == null || b.startsWith("All")) return 0;
        try {
            return Integer.parseInt(b.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public void reset() {
        cmbDept.setSelectedIndex(0);
        cmbBatch.setSelectedIndex(0);
        cmbSemester.setSelectedIndex(0);
    }
}
