package gui.admin.NoticeComponents;

import gui.admin.AdminTheme;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class NoticeFilterBar extends JPanel {

    private JTextField txtSearch;
    private JComboBox<String> cmbType;
    private JComboBox<String> cmbRole;

    public NoticeFilterBar(Runnable onFilterTrigger, Runnable onRefresh) {
        setLayout(null);
        setPreferredSize(new Dimension(1620, 60));
        setBackground(AdminTheme.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));

        JLabel lblSearch = new JLabel("Search Notices:");
        lblSearch.setFont(AdminTheme.FONT_TINY);
        lblSearch.setForeground(AdminTheme.BLACK);
        lblSearch.setBounds(16, 15, 120, 30);
        add(lblSearch);

        txtSearch = new JTextField();
        txtSearch.setFont(AdminTheme.FONT_SMALL);
        txtSearch.setBounds(140, 12, 220, 36);
        txtSearch.addActionListener(e -> onFilterTrigger.run());
        add(txtSearch);

        JLabel lblType = new JLabel("Type:");
        lblType.setFont(AdminTheme.FONT_TINY);
        lblType.setBounds(380, 15, 50, 30);
        add(lblType);

        cmbType = new JComboBox<>(new String[]{ "All Types", "General", "Academic", "Exam", "Emergency" });
        cmbType.setFont(AdminTheme.FONT_SMALL);
        cmbType.setBounds(435, 12, 160, 36);
        cmbType.addActionListener(e -> onFilterTrigger.run());
        add(cmbType);

        JLabel lblRole = new JLabel("Role:");
        lblRole.setFont(AdminTheme.FONT_TINY);
        lblRole.setBounds(615, 15, 50, 30);
        add(lblRole);

        cmbRole = new JComboBox<>(new String[]{ "All Roles", "Undergraduate", "Lecturer", "Technical Officer", "Admin" });
        cmbRole.setFont(AdminTheme.FONT_SMALL);
        cmbRole.setBounds(670, 12, 180, 36);
        cmbRole.addActionListener(e -> onFilterTrigger.run());
        add(cmbRole);

        JButton btnReset = new JButton("Reset Filters");
        btnReset.setFont(AdminTheme.FONT_TINY);
        btnReset.setBackground(AdminTheme.PASTEL_GREEN);
        btnReset.setForeground(AdminTheme.DEEP_GREEN);
        btnReset.setBounds(870, 12, 140, 36);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cmbType.setSelectedIndex(0);
            cmbRole.setSelectedIndex(0);
            onFilterTrigger.run();
        });
        add(btnReset);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(AdminTheme.FONT_TINY);
        btnRefresh.setBackground(AdminTheme.MINT);
        btnRefresh.setForeground(AdminTheme.DEEP_GREEN);
        btnRefresh.setBounds(1460, 12, 130, 36);
        btnRefresh.addActionListener(e -> onRefresh.run());
        add(btnRefresh);
    }

    public String getSearchQuery() { return txtSearch.getText().trim(); }
    public String getSelectedType() { return (String) cmbType.getSelectedItem(); }
    public String getSelectedRole() { return (String) cmbRole.getSelectedItem(); }
}
