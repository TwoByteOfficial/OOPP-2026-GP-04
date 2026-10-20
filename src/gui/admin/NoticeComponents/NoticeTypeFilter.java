package gui.admin.NoticeComponents;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class NoticeTypeFilter extends JPanel {
    private JComboBox<String> cmbType;

    public NoticeTypeFilter(Consumer<String> onFilterChange) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setOpaque(false);

        JLabel lbl = new JLabel("Notice Type:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.BLACK);
        add(lbl);

        String[] types = { "All Types", "General", "Academic", "Exam", "Emergency" };
        cmbType = new JComboBox<>(types);
        cmbType.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cmbType.setBackground(Color.WHITE);
        cmbType.setForeground(Color.BLACK);
        cmbType.setFocusable(false);
        cmbType.addActionListener(e -> onFilterChange.accept((String) cmbType.getSelectedItem()));
        add(cmbType);
    }

    public String getSelectedType() { return (String) cmbType.getSelectedItem(); }
    public void reset() { cmbType.setSelectedIndex(0); }
}
