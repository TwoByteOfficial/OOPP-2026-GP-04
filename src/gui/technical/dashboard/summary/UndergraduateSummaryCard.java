package gui.technical.dashboard.summary;

import gui.theme.UniMisTheme;

/**
 * [OOP - INHERITANCE]: Extends SummaryCardPanel to create a specialized metric card
 * displaying active undergraduate enrollment in the department.
 */
public class UndergraduateSummaryCard extends SummaryCardPanel {

    /**
     * Constructs the Department Undergraduates summary card.
     * Invokes super() to pass specialized title and primary green accent color.
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
