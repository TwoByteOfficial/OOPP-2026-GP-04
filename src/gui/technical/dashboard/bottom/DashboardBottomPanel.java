
package gui.technical.dashboard.bottom;

import javax.swing.*;
import java.awt.*;

public class DashboardBottomPanel extends JPanel {

    public DashboardBottomPanel() {
        setLayout(new GridLayout(1, 2, 18, 0));
        setOpaque(false);

        add(new DepartmentTimetablePanel());
        add(new LatestNoticesPanel());
    }
}
