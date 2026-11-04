package gui.technical;

import gui.theme.UniMisTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Sidebar navigation component for the Technical Officer Dashboard.
 * Displays the university brand logo, navigation buttons for each section,
 * and a styled logout button in the footer.
 */
public class TOSidebarPanel extends JPanel {

    // Callback invoked when a navigation button is clicked to switch views
    private final Consumer<String> navigationHandler;

    // Theme color constants used for sidebar styling
    private final Color SIDEBAR_BG = UniMisTheme.MEDIUM_GREEN;
    private final Color BUTTON_BG = UniMisTheme.PASTEL_GREEN;
    private final Color SELECTED_BG = UniMisTheme.MINT;
    private final Color BUTTON_TEXT = UniMisTheme.TEXT;

    // Tracks the currently active navigation button to update highlight colors
    private JButton selectedButton;

    /**
     * Constructs the Technical Officer sidebar.
     *
     * @param fullName          Name of the officer to display in header
     * @param navigationHandler Callback to switch between pages
     * @param logoutHandler     Callback to handle the logout action
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
     * Builds the top brand section with system name and portal title.
     *
     * @return JPanel containing brand title and subtitle
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
     * Builds the vertical list of navigation buttons for each page.
     *
     * @return JPanel containing stacked navigation buttons
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
     *
     * @param parent   Container panel to add the button to
     * @param label    Button text displayed to the user
     * @param page     Internal page identifier passed to navigationHandler
     * @param selected Whether this button starts selected by default
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

        // On click: highlight button and notify parent container to display selected page
        button.addActionListener(e -> {
            setSelectedButton(button);
            navigationHandler.accept(page);
        });

        parent.add(button);
        parent.add(Box.createVerticalStrut(5));
    }

    /**
     * Updates visual highlight styling so only the clicked button appears active.
     *
     * @param button The button to make active
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
     * Used when navigation is triggered from outside the sidebar (e.g., Quick Actions).
     *
     * @param pageName The name of the target page
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
     * Builds the bottom footer containing the red Logout button.
     *
     * @param logoutHandler Runnable called when logout is clicked
     * @return JPanel containing the logout button
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

        logout.addActionListener(e -> logoutHandler.run());

        footer.add(logout, BorderLayout.CENTER);
        return footer;
    }
}
