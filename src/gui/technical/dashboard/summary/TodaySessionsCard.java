package gui.technical.dashboard.summary;

import gui.theme.UniMisTheme;

/**
 * Summary metric card on the dashboard showing the number of lecture or lab sessions
 * scheduled for today according to the department timetable.
 * Styled with a deep green left accent border.
 */
public class TodaySessionsCard extends SummaryCardPanel {

    /**
     * Constructs the Today's Sessions summary card.
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
