package gui.undergraduate.dashboard.actions;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Quick action shortcuts panel matching Admin Dashboard design.
 * Renders interactive navigation buttons for attendance, grades, timetables, and notices.
 */
public class DashboardQuickActionsPanel extends JPanel {

    // Callback consumer for tab navigation events
    private final Consumer<String> onNavigate;

    public DashboardQuickActionsPanel(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        // Transparent container with a 4-column single-row grid layout
        setOpaque(false);
        setLayout(new GridLayout(1, 4, 12, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        setPreferredSize(new Dimension(0, 44));

        // Create quick navigation action buttons
        add(createActionButton("View Attendance", "attendance"));
        add(createActionButton("View Grades & GPA", "grades"));
        add(createActionButton("View Timetable", "timetables"));
        add(createActionButton("View Notices", "notices"));
    }

    /**
     * Factory method creating a styled quick action button with hover effects and navigation triggers.
     */
    private JButton createActionButton(String title, String tabKey) {
        JButton btn = new JButton(title);
        btn.setFont(AdminTheme.font(Font.BOLD, 13));
        btn.setForeground(AdminTheme.DEEP_GREEN);
        btn.setBackground(AdminTheme.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Default button border and padding compound
        Border line = new LineBorder(new Color(0xD2, 0xD7, 0xE1), 1, true);
        Border padding = new EmptyBorder(8, 10, 8, 10);
        btn.setBorder(new CompoundBorder(line, padding));

        // Mouse hover listeners for smooth visual feedback (Mint background and Forest Green border)
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(AdminTheme.MINT);
                btn.setBorder(new CompoundBorder(new LineBorder(AdminTheme.FOREST_GREEN, 1, true), padding));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(AdminTheme.WHITE);
                btn.setBorder(new CompoundBorder(new LineBorder(new Color(0xD2, 0xD7, 0xE1), 1, true), padding));
            }
        });

        // Action listener triggering navigation callback with target tab key
        btn.addActionListener(e -> {
            if (onNavigate != null) onNavigate.accept(tabKey);
        });

        return btn;
    }
}