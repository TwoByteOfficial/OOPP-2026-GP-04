package gui.undergraduate.enrollment.summary;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Summary metrics strip for curriculum enrollment with 38x38 squircle icon containers.
 * Renders a horizontal 4-card overview displaying curriculum credits, active enrollments,
 * pending review counts, and modules available for registration.
 */
public class EnrollmentSummaryStripPanel extends JPanel {

    // Metric display labels
    private final JLabel totalVal = new JLabel("18 Credits");
    private final JLabel enrolledVal = new JLabel("0 Enrolled");
    private final JLabel pendingVal = new JLabel("0 Pending");
    private final JLabel availableVal = new JLabel("0 Available");

    public EnrollmentSummaryStripPanel() {
        // Transparent container with a 4-column single-row grid layout (16px horizontal gap)
        setLayout(new GridLayout(1, 4, 16, 0));
        setOpaque(false);

        // Add 4 stat cards with corresponding vector icon badges (1: Book, 2: Check, 3: Clock, 4: Plus)
        add(createStatCard(totalVal, "Department Curriculum", 1));
        add(createStatCard(enrolledVal, "Approved & Active", 2));
        add(createStatCard(pendingVal, "Awaiting Admin Review", 3));
        add(createStatCard(availableVal, "Open for Enrollment", 4));
    }

    /**
     * Factory method creating a rounded metric card with a 38x38 custom vector icon squircle.
     */
    private JPanel createStatCard(JLabel valueLbl, String sub, int iconType) {
        // -------------------------------------------------------------
        // Rounded White Card Background with Anti-Aliased Border
        // -------------------------------------------------------------
        JPanel card = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw rounded white card background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Draw subtle card border line
                g2.setColor(new Color(225, 235, 228));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(0, 72));
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        // -------------------------------------------------------------
        // 38x38 Squircle Icon Container with Custom Vector Graphics
        // -------------------------------------------------------------
        JPanel iconBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded theme-colored squircle container
                g2.setColor(AdminTheme.PRIMARY);
                g2.fillRoundRect(0, 0, 38, 38, 12, 12);

                // Configure vector stroke styling for white foreground glyphs
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int cx = 19;
                int cy = 19;

                // Draw specific vector iconography based on iconType identifier
                if (iconType == 1) {
                    // Curriculum / Book icon (split rectangle)
                    g2.drawRoundRect(cx - 7, cy - 7, 14, 14, 2, 2);
                    g2.drawLine(cx, cy - 7, cx, cy + 7);
                } else if (iconType == 2) {
                    // Active / Approved checkmark icon
                    g2.drawLine(cx - 6, cy, cx - 2, cy + 5);
                    g2.drawLine(cx - 2, cy + 5, cx + 6, cy - 4);
                } else if (iconType == 3) {
                    // Pending clock icon (circle with hour and minute hands)
                    g2.drawOval(cx - 8, cy - 8, 16, 16);
                    g2.drawLine(cx, cy - 4, cx, cy);
                    g2.drawLine(cx, cy, cx + 4, cy + 1);
                } else {
                    // Available / Plus addition icon
                    g2.drawLine(cx - 6, cy, cx + 6, cy);
                    g2.drawLine(cx, cy - 6, cx, cy + 6);
                }
                g2.dispose();
            }
        };
        iconBox.setPreferredSize(new Dimension(38, 38));
        iconBox.setMinimumSize(new Dimension(38, 38));
        iconBox.setMaximumSize(new Dimension(38, 38));
        iconBox.setOpaque(false);

        // Center icon vertically inside a GridBagLayout wrapper
        JPanel iconWrapper = new JPanel(new GridBagLayout());
        iconWrapper.setOpaque(false);
        iconWrapper.add(iconBox);
        card.add(iconWrapper, BorderLayout.WEST);

        // -------------------------------------------------------------
        // Right Text Panel (Metric Value & Subtitle Description)
        // -------------------------------------------------------------
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        valueLbl.setFont(AdminTheme.font(Font.BOLD, 15));
        valueLbl.setForeground(new Color(30, 41, 59));

        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(AdminTheme.font(Font.PLAIN, 11));
        subLbl.setForeground(new Color(100, 116, 139));

        textPanel.add(valueLbl);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(subLbl);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Updates label values across all four metric cards with the latest database counts.
     */
    public void updateStats(int totalCredits, int enrolled, int pending, int available) {
        totalVal.setText(totalCredits + " Credits");
        enrolledVal.setText(enrolled + " Enrolled");
        pendingVal.setText(pending + " Pending");
        availableVal.setText(available + " Available");
    }
}