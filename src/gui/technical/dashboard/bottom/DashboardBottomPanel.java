
package gui.technical.dashboard.bottom;

import gui.theme.UniMisTheme;

import javax.swing.*;
import java.awt.*;

public class DashboardBottomPanel extends JPanel {

    public DashboardBottomPanel(long userId) {

        setLayout(new GridLayout(1, 2, 16, 0));
        setOpaque(false);
        setBackground(UniMisTheme.LIGHT_BG);

        add(new DepartmentTimetablePanel(userId));
        add(new LatestNoticesPanel(userId));
    }
}
