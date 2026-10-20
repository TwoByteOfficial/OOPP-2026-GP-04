package gui.admin.DashboardComponents;

import dao.admin.AdminNoticeDAO;
import model.Notice;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

/**
 * NoticesSummary - Recently Published Announcements table (Row 4 Right).
 */
public class NoticesSummary extends JPanel {

    private final Color WHITE        = Color.WHITE;
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color MUTED        = new Color(0x4F, 0x6B, 0x52);

    private AdminNoticeDAO noticeDAO;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public NoticesSummary() {
        setLayout(null);
        setBounds(0, 0, 790, 450);
        setOpaque(false);

        noticeDAO = new AdminNoticeDAO();
        List<Notice> recentNotices = new ArrayList<>();
        try {
            List<Notice> allNotices = noticeDAO.getAllNotices();
            for (int i = 0; i < Math.min(5, allNotices.size()); i++) {
                recentNotices.add(allNotices.get(i));
            }
        } catch (Exception e) {
            System.err.println("[Notices Summary DB Error] Could not load notice board.");
            e.printStackTrace();
        }

        JPanel container = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(0x1B, 0x5E, 0x20, 35));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };

        container.setBounds(0, 0, 790, 450);
        container.setOpaque(false);

        JLabel title = new JLabel("Recent Notices & Announcements");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20)); // 18 -> 20
        title.setForeground(FOREST_GREEN);
        title.setBounds(25, 18, 420, 32);
        container.add(title);

        JLabel subtitle = new JLabel("Latest administrative announcements active on system board");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // 12 -> 14
        subtitle.setForeground(MUTED);
        subtitle.setBounds(25, 48, 500, 22);
        container.add(subtitle);

        JPanel tableHeader = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0xE8, 0xF5, 0xE9));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        tableHeader.setBounds(25, 75, 740, 40);
        tableHeader.setOpaque(false);

        String[] columnHeaders = { "Notice Title", "Type", "Target Role", "Date Published", "Status" };
        int[] columnWidths = { 220, 110, 140, 150, 120 };

        int currentX = 15;
        for (int i = 0; i < columnHeaders.length; i++) {
            JLabel header = new JLabel(columnHeaders[i]);
            header.setFont(new Font("Segoe UI", Font.BOLD, 15)); // 13 -> 15
            header.setForeground(FOREST_GREEN);
            header.setBounds(currentX, 0, columnWidths[i], 40);
            tableHeader.add(header);
            currentX += columnWidths[i];
        }
        container.add(tableHeader);

        for (int row = 0; row < recentNotices.size(); row++) {
            Notice n = recentNotices.get(row);

            String formattedDate = (n.getPublishedAt() != null) ? dateFormat.format(n.getPublishedAt()) : "N/A";
            String noticeTitle = (n.getTitle() != null && n.getTitle().length() > 28)
                    ? n.getTitle().substring(0, 25) + "..."
                    : (n.getTitle() != null ? n.getTitle() : "Untitled");

            String[] liveRowData = {
                    noticeTitle,
                    n.getNoticeType() != null ? n.getNoticeType() : "General",
                    n.getTargetRole() != null ? n.getTargetRole() : "All Roles",
                    formattedDate,
                    n.getStatus() != null ? n.getStatus() : "Published"
            };

            JPanel rowPanel = new JPanel(null);
            rowPanel.setOpaque(false);
            rowPanel.setBounds(25, 122 + (row * 58), 740, 54);

            int cellX = 15;
            for (int col = 0; col < liveRowData.length; col++) {
                JLabel cell = new JLabel(liveRowData[col]);
                cell.setFont(new Font("Segoe UI", Font.PLAIN, 15)); // 13 -> 15
                cell.setForeground(col == 0 ? DEEP_GREEN : MUTED);

                if (col == 4) {
                    cell.setFont(new Font("Segoe UI", Font.BOLD, 15)); // 13 -> 15
                    if ("Published".equalsIgnoreCase(liveRowData[col])) {
                        cell.setForeground(FOREST_GREEN);
                    } else if ("Draft".equalsIgnoreCase(liveRowData[col])) {
                        cell.setForeground(new Color(0xF0, 0xAD, 0x4E));
                    } else {
                        cell.setForeground(new Color(0xDC, 0x35, 0x45));
                    }
                }

                cell.setBounds(cellX, 0, columnWidths[col], 54);
                rowPanel.add(cell);
                cellX += columnWidths[col];
            }

            JSeparator separator = new JSeparator();
            separator.setForeground(PASTEL_GREEN);
            separator.setBounds(0, 53, 740, 1);
            rowPanel.add(separator);

            container.add(rowPanel);
        }

        if (recentNotices.isEmpty()) {
            JLabel emptyLabel = new JLabel("No notices found in system database board.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            emptyLabel.setForeground(MUTED);
            emptyLabel.setBounds(25, 180, 740, 40);
            container.add(emptyLabel);
        }

        add(container);
    }
}
