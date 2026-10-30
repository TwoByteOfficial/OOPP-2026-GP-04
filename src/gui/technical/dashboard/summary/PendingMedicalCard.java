
package gui.technical.dashboard.summary;

import gui.theme.UniMisTheme;

public class PendingMedicalCard extends SummaryCardPanel {

    public PendingMedicalCard(String value) {
        super(
                "Pending Medicals",
                value,
                UniMisTheme.MEDIUM_GREEN
        );
    }
}
