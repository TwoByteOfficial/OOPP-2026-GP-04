package gui.admin.TimetableComponents;

import javax.swing.*;
import java.awt.*;

public class TimetableFilterSet extends JPanel {

    private TimetableDepartmentFilter deptFilter;
    private TimetableBatchFilter batchFilter;
    private TimetableSemesterFilter semesterFilter;

    public TimetableFilterSet(Runnable onFilterTrigger) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 16, 12));
        setBackground(Color.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));

        deptFilter = new TimetableDepartmentFilter(d -> onFilterTrigger.run());
        batchFilter = new TimetableBatchFilter(b -> onFilterTrigger.run());
        semesterFilter = new TimetableSemesterFilter(s -> onFilterTrigger.run());

        add(deptFilter);
        add(batchFilter);
        add(semesterFilter);

        JButton btnReset = new JButton("Reset Filters");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnReset.setBackground(new Color(0xC8, 0xE6, 0xC9));
        btnReset.setForeground(new Color(0x1B, 0x5E, 0x20));
        btnReset.setFocusPainted(false);
        btnReset.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            deptFilter.reset();
            batchFilter.reset();
            semesterFilter.reset();
            onFilterTrigger.run();
        });
        add(btnReset);
    }

    public String getSelectedDepartment() { return deptFilter.getSelectedDepartment(); }
    public String getSelectedBatch() { return batchFilter.getSelectedBatch(); }
    public String getSelectedSemester() { return semesterFilter.getSelectedSemester(); }

    public int getSelectedDepartmentId() {
        String dept = getSelectedDepartment();
        if (dept == null || dept.startsWith("All")) return 0;
        if (dept.startsWith("1")) return 1;
        if (dept.startsWith("2")) return 2;
        if (dept.startsWith("3")) return 3;
        return 0;
    }

    public int getSelectedBatchYear() {
        String b = getSelectedBatch();
        if (b == null || b.startsWith("All")) return 0;
        try {
            return Integer.parseInt(b.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
