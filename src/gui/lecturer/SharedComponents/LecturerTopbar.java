
package gui.lecturer.SharedComponents;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;

public class LecturerTopbar extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color PALE_GREEN = new Color(232, 245, 233);
    private static final Color TEXT_GRAY = new Color(79, 107, 82);
    private static final Color ICON_GREEN = new Color(56, 142, 60);
    private static final Color BORDER_GREEN = new Color(200, 230, 201);
    private static final Color NOTIFICATION_RED = new Color(220, 53, 69);

    private JButton notificationButton;
    private JPanel notificationBadge;
    private AvatarLabel avatar;

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

        // Notification bell
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

        // Lecturer name
        String name = lecturerName == null || lecturerName.isBlank()
                ? "Lecturer"
                : lecturerName.trim();

        JPanel namePanel = new JPanel();
        namePanel.setLayout(
                new BoxLayout(namePanel, BoxLayout.Y_AXIS)
        );
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

        // Larger circular profile picture
        avatar = new AvatarLabel(getInitials(name));
        avatar.setPreferredSize(new Dimension(64, 64));
        avatar.setToolTipText("My Profile");

        avatar.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        avatar.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                avatar.setHovered(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                avatar.setHovered(false);
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    firePropertyChange(
                            "profileRequested", false, true
                    );
                }
            }
        });

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

    /**
     * Load lecturer profile picture from URL.
     * Invalid URLs automatically show initials.
     */
    public void setProfilePictureUrl(String imageUrl) {

        if (avatar == null) {
            return;
        }

        avatar.setImage(null);

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return;
        }

        String urlText = imageUrl.trim();

        if (!urlText.matches("(?i)^https?://.+")) {
            return;
        }

        avatar.setLoading(true);

        new SwingWorker<BufferedImage, Void>() {

            @Override
            protected BufferedImage doInBackground() throws Exception {

                URL url = new URL(urlText);
                URLConnection connection = url.openConnection();

                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty(
                        "User-Agent", "UniMIS"
                );

                try (java.io.InputStream input =
                             connection.getInputStream()) {

                    return ImageIO.read(input);
                }
            }

            @Override
            protected void done() {

                try {
                    BufferedImage image = get();

                    if (image != null) {
                        avatar.setImage(image);
                    } else {
                        avatar.setImage(null);
                    }

                } catch (Exception e) {
                    avatar.setImage(null);
                } finally {
                    avatar.setLoading(false);
                }
            }

        }.execute();
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

    // Circular avatar component
    private static class AvatarLabel extends JLabel {

        private BufferedImage image;
        private boolean hovered;
        private boolean loading;

        AvatarLabel(String initials) {
            super(initials, SwingConstants.CENTER);

            setFont(new Font("Segoe UI", Font.BOLD, 20));
            setForeground(DEEP_GREEN);
            setOpaque(false);
        }

        void setImage(BufferedImage image) {
            this.image = image;
            repaint();
        }

        void setHovered(boolean hovered) {
            this.hovered = hovered;
            repaint();
        }

        void setLoading(boolean loading) {
            this.loading = loading;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            Ellipse2D circle = new Ellipse2D.Double(
                    x, y, size - 1, size - 1
            );

            if (image != null) {

                g2.setClip(circle);

                double scale = Math.max(
                        (double) size / image.getWidth(),
                        (double) size / image.getHeight()
                );

                int width = (int) (image.getWidth() * scale);
                int height = (int) (image.getHeight() * scale);

                int drawX = x + (size - width) / 2;
                int drawY = y + (size - height) / 2;

                g2.drawImage(
                        image, drawX, drawY, width, height, null
                );

                g2.setClip(null);

            } else {

                g2.setColor(PALE_GREEN);
                g2.fill(circle);

                if (!loading) {
                    FontMetrics fm = g2.getFontMetrics();
                    String initials = getText();

                    int textX = x + (size - fm.stringWidth(initials)) / 2;
                    int textY = y + (size - fm.getHeight()) / 2
                            + fm.getAscent();

                    g2.setColor(DEEP_GREEN);
                    g2.drawString(initials, textX, textY);
                }
            }

            g2.setStroke(new BasicStroke(hovered ? 3f : 1.5f));
            g2.setColor(hovered ? DEEP_GREEN : BORDER_GREEN);
            g2.draw(circle);

            if (hovered) {
                g2.setColor(new Color(27, 94, 32, 25));
                g2.fill(circle);
                g2.setColor(DEEP_GREEN);
                g2.setStroke(new BasicStroke(3f));
                g2.draw(circle);
            }

            g2.dispose();
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
