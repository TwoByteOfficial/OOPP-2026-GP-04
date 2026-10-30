
package gui.lecturer.SharedComponents;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

public class LecturerTopbar extends JPanel {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);
    private static final Color PALE_GREEN =
            new Color(232, 245, 233);
    private static final Color TEXT_GRAY =
            new Color(79, 107, 82);
    private static final Color ICON_GREEN =
            new Color(56, 142, 60);
    private static final Color BORDER_GREEN =
            new Color(200, 230, 201);
    private static final Color NOTIFICATION_RED =
            new Color(220, 53, 69);

    private JButton notificationButton;
    private JPanel notificationBadge;

    public LecturerTopbar(String lecturerName) {
        setLayout(new BorderLayout(20, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0, BORDER_GREEN
                ),
                new EmptyBorder(18, 28, 18, 28)
        ));

        JLabel portalLabel = new JLabel("Lecturer Portal");
        portalLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        portalLabel.setForeground(DEEP_GREEN);

        add(portalLabel, BorderLayout.WEST);
        add(createProfilePanel(lecturerName), BorderLayout.EAST);
    }

    private JPanel createProfilePanel(String lecturerName) {
        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 16, 0)
        );
        panel.setOpaque(false);

        // Notification bell and red badge
        JPanel notificationPanel = new JPanel(null);
        notificationPanel.setOpaque(false);
        notificationPanel.setPreferredSize(new Dimension(40, 42));

        notificationButton = new JButton();
        notificationButton.setIcon(new BellIcon());
        notificationButton.setToolTipText("Notifications");
        notificationButton.setBounds(0, 2, 38, 38);
        notificationButton.setBorderPainted(false);
        notificationButton.setFocusPainted(false);
        notificationButton.setContentAreaFilled(false);
        notificationButton.setOpaque(false);
        notificationButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        notificationButton.addActionListener(e ->
                firePropertyChange(
                        "notificationsRequested", false, true
                )
        );
        notificationPanel.add(notificationButton);

        notificationBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2.setColor(NOTIFICATION_RED);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        notificationBadge.setOpaque(false);
        notificationBadge.setBounds(27, 1, 11, 11);
        notificationBadge.setVisible(false);
        notificationPanel.add(notificationBadge);
        notificationPanel.setComponentZOrder(notificationBadge, 0);

        // Lecturer name and role
        String name = lecturerName == null || lecturerName.isBlank()
                ? "Lecturer"
                : lecturerName.trim();

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(DEEP_GREEN);

        JLabel roleLabel = new JLabel("Lecturer");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setForeground(TEXT_GRAY);

        namePanel.add(nameLabel);
        namePanel.add(Box.createVerticalStrut(4));
        namePanel.add(roleLabel);

        // Drawn avatar with initials instead of an emoji
        JLabel avatar = new AvatarLabel(getInitials(name));
        avatar.setPreferredSize(new Dimension(42, 42));

        panel.add(notificationPanel);
        panel.add(namePanel);
        panel.add(avatar);

        return panel;
    }

    private String getInitials(String name) {
        if (name == null || name.isBlank()) {
            return "L";
        }

        String[] parts = name.trim().split("\\s+");

        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }

        return (
                parts[0].substring(0, 1)
                        + parts[parts.length - 1].substring(0, 1)
        ).toUpperCase();
    }

    public void setHasNewNotifications(boolean hasNew) {
        notificationBadge.setVisible(hasNew);
        notificationButton.setToolTipText(
                hasNew
                        ? "You have new notifications"
                        : "Notifications"
        );

        notificationBadge.repaint();
        notificationBadge.getParent().revalidate();
        notificationBadge.getParent().repaint();
    }

    private static class AvatarLabel extends JLabel {
        AvatarLabel(String initials) {
            super(initials, SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setForeground(DEEP_GREEN);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(PALE_GREEN);
            g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.setColor(BORDER_GREEN);
            g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class BellIcon implements Icon {
        @Override
        public int getIconWidth() {
            return 26;
        }

        @Override
        public int getIconHeight() {
            return 28;
        }

        @Override
        public void paintIcon(
                Component component,
                Graphics graphics,
                int x,
                int y
        ) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.translate(x, y);
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(ICON_GREEN);
            g2.setStroke(new BasicStroke(
                    2.0f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            Path2D bell = new Path2D.Float();
            bell.moveTo(5, 18);
            bell.lineTo(7, 15);
            bell.lineTo(7, 10);
            bell.curveTo(7, 2, 19, 2, 19, 10);
            bell.lineTo(19, 15);
            bell.lineTo(21, 18);

            g2.draw(bell);
            g2.drawLine(5, 18, 21, 18);
            g2.draw(new Ellipse2D.Double(11, 20, 4, 4));

            g2.dispose();
        }
    }
}
