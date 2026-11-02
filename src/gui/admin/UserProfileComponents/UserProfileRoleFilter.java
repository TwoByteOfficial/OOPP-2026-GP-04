package gui.admin.UserProfileComponents;

import javax.swing.*;
import java.awt.*;

/**
 * UserProfileRoleFilter - Standalone component for the role filter combo box.
 */
public class UserProfileRoleFilter extends JPanel {

    private final Color WHITE        = Color.WHITE;
    private final Color BLACK        = Color.BLACK;
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);

    private final JComboBox<String> cmbRole;

    public UserProfileRoleFilter(Runnable onFilterChange) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 6, 0));
        setOpaque(false);

        JLabel lbl = new JLabel("Role:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(DEEP_GREEN);

        String[] roles = { "All Roles", "Admin", "Lecturer", "Technical Officer", "Undergraduate" };
        cmbRole = new JComboBox<>(roles);
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cmbRole.setBackground(WHITE);
        cmbRole.setForeground(BLACK);
        cmbRole.setPreferredSize(new Dimension(170, 38));

        cmbRole.setRenderer(new DefaultListCellRenderer() {
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
                l.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                l.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return l;
            }
        });

        cmbRole.addActionListener(e -> {
            if (onFilterChange != null) onFilterChange.run();
        });

        add(lbl);
        add(cmbRole);
    }

    public String getSelectedRole() {
        return (String) cmbRole.getSelectedItem();
    }

    public void reset() {
        cmbRole.setSelectedIndex(0);
    }
}
