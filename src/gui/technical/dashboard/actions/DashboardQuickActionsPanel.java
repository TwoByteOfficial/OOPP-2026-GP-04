package gui.technical.dashboard.actions;

import gui.theme.UniMisTheme;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Quick Actions panel displayed on the Technical Officer Dashboard.
 * Provides 4 one-click shortcut buttons to quickly navigate to primary workflows:
 * - Record Attendance -> switches to AttendancePanel
 * - Add Medical Record -> switches to MedicalRecordsPanel
 * - View Timetable -> switches to DepartmentTimetablePanel
 * - View Notices -> switches to NoticesPanel
 */
public class DashboardQuickActionsPanel extends JPanel {

    /**
     * Constructs the Quick Actions section with styled shortcut buttons.
     *
     * @param navigationHandler Callback function to navigate between pages
     */
    public DashboardQuickActionsPanel(Consumer<String> navigationHandler) {

        setLayout(new BorderLayout(0, 14));
        setOpaque(false);

        // Section header
        JLabel heading = new JLabel("Quick Actions");
        heading.setFont(UniMisTheme.FONT_H3);
        heading.setForeground(UniMisTheme.TEXT);
        add(heading, BorderLayout.NORTH);

        // Grid containing the 4 action shortcut buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        buttonsPanel.setOpaque(false);

        buttonsPanel.add(createButton("Record Attendance", "Attendance", navigationHandler));
        buttonsPanel.add(createButton("Add Medical Record", "Medical Records", navigationHandler));
        buttonsPanel.add(createButton("View Timetable", "Timetables", navigationHandler));
        buttonsPanel.add(createButton("View Notices", "Notices", navigationHandler));

        add(buttonsPanel, BorderLayout.CENTER);
    }

    /**
     * Helper to create a consistently styled action button with hand cursor and click listener.
     *
     * @param text              Label text displayed on the button
     * @param page              Target page name passed to navigationHandler
     * @param navigationHandler Callback to navigate to the target page
     * @return Styled JButton
     */
    private JButton createButton(String text, String page,
                                 Consumer<String> navigationHandler) {

        JButton button = new JButton(text);

        button.setFont(UniMisTheme.FONT_BODY_BOLD);
        button.setForeground(UniMisTheme.TEXT);
        button.setBackground(UniMisTheme.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new LineBorder(UniMisTheme.BORDER, 1, true));

        // When clicked, trigger parent dashboard to switch to corresponding page
        button.addActionListener(e -> navigationHandler.accept(page));

        return button;
    }
}
