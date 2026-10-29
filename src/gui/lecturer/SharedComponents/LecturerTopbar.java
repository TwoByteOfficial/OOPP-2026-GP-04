
package gui.lecturer.SharedComponents;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static gui.lecturer.SharedComponents.Colors.TOPBAR_BACKGROUND;
import static gui.lecturer.SharedComponents.Colors.TOPBAR_DEEP_GREEN;
import static gui.lecturer.SharedComponents.Colors.TOPBAR_PALE_GREEN;
import static gui.lecturer.SharedComponents.Colors.TOPBAR_TEXT_GRAY;

public class LecturerTopbar extends JPanel {

    public LecturerTopbar(String lecturerName) {

        setLayout(new BorderLayout(20, 0));
        setBackground(TOPBAR_BACKGROUND);
        setBorder(new EmptyBorder(20, 28, 20, 28));

        JLabel portalLabel = new JLabel("Lecturer Portal");
        portalLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        portalLabel.setForeground(TOPBAR_DEEP_GREEN);

        add(portalLabel, BorderLayout.WEST);
        add(createProfilePanel(lecturerName), BorderLayout.EAST);
    }

    private JPanel createProfilePanel(String lecturerName) {

        JPanel panel = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 16, 0));
        panel.setOpaque(false);

        JButton notificationButton = new JButton("\uD83D\uDD14");
        notificationButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        notificationButton.setToolTipText("Notifications");
        notificationButton.setFocusPainted(false);
        notificationButton.setBorderPainted(false);
        notificationButton.setContentAreaFilled(false);
        notificationButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        notificationButton.addActionListener(e ->
                firePropertyChange("notificationsRequested", false, true));

        String name = (lecturerName == null || lecturerName.isBlank())
                ? "Lecturer"
                : lecturerName;

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        nameLabel.setForeground(TOPBAR_DEEP_GREEN);

        JLabel roleLabel = new JLabel("Lecturer");
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        roleLabel.setForeground(TOPBAR_TEXT_GRAY);

        namePanel.add(nameLabel);
        namePanel.add(Box.createVerticalStrut(4));
        namePanel.add(roleLabel);

        JLabel avatar = new JLabel("\uD83D\uDC64");
        avatar.setOpaque(true);
        avatar.setBackground(TOPBAR_PALE_GREEN);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setFont(new Font("SansSerif", Font.PLAIN, 20));

        panel.add(notificationButton);
        panel.add(namePanel);
        panel.add(avatar);

        return panel;
    }
}
