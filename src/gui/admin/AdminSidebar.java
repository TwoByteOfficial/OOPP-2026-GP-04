package gui.admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Navigation sidebar matching fixed full-screen green layout.
 */
public class AdminSidebar extends JPanel {

    private final Color MINT          = new Color(0xE8, 0xF5, 0xE9);
    private final Color PASTEL_GREEN  = new Color(0xC8, 0xE6, 0xC9);
    private final Color FRESH_GREEN   = new Color(0xA5, 0xD6, 0xA7);
    private final Color MEDIUM_GREEN  = new Color(0x81, 0xC7, 0x84);
    private final Color FOREST_GREEN  = new Color(0x38, 0x8E, 0x3C);
    private final Color DEEP_GREEN    = new Color(0x1B, 0x5E, 0x20);

    private final Color LIGHT_BG      = MEDIUM_GREEN;
    private final Color WHITE         = Color.WHITE;
    private final Color TEXT          = DEEP_GREEN;
    private final Color DANGER        = new Color(0xDC, 0x35, 0x45);

    public static final String PAGE_DASHBOARD = "Dashboard";
    public static final String PAGE_USERS     = "User Profiles";
    public static final String PAGE_COURSES   = "Courses";
    public static final String PAGE_NOTICES   = "Notices";
    public static final String PAGE_TIMETABLE = "Timetables";
    public static final String PAGE_SETTINGS  = "Settings";

    private Consumer<String> onNavigate;
    private Runnable         onLogout;

    private final Map<String, JButton> menuButtons = new HashMap<>();
    private JButton activeInterfaceButton = null;

    public AdminSidebar() {
        setLayout(null);
        setBounds(0, 0, 260, 1080);
        setBackground(LIGHT_BG);

        /* 1. Project Title Brand Header Component */
        JLabel logoLabel = new JLabel("UniMis");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 48)); // FONT_LOGO
        logoLabel.setForeground(TEXT);
        logoLabel.setBounds(36, 30, 210, 48);
        add(logoLabel);

        /* 2. Menu Link Navigation Matrix */
        String[] routes = { PAGE_DASHBOARD, PAGE_USERS, PAGE_COURSES, PAGE_NOTICES, PAGE_TIMETABLE, PAGE_SETTINGS };
        int initialY = 100;
        int buttonSpacing = 58;

        for (int i = 0; i < routes.length; i++) {
            String routeKey = routes[i];
            JButton btn = createMenuButton(routeKey, false);

            btn.setBounds(12, initialY + (i * buttonSpacing), 248, 48);
            btn.addActionListener(e -> {
                if (onNavigate != null) {
                    onNavigate.accept(routeKey);
                }
            });

            menuButtons.put(routeKey, btn);
            add(btn);
        }

        /* 3. Footer Section - Fixed Logout Placement */
        JButton btnLogout = createMenuButton("Logout", true);
        btnLogout.setBounds(12, 950, 236, 48);
        btnLogout.addActionListener(e -> {
            if (onLogout != null) {
                onLogout.run();
            }
        });
        add(btnLogout);
    }

    private JButton createMenuButton(String text, boolean isLogout) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Increment +2px (18 -> 20)
        btn.setHorizontalAlignment(SwingConstants.LEADING);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));

        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setRolloverEnabled(false);

        Color baseBg = FRESH_GREEN;
        Color baseText = DEEP_GREEN;

        if (isLogout) {
            baseBg = DANGER;
            baseText = WHITE;
        }

        btn.setBackground(baseBg);
        btn.setForeground(baseText);

        final Color finalBaseBg = baseBg;
        final Color finalBaseText = baseText;

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != activeInterfaceButton) {
                    btn.setBackground(MINT);
                    btn.setForeground(isLogout ? DANGER : TEXT);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != activeInterfaceButton) {
                    btn.setBackground(finalBaseBg);
                    btn.setForeground(finalBaseText);
                }
            }
        });

        return btn;
    }

    public void setActive(String key) {
        if (activeInterfaceButton != null) {
            activeInterfaceButton.setBackground(FRESH_GREEN);
            activeInterfaceButton.setForeground(DEEP_GREEN);
        }

        JButton currentTarget = menuButtons.get(key);
        if (currentTarget != null) {
            activeInterfaceButton = currentTarget;
            activeInterfaceButton.setBackground(MINT);
            activeInterfaceButton.setForeground(TEXT);
        }
    }

    public void setOnNavigate(Consumer<String> onNavigate) { this.onNavigate = onNavigate; }
    public void setOnLogout(Runnable onLogout) { this.onLogout = onLogout; }
}
