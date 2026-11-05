package gui.technical.dashboard.summary;

import gui.theme.UniMisTheme;

/**
 * [OOP - INHERITANCE]: Extends SummaryCardPanel, inheriting its layout, border styling,
 * and setValue() method while supplying specialized configuration (title and accent color).
 */
public class AttendanceSummaryCard extends SummaryCardPanel {

    /**
     * Constructs the Attendance Profiles card.
     * Invokes the superclass constructor via super() (Inheritance).
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
