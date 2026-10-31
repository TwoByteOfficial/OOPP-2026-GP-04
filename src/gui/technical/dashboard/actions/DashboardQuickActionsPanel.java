
package gui.technical.dashboard.actions;

import gui.theme.UniMisTheme;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.function.Consumer;

public class DashboardQuickActionsPanel extends JPanel {

    public DashboardQuickActionsPanel(Consumer<String> navigationHandler) {

        setLayout(new BorderLayout(0, 14));
        setOpaque(false);

        JLabel heading = new JLabel("Quick Actions");
        heading.setFont(UniMisTheme.FONT_H3);
        heading.setForeground(UniMisTheme.TEXT);

        add(heading, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        buttonsPanel.setOpaque(false);

        buttonsPanel.add(createButton("Record Attendance",
                "Attendance", navigationHandler));

        buttonsPanel.add(createButton("Add Medical Record",
                "Medical Records", navigationHandler));

        buttonsPanel.add(createButton("View Timetable",
                "Timetables", navigationHandler));

        buttonsPanel.add(createButton("View Notices",
                "Notices", navigationHandler));

        add(buttonsPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text, String page,
                                 Consumer<String> navigationHandler) {

        JButton button = new JButton(text);

        button.setFont(UniMisTheme.FONT_BODY_BOLD);
        button.setForeground(UniMisTheme.TEXT);
        button.setBackground(UniMisTheme.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new LineBorder(UniMisTheme.BORDER, 1, true));

        button.addActionListener(e -> navigationHandler.accept(page));

        return button;
    }
}
