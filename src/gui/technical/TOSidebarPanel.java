
package gui.technical;
import gui.theme.UniMisTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class TOSidebarPanel extends JPanel {

    private final Consumer<String> navigationHandler;
    private final Color NAVY = UniMisTheme.PRIMARY_DARK;
    private final Color SELECTED = UniMisTheme.PASTEL_GREEN;
    private final Color ACCENT = UniMisTheme.PRIMARY;

    private JButton selectedButton;

    public TOSidebarPanel(
            String fullName,
            Consumer<String> navigationHandler,
            Runnable logoutHandler) {

        this.navigationHandler = navigationHandler;

        setLayout(new BorderLayout(0, 20));
        setBackground(NAVY);
        setBorder(new EmptyBorder(20, 12, 16, 12));
        setPreferredSize(
                new Dimension(UniMisTheme.SIDEBAR_WIDTH, 0));

        add(buildBrand(), BorderLayout.NORTH);
        add(buildNavigation(), BorderLayout.CENTER);
        add(buildFooter(logoutHandler), BorderLayout.SOUTH);
    }

    private JPanel buildBrand() {
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("  FoT Portal");
        title.setFont(UniMisTheme.FONT_LOGO);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("  Technical Officer");
        subtitle.setFont(UniMisTheme.FONT_TINY);
        subtitle.setForeground(UniMisTheme.PASTEL_GREEN);

        brand.add(title);
        brand.add(Box.createVerticalStrut(7));
        brand.add(subtitle);

        return brand;
    }

    private JPanel buildNavigation() {
        JPanel navigation = new JPanel();
        navigation.setOpaque(false);
        navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));

        addNavigationButton(navigation, "Dashboard", "Dashboard", true);
        addNavigationButton(navigation, "Attendance", "Attendance", false);
        addNavigationButton(navigation, "Medical Records", "Medical Records", false);
        addNavigationButton(navigation, "Undergraduates", "Undergraduates", false);
        addNavigationButton(navigation, "Timetables", "Timetables", false);
        addNavigationButton(navigation, "Notices", "Notices", false);
        addNavigationButton(navigation, "My Profile", "My Profile", false);

        return navigation;
    }

    private void addNavigationButton(
            JPanel parent, String label, String page, boolean selected) {

        JButton button = new JButton(label);

        button.setFont(UniMisTheme.FONT_MENU);
        button.setForeground(
                selected ? UniMisTheme.TEXT : UniMisTheme.WHITE);
        button.setBackground(selected ? SELECTED : NAVY);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setPreferredSize(
                new Dimension(UniMisTheme.SIDEBAR_WIDTH - 24, 46));
        button.setBorder(new EmptyBorder(8, 14, 8, 8));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (selected) {
            selectedButton = button;
        }

        button.addActionListener(e -> {
            setSelectedButton(button);
            navigationHandler.accept(page);
        });

        parent.add(button);
        parent.add(Box.createVerticalStrut(5));
    }


    private void setSelectedButton(JButton button) {
        if (selectedButton != null) {
            selectedButton.setBackground(NAVY);
            selectedButton.setForeground(UniMisTheme.WHITE);
        }

        selectedButton = button;
        selectedButton.setBackground(SELECTED);
        selectedButton.setForeground(UniMisTheme.TEXT);
    }

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

    private JPanel buildFooter(Runnable logoutHandler) {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JButton logout = new JButton("Logout");
        logout.setFont(UniMisTheme.FONT_BODY_BOLD);
        logout.setForeground(Color.WHITE);
        logout.setBackground(NAVY);

        logout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UniMisTheme.BORDER),
                new EmptyBorder(8, 14, 8, 14)
        ));

        logout.setPreferredSize(new Dimension(200, 44));
        logout.setOpaque(true);
        logout.setFocusPainted(false);
        logout.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        logout.addActionListener(e -> logoutHandler.run());

        footer.add(logout, BorderLayout.CENTER);
        return footer;
    }
}