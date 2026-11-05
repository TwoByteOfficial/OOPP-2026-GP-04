package gui.undergraduate.courses.summary;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Summary metrics strip for enrolled courses displaying registered credits,
 * core curriculum, elective tracks, and registration standing.
 * Renders 4 high-level stat cards with vector-drawn custom iconography.
 */
public class CoursesSummaryStripPanel extends JPanel {

    // Labels displaying quantitative values across summary cards
    private final JLabel totalCreditsVal = new JLabel("12 Credits");
    private final JLabel coreCountVal = new JLabel("0 Core");
    private final JLabel electiveCountVal = new JLabel("6 Electives");
    private final JLabel statusVal = new JLabel("100% Valid");

    public CoursesSummaryStripPanel() {
        // Transparent container with a 4-column single-row grid layout
        setLayout(new GridLayout(1, 4, 16, 0));
        setOpaque(false);
        setPreferredSize(new Dimension(0, 72));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        // Construct 4 metric cards: Credits (Icon 1), Core (Icon 2), Electives (Icon 3), Status (Icon 4)
        add(createStatCard(totalCreditsVal, "Registered Total", 1));
        add(createStatCard(coreCountVal, "Mandatory Track", 2));
        add(createStatCard(electiveCountVal, "Elective Track", 3));
        add(createStatCard(statusVal, "Dean Registered", 4));
    }

    /**
     * Builds an individual stat card featuring a custom vector icon and text metrics.
     */
    private JPanel createStatCard(JLabel valueLbl, String sub, int iconType) {
        // Outer rounded card panel with custom anti-aliased border painting
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

        // Custom painted 38x38 square icon box rendering vector glyphs
        JPanel iconBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Rounded colored badge background
                g2.setColor(AdminTheme.PRIMARY);
                g2.fillRoundRect(0, 0, 38, 38, 12, 12);

                // Set white stroke for foreground vector drawing
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int cx = 19;
                int cy = 19;

                // Vector drawings: 1=Book/Credits, 2=Core/Layers, 3=Elective/Target, 4=Checkmark/Valid
                if (iconType == 1) {
                    g2.drawRoundRect(cx - 7, cy - 7, 14, 14, 2, 2);
                    g2.drawLine(cx, cy - 7, cx, cy + 7);
                } else if (iconType == 2) {
                    g2.drawRect(cx - 7, cy - 6, 14, 4);
                    g2.drawRect(cx - 7, cy, 14, 4);
                    g2.drawRect(cx - 7, cy + 6, 14, 4);
                } else if (iconType == 3) {
                    g2.drawOval(cx - 7, cy - 7, 14, 14);
                    g2.drawOval(cx - 3, cy - 3, 6, 6);
                } else {
                    g2.drawLine(cx - 6, cy, cx - 2, cy + 4);
                    g2.drawLine(cx - 2, cy + 4, cx + 6, cy - 4);
                }
                g2.dispose();
            }
        };
        iconBox.setPreferredSize(new Dimension(38, 38));
        iconBox.setMinimumSize(new Dimension(38, 38));
        iconBox.setMaximumSize(new Dimension(38, 38));
        iconBox.setOpaque(false);

        // Center icon vertically inside a wrapper panel
        JPanel iconWrapper = new JPanel(new GridBagLayout());
        iconWrapper.setOpaque(false);
        iconWrapper.add(iconBox);
        card.add(iconWrapper, BorderLayout.WEST);

        // Value and subtitle text container
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
     * Updates label counts dynamically based on fetched database records.
     */
    public void updateMetrics(int totalCredits, int coreCount, int electiveCount) {
        totalCreditsVal.setText(totalCredits + " Credits");
        coreCountVal.setText(coreCount + " Core");
        electiveCountVal.setText(electiveCount + " Electives");
    }
}