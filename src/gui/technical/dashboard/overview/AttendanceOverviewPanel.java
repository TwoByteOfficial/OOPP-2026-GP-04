
package gui.technical.dashboard.overview;

import gui.theme.UniMisTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class AttendanceOverviewPanel extends JPanel {

    public AttendanceOverviewPanel() {

        setLayout(new BorderLayout(0, 16));
        setBackground(UniMisTheme.WHITE);

        setBorder(new CompoundBorderHelper());

        JLabel heading = new JLabel("Attendance Overview");
        heading.setFont(UniMisTheme.FONT_H3);
        heading.setForeground(UniMisTheme.TEXT);

        add(heading, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel description = new JLabel(
                "Attendance statistics will appear here.");
        description.setFont(UniMisTheme.FONT_TINY);
        description.setForeground(UniMisTheme.MUTED);

        JLabel status = new JLabel("No attendance data available");
        status.setFont(UniMisTheme.FONT_BODY_BOLD);
        status.setForeground(UniMisTheme.PRIMARY_DARK);

        content.add(description);
        content.add(Box.createVerticalStrut(16));
        content.add(status);

        add(content, BorderLayout.CENTER);
    }

    private static class CompoundBorderHelper
            extends EmptyBorder {

        private CompoundBorderHelper() {
            super(18, 18, 18, 18);
        }
    }
}
