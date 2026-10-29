
package gui.lecturer.Dashboard.DashboardComponents;
import static gui.lecturer.SharedComponents.Colors.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class StatCard extends JPanel {


    private final JLabel valueLabel;

    public StatCard(String title, String value, String iconText) {

        setLayout(new BorderLayout(12, 12));
        setBackground(WHITE);

        setBorder(new CompoundBorder(
                new LineBorder(BORDER_GREEN, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        // Heading panel
        JPanel headingPanel = new JPanel(new BorderLayout(12, 0));
        headingPanel.setOpaque(false);

        // Custom icon selected according to the card title
        DashboardIcon icon = new DashboardIcon(getIconType(title));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_GRAY);

        headingPanel.add(icon, BorderLayout.WEST);
        headingPanel.add(titleLabel, BorderLayout.CENTER);

        // Card value
        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        valueLabel.setForeground(DEEP_GREEN);

        add(headingPanel, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);

        setPreferredSize(new Dimension(220, 125));
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    private String getIconType(String title) {

        String normalizedTitle = title.toLowerCase();

        if (normalizedTitle.contains("course")) {
            return "courses";
        } else if (normalizedTitle.contains("undergraduate")
                || normalizedTitle.contains("student")) {
            return "students";
        } else if (normalizedTitle.contains("mark")
                || normalizedTitle.contains("pending")) {
            return "marks";
        } else if (normalizedTitle.contains("notice")) {
            return "notices";
        }

        return "default";
    }

    // Custom-drawn dashboard icon
    private static class DashboardIcon extends JComponent {

        private final String type;

        DashboardIcon(String type) {
            this.type = type;
            setPreferredSize(new Dimension(42, 42));
            setMinimumSize(new Dimension(42, 42));
            setMaximumSize(new Dimension(42, 42));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g2 = (Graphics2D) graphics.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            // Pale green rounded background
            g2.setColor(LIGHT_GREEN);
            g2.fill(new RoundRectangle2D.Float(
                    1, 1, width - 2, height - 2, 12, 12
            ));

            g2.setColor(MEDIUM_GREEN);
            g2.setStroke(new BasicStroke(
                    2.0f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            switch (type) {
                case "courses":
                    drawCoursesIcon(g2);
                    break;

                case "students":
                    drawStudentsIcon(g2);
                    break;

                case "marks":
                    drawMarksIcon(g2);
                    break;

                case "notices":
                    drawNoticesIcon(g2);
                    break;

                default:
                    drawDefaultIcon(g2);
                    break;
            }

            g2.dispose();
        }

        // Document with text lines
        private void drawCoursesIcon(Graphics2D g2) {

            g2.drawRoundRect(11, 8, 20, 26, 3, 3);

            g2.drawLine(16, 15, 26, 15);
            g2.drawLine(16, 20, 26, 20);
            g2.drawLine(16, 25, 24, 25);

            // Small folded-corner detail
            g2.drawLine(25, 8, 25, 13);
            g2.drawLine(25, 13, 30, 13);
        }

        // Two student silhouettes
        private void drawStudentsIcon(Graphics2D g2) {

            // Left student
            g2.draw(new Ellipse2D.Float(10, 9, 9, 9));
            g2.drawArc(6, 21, 18, 14, 0, 180);

            // Right student
            g2.draw(new Ellipse2D.Float(23, 11, 8, 8));
            g2.drawArc(20, 23, 16, 12, 0, 180);
        }

        // Checklist with check marks
        private void drawMarksIcon(Graphics2D g2) {

            g2.drawRoundRect(10, 8, 23, 27, 3, 3);

            // First checkbox and tick
            g2.drawRoundRect(14, 13, 5, 5, 1, 1);
            g2.drawLine(14, 15, 16, 17);
            g2.drawLine(16, 17, 20, 12);
            g2.drawLine(22, 15, 29, 15);

            // Second checkbox and tick
            g2.drawRoundRect(14, 22, 5, 5, 1, 1);
            g2.drawLine(14, 24, 16, 26);
            g2.drawLine(16, 26, 20, 21);
            g2.drawLine(22, 24, 29, 24);
        }

        // Notification bell
        private void drawNoticesIcon(Graphics2D g2) {

            // Bell body
            g2.drawArc(12, 10, 18, 22, 0, 180);
            g2.drawLine(12, 21, 12, 27);
            g2.drawLine(30, 21, 30, 27);

            // Bell base
            g2.drawLine(10, 28, 32, 28);

            // Bell clapper
            g2.drawArc(17, 25, 8, 8, 180, 180);

            // Notification dot
            g2.fill(new Ellipse2D.Float(27, 7, 6, 6));
        }

        // Fallback icon
        private void drawDefaultIcon(Graphics2D g2) {

            g2.drawRoundRect(11, 9, 20, 24, 3, 3);
            g2.drawLine(16, 16, 26, 16);
            g2.drawLine(16, 21, 26, 21);
            g2.drawLine(16, 26, 23, 26);
        }
    }
}
