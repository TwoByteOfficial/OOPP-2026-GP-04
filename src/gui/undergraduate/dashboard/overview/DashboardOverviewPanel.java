package gui.undergraduate.dashboard.overview;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 1.1.4 - Overview Row combining Attendance Breakdown & Academic Summary.
 * Renders the attendance overview panel and academic/medical overview panel side by side in a 1x2 grid.
 */
public class DashboardOverviewPanel extends JPanel {

    public DashboardOverviewPanel(double theoryPct, double practicalPct, double combinedPct,
                                  int totalEnrolled, int caCompleted, int approvedMed, int pendingMed) {
        // Transparent container with a 1x2 grid layout (16px horizontal gap) and bottom padding
        setOpaque(false);
        setLayout(new GridLayout(1, 2, 16, 0));
        setBorder(new EmptyBorder(0, 0, 18, 0));

        // 1.1.4.1 - Left Card: Attendance Breakdown & Exam Eligibility Overview
        add(new AttendanceOverviewPanel(theoryPct, practicalPct, combinedPct));

        // 1.1.4.2 - Right Card: Academic Performance & Medical Summary Overview
        add(new AcademicOverviewPanel(totalEnrolled, caCompleted, approvedMed, pendingMed));
    }
}