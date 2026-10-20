package gui.admin;

import dao.admin.AdminUserDAO;
import gui.admin.SettingsComponents.*;

import javax.swing.*;
import java.awt.*;

public class AdminSettings extends JPanel {

    private final long userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    private AdminUserDAO userDAO;

    public AdminSettings(long userId, String username, String fullName, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;

        setLayout(null);
        setBounds(0, 0, 1660, 1080);
        setBackground(new Color(0xE8, 0xF5, 0xE9));

        userDAO = new AdminUserDAO();

        SettingsTitleBar titleBar = new SettingsTitleBar();
        titleBar.setLocation(20, 20);
        add(titleBar);

        JPanel pnlGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        pnlGrid.setOpaque(false);
        pnlGrid.setBounds(20, 120, 1620, 920);

        GeneralSettings general = new GeneralSettings(userId, fullName, email, userDAO);
        AccountSettings account = new AccountSettings(username);
        AppearanceSettings appearance = new AppearanceSettings();
        SystemSettings system = new SystemSettings();

        pnlGrid.add(general);
        pnlGrid.add(account);
        pnlGrid.add(appearance);
        pnlGrid.add(system);

        add(pnlGrid);
    }
}