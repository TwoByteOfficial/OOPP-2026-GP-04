
package gui.admin.DashboardComponents;

import javax.swing.*;
import java.awt.*;

public class NoticesSummary extends JPanel {

    private final Color WHITE = Color.WHITE;
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color DEEP_GREEN = new Color(0x1B, 0x5E, 0x20);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color MUTED = new Color(0x4F, 0x6B, 0x52);

    public NoticesSummary() {
        setLayout(null);
        setBounds(0, 0, 790, 370);
        setOpaque(false);

        JPanel container = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(new Color(0x1B, 0x5E, 0x20, 30));
                g2.drawRoundRect(
                        0, 0, getWidth() - 1, getHeight() - 1, 12, 12
                );

                g2.dispose();
            }
        };

        container.setBounds(0, 0, 790, 370);
        container.setOpaque(false);

        JLabel title = new JLabel("Latest Notices");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(FOREST_GREEN);
        title.setBounds(24, 20, 400, 25);
        container.add(title);

        String[][] noticeItems = {
                {
                        "Semester I Examination Timetable",
                        "The final examination timetable for Semester I - 2026 will be released...",
                        "2026-09-10"
                },
                {
                        "Medical Submission Deadline",
                        "All undergraduates must submit medical certificates within seven (07) working days...",
                        "2026-09-08"
                },
                {
                        "Course Registration Reminder",
                        "All undergraduates should verify their registered course modules before 20th Sep...",
                        "2026-09-05"
                }
        };

        for (int i = 0; i < noticeItems.length; i++) {

            JPanel noticeCard = new JPanel(null);
            noticeCard.setOpaque(false);
            noticeCard.setBounds(24, 65 + (i * 96), 742, 96);

            JLabel itemTitle = new JLabel(noticeItems[i][0]);
            itemTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
            itemTitle.setForeground(DEEP_GREEN);
            itemTitle.setBounds(0, 10, 700, 20);
            noticeCard.add(itemTitle);

            JLabel itemBody = new JLabel(noticeItems[i][1]);
            itemBody.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            itemBody.setForeground(MUTED);
            itemBody.setBounds(0, 34, 740, 20);
            noticeCard.add(itemBody);

            JLabel itemDate = new JLabel(
                    "Posted: " + noticeItems[i][2]
            );
            itemDate.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            itemDate.setForeground(FOREST_GREEN);
            itemDate.setBounds(0, 58, 300, 16);
            noticeCard.add(itemDate);

            if (i < noticeItems.length - 1) {
                JSeparator separator = new JSeparator();
                separator.setForeground(PASTEL_GREEN);
                separator.setBounds(0, 88, 742, 1);
                noticeCard.add(separator);
            }

            container.add(noticeCard);
        }

        add(container);
    }
}
