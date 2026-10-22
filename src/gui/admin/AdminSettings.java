package gui.admin;

import dao.admin.AdminUserDAO;
import gui.admin.SettingsComponents.*;

import javax.swing.*;
import java.awt.*;

public class AdminSettings extends JPanel {

    private final AdminUserDAO userDAO;

    public AdminSettings(long userId, String username, String fullName, String email, String role) {
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(0xE8, 0xF5, 0xE9));
        setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

        this.userDAO = new AdminUserDAO();

        SettingsTitleBar titleBar = new SettingsTitleBar();
        add(titleBar, BorderLayout.NORTH);

        JPanel mainCard = new JPanel(new GridLayout(1, 2, 20, 0));
        mainCard.setBackground(Color.WHITE);
        mainCard.setPreferredSize(new Dimension(880, 600));
        mainCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));

        GeneralSettings profileSettings = new GeneralSettings(userId, fullName, email, userDAO);
        AccountSettings accountSettings = new AccountSettings(username);

        mainCard.add(profileSettings);
        mainCard.add(accountSettings);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(mainCard);

        add(centerWrapper, BorderLayout.CENTER);
    }
}
