
package gui.lecturer.Dashboard.DashboardComponents;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class RecentNotices extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color BORDER_GREEN = new Color(200, 230, 201);
    private static final Color TEXT_GRAY = new Color(96, 112, 100);

    private final JPanel noticesList;

    public RecentNotices() {

        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);

        setBorder(new CompoundBorder(
                new LineBorder(BORDER_GREEN, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel titleLabel = new JLabel("Recent Notices");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(DEEP_GREEN);

        noticesList = new JPanel();
        noticesList.setLayout(new BoxLayout(noticesList, BoxLayout.Y_AXIS));
        noticesList.setOpaque(false);

        add(titleLabel, BorderLayout.NORTH);
        add(noticesList, BorderLayout.CENTER);

        showNotices(List.of(
                new String[]{
                        "CA Marks Submission",
                        "Please submit outstanding continuous assessment marks."
                },
                new String[]{
                        "Examination Notice",
                        "Review the latest examination-related announcements."
                }
        ));
    }

    public void showNotices(List<String[]> notices) {

        noticesList.removeAll();

        if (notices == null || notices.isEmpty()) {
            JLabel emptyLabel = new JLabel("No recent notices available.");
            emptyLabel.setForeground(TEXT_GRAY);
            emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
            noticesList.add(emptyLabel);
        } else {
            for (String[] notice : notices) {

                String title = notice.length > 0
                        ? notice[0] : "Untitled Notice";

                String description = notice.length > 1
                        ? notice[1] : "";

                JPanel noticePanel = new JPanel();
                noticePanel.setLayout(
                        new BoxLayout(noticePanel, BoxLayout.Y_AXIS));
                noticePanel.setOpaque(false);
                noticePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel noticeTitle = new JLabel(title);
                noticeTitle.setFont(
                        new Font("SansSerif", Font.BOLD, 14));
                noticeTitle.setForeground(DEEP_GREEN);

                JLabel noticeDescription =
                        new JLabel("<html><div style='width:260px;'>"
                                + escapeHtml(description)
                                + "</div></html>");

                noticeDescription.setFont(
                        new Font("SansSerif", Font.PLAIN, 12));
                noticeDescription.setForeground(TEXT_GRAY);

                noticePanel.add(noticeTitle);
                noticePanel.add(Box.createVerticalStrut(6));
                noticePanel.add(noticeDescription);

                noticesList.add(noticePanel);
                noticesList.add(Box.createVerticalStrut(18));
            }
        }

        noticesList.revalidate();
        noticesList.repaint();
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
