
package gui.technical.dashboard.summary;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SummaryCardPanel extends JPanel {

    public SummaryCardPanel(String title, String value, Color accent) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(18, 14, 18, 10)));

        JLabel number = new JLabel(value);
        number.setFont(new Font("Segoe UI", Font.BOLD, 25));
        number.setForeground(new Color(40, 55, 75));

        JLabel caption = new JLabel(title);
        caption.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        caption.setForeground(new Color(100, 112, 128));

        add(number);
        add(Box.createVerticalStrut(7));
        add(caption);
    }
}
