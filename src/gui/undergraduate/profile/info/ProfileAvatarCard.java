package gui.undergraduate.profile.info;

import gui.admin.AdminTheme;
import gui.undergraduate.profile.model.UserProfileData;

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
 * Visual profile banner card displaying the student's circular avatar, full name,
 * registration details, and academic standing status badge.
 * Supports asynchronous remote image downloading from standard URLs or Google Drive share links,
 * providing graceful initial fallback rendering when no avatar image is present.
 */
public class ProfileAvatarCard extends JPanel {

    // Card styling constants for rounded geometry, border strokes, and status badge color accents
    private static final int CORNER_RADIUS = 16;
    private static final Color BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);
    private static final Color BADGE_BG = new Color(0xEC, 0xFD, 0xF5);
    private static final Color BADGE_TEXT = new Color(0x04, 0x78, 0x57);

    // Sub-components for avatar graphics, textual identifiers, and standing indicator
    private final AvatarCircle avatarCircle;
    private final JLabel nameLabel;
    private final JLabel subLabel;
    private final JLabel statusBadge;

    /**
     * Constructs the profile avatar card, initializes circular graphics, and arranges layout regions.
     */
    public ProfileAvatarCard() {
        // Base container layout and margin configuration
        setOpaque(false);
        setLayout(new BorderLayout(14, 0));
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // -------------------------------------------------------------
        // West Region: Circular Avatar Component
        // -------------------------------------------------------------
        avatarCircle = new AvatarCircle();
        add(avatarCircle, BorderLayout.WEST);

        // -------------------------------------------------------------
        // Center Region: Student Name and Academic Registration Subtitle
        // -------------------------------------------------------------
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        nameLabel = new JLabel("STUDENT NAME");
        nameLabel.setFont(AdminTheme.font(Font.BOLD, 16));
        nameLabel.setForeground(AdminTheme.DEEP_GREEN);

        subLabel = new JLabel("REG/NO • Status");
        subLabel.setFont(AdminTheme.font(Font.PLAIN, 12));
        subLabel.setForeground(AdminTheme.MUTED);

        textPanel.add(nameLabel);
        textPanel.add(subLabel);
        add(textPanel, BorderLayout.CENTER);

        // -------------------------------------------------------------
        // East Region: Academic Standing Pill Badge
        // -------------------------------------------------------------
        statusBadge = new JLabel("Proper Candidate", SwingConstants.CENTER);
        statusBadge.setFont(AdminTheme.font(Font.BOLD, 11));
        statusBadge.setForeground(BADGE_TEXT);
        statusBadge.setBackground(BADGE_BG);
        statusBadge.setOpaque(true);
        statusBadge.setBorder(new EmptyBorder(6, 12, 6, 12));

        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(statusBadge);
        add(badgeWrapper, BorderLayout.EAST);
    }

    /**
     * Binds student profile data model properties to the card's labels and avatar renderer.
     *
     * @param data User profile entity holding registration details and avatar URL
     */
    public void updateProfile(UserProfileData data) {
        if (data == null) return;
        nameLabel.setText(data.getFullName());
        subLabel.setText(data.getStudentRegNo() + "  •  " + data.getAcademicStatus());
        statusBadge.setText(data.getAcademicStatus());
        avatarCircle.setAvatar(data.getFullName(), data.getProfilePicture());
    }

    /**
     * Triggers dynamic avatar updating for real-time live preview during URL editing.
     *
     * @param imageUrl Web URL or cloud thumbnail link to render
     */
    public void setLiveImagePreview(String imageUrl) {
        avatarCircle.loadImageFromUrl(imageUrl);
    }

    /**
     * Paints an anti-aliased rounded white card background and outer perimeter border.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(AdminTheme.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.setColor(BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * Custom lightweight component rendering an elliptical clipped profile avatar or initial text fallback.
     */
    private static class AvatarCircle extends JComponent {
        private String initials = "UG";
        private BufferedImage loadedImage = null;

        /**
         * Initializes fixed avatar component dimensions.
         */
        public AvatarCircle() {
            setPreferredSize(new Dimension(54, 54));
        }

        /**
         * Computes user initials and initiates background image retrieval.
         *
         * @param name     Full student name for monogram extraction
         * @param imageUrl Remote picture link
         */
        public void setAvatar(String name, String imageUrl) {
            this.initials = extractInitials(name);
            loadImageFromUrl(imageUrl);
        }

        /**
         * Asynchronously downloads and parses the image bitmap on a background worker thread.
         *
         * @param rawUrl Unparsed image URL
         */
        public void loadImageFromUrl(String rawUrl) {
            if (rawUrl == null || rawUrl.trim().isEmpty() || !rawUrl.startsWith("http")) {
                this.loadedImage = null;
                repaint();
                return;
            }

            final String processedUrl = convertToDirectImageUrl(rawUrl.trim());

            // Asynchronous fetch to keep UI responsive
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

        /**
         * Converts standard Google Drive view/preview URLs into direct thumbnail image endpoints.
         *
         * @param url Original web address
         * @return Direct image stream URL or original string
         */
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

        /**
         * Extracts a two-letter uppercase monogram from a person's full name.
         *
         * @param name Student full name
         * @return Two-character uppercase initial string
         */
        private String extractInitials(String name) {
            if (name == null || name.trim().isEmpty()) return "UG";
            String[] parts = name.trim().split("\\s+");
            if (parts.length >= 2) {
                return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
            }
            return name.substring(0, Math.min(2, name.length())).toUpperCase();
        }

        /**
         * Renders the circular clipped image bitmap or the centered initial monogram fallback.
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int size = Math.min(getWidth(), getHeight());
            Shape circle = new Ellipse2D.Float(0, 0, size, size);

            if (loadedImage != null) {
                // Clip rendering surface to circular geometry and draw bitmap
                g2.setClip(circle);
                g2.drawImage(loadedImage, 0, 0, size, size, null);
            } else {
                // Fallback: draw circular forest green background and centered monogram text
                g2.setColor(AdminTheme.FOREST_GREEN);
                g2.fill(circle);
                g2.setColor(Color.WHITE);
                g2.setFont(AdminTheme.font(Font.BOLD, 17));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (size - fm.stringWidth(initials)) / 2;
                int ty = ((size - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, tx, ty);
            }
            g2.dispose();
        }
    }
}