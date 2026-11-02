package gui.undergraduate.dashboard.summary;

import gui.admin.AdminTheme;

import java.awt.Color;

public class MedicalSummaryCard extends SummaryCardPanel {
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