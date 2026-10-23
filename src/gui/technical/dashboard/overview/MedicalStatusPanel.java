
package gui.technical.dashboard.overview;

import gui.theme.UniMisTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MedicalStatusPanel extends JPanel {

    public MedicalStatusPanel() {

        setLayout(new BorderLayout(0, 16));
        setBackground(UniMisTheme.WHITE);
        setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel heading = new JLabel("Medical Status");
        heading.setFont(UniMisTheme.FONT_H3);
        heading.setForeground(UniMisTheme.TEXT);

        add(heading, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel description = new JLabel(
                "Medical record approval status");
        description.setFont(UniMisTheme.FONT_TINY);
        description.setForeground(UniMisTheme.MUTED);

        JLabel pendingLabel = new JLabel(
                "Pending medical records: —");
        pendingLabel.setFont(UniMisTheme.FONT_BODY_BOLD);
        pendingLabel.setForeground(UniMisTheme.PRIMARY_DARK);

        JButton viewButton = new JButton("View Medical Records");
        viewButton.setFont(UniMisTheme.FONT_SMALL);
        viewButton.setForeground(UniMisTheme.WHITE);
        viewButton.setBackground(UniMisTheme.PRIMARY);
        viewButton.setFocusPainted(false);
        viewButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        viewButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(description);
        content.add(Box.createVerticalStrut(16));
        content.add(pendingLabel);
        content.add(Box.createVerticalStrut(18));
        content.add(viewButton);

        add(content, BorderLayout.CENTER);
    }
}
