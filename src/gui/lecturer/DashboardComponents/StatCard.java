
package gui.lecturer.DashboardComponents;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class StatCard extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color MEDIUM_GREEN = new Color(56, 142, 60);
    private static final Color BORDER_GREEN = new Color(200, 230, 201);
    private static final Color TEXT_GRAY = new Color(96, 112, 100);

    private final JLabel valueLabel;

    public StatCard(String title, String value, String iconText) {

        setLayout(new BorderLayout(12, 12));
        setBackground(Color.WHITE);

        setBorder(new CompoundBorder(
                new LineBorder(BORDER_GREEN, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JPanel headingPanel = new JPanel(new BorderLayout());
        headingPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(iconText);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));
        iconLabel.setForeground(MEDIUM_GREEN);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_GRAY);

        headingPanel.add(iconLabel, BorderLayout.WEST);
        headingPanel.add(titleLabel, BorderLayout.CENTER);

        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        valueLabel.setForeground(DEEP_GREEN);

        add(headingPanel, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);

        setPreferredSize(new Dimension(220, 125));
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
