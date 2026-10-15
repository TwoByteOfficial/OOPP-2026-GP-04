package gui.lecturer.SharedComponents;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class LecturerSidebar extends JPanel {

    private final Color deepGreen = new Color(27, 94, 32);
    private final Color mediumGreen = new Color(129, 199, 132);
    private final Color white = Color.WHITE;

    private final Map<String, JButton> navigationButtons =
            new LinkedHashMap<>();

    public LecturerSidebar(
            Consumer<String> onNavigate,
            Runnable onLogout
    ) {
        setPreferredSize(new Dimension(245, 0));
        setBackground(deepGreen);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(25, 15, 20, 15));

        JLabel logo = new JLabel("UniMIS");
        logo.setFont(new Font("SansSerif", Font.BOLD, 28));
        logo.setForeground(white);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel panelName = new JLabel("Lecturer Panel");
        panelName.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panelName.setForeground(new Color(220, 240, 220));
        panelName.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(logo);
        add(Box.createVerticalStrut(5));
        add(panelName);
        add(Box.createVerticalStrut(35));

        String[][] menuItems = {
                {"Dashboard", "dashboard"},
                {"My Courses", "courses"},
                {"Course Materials", "materials"},
                {"Marks & Grades", "marks"},
                {"Undergraduates", "undergraduates"},
                {"Eligibility", "eligibility"},
                {"Attendance", "attendance"},
                {"Notices", "notices"},
                {"My Profile", "profile"}
        };

        for (String[] item : menuItems) {
            String label = item[0];
            String pageKey = item[1];

            JButton button = createNavigationButton(label);

            button.addActionListener(
                    e -> onNavigate.accept(pageKey)
            );

            navigationButtons.put(pageKey, button);

            add(button);
            add(Box.createVerticalStrut(7));
        }

        add(Box.createVerticalGlue());

        JButton logoutButton =
                createNavigationButton("Logout");

        logoutButton.setBackground(mediumGreen);
        logoutButton.setForeground(deepGreen);

        logoutButton.addActionListener(
                e -> onLogout.run()
        );

        add(logoutButton);

        setActivePage("dashboard");
    }

    private JButton createNavigationButton(String text) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("SansSerif", Font.BOLD, 15)
        );

        button.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        button.setMargin(
                new Insets(0, 15, 0, 10)
        );

        button.setPreferredSize(
                new Dimension(215, 45)
        );

        button.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 45)
        );

        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        return button;
    }

    public void setActivePage(String pageKey) {

        for (Map.Entry<String, JButton> entry
                : navigationButtons.entrySet()) {

            JButton button = entry.getValue();

            if (entry.getKey().equals(pageKey)) {
                button.setBackground(mediumGreen);
                button.setForeground(deepGreen);
            } else {
                button.setBackground(deepGreen);
                button.setForeground(white);
            }
        }
    }
}
