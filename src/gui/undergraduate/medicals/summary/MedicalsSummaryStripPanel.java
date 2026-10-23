package gui.undergraduate.medicals.summary;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Metric container assembling the 4 status cards in a horizontal grid.
 */
public class MedicalsSummaryStripPanel extends JPanel {

    private final MedicalStatCard totalCard;
    private final MedicalStatCard approvedCard;
    private final MedicalStatCard pendingCard;
    private final MedicalStatCard rejectedCard;

    public MedicalsSummaryStripPanel() {
        setOpaque(false);
        setLayout(new GridLayout(1, 4, 15, 0));
        setPreferredSize(new Dimension(0, 75));

        totalCard = new MedicalStatCard("0 Submissions", "Total Applications", AdminTheme.FOREST_GREEN);
        approvedCard = new MedicalStatCard("0 Approved", "Verified & Counted", AdminTheme.FOREST_GREEN);
        pendingCard = new MedicalStatCard("0 Pending", "Awaiting Review", AdminTheme.WARNING);
        rejectedCard = new MedicalStatCard("0 Rejected", "Declined / Invalid", AdminTheme.MUTED);

        add(totalCard);
        add(approvedCard);
        add(pendingCard);
        add(rejectedCard);
    }

    public void updateCounts(int total, int approved, int pending, int rejected) {
        totalCard.setValue(total + " Submissions");
        approvedCard.setValue(approved + " Approved");
        pendingCard.setValue(pending + " Pending");
        rejectedCard.setValue(rejected + " Rejected");
    }
}