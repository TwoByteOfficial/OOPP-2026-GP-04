package gui.technical.dashboard.summary;

import gui.theme.UniMisTheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * Base UI card component for displaying key metric summaries on the Technical Officer Dashboard.
 * Displays a large numeric value with a descriptive title label and an accent color border strip on the left.
 */
public class SummaryCardPanel extends JPanel {

    // Label showing the prominent statistic value (e.g. "45", "12")
    private JLabel valueLabel;

    /**
     * Constructs a summary card with a title, initial value, and left accent border.
     *
     * @param title  Description of what the number represents (e.g. "Total Undergraduates")
     * @param value  Initial metric value string
     * @param accent Color used for the left vertical indicator stripe
     */
    public SummaryCardPanel(String title, String value, Color accent) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UniMisTheme.WHITE);

        // Left accent border strip with inner padding
        setBorder(new CompoundBorder(
                new MatteBorder(0, 5, 0, 0, accent),
                new EmptyBorder(12, 16, 12, 16)
        ));

        setPreferredSize(new Dimension(200, 100));

        valueLabel = new JLabel(value);
        valueLabel.setFont(UniMisTheme.FONT_H2);
        valueLabel.setForeground(UniMisTheme.TEXT);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UniMisTheme.FONT_TINY);
        titleLabel.setForeground(UniMisTheme.MUTED);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(valueLabel);
        add(Box.createVerticalStrut(6));
        add(titleLabel);
    }

    /**
     * Updates the displayed metric value dynamically (e.g., after database query completes).
     *
     * @param value New value string to display
     */
    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
