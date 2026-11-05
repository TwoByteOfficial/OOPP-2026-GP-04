package gui.technical;

import gui.theme.UniMisTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * [OOP - INHERITANCE]: Extends javax.swing.JPanel to create a custom reusable UI component
 * for sidebar navigation in the Technical Officer portal.
 */
public class TOSidebarPanel extends JPanel {

    // [OOP - INTERFACE]: Uses Java standard functional interface Consumer<String> 
    // to pass page navigation commands without tightly coupling to the dashboard frame.
    private final Consumer<String> navigationHandler;

    // [OOP - ENCAPSULATION]: Private constants and state protect sidebar visual styling.
    private final Color SIDEBAR_BG = UniMisTheme.MEDIUM_GREEN;
    private final Color BUTTON_BG = UniMisTheme.PASTEL_GREEN;
    private final Color SELECTED_BG = UniMisTheme.MINT;
    private final Color BUTTON_TEXT = UniMisTheme.TEXT;

    private JButton selectedButton;

    /**
     * Constructs the Technical Officer sidebar.
     *
     * @param fullName          Name of the officer to display in header
     * @param navigationHandler [OOP - INTERFACE]: Callback interface for navigation
     * @param logoutHandler     [OOP - INTERFACE]: java.lang.Runnable interface callback for logout
     */
    public TOSidebarPanel(
            String fullName,
            Consumer<String> navigationHandler,
            Runnable logoutHandler) {

        this.navigationHandler = navigationHandler;

        setLayout(new BorderLayout(0, 20));
        setBackground(SIDEBAR_BG);
        setBorder(new EmptyBorder(20, 12, 16, 12));
        setPreferredSize(new Dimension(UniMisTheme.SIDEBAR_WIDTH, 0));

        // Assemble three main vertical sections: Brand, Nav buttons, and Logout footer
        add(buildBrand(), BorderLayout.NORTH);
        add(buildNavigation(), BorderLayout.CENTER);
        add(buildFooter(logoutHandler), BorderLayout.SOUTH);
    }

    /**
     * [OOP - ENCAPSULATION]: Private helper method encapsulating brand header construction.
     */
    private JPanel buildBrand() {
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("  UniMIS");
        title.setFont(UniMisTheme.FONT_LOGO);
        title.setForeground(UniMisTheme.TEXT);

        JLabel subtitle = new JLabel("  Technical Officer");
        subtitle.setFont(UniMisTheme.FONT_TINY);
        subtitle.setForeground(UniMisTheme.MUTED);

        brand.add(title);
        brand.add(Box.createVerticalStrut(7));
        brand.add(subtitle);

        return brand;
    }

    /**
     * [OOP - ENCAPSULATION]: Private method encapsulating navigation button list assembly.
     */
    private JPanel buildNavigation() {
        JPanel navigation = new JPanel();
        navigation.setOpaque(false);
        navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));

        // Add menu items with initial active state
        addNavigationButton(navigation, "Dashboard", "Dashboard", true);
        addNavigationButton(navigation, "Attendance", "Attendance", false);
        addNavigationButton(navigation, "Medical Records", "Medical Records", false);
        addNavigationButton(navigation, "Timetables", "Timetables", false);
        addNavigationButton(navigation, "Notices", "Notices", false);
        addNavigationButton(navigation, "My Profile", "My Profile", false);

        return navigation;
    }

    /**
     * Helper to create and style a single navigation button.
     * [OOP - INTERFACE]: Implements java.awt.event.ActionListener via lambda expression (e -> ...).
     */
    private void addNavigationButton(
            JPanel parent, String label, String page, boolean selected) {

        JButton button = new JButton(label);

        button.setFont(UniMisTheme.FONT_MENU);
        button.setForeground(BUTTON_TEXT);
        button.setBackground(selected ? SELECTED_BG : BUTTON_BG);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setPreferredSize(new Dimension(UniMisTheme.SIDEBAR_WIDTH - 24, 46));
        button.setBorder(new EmptyBorder(8, 14, 8, 8));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (selected) {
            selectedButton = button;
        }

        // [OOP - INTERFACE]: ActionListener lambda expression
        button.addActionListener(e -> {
            setSelectedButton(button);
            navigationHandler.accept(page);
        });

        parent.add(button);
        parent.add(Box.createVerticalStrut(5));
    }

    /**
     * [OOP - ENCAPSULATION]: Private helper managing button selection colors internally.
     */
    private void setSelectedButton(JButton button) {
        if (selectedButton != null) {
            selectedButton.setBackground(BUTTON_BG);
            selectedButton.setForeground(BUTTON_TEXT);
        }

        selectedButton = button;
        selectedButton.setBackground(SELECTED_BG);
        selectedButton.setForeground(BUTTON_TEXT);
    }

    /**
     * Programmatically selects a sidebar button matching the given page name.
     */
    public void selectPage(String pageName) {
        for (Component component : getComponents()) {
            if (component instanceof JPanel panel) {
                for (Component item : panel.getComponents()) {
                    if (item instanceof JButton button
                            && button.getText().equals(pageName)) {
                        setSelectedButton(button);
                        return;
                    }
                }
            }
        }
    }

    /**
     * [OOP - INTERFACE]: Uses Runnable functional interface to invoke logout callback.
     */
    private JPanel buildFooter(Runnable logoutHandler) {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JButton logout = new JButton("Logout");
        logout.setFont(UniMisTheme.FONT_BODY_BOLD);
        logout.setForeground(Color.WHITE);
        logout.setBackground(UniMisTheme.DANGER);

        logout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UniMisTheme.DANGER),
                new EmptyBorder(8, 14, 8, 14)
        ));

        logout.setPreferredSize(new Dimension(200, 44));
        logout.setOpaque(true);
        logout.setFocusPainted(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // [OOP - INTERFACE]: ActionListener lambda triggering Runnable.run()
        logout.addActionListener(e -> logoutHandler.run());

        footer.add(logout, BorderLayout.CENTER);
        return footer;
    }
}
