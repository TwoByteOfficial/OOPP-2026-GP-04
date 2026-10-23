package gui.undergraduate.profile.info;

import gui.admin.AdminTheme;
import gui.undergraduate.profile.model.UserProfileData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Visual identification card displaying student initials avatar, full name, and reg number.
 */
public class ProfileAvatarCard extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);
    private static final int AVATAR_SIZE = 54;

    private final JLabel nameLabel;
    private final JLabel regNoLabel;
    private final JLabel statusBadge;
    private final JLabel initialsLabel;

    public ProfileAvatarCard() {
        setOpaque(false);
        setLayout(new BorderLayout(16, 0));
        setBorder(new EmptyBorder(16, 18, 16, 18));
        setPreferredSize(new Dimension(0, 95));

        // Perfectly circular avatar container
        JPanel avatarCircle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AdminTheme.FOREST_GREEN);

                int diameter = Math.min(getWidth(), getHeight());
                int x = (getWidth() - diameter) / 2;
                int y = (getHeight() - diameter) / 2;
                g2.fillOval(x, y, diameter, diameter);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(AVATAR_SIZE, AVATAR_SIZE);
            }
        };
        avatarCircle.setOpaque(false);

        initialsLabel = new JLabel("US");
        initialsLabel.setFont(AdminTheme.font(Font.BOLD, 17));
        initialsLabel.setForeground(AdminTheme.WHITE);
        avatarCircle.add(initialsLabel);

        // GridBag container to preserve 1:1 aspect ratio
        JPanel avatarWrapper = new JPanel(new GridBagLayout());
        avatarWrapper.setOpaque(false);
        avatarWrapper.add(avatarCircle);

        // Text identity block
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        nameLabel = new JLabel("PIUMI SENANAYAKE");
        nameLabel.setFont(AdminTheme.font(Font.BOLD, 16));
        nameLabel.setForeground(AdminTheme.DEEP_GREEN);

        regNoLabel = new JLabel("TG/2022/1074  •  Undergraduate Candidate");
        regNoLabel.setFont(AdminTheme.font(Font.PLAIN, 12));
        regNoLabel.setForeground(AdminTheme.MUTED);

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(regNoLabel);
        textPanel.add(Box.createVerticalGlue());

        // Status badge
        statusBadge = new JLabel("Active Enrolment");
        statusBadge.setFont(AdminTheme.font(Font.BOLD, 11));
        statusBadge.setForeground(AdminTheme.FOREST_GREEN);
        statusBadge.setOpaque(true);
        statusBadge.setBackground(AdminTheme.MINT);
        statusBadge.setBorder(new EmptyBorder(4, 10, 4, 10));

        JPanel badgeWrapper = new JPanel(new GridBagLayout());
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(statusBadge);

        add(avatarWrapper, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
        add(badgeWrapper, BorderLayout.EAST);
    }

    public void updateProfile(UserProfileData data) {
        if (data == null) return;
        nameLabel.setText(data.getFullName());
        regNoLabel.setText(data.getStudentRegNo() + "  •  Undergraduate Candidate");
        statusBadge.setText(data.getAcademicStatus());

        String rawName = data.getFullName().replaceAll("[^a-zA-Z\\s]", "").trim();
        String[] parts = rawName.split("\\s+");
        if (parts.length >= 2) {
            initialsLabel.setText("" + parts[0].charAt(0) + parts[1].charAt(0));
        } else if (parts.length == 1 && parts[0].length() >= 2) {
            initialsLabel.setText(parts[0].substring(0, 2).toUpperCase());
        } else {
            initialsLabel.setText("US");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(AdminTheme.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.setColor(CARD_BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.dispose();
        super.paintComponent(g);
    }
}