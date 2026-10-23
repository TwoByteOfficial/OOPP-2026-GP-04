
package gui.technical.dashboard.summary;

import javax.swing.*;
import java.awt.*;

public class DashboardSummaryPanel extends JPanel {

    public DashboardSummaryPanel() {

        setLayout(new GridLayout(1, 4, 14, 0));
        setOpaque(false);

        add(new UndergraduateSummaryCard("—"));
        add(new AttendanceSummaryCard("—"));
        add(new PendingMedicalCard("—"));
        add(new TodaySessionsCard("—"));
    }
}
