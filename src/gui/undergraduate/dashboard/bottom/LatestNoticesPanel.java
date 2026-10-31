package gui.undergraduate.dashboard.bottom;

import dao.undergraduate.UndergraduateNoticeDAO;
import dao.undergraduate.UndergraduateNoticeDAO.NoticeItemDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Undergraduate Latest Notices Card connected directly to database.
 */
public class LatestNoticesPanel extends JPanel {

    private final JPanel listPanel;

    public LatestNoticesPanel(Consumer<String> navigationCallback) {
        setLayout(new BorderLayout(0, 12));
        setBackground(AdminTheme.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD2, 0xD7, 0xE1), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Latest Relevant Notices");
        title.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 17));
        title.setForeground(AdminTheme.DEEP_GREEN);

        JButton viewAllBtn = new JButton("View All Notices →");
        viewAllBtn.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 13));
        viewAllBtn.setForeground(AdminTheme.FOREST_GREEN);
        viewAllBtn.setBorder(null);
        viewAllBtn.setContentAreaFilled(false);
        viewAllBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewAllBtn.addActionListener(e -> {
            if (navigationCallback != null) navigationCallback.accept("notices");
        });

        header.add(title, BorderLayout.WEST);
        header.add(viewAllBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        add(listPanel, BorderLayout.CENTER);
    }

    public void loadLatestNotices() {
        listPanel.removeAll();
        List<NoticeItemDTO> notices = UndergraduateNoticeDAO.getActiveNotices();

        int count = 0;
        for (NoticeItemDTO n : notices) {
            if (count < 2) {
                listPanel.add(createNoticeItem(n.title, n.content, n.publishedAt));
                listPanel.add(Box.createVerticalStrut(8));
                count++;
            }
        }

        if (count == 0) {
            JLabel empty = new JLabel("No active notices found.");
            empty.setFont(AdminTheme.font(Font.PLAIN, 12));
            empty.setForeground(AdminTheme.MUTED);
            listPanel.add(empty);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createNoticeItem(String noticeTitle, String snippet, String date) {
        JPanel p = new JPanel(new BorderLayout(8, 2));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel titleLbl = new JLabel(noticeTitle);
        titleLbl.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 14));
        titleLbl.setForeground(AdminTheme.DEEP_GREEN);

        String preview = (snippet.length() > 80) ? snippet.substring(0, 80) + "..." : snippet;
        JLabel desc = new JLabel(preview);
        desc.setFont(new Font(AdminTheme.FONT_FAMILY, Font.PLAIN, 12));
        desc.setForeground(AdminTheme.TEXT);

        JLabel dateLbl = new JLabel("Posted: " + date);
        dateLbl.setFont(new Font(AdminTheme.FONT_FAMILY, Font.PLAIN, 11));
        dateLbl.setForeground(AdminTheme.MUTED);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(titleLbl);
        content.add(Box.createVerticalStrut(2));
        content.add(desc);

        p.add(content, BorderLayout.CENTER);
        p.add(dateLbl, BorderLayout.EAST);
        return p;
    }
}