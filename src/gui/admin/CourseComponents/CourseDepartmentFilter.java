package gui.admin.CourseComponents;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class CourseDepartmentFilter extends JPanel {
    private JComboBox<String> cmbDept;

    public CourseDepartmentFilter(Consumer<String> onFilterChange) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setOpaque(false);

        JLabel lbl = new JLabel("Department:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.BLACK);
        add(lbl);

        String[] depts = { "All Departments", "1 - ICT", "2 - Engineering Technology", "3 - Bio Systems Technology" };
        cmbDept = new JComboBox<>(depts);
        cmbDept.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cmbDept.setBackground(Color.WHITE);
        cmbDept.setForeground(Color.BLACK);
        cmbDept.setFocusable(false);
        cmbDept.addActionListener(e -> onFilterChange.accept((String) cmbDept.getSelectedItem()));
        add(cmbDept);
    }

    public String getSelectedDepartment() { return (String) cmbDept.getSelectedItem(); }
    public void reset() { cmbDept.setSelectedIndex(0); }

    public int getSelectedDepartmentId() {
        String dept = getSelectedDepartment();
        if (dept == null || dept.startsWith("All")) return 0;
        if (dept.startsWith("1")) return 1;
        if (dept.startsWith("2")) return 2;
        if (dept.startsWith("3")) return 3;
        return 0;
    }
}
