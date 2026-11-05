package gui.technical.dashboard.overview;

import gui.theme.UniMisTheme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * [OOP - INHERITANCE]: Extends javax.swing.JPanel to create a 2-column overview container.
 */
public class DashboardOverviewPanel extends JPanel {

    /**
     * Constructs the overview grid container.
     *
     * @param userId            Database user ID of the Technical Officer
     * @param navigationHandler [OOP - INTERFACE]: java.util.function.Consumer<String> callback
     */
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
