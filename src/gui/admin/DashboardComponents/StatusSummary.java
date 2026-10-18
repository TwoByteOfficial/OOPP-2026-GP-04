package gui.admin.DashboardComponents;

import gui.admin.AdminTheme;

/** Placeholder system-status stat card. */
public class StatusSummary extends StatCard {

    public StatusSummary(long userId, String username, String fullName,
                         String email, String role) {
        super("✅",
                AdminTheme.DEEP_GREEN,
                "System Status",
                "Online",
                "All services running");
    }
}