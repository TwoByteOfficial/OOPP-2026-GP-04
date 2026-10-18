package gui.undergraduate.dashboard.summary;

import gui.admin.AdminTheme;

import java.awt.Color;

public class AttendanceSummaryCard extends SummaryCardPanel {
    public AttendanceSummaryCard(double attendancePercentage) {
        super(
                "📊",
                attendancePercentage >= 80.0 ? AdminTheme.FOREST_GREEN : new Color(0xDC, 0x35, 0x45),
                String.format("%.1f%%", attendancePercentage),
                "Overall Attendance",
                attendancePercentage >= 80.0 ? "Exam Eligible (≥80%)" : "At Risk (<80%)"
        );
    }
}