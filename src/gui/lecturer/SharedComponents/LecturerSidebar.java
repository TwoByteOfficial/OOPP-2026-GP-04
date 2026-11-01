
package gui.lecturer.SharedComponents;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class LecturerSidebar extends JPanel {

    private final Map<String, SidebarButton> navigationButtons =
            new LinkedHashMap<>();

    private String selectedPage = "Dashboard";

    private static final Color SIDEBAR_BACKGROUND =
            new Color(165, 214, 167); // Undergraduate green

    private static final Color ACTIVE_BACKGROUND = Color.WHITE;
    private static final Color HOVER_BACKGROUND =
            new Color(232, 245, 233);
    private static final Color NORMAL_BUTTON_BACKGROUND =
            new Color(200, 230, 201);
    private static final Color DANGER =
            new Color(220, 53, 69);
    private static final Color DANGER_HOVER =
            new Color(180, 35, 50);


    public LecturerSidebar() {
        setLayout(new BorderLayout());
        setBackground(SIDEBAR_BACKGROUND);
        setPreferredSize(new Dimension(260, 0));
        setBorder(new EmptyBorder(20, 14, 18, 0));

        add(createBrandPanel(), BorderLayout.NORTH);
        add(createNavigationPanel(), BorderLayout.CENTER);
        createLogoutButton();

        selectPage("Dashboard");
    }

    private JPanel createBrandPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(5, 8, 28, 8));

        JLabel brand = new JLabel("UniMIS");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 28));
        brand.setForeground(Colors.DEEP_GREEN);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("LECTURER PORTAL");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(Colors.DEEP_GREEN);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(brand);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);

        return panel;
    }

    private JScrollPane createNavigationPanel() {
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

            SidebarButton button = new SidebarButton(label, false);
            navigationButtons.put(pageKey, button);

            button.addActionListener(e -> {
                selectPage(pageKey);
                firePropertyChange("pageSelected", null, pageKey);
            });

            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            button.setPreferredSize(new Dimension(236, 44));
            button.setAlignmentX(Component.LEFT_ALIGNMENT);

            panel.add(button);
            panel.add(Box.createVerticalStrut(7));
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    private JButton createLogoutButton() {
        SidebarButton logout = new SidebarButton("Logout", true);
        logout.setPreferredSize(new Dimension(236, 46));
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 14));
        bottomPanel.add(logout, BorderLayout.CENTER);

        logout.addActionListener(e ->
                firePropertyChange("logoutRequested", false, true)
        );

        add(bottomPanel, BorderLayout.SOUTH);
        return logout;
    }

    public void selectPage(String pageKey) {
        selectedPage = pageKey;

        for (Map.Entry<String, SidebarButton> entry
                : navigationButtons.entrySet()) {

            boolean selected = entry.getKey().equals(pageKey);
            entry.getValue().setSelectedStyle(selected);
        }
    }

    public String getSelectedPage() {
        return selectedPage;
    }

    private static class SidebarButton extends JButton {

        private boolean selected;
        private boolean hovered;
        private final boolean logoutButton;

        SidebarButton(String text, boolean logoutButton) {
            super(text);
            this.logoutButton = logoutButton;

            setFont(new Font(
                    "Segoe UI",
                    logoutButton ? Font.BOLD : Font.PLAIN,
                    15
            ));
            setHorizontalAlignment(SwingConstants.LEFT);
            setForeground(logoutButton ? Color.WHITE : Colors.DEEP_GREEN);
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBorder(new EmptyBorder(0, 18, 0, 10));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        void setSelectedStyle(boolean selected) {
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

            Color background;

            if (logoutButton) {
                background = hovered ? DANGER_HOVER : DANGER;
            } else if (selected) {
                background = ACTIVE_BACKGROUND;
            } else if (hovered) {
                background = HOVER_BACKGROUND;
            } else {
                background = NORMAL_BUTTON_BACKGROUND;
            }

            g2.setColor(background);

            g2.fillRoundRect(
                    0, 0, getWidth(), getHeight(), 8, 8
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }
}
