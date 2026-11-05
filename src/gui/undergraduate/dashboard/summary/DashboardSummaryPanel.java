package gui.undergraduate.dashboard.summary;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 1.1.2 - Summary Row combining all 4 undergraduate cards in a horizontal grid.
 * Renders courses, attendance, GPA, and medical summary cards side by side in a 4-column layout.
 */
public class DashboardSummaryPanel extends JPanel {

    public DashboardSummaryPanel(int courses, double attendance, double sgpa, double cgpa, int approvedMed, int pendingMed) {
        // Transparent container with a 1x4 grid layout (16px horizontal gap) and bottom padding
        setOpaque(false);
        setLayout(new GridLayout(1, 4, 16, 0));
        setBorder(new EmptyBorder(0, 0, 18, 0));

        // 1.1.2.1 - Enrolled Courses Summary Card
        add(new CoursesSummaryCard(courses));

        // 1.1.2.2 - Overall Attendance Summary Card
        add(new AttendanceSummaryCard(attendance));

        // 1.1.2.3 - GPA (SGPA & CGPA) Summary Card
        add(new GpaSummaryCard(sgpa, cgpa));

        // 1.1.2.4 - Medical Submissions Summary Card
        add(new MedicalSummaryCard(approvedMed, pendingMed));
    }
}