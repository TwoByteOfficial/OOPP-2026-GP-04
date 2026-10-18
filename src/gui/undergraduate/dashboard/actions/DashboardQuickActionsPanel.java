package gui.undergraduate.dashboard.actions;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * 1.1.3 - Undergraduate Quick Action Buttons Strip
 */
public class DashboardQuickActionsPanel extends JPanel {

    public DashboardQuickActionsPanel(Consumer<String> navigationCallback) {
        setOpaque(false);
        setLayout(new GridLayout(1, 4, 16, 0));
        setBorder(new EmptyBorder(0, 0, 18, 0));

        // Quick action buttons 4
        add(createActionButton("📊 View Attendance", "attendance", navigationCallback));
        add(createActionButton("🎓 View Grades & GPA", "grades", navigationCallback));
        add(createActionButton("📅 View Timetable", "timetable", navigationCallback));
        add(createActionButton("📢 View Notices", "notices", navigationCallback));
    }

    private JButton createActionButton(String text, String targetTab, Consumer<String> callback) {
        JButton button = new JButton(text);
        button.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 15));
        button.setForeground(AdminTheme.DEEP_GREEN);
        button.setBackground(AdminTheme.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Soft border matching theme
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                new EmptyBorder(12, 14, 12, 14)
        ));

        // Hover effect for smooth UI experience
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(AdminTheme.MINT);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(AdminTheme.WHITE);
            }
        });

        // Click action: notifies main dashboard to switch tabs
        button.addActionListener(e -> {
            if (callback != null) {
                callback.accept(targetTab);
            }
        });

        return button;
    }
}