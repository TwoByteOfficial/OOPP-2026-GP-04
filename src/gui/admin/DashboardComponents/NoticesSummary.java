package gui.admin.DashboardComponents;

import gui.admin.AdminTheme;

/** Placeholder notice-summary stat card. */
public class NoticesSummary extends StatCard {

    public NoticesSummary(long userId, String username, String fullName,
                          String email, String role) {
        super("📢",
                AdminTheme.MEDIUM_GREEN,
                "Published Notices",
                "0",
                "Loaded from NoticeDAO later");
    }
}