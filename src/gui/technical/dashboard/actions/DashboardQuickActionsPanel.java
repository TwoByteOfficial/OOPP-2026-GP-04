
package gui.technical.dashboard.actions;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardQuickActionsPanel extends JPanel {

    private final Color BLUE = new Color(43, 99, 235);
    private final Color ORANGE = new Color(230, 126, 20);
    private final Color NAVY = new Color(24, 58, 99);
    private final Color TEAL = new Color(15, 118, 110);

    public DashboardQuickActionsPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(5, 5, 5, 5));

        add(createButton("Record Attendance", BLUE));
        add(createButton("Add Medical", ORANGE));
        add(createButton("View Timetable", NAVY));
        add(createButton("View Notices", TEAL));
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);

        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(10, 14, 10, 14));

        return button;
    }
}
