package gui.undergraduate.timetable.filter;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Clean weekday selector bar to filter timetable entries.
 */
public class DayFilterBar extends JPanel {

    private final String[] days = {"All Days", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private JButton activeButton = null;

    public DayFilterBar(Consumer<String> onDaySelected) {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0)); // Gap එක 5px කළා

        for (String day : days) {
            JButton btn = new JButton(day);
            btn.setFont(AdminTheme.font(Font.BOLD, 11)); // Font එක 11 කළා
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(82, 30)); // 82px පළල සියලුම buttons 6ට හොඳටම ඇති

            if ("All Days".equals(day)) {
                applyActiveStyle(btn);
                activeButton = btn;
            } else {
                applyInactiveStyle(btn);
            }

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

    private void applyActiveStyle(JButton btn) {
        btn.setBackground(AdminTheme.FOREST_GREEN);
        btn.setForeground(AdminTheme.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
    }

    private void applyInactiveStyle(JButton btn) {
        btn.setBackground(AdminTheme.WHITE);
        btn.setForeground(AdminTheme.DEEP_GREEN);
        btn.setBorder(BorderFactory.createLineBorder(new Color(0xD2, 0xD7, 0xE1), 1, true));
    }
}