package gui.undergraduate.timetable.filter;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Clean weekday selector bar to filter timetable entries.
 * Provides interactive pill-style buttons for standard working weekdays and an aggregated view,
 * visually updating selection state and broadcasting filter events to listener components.
 */
public class DayFilterBar extends JPanel {

    // Available weekday filter tabs aligned with academic scheduling
    private final String[] days = {"All Days", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    // Tracks the currently selected filter button to manage active highlight styles
    private JButton activeButton = null;

    /**
     * Constructs the weekday filter bar and registers the selection callback.
     *
     * @param onDaySelected Consumer callback receiving the chosen day name on button click
     */
    public DayFilterBar(Consumer<String> onDaySelected) {
        // Transparent container with a compact 5px horizontal gap
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0)); // Gap එක 5px කළා

        // -------------------------------------------------------------
        // Day Filter Buttons Generation & Event Binding
        // -------------------------------------------------------------
        for (String day : days) {
            JButton btn = new JButton(day);
            btn.setFont(AdminTheme.font(Font.BOLD, 11)); // Font එක 11 කළා
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(82, 30)); // 82px පළල සියලුම buttons 6ට හොඳටම ඇති

            // Default initial selection state to "All Days"
            if ("All Days".equals(day)) {
                applyActiveStyle(btn);
                activeButton = btn;
            } else {
                applyInactiveStyle(btn);
            }

            // Click listener: toggle visual state and trigger callback
            btn.addActionListener(e -> {
                if (activeButton != null) {
                    applyInactiveStyle(activeButton);
                }
                applyActiveStyle(btn);
                activeButton = btn;
                if (onDaySelected != null) {
                    onDaySelected.accept(day);
                }
            });

            add(btn);
        }
    }

    /**
     * Applies the active highlight styling to the selected filter button.
     * Uses Forest Green background with white foreground text.
     *
     * @param btn The button receiving active styling
     */
    private void applyActiveStyle(JButton btn) {
        btn.setBackground(AdminTheme.FOREST_GREEN);
        btn.setForeground(AdminTheme.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
    }

    /**
     * Applies the inactive baseline styling to unselected buttons.
     * Uses white background, deep green text, and a subtle rounded border.
     *
     * @param btn The button receiving inactive styling
     */
    private void applyInactiveStyle(JButton btn) {
        btn.setBackground(AdminTheme.WHITE);
        btn.setForeground(AdminTheme.DEEP_GREEN);
        btn.setBorder(BorderFactory.createLineBorder(new Color(0xD2, 0xD7, 0xE1), 1, true));
    }
}