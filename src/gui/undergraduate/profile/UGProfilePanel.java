package gui.undergraduate.profile;

import dao.undergraduate.UndergraduateProfileDAO;
import gui.admin.AdminTheme;
import gui.undergraduate.profile.header.ProfileHeaderPanel;
import gui.undergraduate.profile.info.AcademicDetailsCard;
import gui.undergraduate.profile.info.ProfileAvatarCard;
import gui.undergraduate.profile.model.UserProfileData;
import gui.undergraduate.profile.security.PasswordChangeCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Module 1.8: My Profile & Account Security dynamic coordinator panel.
 */
public class UGProfilePanel extends JPanel {

    private final ProfileAvatarCard avatarCard;
    private final AcademicDetailsCard detailsCard;
    private final PasswordChangeCard passwordCard;

    public UGProfilePanel(long userId, String username) {
        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // 1. Top Section: Header
        add(new ProfileHeaderPanel(), BorderLayout.NORTH);

        // 2. Center Section: 2 Columns Split
        JPanel splitPanel = new JPanel(new BorderLayout(16, 0));
        splitPanel.setOpaque(false);

        // Left Panel: Avatar + Academic Placement
        JPanel leftPanel = new JPanel(new BorderLayout(0, 14));
        leftPanel.setOpaque(false);

        avatarCard = new ProfileAvatarCard();
        detailsCard = new AcademicDetailsCard();
        leftPanel.add(avatarCard, BorderLayout.NORTH);
        leftPanel.add(detailsCard, BorderLayout.CENTER);

        // Right Panel: Security / Password Change
        passwordCard = new PasswordChangeCard();
        passwordCard.setPreferredSize(new Dimension(360, 0));

        splitPanel.add(leftPanel, BorderLayout.CENTER);
        splitPanel.add(passwordCard, BorderLayout.EAST);

        add(splitPanel, BorderLayout.CENTER);

        // Load profile dynamically for the logged-in student
        loadProfileData(userId, username);
    }

    private void loadProfileData(long userId, String username) {
        UserProfileData profile = UndergraduateProfileDAO.getProfile(userId, username);
        avatarCard.updateProfile(profile);
        detailsCard.updateDetails(profile);
    }
}