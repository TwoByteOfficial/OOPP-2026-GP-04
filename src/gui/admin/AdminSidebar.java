package gui.admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Absolute positioned navigation sidebar matching the fixed full-screen green layout criteria.
 */
public class AdminSidebar extends JPanel {

    /* ---- Color Theme Variables ---- */
    private final Color MINT          = new Color(0xE8, 0xF5, 0xE9);
    private final Color PASTEL_GREEN  = new Color(0xC8, 0xE6, 0xC9);
    private final Color FRESH_GREEN   = new Color(0xA5, 0xD6, 0xA7);
    private final Color MEDIUM_GREEN  = new Color(0x81, 0xC7, 0x84);
    private final Color FOREST_GREEN  = new Color(0x38, 0x8E, 0x3C);
    private final Color DEEP_GREEN    = new Color(0x1B, 0x5E, 0x20);

    private final Color PRIMARY       = FOREST_GREEN;
    private final Color PRIMARY_DARK  = DEEP_GREEN;
    private final Color SECONDARY     = FRESH_GREEN;
    private final Color SUCCESS       = FOREST_GREEN;
    private final Color DANGER        = new Color(0xDC, 0x35, 0x45);
    private final Color WARNING       = new Color(0xF0, 0xAD, 0x4E);
    private final Color INFO          = FOREST_GREEN;

    private final Color LIGHT_BG      = MEDIUM_GREEN;
    private final Color WHITE         = Color.WHITE;
    private final Color TEXT          = DEEP_GREEN;
    private final Color MUTED         = new Color(0x4F, 0x6B, 0x52);
    private final Color BORDER        = PASTEL_GREEN;

    /* ---- Navigation Menu Route Keys ---- */
    public static final String PAGE_DASHBOARD = "Dashboard";
    public static final String PAGE_USERS     = "User Profiles";
    public static final String PAGE_COURSES   = "Courses";
    public static final String PAGE_NOTICES   = "Notices";
    public static final String PAGE_TIMETABLE = "Timetables";
    public static final String PAGE_SETTINGS  = "Settings";

    // Callbacks to communicate window structural routing changes downstream
    private Consumer<String> onNavigate;
    private Runnable         onLogout;

    // Track button states dynamically for navigation swaps
    private final Map<String, JButton> menuButtons = new HashMap<>();
    private JButton activeInterfaceButton = null;

    public AdminSidebar() {
        // Enforce null layout wrapper rules for complete setBounds control
        setLayout(null);

        // Match the fixed layout configuration limits
        setBounds(0, 0, 260, 1080);
        setBackground(LIGHT_BG);

        /* 1. Project Title Brand Header Component */
        JLabel logoLabel = new JLabel("UniMis");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 50)); // FONT_LOGO
        logoLabel.setForeground(TEXT);
        logoLabel.setBounds(36, 33, 210, 40);
        add(logoLabel);

        /* 2. Menu Link Navigation Matrix */
        String[] routes = { PAGE_DASHBOARD, PAGE_USERS, PAGE_COURSES, PAGE_NOTICES, PAGE_TIMETABLE, PAGE_SETTINGS };
        int initialY = 100;
        int buttonSpacing = 55;

        for (int i = 0; i < routes.length; i++) {
            String routeKey = routes[i];
            JButton btn = createMenuButton(routeKey, false);

            // Increment placement vertically using manual absolute adjustments
            btn.setBounds(12, initialY + (i * buttonSpacing), 248, 46);
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
        // Positioned statically near the base of standard full screen dimension boundaries
        btnLogout.setBounds(12, 950, 236, 46);
        btnLogout.addActionListener(e -> {
            if (onLogout != null) {
                onLogout.run();
            }
        });
        add(btnLogout);
    }

    /**
     * Engine building structural elements optimized for specific alignment colors and boundaries.
     */

    private JButton createMenuButton(String text, boolean isLogout) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setHorizontalAlignment(SwingConstants.LEADING);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        // --- THE FIXED RENDER CONFIGURATIONS ---
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setRolloverEnabled(false);
        // ----------------------------------------

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


    /**
     * Routes and changes visual focus configurations between absolute interface button blocks.
     */
    public void setActive(String key) {
        // Reset background highlights from old selection node back to default state
        if (activeInterfaceButton != null) {
            activeInterfaceButton.setBackground(FRESH_GREEN); // Default unselected background
            activeInterfaceButton.setForeground(DEEP_GREEN);   // Default unselected text color
        }

        // Apply active white selection layer to the newly selected target button
        JButton currentTarget = menuButtons.get(key);
        if (currentTarget != null) {
            activeInterfaceButton = currentTarget;
            activeInterfaceButton.setBackground(MINT); // Keeps selection white persistently
            activeInterfaceButton.setForeground(TEXT);        // Keeps text dark green
        }
    }

    /* ---- Configuration structural bindings back to main window class ---- */
    public void setOnNavigate(Consumer<String> onNavigate) { this.onNavigate = onNavigate; }
    public void setOnLogout(Runnable onLogout) { this.onLogout = onLogout; }
}
