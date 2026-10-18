
package gui.technical.dashboard.bottom;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LatestNoticesPanel extends JPanel {

    private final Color NAVY = new Color(24, 58, 99);
    private final Color MUTED = new Color(105, 118, 135);

    public LatestNoticesPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 230, 238)),
                new EmptyBorder(18, 18, 18, 18)));

        JLabel title = new JLabel("Latest Relevant Notices");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(NAVY);

        JPanel notices = new JPanel();
        notices.setOpaque(false);
        notices.setLayout(new BoxLayout(notices, BoxLayout.Y_AXIS));

        JLabel emptyMessage = new JLabel(
                "Latest notices will appear here.");
        emptyMessage.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emptyMessage.setForeground(MUTED);

        notices.add(emptyMessage);

        JButton viewButton = new JButton("View All Notices");
        viewButton.setFocusPainted(false);
        viewButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(title, BorderLayout.NORTH);
        add(notices, BorderLayout.CENTER);
        add(viewButton, BorderLayout.SOUTH);
    }
}
