package gui.admin.EnrollmentComponents;

import javax.swing.*;
import java.awt.*;

/**
 * EnrollmentSemesterFilter - Standalone filter dropdown for semester selection.
 */
public class EnrollmentSemesterFilter extends JPanel {

    private final Color WHITE        = Color.WHITE;
    private final Color BLACK        = Color.BLACK;
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);

    private final JComboBox<String> cmbSemester;

    public EnrollmentSemesterFilter(Runnable onFilterChange) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 6, 0));
        setOpaque(false);

        JLabel lbl = new JLabel("Semester:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(DEEP_GREEN);

        String[] semesters = { "All Semesters", "Semester I", "Semester II" };
        cmbSemester = new JComboBox<>(semesters);
        cmbSemester.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cmbSemester.setBackground(WHITE);
        cmbSemester.setForeground(BLACK);
        cmbSemester.setPreferredSize(new Dimension(150, 38));

        cmbSemester.setRenderer(new DefaultListCellRenderer() {
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

        cmbSemester.addActionListener(e -> {
            if (onFilterChange != null) onFilterChange.run();
        });

        add(lbl);
        add(cmbSemester);
    }

    public String getSelectedSemester() {
        return (String) cmbSemester.getSelectedItem();
    }

    public void reset() {
        cmbSemester.setSelectedIndex(0);
    }
}
