
package gui.technical.dashboard.bottom;

import gui.theme.UniMisTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LatestNoticesPanel extends JPanel {

    public LatestNoticesPanel() {

        setLayout(new BorderLayout(0, 14));
        setBackground(UniMisTheme.WHITE);
        setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel heading = new JLabel("Latest Notices");
        heading.setFont(UniMisTheme.FONT_H3);
        heading.setForeground(UniMisTheme.TEXT);

        add(heading, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel message = new JLabel("No notices available.");
        message.setFont(UniMisTheme.FONT_TINY);
        message.setForeground(UniMisTheme.MUTED);

        message.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(message);

        add(content, BorderLayout.CENTER);
    }
}
