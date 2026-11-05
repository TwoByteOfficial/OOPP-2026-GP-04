package gui.undergraduate.attendance.header;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Header panel displaying attendance module title and semester eligibility threshold notice.
 * Provides consistent top-level header branding for the attendance tracking module.
 */
public class AttendanceHeaderPanel extends JPanel {

    public AttendanceHeaderPanel() {
        // Set transparent background and align components vertically
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Main module heading using theme H1 font
        JLabel title = new JLabel("Attendance & Exam Eligibility");
        title.setFont(AdminTheme.FONT_H1);
        title.setForeground(AdminTheme.DEEP_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Subtitle displaying academic semester context and institutional 80% regulation threshold
        JLabel subtitle = new JLabel("Academic Year 2026/2027 • Minimum 80% attendance required for exam entry");
        subtitle.setFont(AdminTheme.font(Font.PLAIN, 13));
        subtitle.setForeground(AdminTheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add components with a subtle 2px vertical gap
        add(title);
        add(Box.createVerticalStrut(2));
        add(subtitle);
    }
}