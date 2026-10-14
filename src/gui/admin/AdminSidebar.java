package gui.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Left navigation sidebar for the Administrator UI.
 * Mirrors the sidebar in admin.html / styles.css (gradient green, logo, menu, logout).
 */
public class AdminSidebar extends JPanel {

    /** Keys used to identify navigation items. */
    public static final String PAGE_DASHBOARD = "dashboard";
    public static final String PAGE_USERS     = "users";
    public static final String PAGE_COURSES   = "courses";
    public static final String PAGE_NOTICES   = "notices";
    public static final String PAGE_TIMETABLE = "timetables";
    public static final String PAGE_REPORTS   = "reports";
    public static final String PAGE_SETTINGS  = "settings";

    private final Map<String, MenuItem> items = new LinkedHashMap<>();
    private String activeKey = PAGE_DASHBOARD;

    /** Called when a menu item is clicked. */
    private Consumer<String> onNavigate = key -> {};
    /** Called when logout is clicked. */
    private Runnable onLogout = () -> {};

    public AdminSidebar() {
        setPreferredSize(new Dimension(AdminTheme.SIDEBAR_WIDTH, 10));
        setLayout(new BorderLayout());
        setBackground(AdminTheme.PRIMARY_DARK);

        add(buildLogoSection(), BorderLayout.NORTH);
        add(buildMenu(),        BorderLayout.CENTER);
        add(buildFooter(),      BorderLayout.SOUTH);

        setActive(PAGE_DASHBOARD);
    }

    /* ---------- Public API ---------- */

    public void setOnNavigate(Consumer<String> handler) {
        this.onNavigate = (handler != null) ? handler : key -> {};
    }

    public void setOnLogout(Runnable handler) {
        this.onLogout = (handler != null) ? handler : () -> {};
    }

    public void setActive(String key) {
        this.activeKey = key;
        for (Map.Entry<String, MenuItem> e : items.entrySet()) {
            e.getValue().setActive(e.getKey().equals(key));
        }
    }

    public String getActive() {
        return activeKey;
    }

    /* ---------- UI Build ---------- */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(
                0, 0, AdminTheme.PRIMARY,
                0, getHeight(), AdminTheme.PRIMARY_DARK));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    private JPanel buildLogoSection() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 40)),
                new EmptyBorder(22, 22, 22, 22)));

        // Logo icon
        JLabel icon = new JLabel("\uD83C\uDF93", SwingConstants.CENTER); // graduation cap emoji
        icon.setPreferredSize(new Dimension(45, 45));
        icon.setOpaque(true);
        icon.setBackground(AdminTheme.SECONDARY);
        icon.setForeground(AdminTheme.PRIMARY_DARK);
        icon.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 22));
        icon.setBorder(BorderFactory.createEmptyBorder());

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("UNIMIS");
        title.setForeground(Color.WHITE);
        title.setFont(AdminTheme.FONT_LOGO);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Administrator");
        subtitle.setForeground(AdminTheme.PASTEL_GREEN);
        subtitle.setFont(new Font(AdminTheme.FONT_FAMILY, Font.PLAIN, 11));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(title);
        textPanel.add(subtitle);

        p.add(icon,      BorderLayout.WEST);
        p.add(textPanel, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildMenu() {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(new EmptyBorder(18, 12, 18, 12));

        addMenuItem(wrapper, PAGE_DASHBOARD, "📊", "Dashboard");
        addMenuItem(wrapper, PAGE_USERS,     "👥", "User Profiles");
        addMenuItem(wrapper, PAGE_COURSES,   "📖", "Courses");
        addMenuItem(wrapper, PAGE_NOTICES,   "📢", "Notices");
        addMenuItem(wrapper, PAGE_TIMETABLE, "📅", "Timetables");
        addMenuItem(wrapper, PAGE_REPORTS,   "📄", "Academic Reports");
        addMenuItem(wrapper, PAGE_SETTINGS,  "⚙",  "Settings");

        JPanel scrollPaneWrap = new JPanel(new BorderLayout());
        scrollPaneWrap.setOpaque(false);
        scrollPaneWrap.add(wrapper, BorderLayout.NORTH);
        return scrollPaneWrap;
    }

    private void addMenuItem(JPanel parent, String key, String icon, String label) {
        MenuItem item = new MenuItem(key, icon, label);
        items.put(key, item);
        parent.add(item);
        parent.add(Box.createVerticalStrut(4));
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(16, 16, 16, 16));

        JButton logout = new JButton("⎋  Logout");
        logout.setFont(new Font(AdminTheme.FONT_FAMILY, Font.PLAIN, 13));
        logout.setForeground(Color.WHITE);
        logout.setBackground(new Color(255, 255, 255, 20));
        logout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 60)),
                new EmptyBorder(10, 12, 10, 12)));
        logout.setContentAreaFilled(false);
        logout.setOpaque(false);
        logout.setFocusPainted(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        logout.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                logout.setOpaque(true);
                logout.setBackground(new Color(255, 255, 255, 35));
                logout.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                logout.setOpaque(false);
                logout.repaint();
            }
        });

        logout.addActionListener(e -> onLogout.run());
        footer.add(logout, BorderLayout.CENTER);
        return footer;
    }

    /* ---------- Inner class: menu item ---------- */

    private class MenuItem extends JPanel {
        private final String key;
        private final JLabel iconLbl;
        private final JLabel textLbl;
        private boolean active = false;
        private boolean hover  = false;

        MenuItem(String key, String icon, String label) {
            this.key = key;
            setLayout(new BorderLayout(12, 0));
            setOpaque(false);
            setBorder(new EmptyBorder(11, 14, 11, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

            iconLbl = new JLabel(icon, SwingConstants.CENTER);
            iconLbl.setPreferredSize(new Dimension(22, 22));
            iconLbl.setForeground(AdminTheme.MINT);
            iconLbl.setFont(new Font(AdminTheme.FONT_FAMILY, Font.PLAIN, 15));

            textLbl = new JLabel(label);
            textLbl.setForeground(AdminTheme.MINT);
            textLbl.setFont(AdminTheme.FONT_MENU);

            add(iconLbl, BorderLayout.WEST);
            add(textLbl, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    hover = true; repaint();
                }
                @Override public void mouseExited(MouseEvent e) {
                    hover = false; repaint();
                }
                @Override public void mouseClicked(MouseEvent e) {
                    AdminSidebar.this.setActive(key);
                    onNavigate.accept(key);
                }
            });
        }

        void setActive(boolean active) {
            this.active = active;
            textLbl.setForeground(active ? Color.WHITE : AdminTheme.MINT);
            iconLbl.setForeground(active ? Color.WHITE : AdminTheme.MINT);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (active || hover) {
                g2.setColor(new Color(255, 255, 255, active ? 45 : 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
            if (active) {
                g2.setColor(AdminTheme.SECONDARY);
                g2.fillRect(0, 0, 4, getHeight());
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}