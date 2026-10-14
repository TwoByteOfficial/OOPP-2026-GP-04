package gui.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/** Fixed left navigation — same items as the HTML sidebar. */
public class AdminSidebar extends JPanel {

    private static final int WIDTH = 260;

    /** key -> [icon, label] */
    private static final Map<String, String[]> ITEMS = new LinkedHashMap<>();
    static {
        ITEMS.put("dashboard",  new String[]{"\uD83D\uDCC8", "Dashboard"});
        ITEMS.put("users",      new String[]{"\uD83D\uDC65", "User Profiles"});
        ITEMS.put("courses",    new String[]{"\uD83D\uDCDA", "Courses"});
        ITEMS.put("notices",    new String[]{"\uD83D\uDCE3", "Notices"});
        ITEMS.put("timetables", new String[]{"\uD83D\uDCC5", "Timetables"});
        ITEMS.put("reports",    new String[]{"\uD83D\uDCC4", "Academic Reports"});
        ITEMS.put("settings",   new String[]{"\u2699", "Settings"});
    }

    private final Map<String, JLabel> itemLabels = new LinkedHashMap<>();
    private String activeKey = "dashboard";

    public AdminSidebar(String adminName, Consumer<String> onSelect, Runnable onLogout) {
        setLayout(new BorderLayout());
        setBackground(AdminTheme.PRIMARY);
        setPreferredSize(new Dimension(WIDTH, 0));
        setMinimumSize(new Dimension(WIDTH, 0));

        add(buildLogo(adminName), BorderLayout.NORTH);

        JPanel menu = new JPanel();
        menu.setBackground(AdminTheme.PRIMARY);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(18, 12, 18, 12));

        for (Map.Entry<String, String[]> e : ITEMS.entrySet()) {
            JLabel item = buildMenuItem(e.getKey(), e.getValue()[0], e.getValue()[1], onSelect);
            itemLabels.put(e.getKey(), item);
            menu.add(item);
            menu.add(Box.createVerticalStrut(4));
        }
        menu.add(Box.createVerticalGlue());
        add(menu, BorderLayout.CENTER);

        JPanel foot = new JPanel(new BorderLayout());
        foot.setBackground(AdminTheme.PRIMARY);
        foot.setBorder(new EmptyBorder(16, 16, 16, 16));
        JButton logout = new JButton("Logout");
        logout.setFont(AdminTheme.F_BODY);
        logout.setForeground(AdminTheme.WHITE);
        logout.setBackground(AdminTheme.PRIMARY);
        logout.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 1));
        logout.setFocusPainted(false);
        logout.setCursor(AdminTheme.HAND);
        logout.setPreferredSize(new Dimension(WIDTH - 32, 40));
        logout.addActionListener(e -> onLogout.run());
        foot.add(logout, BorderLayout.CENTER);
        add(foot, BorderLayout.SOUTH);

        setActive("dashboard");
    }

    private JPanel buildLogo(String adminName) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(AdminTheme.PRIMARY_DARK);
        wrap.setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel icon = new JLabel("\uD83C\uDF93", SwingConstants.CENTER);
        icon.setOpaque(true);
        icon.setBackground(AdminTheme.SECONDARY);
        icon.setForeground(AdminTheme.PRIMARY_DARK);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        icon.setPreferredSize(new Dimension(45, 45));
        icon.setBorder(BorderFactory.createEmptyBorder());

        JPanel text = new JPanel();
        text.setBackground(AdminTheme.PRIMARY_DARK);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel t1 = AdminTheme.label("FoT Portal", new Font("Segoe UI", Font.BOLD, 17), AdminTheme.WHITE);
        JLabel t2 = AdminTheme.label("Administrator", AdminTheme.F_SMALL, new Color(0xCB, 0xD7, 0xE8));
        text.add(t1);
        text.add(t2);

        wrap.add(icon, BorderLayout.WEST);
        wrap.add(text, BorderLayout.CENTER);
        return wrap;
    }

    private JLabel buildMenuItem(String key, String iconChar, String title, Consumer<String> onSelect) {
        JLabel l = new JLabel("  " + iconChar + "   " + title);
        l.setOpaque(true);
        l.setBackground(AdminTheme.PRIMARY);
        l.setForeground(new Color(0xD8, 0xE3, 0xF0));
        l.setFont(AdminTheme.F_MENU);
        l.setBorder(new EmptyBorder(12, 14, 12, 14));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        l.setCursor(AdminTheme.HAND);
        l.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { onSelect.accept(key); }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!key.equals(activeKey)) l.setBackground(new Color(0x24, 0x4B, 0x7E));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (!key.equals(activeKey)) l.setBackground(AdminTheme.PRIMARY);
            }
        });
        return l;
    }

    public void setActive(String key) {
        activeKey = key;
        for (Map.Entry<String, JLabel> e : itemLabels.entrySet()) {
            JLabel l = e.getValue();
            if (e.getKey().equals(key)) {
                l.setBackground(new Color(0x24, 0x4B, 0x7E));
                l.setForeground(AdminTheme.WHITE);
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, AdminTheme.SECONDARY),
                        new EmptyBorder(12, 10, 12, 14)));
            } else {
                l.setBackground(AdminTheme.PRIMARY);
                l.setForeground(new Color(0xD8, 0xE3, 0xF0));
                l.setBorder(new EmptyBorder(12, 14, 12, 14));
            }
        }
    }
}