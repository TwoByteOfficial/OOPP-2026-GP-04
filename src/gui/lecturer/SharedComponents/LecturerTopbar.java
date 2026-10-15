package gui.lecturer.SharedComponents;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class LecturerTopbar extends JPanel {

    private final Color deepGreen = new Color(27, 94, 32);
    private final Color forestGreen = new Color(56, 142, 60);
    private final Color pastelGreen = new Color(200, 230, 201);
    private final Color mutedColor = new Color(95, 110, 98);

    private JLabel pageTitleLabel;
    private JLabel pageSubtitleLabel;

    public LecturerTopbar(String fullName, String role) {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        setPreferredSize(new Dimension(0, 90));

        setBorder(
                new MatteBorder(
                        0, 0, 1, 0, pastelGreen
                )
        );

        // Left side: page title and subtitle
        JPanel titlePanel = new JPanel();

        titlePanel.setLayout(
                new BoxLayout(titlePanel, BoxLayout.Y_AXIS)
        );

        titlePanel.setOpaque(false);
        titlePanel.setBorder(
                new EmptyBorder(15, 30, 15, 10)
        );

        pageTitleLabel = new JLabel("Dashboard");

        pageTitleLabel.setFont(
                new Font("SansSerif", Font.BOLD, 25)
        );

        pageTitleLabel.setForeground(deepGreen);

        pageSubtitleLabel = new JLabel(
                "Overview of your teaching activities"
        );

        pageSubtitleLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        pageSubtitleLabel.setForeground(mutedColor);

        titlePanel.add(pageTitleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(pageSubtitleLabel);

        add(titlePanel, BorderLayout.WEST);

        // Right side: logged-in lecturer
        JPanel userPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 0)
        );

        userPanel.setBackground(Color.WHITE);
        userPanel.setBorder(
                new EmptyBorder(18, 10, 18, 25)
        );

        JLabel avatar = new JLabel(
                getInitials(fullName),
                SwingConstants.CENTER
        );

        avatar.setFont(
                new Font("SansSerif", Font.BOLD, 15)
        );

        avatar.setForeground(Color.WHITE);
        avatar.setBackground(forestGreen);
        avatar.setOpaque(true);
        avatar.setPreferredSize(new Dimension(44, 44));

        JPanel userDetails = new JPanel();

        userDetails.setLayout(
                new BoxLayout(userDetails, BoxLayout.Y_AXIS)
        );

        userDetails.setOpaque(false);

        JLabel nameLabel = new JLabel(fullName);

        nameLabel.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );

        nameLabel.setForeground(deepGreen);

        String displayRole = "Lecturer";

        if (role != null && !role.isBlank()) {
            displayRole = role.substring(0, 1).toUpperCase()
                    + role.substring(1);
        }

        JLabel roleLabel = new JLabel(displayRole);

        roleLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );

        roleLabel.setForeground(mutedColor);

        userDetails.add(nameLabel);
        userDetails.add(Box.createVerticalStrut(4));
        userDetails.add(roleLabel);

        userPanel.add(avatar);
        userPanel.add(userDetails);

        add(userPanel, BorderLayout.EAST);
    }

    public void setPageInfo(String title, String subtitle) {
        pageTitleLabel.setText(title);
        pageSubtitleLabel.setText(subtitle);
    }

    private String getInitials(String fullName) {

        if (fullName == null || fullName.isBlank()) {
            return "L";
        }

        String[] names = fullName.trim().split("\\s+");

        if (names.length == 1) {
            return names[0].substring(0, 1).toUpperCase();
        }

        return (
                names[0].substring(0, 1)
                        + names[names.length - 1].substring(0, 1)
        ).toUpperCase();
    }
}
