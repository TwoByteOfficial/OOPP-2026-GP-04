package gui.admin.DashboardComponents;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Simple stat card showing a placeholder "Your User ID" + logged-in username.
 * Will later show total user count from UserDAO.
 */
public class UserSummary extends StatCard {

    public UserSummary(long userId, String username, String fullName,
                       String email, String role) {
        super("👥",
                AdminTheme.PRIMARY,
                "Logged in as",
                username == null || username.isBlank() ? "-" : username,
                "User ID: " + userId);
    }
}