package gui.technical.dashboard.summary;

import gui.theme.UniMisTheme;

/**
 * [OOP - INHERITANCE]: Extends SummaryCardPanel to create a specialized card
 * showing the number of sessions scheduled for today.
 */
public class TodaySessionsCard extends SummaryCardPanel {

    /**
     * Constructs the Today's Sessions summary card.
     * Invokes super() to pass specialized title and deep green accent color.
     *
     * @param value Count string to display (e.g., "3")
     */
    public TodaySessionsCard(String value) {
        super(
                "Today's Sessions",
                value,
                UniMisTheme.DEEP_GREEN
        );
    }
}
