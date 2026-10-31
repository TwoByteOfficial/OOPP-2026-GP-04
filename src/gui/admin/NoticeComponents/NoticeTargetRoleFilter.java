package gui.admin.NoticeComponents;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class NoticeTargetRoleFilter extends JPanel {
    private JComboBox<String> cmbRole;

    public NoticeTargetRoleFilter(Consumer<String> onFilterChange) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setOpaque(false);

        JLabel lbl = new JLabel("Target Role:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.BLACK);
        add(lbl);

        String[] roles = { "All Roles", "Undergraduate", "Lecturer", "Technical Officer", "Admin" };
        cmbRole = new JComboBox<>(roles);
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cmbRole.setBackground(Color.WHITE);
        cmbRole.setForeground(Color.BLACK);
        cmbRole.setFocusable(false);
        cmbRole.addActionListener(e -> onFilterChange.accept((String) cmbRole.getSelectedItem()));
        add(cmbRole);
    }

    public String getSelectedRole() { return (String) cmbRole.getSelectedItem(); }
    public void reset() { cmbRole.setSelectedIndex(0); }
}
