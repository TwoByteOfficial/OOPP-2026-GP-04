package gui.admin.EnrollmentComponents;

import javax.swing.*;
import java.awt.*;

/**
 * EnrollmentFilterSet - Composite filter panel aggregating SearchBar, StatusFilter, SemesterFilter, Search button, and Reset button.
 */
public class EnrollmentFilterSet extends JPanel {

    private final Color WHITE        = Color.WHITE;
    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);

    private final EnrollmentSearchBar searchBar;
    private final EnrollmentStatusFilter statusFilter;
    private final EnrollmentSemesterFilter semesterFilter;
    private final JButton btnSearch;
    private final JButton btnReset;

    public EnrollmentFilterSet(Runnable onFilterChanged) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 16, 10));
        setBackground(WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));

        searchBar = new EnrollmentSearchBar(onFilterChanged);
        statusFilter = new EnrollmentStatusFilter(onFilterChanged);
        semesterFilter = new EnrollmentSemesterFilter(onFilterChanged);

        btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSearch.setBackground(new Color(0x38, 0x8E, 0x3C));
        btnSearch.setForeground(WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(100, 38));
        btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(e -> {
            if (onFilterChanged != null) onFilterChanged.run();
        });

        btnReset = new JButton("Reset Filters");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnReset.setBackground(PASTEL_GREEN);
        btnReset.setForeground(DEEP_GREEN);
        btnReset.setFocusPainted(false);
        btnReset.setPreferredSize(new Dimension(130, 38));
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            searchBar.clear();
            statusFilter.reset();
            semesterFilter.reset();
            if (onFilterChanged != null) onFilterChanged.run();
        });

        add(searchBar);
        add(statusFilter);
        add(semesterFilter);
        add(btnSearch);
        add(btnReset);
    }

    public String getSearchQuery() { return searchBar.getSearchQuery(); }
    public String getSelectedStatus() { return statusFilter.getSelectedStatus(); }
    public String getSelectedSemester() { return semesterFilter.getSelectedSemester(); }
}
