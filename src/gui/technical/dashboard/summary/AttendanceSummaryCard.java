package gui.technical.dashboard.summary;

import gui.theme.UniMisTheme;

/**
 * Summary metric card on the dashboard showing the number of distinct student attendance profiles.
 * Styled with a forest green left accent border.
 */
public class AttendanceSummaryCard extends SummaryCardPanel {

    /**
     * Constructs the Attendance Profiles card.
     *
     * @param value Count string to display (e.g., "120")
     */
    public AttendanceSummaryCard(String value) {
        super(
                "Attendance Profiles",
                value,
                UniMisTheme.FOREST_GREEN
        );
    }
}
