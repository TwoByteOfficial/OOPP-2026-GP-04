package gui.undergraduate.dashboard.header;

import gui.admin.AdminTheme;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Top-level header component for Undergraduate Dashboard displaying student full name and TG number only.
 */
public class DashboardHeaderPanel extends JPanel {

    private final JLabel studentNameLabel;
    private final JLabel studentIdentifierLabel;
    private final HeaderAvatarCircle avatarCircle;

    public DashboardHeaderPanel() {
        this("tg2063", "STUDENT NAME", "Undergraduate", null);
    }

    public DashboardHeaderPanel(String username, String fullName, String role) {
        this(username, fullName, role, null);
    }

    public DashboardHeaderPanel(String username, String fullName, String role, String pictureUrl) {
        setOpaque(false);
        setLayout(new BorderLayout(16, 0));
        setBorder(new EmptyBorder(0, 0, 16, 0));

        // 1. Left Welcome Titles
        JPanel leftTitles = new JPanel(new GridLayout(2, 1, 0, 2));
        leftTitles.setOpaque(false);

        JLabel titleLabel = new JLabel("Undergraduate Portal");
        titleLabel.setFont(AdminTheme.font(Font.BOLD, 22));
        titleLabel.setForeground(AdminTheme.DEEP_GREEN);

        JLabel subtitleLabel = new JLabel("Academic performance, course attendance and notices");
        subtitleLabel.setFont(AdminTheme.font(Font.PLAIN, 13));
        subtitleLabel.setForeground(AdminTheme.MUTED);

        leftTitles.add(titleLabel);
        leftTitles.add(subtitleLabel);
        add(leftTitles, BorderLayout.WEST);

        // 2. Right Student Identifier Panel
        JPanel rightUserPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        rightUserPanel.setOpaque(false);

        JPanel userTextPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        userTextPanel.setOpaque(false);

        String fName = (fullName != null && !fullName.trim().isEmpty()) ? fullName.toUpperCase() : "STUDENT NAME";
        String uName = (username != null && !username.trim().isEmpty()) ? username.toLowerCase() : "tg";

        // Top line: Student Full Name (Bold Deep Green)
        studentNameLabel = new JLabel(fName, SwingConstants.RIGHT);
        studentNameLabel.setFont(AdminTheme.font(Font.BOLD, 14));
        studentNameLabel.setForeground(AdminTheme.DEEP_GREEN);

        // Bottom line: TG Username only (Plain Muted)
        studentIdentifierLabel = new JLabel(uName, SwingConstants.RIGHT);
        studentIdentifierLabel.setFont(AdminTheme.font(Font.PLAIN, 12));
        studentIdentifierLabel.setForeground(AdminTheme.MUTED);

        userTextPanel.add(studentNameLabel);
        userTextPanel.add(studentIdentifierLabel);

        avatarCircle = new HeaderAvatarCircle();
        avatarCircle.setAvatarData(uName, pictureUrl);

        rightUserPanel.add(userTextPanel);
        rightUserPanel.add(avatarCircle);

        add(rightUserPanel, BorderLayout.EAST);
    }

    /**
     * Dynamically updates student full name on top and only TG registration number below.
     */
    public void setStudentInfo(String username, String fullName, String role, String pictureUrl) {
        if (fullName != null && !fullName.trim().isEmpty()) {
            studentNameLabel.setText(fullName.toUpperCase());
        }
        String uName = (username != null && !username.trim().isEmpty()) ? username.toLowerCase() : "tg";
        studentIdentifierLabel.setText(uName);

        avatarCircle.setAvatarData(username != null ? username : "TG", pictureUrl);
    }

    /**
     * Circular avatar rendering component using memory streaming without local file storage.
     */
    private static class HeaderAvatarCircle extends JComponent {
        private String initials = "TG";
        private BufferedImage loadedImage = null;

        public HeaderAvatarCircle() {
            setPreferredSize(new Dimension(42, 42));
        }

        public void setAvatarData(String identifier, String imageUrl) {
            if (identifier != null && identifier.length() >= 2) {
                this.initials = identifier.substring(0, 2).toUpperCase();
            }
            loadImageFromUrl(imageUrl);
        }

        public void loadImageFromUrl(String rawUrl) {
            if (rawUrl == null || rawUrl.trim().isEmpty() || !rawUrl.startsWith("http")) {
                this.loadedImage = null;
                repaint();
                return;
            }

            final String processedUrl = convertToDirectImageUrl(rawUrl.trim());

            SwingWorker<BufferedImage, Void> worker = new SwingWorker<>() {
                @Override
                protected BufferedImage doInBackground() {
                    try {
                        URL url = new URL(processedUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(6000);
                        conn.setReadTimeout(6000);
                        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
                        conn.setInstanceFollowRedirects(true);
                        return ImageIO.read(conn.getInputStream());
                    } catch (Exception e) {
                        return null;
                    }
                }

                @Override
                protected void done() {
                    try {
                        loadedImage = get();
                        repaint();
                    } catch (Exception ignored) {}
                }
            };
            worker.execute();
        }

        private String convertToDirectImageUrl(String url) {
            if (url.contains("drive.google.com")) {
                Pattern pattern = Pattern.compile("/file/d/([a-zA-Z0-9_-]+)|id=([a-zA-Z0-9_-]+)");
                Matcher matcher = pattern.matcher(url);
                if (matcher.find()) {
                    String fileId = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
                    return "https://drive.google.com/thumbnail?id=" + fileId + "&sz=w500";
                }
            }
            return url;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int size = Math.min(getWidth(), getHeight());
            Shape circle = new Ellipse2D.Float(0, 0, size, size);

            if (loadedImage != null) {
                g2.setClip(circle);
                g2.drawImage(loadedImage, 0, 0, size, size, null);
            } else {
                g2.setColor(AdminTheme.FOREST_GREEN);
                g2.fill(circle);
                g2.setColor(Color.WHITE);
                g2.setFont(AdminTheme.font(Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (size - fm.stringWidth(initials)) / 2;
                int ty = ((size - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, tx, ty);
            }
            g2.dispose();
        }
    }
}