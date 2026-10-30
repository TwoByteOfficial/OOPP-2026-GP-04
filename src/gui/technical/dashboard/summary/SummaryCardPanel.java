
package gui.technical.dashboard.summary;

import gui.theme.UniMisTheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class SummaryCardPanel extends JPanel {
    private JLabel valueLabel;
    public SummaryCardPanel(String title, String value, Color accent) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UniMisTheme.WHITE);

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




    public void setValue(String value) {
        valueLabel.setText(value);
    }






}
