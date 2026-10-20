
package gui.technical.dashboard.overview;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MedicalStatusPanel extends JPanel {

    private final Color NAVY = new Color(24, 58, 99);
    private final Color MUTED = new Color(105, 118, 135);
    private final Color ORANGE = new Color(230, 126, 20);

    public MedicalStatusPanel() {
        setLayout(new BorderLayout(0, 18));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 230, 238)),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel title = new JLabel("Medical Status");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(NAVY);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel description = new JLabel(
                "Medical record statistics will appear here.");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        description.setForeground(MUTED);

        JLabel pendingLabel = new JLabel("Pending medical records: —");
        pendingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pendingLabel.setForeground(ORANGE);

        JButton viewButton = new JButton("View Medicals");
        viewButton.setFocusPainted(false);
        viewButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        details.add(description);
        details.add(Box.createVerticalStrut(18));
        details.add(pendingLabel);
        details.add(Box.createVerticalStrut(15));
        details.add(viewButton);

        add(title, BorderLayout.NORTH);
        add(details, BorderLayout.CENTER);
    }
}
