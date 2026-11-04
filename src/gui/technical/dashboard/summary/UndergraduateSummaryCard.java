package gui.technical.dashboard.summary;

import gui.theme.UniMisTheme;

/**
 * Summary metric card on the dashboard showing the total count of active undergraduate students
 * enrolled in the Technical Officer's department.
 * Styled with the primary brand color left accent border.
 */
public class UndergraduateSummaryCard extends SummaryCardPanel {

    /**
     * Constructs the Department Undergraduates summary card.
     *
     * @param value Count string to display (e.g., "150")
     */
    public UndergraduateSummaryCard(String value) {
        super(
                "Department Undergraduates",
                value,
                UniMisTheme.PRIMARY
        );
    }
}
