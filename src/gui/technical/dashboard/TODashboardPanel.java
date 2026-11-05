package gui.technical.dashboard;

import java.util.function.Consumer;
import dao.TechnicalOfficer.TechnicalOfficerProfileDAO;
import gui.theme.UniMisTheme;
import gui.technical.dashboard.header.DashboardHeaderPanel;
import gui.technical.dashboard.summary.DashboardSummaryPanel;
import gui.technical.dashboard.actions.DashboardQuickActionsPanel;
import gui.technical.dashboard.overview.DashboardOverviewPanel;
import gui.technical.dashboard.bottom.DashboardBottomPanel;
import model.TechnicalOfficer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * [OOP - INHERITANCE]: Extends javax.swing.JPanel to create the main Technical Officer Dashboard container.
 * 
 * [OOP - ABSTRACTION]: Aggregates modular sub-panels (header, summary cards, quick actions,
 * overview charts, timetable, notices), abstracting complex child layouts from the parent frame.
 */
public class TODashboardPanel extends JPanel {

    // [OOP - ENCAPSULATION]: Private session state and navigation callback
    private final String fullName;
    private final String role;
    private final Consumer<String> navigationHandler;
    private final long userId;
    private final DashboardHeaderPanel headerPanel;

    /**
     * Constructs the Technical Officer Dashboard landing page.
     *
     * @param userId            Database user ID of the officer
     * @param fullName          Full name of the officer
     * @param role              Role title ("Technical Officer")
     * @param navigationHandler [OOP - INTERFACE]: java.util.function.Consumer<String> callback
     */
    public TODashboardPanel(
            long userId,
            String fullName,
            String role,
            Consumer<String> navigationHandler) {

        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
        this.navigationHandler = navigationHandler;

        setLayout(new BorderLayout(0, 18));
        setBackground(UniMisTheme.LIGHT_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Welcome header banner with profile card avatar
        headerPanel = new DashboardHeaderPanel(this.fullName, this.role);
        add(headerPanel, BorderLayout.NORTH);

        // Core dashboard body
        add(buildDashboardContent(), BorderLayout.CENTER);

        // Load initial profile picture thumbnail in the top right card
        loadInitialProfilePicture();
    }

    /**
     * Loads the existing profile picture from the database and updates the header avatar.
     */
    private void loadInitialProfilePicture() {
        try {
            TechnicalOfficer officer = new TechnicalOfficerProfileDAO().getProfile(userId);
            if (officer != null && officer.getProfilePicture() != null) {
                headerPanel.setProfilePicture(officer.getProfilePicture());
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Updates the avatar profile picture in real-time when changed from My Profile.
     *
     * @param path The updated image file path or URL (or null if deleted)
     */
    public void updateProfilePicture(String path) {
        if (headerPanel != null) {
            headerPanel.setProfilePicture(path);
        }
    }

    /**
     * [OOP - ENCAPSULATION]: Private helper assembling sub-panels into a vertical box layout.
     */
    private JPanel buildDashboardContent() {

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        DashboardSummaryPanel summaryPanel = new DashboardSummaryPanel(userId);
        DashboardQuickActionsPanel quickActionsPanel = new DashboardQuickActionsPanel(navigationHandler);
        DashboardOverviewPanel overviewPanel = new DashboardOverviewPanel(userId, navigationHandler);
        DashboardBottomPanel bottomPanel = new DashboardBottomPanel(userId);

        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        quickActionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        overviewPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(summaryPanel);
        content.add(Box.createVerticalStrut(18));

        content.add(quickActionsPanel);
        content.add(Box.createVerticalStrut(18));

        content.add(overviewPanel);
        content.add(Box.createVerticalStrut(18));

        content.add(bottomPanel);

        return content;
    }
}
