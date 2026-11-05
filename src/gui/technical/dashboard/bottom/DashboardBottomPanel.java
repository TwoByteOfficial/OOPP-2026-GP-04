package gui.technical.dashboard.bottom;

import gui.theme.UniMisTheme;

import javax.swing.*;
import java.awt.*;

/**
 * [OOP - INHERITANCE]: Extends javax.swing.JPanel to create the bottom 2-column container.
 */
public class DashboardBottomPanel extends JPanel {

    /**
     * Constructs the bottom dashboard section.
     *
     * @param userId Logged-in Technical Officer's database user ID
     */
    public DashboardBottomPanel(long userId) {

        setLayout(new GridLayout(1, 2, 16, 0));
        setOpaque(false);
        setBackground(UniMisTheme.LIGHT_BG);

        add(new DepartmentTimetablePanel(userId));
        add(new LatestNoticesPanel(userId));
    }
}
