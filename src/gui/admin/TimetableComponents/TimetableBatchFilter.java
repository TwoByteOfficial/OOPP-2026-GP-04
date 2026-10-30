package gui.admin.TimetableComponents;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class TimetableBatchFilter extends JPanel {
    private JComboBox<String> cmbBatch;

    public TimetableBatchFilter(Consumer<String> onFilterChange) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setOpaque(false);

        JLabel lbl = new JLabel("Batch Year:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.BLACK);
        add(lbl);

        String[] batches = { "All Batches", "2021", "2022", "2023", "2024" };
        cmbBatch = new JComboBox<>(batches);
        cmbBatch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cmbBatch.setBackground(Color.WHITE);
        cmbBatch.setForeground(Color.BLACK);
        cmbBatch.setFocusable(false);
        cmbBatch.addActionListener(e -> onFilterChange.accept((String) cmbBatch.getSelectedItem()));
        add(cmbBatch);
    }

    public String getSelectedBatch() { return (String) cmbBatch.getSelectedItem(); }

    public int getSelectedBatchYear() {
        String b = getSelectedBatch();
        if (b == null || b.startsWith("All")) return 0;
        try {
            return Integer.parseInt(b.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public void reset() { cmbBatch.setSelectedIndex(0); }
}
