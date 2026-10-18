package gui.admin.DashboardComponents;

import gui.admin.AdminTheme;

/** Placeholder attendance-summary stat card. */
public class AttendanceSummary extends StatCard {

    public AttendanceSummary(long userId, String username, String fullName,
                             String email, String role) {
        super("📅",
                AdminTheme.FOREST_GREEN,
                "Attendance Sessions",
                "0",
                "Loaded from AttendanceDAO later");
    }
}