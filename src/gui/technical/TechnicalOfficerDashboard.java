
package gui.technical;

import gui.login.Login;
import gui.technical.dashboard.TODashboardPanel;

import javax.swing.*;
import java.awt.*;

public class TechnicalOfficerDashboard extends JFrame {

    private final long userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pageContainer = new JPanel(cardLayout);

    public TechnicalOfficerDashboard(
            long userId,
            String username,
            String fullName,
            String email,
            String role) {

        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;

        setTitle("UniMIS - Technical Officer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setResizable(true);

        // Dashboard page
        TODashboardPanel dashboardPanel =
                new TODashboardPanel(fullName, role);

        pageContainer.add(dashboardPanel, "Dashboard");

        // Temporary pages for navigation testing
        addPlaceholderPage("Attendance");
        addPlaceholderPage("Medical Records");
        addPlaceholderPage("Undergraduates");
        addPlaceholderPage("Timetables");
        addPlaceholderPage("Notices");
        addPlaceholderPage("My Profile");

        // Sidebar
        TOSidebarPanel sidebar = new TOSidebarPanel(
                fullName,
                pageName -> cardLayout.show(pageContainer, pageName),
                this::logout
        );

        // Main window layout
        JPanel root = new JPanel(new BorderLayout());
        root.add(sidebar, BorderLayout.WEST);
        root.add(pageContainer, BorderLayout.CENTER);

        setContentPane(root);
        setLocationRelativeTo(null);
    }

    private void addPlaceholderPage(String pageName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 251));

        JLabel title = new JLabel(
                pageName + " - Under Development",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(24, 58, 99));

        panel.add(title, BorderLayout.CENTER);
        pageContainer.add(panel, pageName);
    }

    private void logout() {
        dispose();
        Login.showLogin();
    }
}
