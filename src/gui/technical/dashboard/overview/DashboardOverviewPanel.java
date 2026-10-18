
package gui.technical.dashboard.overview;

import javax.swing.*;
import java.awt.*;

public class DashboardOverviewPanel extends JPanel {

    public DashboardOverviewPanel() {
        setLayout(new GridLayout(1, 2, 18, 0));
        setOpaque(false);

        add(new AttendanceOverviewPanel());
        add(new MedicalStatusPanel());
    }
}
