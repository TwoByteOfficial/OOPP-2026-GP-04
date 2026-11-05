package gui.undergraduate.timetable.header;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Header panel displaying weekly timetable schedule title and semester details.
 * Provides standard module branding, academic period context, and left-aligned
 * typography for the Undergraduate Timetable module.
 */
public class TimetableHeaderPanel extends JPanel {

    /**
     * Constructs the timetable header panel with a vertical box layout and styled text labels.
     */
    public TimetableHeaderPanel() {
        // Base container layout and transparency configuration
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // -------------------------------------------------------------
        // Main View Title (Using theme standard H1 font and deep green)
        // -------------------------------------------------------------
        JLabel title = new JLabel("Weekly Academic Lecture Timetable");
        title.setFont(AdminTheme.FONT_H1);
        title.setForeground(AdminTheme.DEEP_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // -------------------------------------------------------------
        // Subtitle Indicating Academic Year and Semester Placement
        // -------------------------------------------------------------
        JLabel subtitle = new JLabel("Academic Year 2026/2027 • Level II Semester I Master Schedule");
        subtitle.setFont(AdminTheme.font(Font.PLAIN, 13));
        subtitle.setForeground(AdminTheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // -------------------------------------------------------------
        // Component Assembly with 2px Vertical Spacing
        // -------------------------------------------------------------
        add(title);
        add(Box.createVerticalStrut(2));
        add(subtitle);
    }
}