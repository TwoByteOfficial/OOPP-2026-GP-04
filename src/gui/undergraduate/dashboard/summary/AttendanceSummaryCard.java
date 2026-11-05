package gui.undergraduate.dashboard.summary;

import gui.admin.AdminTheme;

import java.awt.Color;

/**
 * Summary metric card displaying student overall attendance percentage and exam eligibility status.
 * Dynamically highlights in green if eligible (>=80%) or red if at risk (<80%).
 */
public class AttendanceSummaryCard extends SummaryCardPanel {

    /**
     * Initializes attendance summary card with icon, dynamic color theme, formatted percentage, and status subtitle.
     */
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