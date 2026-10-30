
package gui.technical.dashboard.overview;

import gui.theme.UniMisTheme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class DashboardOverviewPanel extends JPanel {

    public DashboardOverviewPanel(
            long userId,
            Consumer<String> navigationHandler) {

        setLayout(new GridLayout(1, 2, 16, 0));
        setOpaque(false);
        setBackground(UniMisTheme.LIGHT_BG);

        add(new AttendanceOverviewPanel(userId));

        add(new MedicalStatusPanel(
                userId,
                navigationHandler
        ));
    }
}
