package gui.undergraduate;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Undergraduate Left Sidebar Navigation Panel
 */
public class UGSidebarPanel extends JPanel {

    private final Consumer<String> onNavigate;
    private final Runnable onLogout;
    private final List<JButton> navButtons = new ArrayList<>();
    private String activeTabKey = "dashboard";

    public UGSidebarPanel(Consumer<String> onNavigate, Runnable onLogout) {
        this.onNavigate = onNavigate;
        this.onLogout = onLogout;

        setPreferredSize(new Dimension(AdminTheme.SIDEBAR_WIDTH, 0));
        setBackground(AdminTheme.PASTEL_GREEN);
        setLayout(new BorderLayout());

        // 1. Top Logo Section (UniMis)
        add(buildLogoSection(), BorderLayout.NORTH);

        // 2. Navigation Menu Items
        add(buildMenuSection(), BorderLayout.CENTER);

        // 3. Logout Button at the Bottom
        add(buildLogoutSection(), BorderLayout.SOUTH);
    }

    private JPanel buildLogoSection() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        p.setOpaque(false);

        JLabel logoLabel = new JLabel("UniMis");
        logoLabel.setFont(AdminTheme.FONT_LOGO);
        logoLabel.setForeground(AdminTheme.DEEP_GREEN);
        p.add(logoLabel);
        return p;
    }

    private JPanel buildMenuSection() {
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        // 1.1 සිට 1.7 දක්වා Navigation Buttons
        addNavItem(menuPanel, "Dashboard", "dashboard");
        addNavItem(menuPanel, "My Courses", "courses");
        addNavItem(menuPanel, "Attendance", "attendance");
        addNavItem(menuPanel, "Grades & GPA", "grades");
        addNavItem(menuPanel, "Medicals", "medicals");
        addNavItem(menuPanel, "Timetables", "timetable");
        addNavItem(menuPanel, "Notices", "notices");
        addNavItem(menuPanel, "My Profile", "profile");

        return menuPanel;
    }

    private void addNavItem(JPanel container, String title, String key) {
        JButton btn = new JButton(title);
        btn.setFont(AdminTheme.FONT_MENU);
        btn.setForeground(AdminTheme.DEEP_GREEN);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setPreferredSize(new Dimension(230, 46));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 18, 0, 10));

        updateButtonStyle(btn, key.equals(activeTabKey));

        btn.addActionListener(e -> {
            setActiveTab(key);
            if (onNavigate != null) onNavigate.accept(key);
        });

        navButtons.add(btn);
        container.add(btn);
        container.add(Box.createVerticalStrut(4));
    }

    public void setActiveTab(String key) {
        this.activeTabKey = key;
        for (JButton b : navButtons) {
            String title = b.getText().toLowerCase().replace(" ", "").replace("&", "");
            boolean isActive = title.contains(key.toLowerCase());
            updateButtonStyle(b, isActive);
        }
    }

    private void updateButtonStyle(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(AdminTheme.WHITE);
            btn.setForeground(AdminTheme.FOREST_GREEN);
            btn.setFont(AdminTheme.FONT_BODY_BOLD);
        } else {
            btn.setBackground(AdminTheme.PASTEL_GREEN);
            btn.setForeground(AdminTheme.DEEP_GREEN);
            btn.setFont(AdminTheme.FONT_MENU);
        }
    }

    private JPanel buildLogoutSection() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(15, 12, 18, 12));

        JButton logoutBtn = AdminTheme.dangerButton("Logout");
        logoutBtn.addActionListener(e -> {
            if (onLogout != null) onLogout.run();
        });

        p.add(logoutBtn, BorderLayout.CENTER);
        return p;
    }
}