package gui.undergraduate.dashboard.summary;

import gui.admin.AdminTheme;

/**
 * Summary metric card displaying the total number of enrolled courses for the current semester.
 * Features an educational book icon and a forest green theme badge.
 */
public class CoursesSummaryCard extends SummaryCardPanel {

    /**
     * Initializes courses summary card with icon, theme color, enrolled count value, and semester subtitle.
     */
    public CoursesSummaryCard(int enrolledCount) {
        super(
                "📚",
                AdminTheme.FOREST_GREEN,
                String.valueOf(enrolledCount),
                "Enrolled Modules",
                "Current Semester"
        );
    }
}