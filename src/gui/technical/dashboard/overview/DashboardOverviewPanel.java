
package gui.technical.dashboard.overview;

import gui.theme.UniMisTheme;

import javax.swing.*;
import java.awt.*;

public class DashboardOverviewPanel extends JPanel {

    public DashboardOverviewPanel() {

        setLayout(new GridLayout(1, 2, 16, 0));
        setOpaque(false);
        setBackground(UniMisTheme.LIGHT_BG);

        add(new AttendanceOverviewPanel());
        add(new MedicalStatusPanel());
    }
}
