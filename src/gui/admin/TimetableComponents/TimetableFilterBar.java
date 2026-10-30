package gui.admin.TimetableComponents;

import gui.admin.AdminTheme;
import javax.swing.*;
import java.awt.*;

public class TimetableFilterBar extends JPanel {

    private JComboBox<String> cmbDept;
    private JComboBox<String> cmbBatch;
    private JComboBox<String> cmbSemester;

    public TimetableFilterBar(Runnable onFilterTrigger, Runnable onRefresh) {
        setLayout(null);
        setPreferredSize(new Dimension(1620, 60));
        setBackground(AdminTheme.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));

        JLabel l1 = new JLabel("Department:"); l1.setFont(AdminTheme.FONT_TINY); l1.setBounds(16, 15, 90, 30); add(l1);
        cmbDept = new JComboBox<>(new String[]{ "All Departments", "1 - ICT", "2 - Engineering Tech", "3 - Bio Systems Tech" });
        cmbDept.setFont(AdminTheme.FONT_SMALL); cmbDept.setBounds(110, 12, 200, 36); cmbDept.addActionListener(e -> onFilterTrigger.run()); add(cmbDept);

        JLabel l2 = new JLabel("Batch:"); l2.setFont(AdminTheme.FONT_TINY); l2.setBounds(330, 15, 50, 30); add(l2);
        cmbBatch = new JComboBox<>(new String[]{ "All Batches", "2021", "2022", "2023", "2024" });
        cmbBatch.setFont(AdminTheme.FONT_SMALL); cmbBatch.setBounds(385, 12, 140, 36); cmbBatch.addActionListener(e -> onFilterTrigger.run()); add(cmbBatch);

        JLabel l3 = new JLabel("Semester:"); l3.setFont(AdminTheme.FONT_TINY); l3.setBounds(545, 15, 70, 30); add(l3);
        cmbSemester = new JComboBox<>(new String[]{ "All Semesters", "Semester 1", "Semester 2" });
        cmbSemester.setFont(AdminTheme.FONT_SMALL); cmbSemester.setBounds(620, 12, 160, 36); cmbSemester.addActionListener(e -> onFilterTrigger.run()); add(cmbSemester);

        JButton btnReset = new JButton("Reset Filters");
        btnReset.setFont(AdminTheme.FONT_TINY); btnReset.setBackground(AdminTheme.PASTEL_GREEN); btnReset.setForeground(AdminTheme.DEEP_GREEN);
        btnReset.setBounds(800, 12, 140, 36);
        btnReset.addActionListener(e -> {
            cmbDept.setSelectedIndex(0);
            cmbBatch.setSelectedIndex(0);
            cmbSemester.setSelectedIndex(0);
            onFilterTrigger.run();
        });
        add(btnReset);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(AdminTheme.FONT_TINY); btnRefresh.setBackground(AdminTheme.MINT); btnRefresh.setForeground(AdminTheme.DEEP_GREEN);
        btnRefresh.setBounds(1460, 12, 130, 36);
        btnRefresh.addActionListener(e -> onRefresh.run());
        add(btnRefresh);
    }

    public int getSelectedDepartmentId() { return cmbDept.getSelectedIndex(); }
    public int getSelectedBatchYear() {
        String b = (String) cmbBatch.getSelectedItem();
        if (b == null || b.startsWith("All")) return 0;
        try { return Integer.parseInt(b.trim()); } catch (Exception e) { return 0; }
    }
    public String getSelectedSemester() { return (String) cmbSemester.getSelectedItem(); }
}
