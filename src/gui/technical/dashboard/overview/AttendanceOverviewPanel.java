
package gui.technical.dashboard.overview;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AttendanceOverviewPanel extends JPanel {

    private final Color NAVY = new Color(24, 58, 99);
    private final Color BLUE = new Color(43, 99, 235);
    private final Color MUTED = new Color(105, 118, 135);

    public AttendanceOverviewPanel() {
        setLayout(new BorderLayout(0, 18));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 230, 238)),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel title = new JLabel("Attendance Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(NAVY);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel description = new JLabel(
                "Attendance statistics will appear here.");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        description.setForeground(MUTED);

        JProgressBar progress = new JProgressBar(0, 100);
        progress.setValue(0);
        progress.setStringPainted(true);
        progress.setString("No data");
        progress.setForeground(BLUE);
        progress.setBackground(new Color(235, 239, 245));
        progress.setPreferredSize(new Dimension(250, 18));
        progress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        JButton viewButton = new JButton("Open Attendance");
        viewButton.setFocusPainted(false);
        viewButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        details.add(description);
        details.add(Box.createVerticalStrut(18));
        details.add(progress);
        details.add(Box.createVerticalStrut(15));
        details.add(viewButton);

        add(title, BorderLayout.NORTH);
        add(details, BorderLayout.CENTER);
    }
}
