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

public class UGProfilePanel extends JPanel {

    private final ProfileAvatarCard avatarCard;
    private final AcademicDetailsCard detailsCard;
    private final ContactSettingsCard contactCard;

    public UGProfilePanel(long userId, String username) {
        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // 1. Header Section
        add(new ProfileHeaderPanel(), BorderLayout.NORTH);

        // 2. Main Content Split into 2 equal columns
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 18, 0));
        splitPanel.setOpaque(false);

        // Left Panel (Avatar + Academic Placement)
        JPanel leftPanel = new JPanel(new BorderLayout(0, 14));
        leftPanel.setOpaque(false);

        avatarCard = new ProfileAvatarCard();
        detailsCard = new AcademicDetailsCard();
        leftPanel.add(avatarCard, BorderLayout.NORTH);
        leftPanel.add(detailsCard, BorderLayout.CENTER);

        // Right Panel (Contact & Profile Picture Settings)
        contactCard = new ContactSettingsCard(avatarCard);

        splitPanel.add(leftPanel);
        splitPanel.add(contactCard);

        add(splitPanel, BorderLayout.CENTER);

        // Load profile data from MySQL database
        loadProfileData(userId, username);
    }

    private void loadProfileData(long userId, String username) {
        UserProfileData profile = UndergraduateProfileDAO.getProfile(userId, username);
        avatarCard.updateProfile(profile);
        detailsCard.updateDetails(profile);
        contactCard.updateDetails(profile);
    }
}