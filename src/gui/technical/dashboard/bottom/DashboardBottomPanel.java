
package gui.technical.dashboard.bottom;

import gui.theme.UniMisTheme;

import javax.swing.*;
import java.awt.*;

public class DashboardBottomPanel extends JPanel {

    public DashboardBottomPanel() {

        setLayout(new GridLayout(1, 2, 16, 0));
        setOpaque(false);
        setBackground(UniMisTheme.LIGHT_BG);

        add(new DepartmentTimetablePanel());
        add(new LatestNoticesPanel());
    }
}
