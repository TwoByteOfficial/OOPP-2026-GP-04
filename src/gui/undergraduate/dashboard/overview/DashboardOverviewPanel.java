package gui.undergraduate.dashboard.overview;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 1.1.4 - Overview Row combining Attendance Breakdown & Academic Summary
 */
public class DashboardOverviewPanel extends JPanel {

    public DashboardOverviewPanel(double theoryPct, double practicalPct, double combinedPct,
                                  int totalEnrolled, int caCompleted, int approvedMed, int pendingMed) {
        setOpaque(false);
        setLayout(new GridLayout(1, 2, 16, 0));
        setBorder(new EmptyBorder(0, 0, 18, 0));

        // 1.1.4.1
        add(new AttendanceOverviewPanel(theoryPct, practicalPct, combinedPct));

        // 1.1.4.2
        add(new AcademicOverviewPanel(totalEnrolled, caCompleted, approvedMed, pendingMed));
    }
}