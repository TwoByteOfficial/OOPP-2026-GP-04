package gui.undergraduate.dashboard.summary;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 1.1.2 - Summary Row combining all 4 undergraduate cards in a horizontal grid.
 */
public class DashboardSummaryPanel extends JPanel {

    public DashboardSummaryPanel(int courses, double attendance, double sgpa, double cgpa, int approvedMed, int pendingMed) {
        setOpaque(false);
        setLayout(new GridLayout(1, 4, 16, 0));
        setBorder(new EmptyBorder(0, 0, 18, 0));

        // 1.1.2.1
        add(new CoursesSummaryCard(courses));

        // 1.1.2.2
        add(new AttendanceSummaryCard(attendance));

        // 1.1.2.3
        add(new GpaSummaryCard(sgpa, cgpa));

        // 1.1.2.4
        add(new MedicalSummaryCard(approvedMed, pendingMed));
    }
}