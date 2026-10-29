
package gui.lecturer.SharedComponents;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

public class LecturerTopbar extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color PALE_GREEN = new Color(232, 245, 233);
    private static final Color TEXT_GRAY = new Color(96, 112, 100);
    private static final Color ICON_COLOR = new Color(56, 142, 60);
    private static final Color NOTIFICATION_RED = new Color(220, 38, 38);

    private JButton notificationButton;
    private JPanel notificationBadge;

    public LecturerTopbar(String lecturerName) {

        setLayout(new BorderLayout(20, 0));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 28, 20, 28));

        JLabel portalLabel = new JLabel("Lecturer Portal");
        portalLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        portalLabel.setForeground(DEEP_GREEN);

        add(portalLabel, BorderLayout.WEST);
        add(createProfilePanel(lecturerName), BorderLayout.EAST);
    }

    private JPanel createProfilePanel(String lecturerName) {

        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 16, 0)
        );
        panel.setOpaque(false);

        // Container for the custom-drawn bell and red badge
        JPanel notificationPanel = new JPanel(null);
        notificationPanel.setOpaque(false);
        notificationPanel.setPreferredSize(new Dimension(36, 40));

        // Use a custom Java-drawn bell instead of an emoji
        notificationButton = new JButton();
        notificationButton.setIcon(new BellIcon());
        notificationButton.setToolTipText("Notifications");
        notificationButton.setBounds(0, 2, 36, 36);
        notificationButton.setBorderPainted(false);
        notificationButton.setFocusPainted(false);
        notificationButton.setContentAreaFilled(false);
        notificationButton.setOpaque(false);
        notificationButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        notificationPanel.add(notificationButton);

        // Red circular notification badge
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
        notificationBadge.setBounds(25, 1, 11, 11);
        notificationBadge.setVisible(false);

        notificationPanel.add(notificationBadge);

        // Keep the red badge in front of the bell
        notificationPanel.setComponentZOrder(notificationBadge, 0);

        // Preserve existing navigation behavior
        notificationButton.addActionListener(e ->
                firePropertyChange(
                        "notificationsRequested",
                        false,
                        true
                )
        );

        String name =
                (lecturerName == null || lecturerName.isBlank())
                        ? "Lecturer"
                        : lecturerName;

        JPanel namePanel = new JPanel();
        namePanel.setLayout(
                new BoxLayout(namePanel, BoxLayout.Y_AXIS)
        );
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        nameLabel.setForeground(DEEP_GREEN);

        JLabel roleLabel = new JLabel("Lecturer");
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        roleLabel.setForeground(TEXT_GRAY);

        namePanel.add(nameLabel);
        namePanel.add(Box.createVerticalStrut(4));
        namePanel.add(roleLabel);

        JLabel avatar = new JLabel("\uD83D\uDC64");
        avatar.setOpaque(true);
        avatar.setBackground(PALE_GREEN);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setFont(new Font("SansSerif", Font.PLAIN, 20));

        panel.add(notificationPanel);
        panel.add(namePanel);
        panel.add(avatar);

        return panel;
    }

    // Call this method to show or hide the red notification dot
    public void setHasNewNotifications(boolean hasNew) {

        notificationBadge.setVisible(hasNew);

        notificationButton.setToolTipText(
                hasNew
                        ? "You have new notifications"
                        : "Notifications"
        );

        notificationPanelRefresh();
    }

    private void notificationPanelRefresh() {
        notificationBadge.repaint();
        notificationBadge.getParent().revalidate();
        notificationBadge.getParent().repaint();
    }

    // Draw the bell using Java graphics, without an emoji font
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

            g2.setColor(ICON_COLOR);
            g2.setStroke(new BasicStroke(
                    2.0f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            // Bell dome
            Path2D bell = new Path2D.Float();
            bell.moveTo(5, 18);
            bell.lineTo(7, 15);
            bell.lineTo(7, 10);
            bell.curveTo(7, 2, 19, 2, 19, 10);
            bell.lineTo(19, 15);
            bell.lineTo(21, 18);
            g2.draw(bell);

            // Bell base
            g2.drawLine(5, 18, 21, 18);

            // Bell clapper
            g2.draw(new Ellipse2D.Double(11, 20, 4, 4));

            g2.dispose();
        }
    }
}
