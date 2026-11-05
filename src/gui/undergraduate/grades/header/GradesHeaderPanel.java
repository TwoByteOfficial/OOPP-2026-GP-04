package gui.undergraduate.grades.header;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Header panel displaying Grades and GPA records title with semester progression indicator.
 * Provides consistent typography, branding, and spacing aligned with the Undergraduate portal header standards.
 */
public class GradesHeaderPanel extends JPanel {

    public GradesHeaderPanel() {
        // Transparent vertical box layout aligning children to the left edge
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // -------------------------------------------------------------
        // Main View Title (Using theme standard H1 font and deep green)
        // -------------------------------------------------------------
        JLabel title = new JLabel("Academic Grades & GPA Performance");
        title.setFont(AdminTheme.FONT_H1);
        title.setForeground(AdminTheme.DEEP_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // -------------------------------------------------------------
        // View Subtitle (Transcript and active semester indicator)
        // -------------------------------------------------------------
        JLabel subtitle = new JLabel("Official Academic Transcript • Level II Semester I Records");
        subtitle.setFont(AdminTheme.font(Font.PLAIN, 13));
        subtitle.setForeground(AdminTheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assemble title hierarchy with standard 2px vertical spacing
        add(title);
        add(Box.createVerticalStrut(2));
        add(subtitle);
    }
}