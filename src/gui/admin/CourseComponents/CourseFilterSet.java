package gui.admin.CourseComponents;

import javax.swing.*;
import java.awt.*;

public class CourseFilterSet extends JPanel {

    private CourseSearchBar searchBar;
    private CourseDepartmentFilter deptFilter;
    private CourseSemesterFilter semesterFilter;

    public CourseFilterSet(Runnable onFilterTrigger) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 16, 12));
        setBackground(Color.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));

        searchBar = new CourseSearchBar(query -> onFilterTrigger.run());
        deptFilter = new CourseDepartmentFilter(dept -> onFilterTrigger.run());
        semesterFilter = new CourseSemesterFilter(sem -> onFilterTrigger.run());

        add(searchBar);
        add(deptFilter);
        add(semesterFilter);

        JButton btnReset = new JButton("Reset Filters");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnReset.setBackground(new Color(0xC8, 0xE6, 0xC9));
        btnReset.setForeground(new Color(0x1B, 0x5E, 0x20));
        btnReset.setFocusPainted(false);
        btnReset.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            searchBar.reset();
            deptFilter.reset();
            semesterFilter.reset();
            onFilterTrigger.run();
        });
        add(btnReset);
    }

    public String getSearchQuery() { return searchBar.getSearchQuery(); }
    public String getSelectedDepartment() { return deptFilter.getSelectedDepartment(); }
    public String getSelectedSemester() { return semesterFilter.getSelectedSemester(); }

    public int getSelectedDepartmentId() {
        String dept = getSelectedDepartment();
        if (dept == null || dept.startsWith("All")) return 0;
        if (dept.startsWith("1")) return 1;
        if (dept.startsWith("2")) return 2;
        if (dept.startsWith("3")) return 3;
        return 0;
    }
}
