package gui.undergraduate;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Modular Sidebar for Undergraduate Dashboard.
 * 100% styled to mirror the Admin Sidebar (AdminTheme).
 */
public class UGSidebarPanel extends JPanel {

    private final Consumer<String> onNavigate;
    private final Runnable onLogout;
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private String currentActiveKey = "dashboard";

    public UGSidebarPanel(Consumer<String> onNavigate, Runnable onLogout) {
        this.onNavigate = onNavigate;
        this.onLogout = onLogout;

        setPreferredSize(new Dimension(AdminTheme.SIDEBAR_WIDTH, 0));
        setBackground(AdminTheme.FRESH_GREEN); // Admin Sidebar Background
        setLayout(new BorderLayout());

        initSidebar();
    }

    private void initSidebar() {
        // --- 1. Top Section: Brand Logo ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(25, 20, 20, 20));

        JLabel logoLabel = new JLabel("UniMis");
        logoLabel.setFont(AdminTheme.FONT_LOGO);
        logoLabel.setForeground(AdminTheme.DEEP_GREEN);
        topPanel.add(logoLabel, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        // --- 2. Center Section: Navigation Buttons List ---
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(5, 12, 10, 12));

        // Undergraduate Modules (1.1 - 1.7 සහ Profile)
        addNavButton(menuPanel, "dashboard",  "Dashboard");
        addNavButton(menuPanel, "courses",    "My Courses");
        addNavButton(menuPanel, "attendance", "Attendance");
        addNavButton(menuPanel, "grades",     "Grades & GPA");
        addNavButton(menuPanel, "medicals",   "Medicals");
        addNavButton(menuPanel, "timetable",  "Timetables");
        addNavButton(menuPanel, "notices",    "Notices");
        addNavButton(menuPanel, "profile",    "My Profile");

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Bottom Section: Logout Button ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(15, 15, 20, 15));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(AdminTheme.font(Font.BOLD, 16));
        logoutBtn.setBackground(AdminTheme.DANGER);
        logoutBtn.setForeground(AdminTheme.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(new EmptyBorder(12, 15, 12, 15));
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            if (onLogout != null) onLogout.run();
        });

        bottomPanel.add(logoutBtn, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // first "dashboard" tab is active done
        setActiveTab("dashboard");
    }

    private void addNavButton(JPanel container, String key, String title) {
        JButton btn = new JButton(title);
        btn.setFont(AdminTheme.font(Font.BOLD, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setPreferredSize(new Dimension(AdminTheme.SIDEBAR_WIDTH - 24, 44));
        btn.setBorder(new EmptyBorder(0, 18, 0, 10));

        // Mouse Hover and Click Effects (Admin Style)
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!key.equals(currentActiveKey)) {
                    btn.setBackground(AdminTheme.MINT); // Hover කරද්දී තවත් ලා පාට වීම
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!key.equals(currentActiveKey)) {
                    btn.setBackground(AdminTheme.PASTEL_GREEN); // සාමාන්‍ය ලා කොළ පසුබිම
                }
            }
        });

        btn.addActionListener(e -> {
            setActiveTab(key);
            if (onNavigate != null) {
                onNavigate.accept(key);
            }
        });

        navButtons.put(key, btn);
        container.add(btn);
        container.add(Box.createVerticalStrut(7)); // Buttons spaces
    }


    public void setActiveTab(String activeKey) {
        this.currentActiveKey = activeKey;

        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            String key = entry.getKey();
            JButton btn = entry.getValue();

            if (key.equals(activeKey)) {
                // Active State:  background and Deep Green text
                btn.setBackground(AdminTheme.WHITE);
                btn.setForeground(AdminTheme.DEEP_GREEN);
            } else {
                // Normal Inactive State: Pastel Green background and Deep Green text
                btn.setBackground(AdminTheme.PASTEL_GREEN);
                btn.setForeground(AdminTheme.DEEP_GREEN);
            }
        }
        repaint();
    }
}