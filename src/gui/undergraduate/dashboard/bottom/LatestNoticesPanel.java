package gui.undergraduate.dashboard.bottom;

import dao.undergraduate.UndergraduateNoticeDAO;
import dao.undergraduate.UndergraduateNoticeDAO.NoticeItemDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Latest Relevant Notices widget anchored to NORTH to wrap notice items tightly.
 * Header typography and colors match Attendance Distribution exactly (15px Bold, AdminTheme.PRIMARY).
 * Fetches and displays the most recent active notices in a compact list format.
 */
public class LatestNoticesPanel extends JPanel {

    // Notices list container and navigation callback
    private final JPanel noticesContainer;
    private final Consumer<String> onNavigate;

    public LatestNoticesPanel(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        setLayout(new BorderLayout());
        setOpaque(false);

        // -------------------------------------------------------------
        // Outer Rounded Card Container
        // -------------------------------------------------------------
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw rounded white card background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Draw subtle card border
                g2.setColor(new Color(225, 235, 228));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        // -------------------------------------------------------------
        // Top Header Row (Title & View All Navigation Button)
        // -------------------------------------------------------------
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);

        // Header title (15px Bold & AdminTheme.PRIMARY) matching dashboard design standards
        JLabel titleLbl = new JLabel("Latest Relevant Notices");
        titleLbl.setFont(AdminTheme.font(Font.BOLD, 15));
        titleLbl.setForeground(AdminTheme.PRIMARY);

        // "View All Notices" button directing users to the full notices view
        JButton viewAllBtn = new JButton("View All Notices →");
        viewAllBtn.setFont(AdminTheme.font(Font.BOLD, 11));
        viewAllBtn.setForeground(AdminTheme.PRIMARY);
        viewAllBtn.setBorderPainted(false);
        viewAllBtn.setContentAreaFilled(false);
        viewAllBtn.setFocusPainted(false);
        viewAllBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewAllBtn.addActionListener(e -> {
            if (onNavigate != null) onNavigate.accept("notices");
        });

        headerRow.add(titleLbl, BorderLayout.WEST);
        headerRow.add(viewAllBtn, BorderLayout.EAST);
        card.add(headerRow, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // Notices List Container
        // -------------------------------------------------------------
        noticesContainer = new JPanel();
        noticesContainer.setLayout(new BoxLayout(noticesContainer, BoxLayout.Y_AXIS));
        noticesContainer.setOpaque(false);
        card.add(noticesContainer, BorderLayout.CENTER);

        // Anchored to NORTH so white background tightly terminates after the last notice
        add(card, BorderLayout.NORTH);
    }

    /**
     * Asynchronously fetches active notices from database via DAO using SwingWorker.
     */
    public void loadLatestNotices() {
        SwingWorker<List<NoticeItemDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<NoticeItemDTO> doInBackground() {
                try {
                    return UndergraduateNoticeDAO.getActiveNotices();
                } catch (Exception e) {
                    return new ArrayList<>();
                }
            }

            @Override
            protected void done() {
                try {
                    updateNotices(get());
                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    /**
     * Updates the notices panel UI with the latest retrieved notice entries.
     */
    public void updateNotices(List<NoticeItemDTO> notices) {
        noticesContainer.removeAll();

        if (notices == null || notices.isEmpty()) {
            JLabel emptyLbl = new JLabel("No new notices published.");
            emptyLbl.setFont(AdminTheme.font(Font.PLAIN, 12));
            emptyLbl.setForeground(new Color(120, 140, 130));
            emptyLbl.setBorder(new EmptyBorder(10, 4, 10, 4));
            noticesContainer.add(emptyLbl);
        } else {
            // Display a maximum of 2 latest notices on the dashboard widget
            int displayCount = Math.min(notices.size(), 2);
            for (int i = 0; i < displayCount; i++) {
                if (i > 0) noticesContainer.add(Box.createVerticalStrut(6));
                noticesContainer.add(createCompactNoticeCard(notices.get(i)));
            }
        }
        noticesContainer.revalidate();
        noticesContainer.repaint();
    }

    /**
     * Factory method creating a compact single notice card displaying title, snippet, and date.
     */
    private JPanel createCompactNoticeCard(NoticeItemDTO notice) {
        JPanel noticeCard = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(215, 235, 222));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        noticeCard.setOpaque(false);
        noticeCard.setBorder(new EmptyBorder(7, 12, 7, 12));
        noticeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        // Left: Notice Title & Truncated Content Snippet
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(notice.title);
        titleLbl.setFont(AdminTheme.font(Font.BOLD, 12));
        titleLbl.setForeground(AdminTheme.DEEP_GREEN);

        // Sanitize and truncate content snippet length
        String cleanContent = notice.content != null ? notice.content.replaceAll("\\s+", " ") : "";
        if (cleanContent.length() > 55) {
            cleanContent = cleanContent.substring(0, 52) + "...";
        }
        JLabel snippetLbl = new JLabel(cleanContent);
        snippetLbl.setFont(AdminTheme.font(Font.PLAIN, 11));
        snippetLbl.setForeground(new Color(80, 95, 88));

        textPanel.add(titleLbl);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(snippetLbl);
        noticeCard.add(textPanel, BorderLayout.CENTER);

        // Right: Published Date label
        JLabel dateLbl = new JLabel("Posted: " + notice.publishedAt);
        dateLbl.setFont(AdminTheme.font(Font.PLAIN, 10));
        dateLbl.setForeground(new Color(130, 145, 138));
        dateLbl.setVerticalAlignment(SwingConstants.BOTTOM);
        noticeCard.add(dateLbl, BorderLayout.EAST);

        return noticeCard;
    }
}