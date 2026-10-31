package gui.undergraduate.dashboard.summary;

import gui.admin.AdminTheme;

public class GpaSummaryCard extends SummaryCardPanel {
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