
package gui.technical.dashboard.summary;

import gui.theme.UniMisTheme;

public class TodaySessionsCard extends SummaryCardPanel {

    public TodaySessionsCard(String value) {
        super(
                "Today's Sessions",
                value,
                UniMisTheme.DEEP_GREEN
        );
    }
}
