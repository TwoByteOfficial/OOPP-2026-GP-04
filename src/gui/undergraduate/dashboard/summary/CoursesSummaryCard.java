package gui.undergraduate.dashboard.summary;

import gui.admin.AdminTheme;

public class CoursesSummaryCard extends SummaryCardPanel {
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