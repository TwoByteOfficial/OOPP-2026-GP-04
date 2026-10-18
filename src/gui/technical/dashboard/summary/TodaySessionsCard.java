
package gui.technical.dashboard.summary;

import gui.technical.dashboard.summary.SummaryCardPanel;

import java.awt.Color;

public class TodaySessionsCard extends SummaryCardPanel {
    public TodaySessionsCard(String value) {
        super("Today's Sessions", value,
                new Color(15, 118, 110));
    }
}
