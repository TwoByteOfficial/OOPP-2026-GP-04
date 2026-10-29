
package gui.lecturer.SharedComponents;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static gui.lecturer.SharedComponents.Colors.SIDEBAR_DEEP_GREEN;
import static gui.lecturer.SharedComponents.Colors.SIDEBAR_LIGHT_GREEN;
import static gui.lecturer.SharedComponents.Colors.SIDEBAR_WHITE;

public class LecturerSidebar extends JPanel {

    private final Map<String, JButton> navigationButtons =
            new LinkedHashMap<>();

    private String selectedPage = "Dashboard";

    public LecturerSidebar() {
        setLayout(new BorderLayout());
        setBackground(SIDEBAR_DEEP_GREEN);
        setPreferredSize(new Dimension(250, 0));

        // Keep left padding and remove right padding
        setBorder(new EmptyBorder(20, 14, 18, 0));

        add(createBrandPanel(), BorderLayout.NORTH);
        add(createNavigationPanel(), BorderLayout.CENTER);
        add(createLogoutButton(), BorderLayout.SOUTH);

        selectPage("Dashboard");
    }

    // Custom sidebar button
    private class SidebarButton extends JButton {

        private boolean selected = false;

        public SidebarButton(String label) {
            super(label);

            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        public void setSelectedStyle(boolean selected) {
            this.selected = selected;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (selected) {
                g2.setColor(getBackground());

                int w = getWidth();
                int h = getHeight();
                int arc = 24;

                if ("Logout".equals(getText())) {
                    // Logout stays rounded on both sides
                    g2.fillRoundRect(0, 0, w, h, arc, arc);
                } else {
                    // Navigation: rounded left, straight right
                    g2.fillRoundRect(0, 0, w + arc, h, arc, arc);
                    g2.fillRect(arc, 0, w, h);
                }
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private JPanel createBrandPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 8, 28, 8));

        JLabel brand = new JLabel("UniMIS");
        brand.setFont(new Font("SansSerif", Font.BOLD, 27));
        brand.setForeground(SIDEBAR_WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("LECTURER PORTAL");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(SIDEBAR_LIGHT_GREEN);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(brand);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);

        return panel;
    }

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        String[][] pages = {
                {"Dashboard", "Dashboard"},
                {"My Courses", "MyCourses"},
                {"Course Materials", "CourseMaterials"},
                {"Marks & Grades", "MarksGrades"},
                {"Undergraduates", "Undergraduates"},
                {"Eligibility", "Eligibility"},
                {"Attendance", "Attendance"},
                {"Notices", "Notices"},
                {"My Profile", "Profile"}
        };

        for (String[] page : pages) {
            String label = page[0];
            String pageKey = page[1];

            JButton button = createNavigationButton(label);
            navigationButtons.put(pageKey, button);

            button.addActionListener(e -> {
                selectPage(pageKey);
                firePropertyChange(
                        "pageSelected", null, pageKey
                );
            });

            // Navigation buttons fill the available width
            button.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 44)
            );

            panel.add(button);
            panel.add(Box.createVerticalStrut(5));
        }

        return panel;
    }

    private JButton createNavigationButton(String label) {
        JButton button = new SidebarButton(label);

        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(SIDEBAR_DEEP_GREEN);
        button.setForeground(SIDEBAR_WHITE);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        button.setBorder(new EmptyBorder(12, 14, 12, 10));
        button.setMaximumSize(new Dimension(220, 44));
        button.setPreferredSize(new Dimension(220, 44));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        return button;
    }


    private JButton createLogoutButton() {
        JButton logout = new SidebarButton("Logout");

        logout.setFont(new Font("SansSerif", Font.BOLD, 14));
        logout.setHorizontalAlignment(SwingConstants.LEFT);
        logout.setBackground(SIDEBAR_LIGHT_GREEN);
        logout.setForeground(SIDEBAR_DEEP_GREEN);
        logout.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        logout.setBorder(new EmptyBorder(12, 14, 12, 10));
        logout.setPreferredSize(new Dimension(220, 44));
        logout.setMaximumSize(new Dimension(220, 44));
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Rounded light-green Logout button
        ((SidebarButton) logout).setSelectedStyle(true);

        logout.addActionListener(e ->
                firePropertyChange("logoutRequested", false, true)
        );

        return logout;
    }


    public void selectPage(String pageKey) {
        selectedPage = pageKey;

        for (Map.Entry<String, JButton> entry
                : navigationButtons.entrySet()) {

            boolean selected = entry.getKey().equals(pageKey);
            JButton button = entry.getValue();

            button.setBackground(
                    selected ? SIDEBAR_LIGHT_GREEN : SIDEBAR_DEEP_GREEN
            );

            button.setForeground(
                    selected ? SIDEBAR_DEEP_GREEN : SIDEBAR_WHITE
            );

            button.setFont(new Font(
                    "SansSerif",
                    selected ? Font.BOLD : Font.PLAIN,
                    14
            ));

            ((SidebarButton) button).setSelectedStyle(selected);
        }
    }

    public String getSelectedPage() {
        return selectedPage;
    }
}
