package gui.undergraduate.dashboard.summary;

import gui.admin.AdminTheme;

/**
 * Summary metric card displaying the student's current Semester GPA (SGPA)
 * and cumulative CGPA across academic terms.
 */
public class GpaSummaryCard extends SummaryCardPanel {

    /**
     * Initializes GPA summary card with a graduation cap icon, medium green theme color,
     * formatted SGPA value, and cumulative CGPA detail in the subtitle.
     */
    public GpaSummaryCard(double sgpa, double cgpa) {
        super(
                "🎓",
                AdminTheme.MEDIUM_GREEN,
                String.format("%.2f", sgpa),
                "Current SGPA",
                String.format("Cumulative CGPA: %.2f", cgpa)
        );
    }
}