package gui.technical.dashboard.summary;

import gui.theme.UniMisTheme;

/**
 * Summary metric card on the dashboard showing the count of pending medical certificates
 * submitted by students awaiting Technical Officer approval.
 * Styled with a medium green left accent border.
 */
public class PendingMedicalCard extends SummaryCardPanel {

    /**
     * Constructs the Pending Medicals summary card.
     *
     * @param value Count string to display (e.g., "5")
     */
    public PendingMedicalCard(String value) {
        super(
                "Pending Medicals",
                value,
                UniMisTheme.MEDIUM_GREEN
        );
    }
}
