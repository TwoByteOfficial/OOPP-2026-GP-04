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
 * Removes missing font glyphs and applies interactive hover states.
 */
public class DashboardQuickActionsPanel extends JPanel {

    private final Consumer<String> onNavigate;

    public DashboardQuickActionsPanel(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        setOpaque(false);
        setLayout(new GridLayout(1, 4, 15, 0));
        setPreferredSize(new Dimension(0, 48));

        add(createActionButton("View Attendance", "attendance"));
        add(createActionButton("View Grades & GPA", "grades"));
        add(createActionButton("View Timetable", "timetables"));
        add(createActionButton("View Notices", "notices"));
    }

    private JButton createActionButton(String title, String tabKey) {
        JButton btn = new JButton(title);
        btn.setFont(AdminTheme.font(Font.BOLD, 15));
        btn.setForeground(AdminTheme.DEEP_GREEN);
        btn.setBackground(AdminTheme.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Border line = new LineBorder(new Color(0xD2, 0xD7, 0xE1), 1, true);
        Border padding = new EmptyBorder(10, 15, 10, 15);
        btn.setBorder(new CompoundBorder(line, padding));

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

        btn.addActionListener(e -> {
            if (onNavigate != null) onNavigate.accept(tabKey);
        });

        return btn;
    }
}