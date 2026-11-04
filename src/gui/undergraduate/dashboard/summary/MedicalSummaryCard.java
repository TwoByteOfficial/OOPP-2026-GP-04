package gui.undergraduate.dashboard.summary;

import gui.admin.AdminTheme;

import java.awt.Color;

/**
 * Summary metric card displaying approved medical record counts and pending review statuses.
 * Dynamically highlights in orange if pending submissions exist, or forest green when fully processed.
 */
public class MedicalSummaryCard extends SummaryCardPanel {

    /**
     * Initializes medical summary card with hospital icon, conditional theme color,
     * approved count value, and pending review status subtitle.
     */
    public MedicalSummaryCard(int approvedCount, int pendingCount) {
        super(
                "🏥",
                pendingCount > 0 ? new Color(0xF0, 0xAD, 0x4E) : AdminTheme.FOREST_GREEN,
                String.valueOf(approvedCount),
                "Approved Medicals",
                pendingCount > 0 ? pendingCount + " Pending Review" : "All Processed"
        );
    }
}