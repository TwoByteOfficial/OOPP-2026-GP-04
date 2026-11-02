package gui.admin;

import dao.admin.AdminUserDAO;
import gui.admin.SettingsComponents.*;

import javax.swing.*;
import java.awt.*;

public class AdminSettings extends JPanel {

    private final AdminUserDAO userDAO;

    public AdminSettings(long userId, String username, String fullName, String email, String role) {
        setLayout(null); // Absolute positioning with setBounds
        setBackground(new Color(0xE8, 0xF5, 0xE9));
        setPreferredSize(new Dimension(1620, 950));
        setBounds(0, 0, 1620, 950);

        this.userDAO = new AdminUserDAO();

        SettingsTitleBar titleBar = new SettingsTitleBar();
        titleBar.setBounds(40, 30, 1540, 80);
        add(titleBar);

        GeneralSettings profileSettings = new GeneralSettings(userId, fullName, email, userDAO);
        profileSettings.setBounds(40, 130, 750, 680);
        add(profileSettings);

        AccountSettings accountSettings = new AccountSettings(username, userDAO);
        accountSettings.setBounds(820, 130, 750, 680);
        add(accountSettings);
    }
}