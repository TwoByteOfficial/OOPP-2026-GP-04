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
 * Custom painted flat buttons to prevent Windows Look-and-Feel grey overrides.
 */
public class UGSidebarPanel extends JPanel {

    private final Consumer<String> onNavigate;
    private final Runnable onLogout;
    private final Map<String, NavButton> navButtons = new LinkedHashMap<>();
    private String currentActiveKey = "dashboard";

    public UGSidebarPanel(Consumer<String> onNavigate, Runnable onLogout) {
        this.onNavigate = onNavigate;
        this.onLogout = onLogout;

        setPreferredSize(new Dimension(AdminTheme.SIDEBAR_WIDTH, 0));
        setBackground(AdminTheme.FRESH_GREEN);
        setLayout(new BorderLayout());

        initSidebar();
    }

    private void initSidebar() {
        // --- 1. Top Section: Logo ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(25, 22, 20, 22));

        JLabel logoLabel = new JLabel("UniMis");
        logoLabel.setFont(AdminTheme.FONT_LOGO);
        logoLabel.setForeground(AdminTheme.DEEP_GREEN);
        topPanel.add(logoLabel, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        // --- 2. Center Section: Navigation Buttons ---
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(5, 12, 10, 12));

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

        // --- 3. Bottom Section: Solid Red Logout Button ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(15, 15, 20, 15));

        JButton logoutBtn = new JButton("Logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AdminTheme.DANGER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoutBtn.setFont(AdminTheme.font(Font.BOLD, 16));
        logoutBtn.setForeground(AdminTheme.WHITE);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(new EmptyBorder(12, 15, 12, 15));
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            if (onLogout != null) onLogout.run();
        });

        bottomPanel.add(logoutBtn, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setActiveTab("dashboard");
    }

    private void addNavButton(JPanel container, String key, String title) {
        NavButton btn = new NavButton(title);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setPreferredSize(new Dimension(AdminTheme.SIDEBAR_WIDTH - 24, 44));

        btn.addActionListener(e -> {
            setActiveTab(key);
            if (onNavigate != null) onNavigate.accept(key);
        });

        navButtons.put(key, btn);
        container.add(btn);
        container.add(Box.createVerticalStrut(7));
    }

    public void setActiveTab(String activeKey) {
        this.currentActiveKey = activeKey;
        for (Map.Entry<String, NavButton> entry : navButtons.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(activeKey));
        }
        repaint();
    }

    /**
     * Custom rendered flat button that bypasses native OS bevels and borders.
     */
    private static class NavButton extends JButton {
        private boolean isActive = false;
        private boolean isHovered = false;

        NavButton(String text) {
            super(text);
            setFont(AdminTheme.font(Font.BOLD, 15));
            setForeground(AdminTheme.DEEP_GREEN);
            setHorizontalAlignment(SwingConstants.LEFT);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(new EmptyBorder(0, 18, 0, 10));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        void setActive(boolean active) {
            this.isActive = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bgColor;
            if (isActive) {
                bgColor = AdminTheme.WHITE;
            } else if (isHovered) {
                bgColor = AdminTheme.MINT;
            } else {
                bgColor = AdminTheme.PASTEL_GREEN;
            }

            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.dispose();

            super.paintComponent(g);
        }
    }
}