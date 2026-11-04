package gui.admin.TimetableComponents;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class TimetableSemesterFilter extends JPanel {
    private JComboBox<String> cmbSemester;

    public TimetableSemesterFilter(Consumer<String> onFilterChange) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setOpaque(false);

        JLabel lbl = new JLabel("Semester:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.BLACK);
        add(lbl);

        String[] sems = { "All Semesters", "Semester 1", "Semester 2" };
        cmbSemester = new JComboBox<>(sems);
        cmbSemester.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cmbSemester.setBackground(Color.WHITE);
        cmbSemester.setForeground(Color.BLACK);
        cmbSemester.setFocusable(false);
        cmbSemester.addActionListener(e -> onFilterChange.accept((String) cmbSemester.getSelectedItem()));
        add(cmbSemester);
    }

    public String getSelectedSemester() { return (String) cmbSemester.getSelectedItem(); }
    public void reset() { cmbSemester.setSelectedIndex(0); }
}
