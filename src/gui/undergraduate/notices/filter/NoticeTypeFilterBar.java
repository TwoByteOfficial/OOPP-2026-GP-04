package gui.undergraduate.notices.filter;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Category filter bar allowing undergraduates to filter circulars by academic classifications.
 * Aligned strictly with database ENUM values (Academic, Exam, Attendance, Emergency).
 * Provides interactive pill-style toggle buttons with dynamic visual feedback for active selection.
 */
public class NoticeTypeFilterBar extends JPanel {

    // Available notice filter category labels matching system classifications
    private final String[] categories = {"All Notices", "Academic", "Exam", "Attendance", "Emergency"};

    // Tracks the currently highlighted active filter button
    private JButton activeButton = null;

    /**
     * Constructs the notice category filter bar and registers filter event callbacks.
     *
     * @param onFilterSelected Consumer callback receiving the chosen category name on click
     */
    public NoticeTypeFilterBar(Consumer<String> onFilterSelected) {
        // Transparent left-aligned horizontal flow layout
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));

        // -------------------------------------------------------------
        // Category Filter Buttons Generation & Setup
        // -------------------------------------------------------------
        for (String cat : categories) {
            JButton btn = new JButton(cat);
            btn.setFont(AdminTheme.font(Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(110, 32));

            // Default initial selection to "All Notices"
            if ("All Notices".equals(cat)) {
                applyActiveStyle(btn);
                activeButton = btn;
            } else {
                applyInactiveStyle(btn);
            }

            // Register click handler to toggle button styles and trigger filter action
            btn.addActionListener(e -> {
                if (activeButton != null) {
                    applyInactiveStyle(activeButton);
                }
                applyActiveStyle(btn);
                activeButton = btn;
                if (onFilterSelected != null) {
                    onFilterSelected.accept(cat);
                }
            });

            add(btn);
        }
    }

    /**
     * Applies highlighted active theme styling (Forest Green solid background with white text).
     *
     * @param btn The button receiving active styling
     */
    private void applyActiveStyle(JButton btn) {
        btn.setBackground(AdminTheme.FOREST_GREEN);
        btn.setForeground(AdminTheme.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    }

    /**
     * Applies standard inactive theme styling (White card background with subtle stroke border).
     *
     * @param btn The button receiving inactive styling
     */
    private void applyInactiveStyle(JButton btn) {
        btn.setBackground(AdminTheme.WHITE);
        btn.setForeground(AdminTheme.DEEP_GREEN);
        btn.setBorder(BorderFactory.createLineBorder(new Color(0xD2, 0xD7, 0xE1), 1, true));
    }
}