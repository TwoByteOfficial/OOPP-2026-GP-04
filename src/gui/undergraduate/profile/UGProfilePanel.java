package gui.undergraduate.profile;

import dao.undergraduate.UndergraduateProfileDAO;
import gui.admin.AdminTheme;
import gui.undergraduate.profile.header.ProfileHeaderPanel;
import gui.undergraduate.profile.info.AcademicDetailsCard;
import gui.undergraduate.profile.info.ContactSettingsCard;
import gui.undergraduate.profile.info.ProfileAvatarCard;
import gui.undergraduate.profile.model.UserProfileData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Module 1.8: Student Profile & Settings view coordinator connected directly to database.
 * Organizes the undergraduate profile screen into a two-column responsive split layout,
 * presenting identity credentials and academic standing on the left alongside
 * personal contact settings and live avatar customization on the right.
 */
public class UGProfilePanel extends JPanel {

    // Sub-view presentation and form card components
    private final ProfileAvatarCard avatarCard;
    private final AcademicDetailsCard detailsCard;
    private final ContactSettingsCard contactCard;

    /**
     * Constructs the primary undergraduate profile panel and arranges child views.
     *
     * @param userId   Logged-in student account primary key identifier
     * @param username Logged-in student institutional username
     */
    public UGProfilePanel(long userId, String username) {
        // Base container layout configuration and standard outer margin padding
        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // -------------------------------------------------------------
        // 1. Header Section (Module Title & Institutional Context)
        // -------------------------------------------------------------
        add(new ProfileHeaderPanel(), BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Main Content Area: Balanced 2-Column Split Layout
        // -------------------------------------------------------------
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 18, 0));
        splitPanel.setOpaque(false);

        // Left Column (Avatar Summary Card + Academic Placement Records)
        JPanel leftPanel = new JPanel(new BorderLayout(0, 14));
        leftPanel.setOpaque(false);

        avatarCard = new ProfileAvatarCard();
        detailsCard = new AcademicDetailsCard();
        leftPanel.add(avatarCard, BorderLayout.NORTH);
        leftPanel.add(detailsCard, BorderLayout.CENTER);

        // Right Column (Contact & Profile Picture Customization Settings)
        contactCard = new ContactSettingsCard(avatarCard);

        splitPanel.add(leftPanel);
        splitPanel.add(contactCard);

        add(splitPanel, BorderLayout.CENTER);

        // -------------------------------------------------------------
        // Database Data Hydration
        // -------------------------------------------------------------
        loadProfileData(userId, username);
    }

    /**
     * Fetches current undergraduate profile credentials from the database DAO layer
     * and hydrates the avatar card, academic placement card, and contact form controls.
     *
     * @param userId   Account primary key ID
     * @param username Student username identifier
     */
    private void loadProfileData(long userId, String username) {
        UserProfileData profile = UndergraduateProfileDAO.getProfile(userId, username);
        avatarCard.updateProfile(profile);
        detailsCard.updateDetails(profile);
        contactCard.updateDetails(profile);
    }
}