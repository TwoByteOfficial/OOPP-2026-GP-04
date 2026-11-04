package gui.undergraduate.notices.filter;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Category filter bar allowing undergraduates to filter circulars by department & urgency.
 */
public class NoticeTypeFilterBar extends JPanel {

    private final String[] categories = {"All Notices", "Academic", "Exam", "Attendance", "Urgent"};
    private JButton activeButton = null;

    public NoticeTypeFilterBar(Consumer<String> onFilterSelected) {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));

        for (String cat : categories) {
            JButton btn = new JButton(cat);
            btn.setFont(AdminTheme.font(Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(105, 32));

            if ("All Notices".equals(cat)) {
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
                if (onFilterSelected != null) {
                    onFilterSelected.accept(cat);
                }
            });

            add(btn);
        }
    }

    private void applyActiveStyle(JButton btn) {
        btn.setBackground(AdminTheme.FOREST_GREEN);
        btn.setForeground(AdminTheme.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    }

    private void applyInactiveStyle(JButton btn) {
        btn.setBackground(AdminTheme.WHITE);
        btn.setForeground(AdminTheme.DEEP_GREEN);
        btn.setBorder(BorderFactory.createLineBorder(new Color(0xD2, 0xD7, 0xE1), 1, true));
    }
}