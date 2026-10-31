
package gui.technical.dashboard.bottom;

import dao.TechnicalOfficer.NoticeDAO;
import gui.theme.UniMisTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class LatestNoticesPanel extends JPanel {

    public LatestNoticesPanel(long userId) {

        setLayout(new BorderLayout(0, 14));
        setBackground(UniMisTheme.WHITE);
        setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel heading = new JLabel("Latest Notices");
        heading.setFont(UniMisTheme.FONT_H3);
        heading.setForeground(UniMisTheme.TEXT);

        add(heading, BorderLayout.NORTH);

        JPanel noticesPanel = new JPanel();
        noticesPanel.setOpaque(false);
        noticesPanel.setLayout(
                new BoxLayout(noticesPanel, BoxLayout.Y_AXIS)
        );

        try {
            NoticeDAO dao = new NoticeDAO();

            List<Object[]> notices =
                    dao.getNoticesForTechnicalOfficer(userId);

            if (notices.isEmpty()) {
                JLabel message = new JLabel("No notices available.");
                message.setFont(UniMisTheme.FONT_TINY);
                message.setForeground(UniMisTheme.MUTED);

                noticesPanel.add(message);
            } else {
                int count = Math.min(5, notices.size());

                for (int i = 0; i < count; i++) {
                    Object[] notice = notices.get(i);

                    String title = String.valueOf(notice[1]);
                    String type = String.valueOf(notice[3]);
                    Timestamp publishedAt =
                            (Timestamp) notice[5];

                    JPanel noticeItem = new JPanel(
                            new BorderLayout(0, 5)
                    );
                    noticeItem.setOpaque(false);
                    noticeItem.setBorder(
                            new EmptyBorder(8, 0, 8, 0)
                    );

                    JLabel titleLabel = new JLabel(title);
                    titleLabel.setFont(UniMisTheme.FONT_BODY_BOLD);
                    titleLabel.setForeground(UniMisTheme.TEXT);

                    String publishedDate = "";

                    if (publishedAt != null) {
                        publishedDate = new SimpleDateFormat(
                                "dd MMM yyyy"
                        ).format(publishedAt);
                    }

                    JLabel detailsLabel = new JLabel(
                            type + "  •  " + publishedDate
                    );
                    detailsLabel.setFont(UniMisTheme.FONT_TINY);
                    detailsLabel.setForeground(UniMisTheme.MUTED);

                    noticeItem.add(titleLabel, BorderLayout.NORTH);
                    noticeItem.add(detailsLabel, BorderLayout.CENTER);

                    noticesPanel.add(noticeItem);

                    if (i < count - 1) {
                        noticesPanel.add(
                                new JSeparator(SwingConstants.HORIZONTAL)
                        );
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

            JLabel errorLabel =
                    new JLabel("Unable to load notices.");
            errorLabel.setFont(UniMisTheme.FONT_TINY);
            errorLabel.setForeground(UniMisTheme.MUTED);

            noticesPanel.add(errorLabel);
        }

        add(noticesPanel, BorderLayout.CENTER);
    }
}
