package gui.technical.dashboard.summary;

import gui.theme.UniMisTheme;

/**
 * [OOP - INHERITANCE]: Extends SummaryCardPanel to create a specialized metric card
 * for pending medical certificate requests.
 */
public class PendingMedicalCard extends SummaryCardPanel {

    /**
     * Constructs the Pending Medicals summary card.
     * Invokes super() to pass specialized title and medium green accent color.
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
