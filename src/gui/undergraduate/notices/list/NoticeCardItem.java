package gui.undergraduate.notices.list;

import gui.admin.AdminTheme;
import gui.undergraduate.notices.model.NoticeData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Notice card item rendering category badge, title, publisher, date, preview content, and action link.
 * Uses HTML word wrapping to eliminate content truncation.
 */
public class NoticeCardItem extends JPanel {

    private static final int CORNER_RADIUS = 14;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JLabel categoryBadge;
    private final JLabel publisherLabel;
    private final JLabel dateLabel;
    private final JLabel titleLabel;
    private final JLabel contentPreviewLabel;
    private final JLabel readMoreLabel;

    public NoticeCardItem(NoticeData notice, Consumer<NoticeData> onReadMore) {
        setOpaque(false);
        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(12, 16, 12, 16));

        // 1. Top Meta Row: Badge + Publisher on Left, Date on Right
        JPanel topMeta = new JPanel(new BorderLayout());
        topMeta.setOpaque(false);

        JPanel leftMeta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftMeta.setOpaque(false);

        categoryBadge = new JLabel("General");
        categoryBadge.setFont(AdminTheme.font(Font.BOLD, 11));
        categoryBadge.setForeground(AdminTheme.FOREST_GREEN);
        categoryBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                new EmptyBorder(2, 8, 2, 8)
        ));

        publisherLabel = new JLabel("• Faculty Dean's Office");
        publisherLabel.setFont(AdminTheme.font(Font.PLAIN, 12));
        publisherLabel.setForeground(AdminTheme.MUTED);

        leftMeta.add(categoryBadge);
        leftMeta.add(publisherLabel);

        dateLabel = new JLabel("2026-10-30");
        dateLabel.setFont(AdminTheme.font(Font.PLAIN, 11));
        dateLabel.setForeground(AdminTheme.MUTED);

        topMeta.add(leftMeta, BorderLayout.WEST);
        topMeta.add(dateLabel, BorderLayout.EAST);
        add(topMeta, BorderLayout.NORTH);

        // 2. Center Content Area: Title + Content Preview
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Notice Title");
        titleLabel.setFont(AdminTheme.font(Font.BOLD, 14));
        titleLabel.setForeground(AdminTheme.FOREST_GREEN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPreviewLabel = new JLabel();
        contentPreviewLabel.setFont(AdminTheme.font(Font.PLAIN, 12));
        contentPreviewLabel.setForeground(new Color(0x55, 0x55, 0x55));
        contentPreviewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(contentPreviewLabel);
        add(centerPanel, BorderLayout.CENTER);

        // 3. Bottom Action Row: "Read Full Notice ->"
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomRow.setOpaque(false);

        readMoreLabel = new JLabel("Read Full Notice →");
        readMoreLabel.setFont(AdminTheme.font(Font.BOLD, 12));
        readMoreLabel.setForeground(AdminTheme.FOREST_GREEN);
        readMoreLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        readMoreLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onReadMore != null && notice != null) {
                    onReadMore.accept(notice);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                readMoreLabel.setForeground(AdminTheme.DEEP_GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                readMoreLabel.setForeground(AdminTheme.FOREST_GREEN);
            }
        });

        bottomRow.add(readMoreLabel);
        add(bottomRow, BorderLayout.SOUTH);

        // Populate values dynamically
        bindData(notice);
    }

    private void bindData(NoticeData item) {
        if (item == null) return;
        String type = extractValue(item, "getNoticeType", "getType", "noticeType", "type");
        String pub = extractValue(item, "getPublishedBy", "getPublisher", "publishedBy", "publisher");
        String date = extractValue(item, "getPublishedAt", "getDate", "publishedAt", "date");
        String title = extractValue(item, "getTitle", "title");
        String content = extractValue(item, "getContent", "content", "getDescription", "description");

        if (type.isEmpty()) type = "General";
        categoryBadge.setText(type);
        setCategoryBadgeColor(type);

        publisherLabel.setText("• " + (!pub.isEmpty() ? pub : "System Admin"));
        dateLabel.setText(!date.isEmpty() ? date : "2026-10-30");
        titleLabel.setText(!title.isEmpty() ? title : "Notice");

        // Format content with HTML word-wrap so that sentences flow smoothly without truncation
        String rawContent = !content.isEmpty() ? content : "";
        contentPreviewLabel.setText("<html><body style='width: 650px;'>" + rawContent + "</body></html>");
    }

    private void setCategoryBadgeColor(String type) {
        if ("Exam".equalsIgnoreCase(type)) {
            categoryBadge.setForeground(new Color(0x19, 0x76, 0xD2)); // Blue
            categoryBadge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xBB, 0xDE, 0xFB), 1, true),
                    new EmptyBorder(2, 8, 2, 8)
            ));
        } else if ("Attendance".equalsIgnoreCase(type)) {
            categoryBadge.setForeground(new Color(0x2E, 0x7D, 0x32)); // Green
            categoryBadge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1, true),
                    new EmptyBorder(2, 8, 2, 8)
            ));
        } else if ("Urgent".equalsIgnoreCase(type) || "Emergency".equalsIgnoreCase(type)) {
            categoryBadge.setForeground(new Color(0xC6, 0x28, 0x28)); // Red
            categoryBadge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xFF, 0xCD, 0xD2), 1, true),
                    new EmptyBorder(2, 8, 2, 8)
            ));
        } else {
            categoryBadge.setForeground(AdminTheme.FOREST_GREEN);
            categoryBadge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                    new EmptyBorder(2, 8, 2, 8)
            ));
        }
    }

    private String extractValue(Object obj, String... methodsOrFields) {
        if (obj == null) return "";
        for (String name : methodsOrFields) {
            try {
                Method m = obj.getClass().getMethod(name);
                Object val = m.invoke(obj);
                if (val != null) return String.valueOf(val);
            } catch (Exception ignored) {}
            try {
                java.lang.reflect.Field f = obj.getClass().getField(name);
                Object val = f.get(obj);
                if (val != null) return String.valueOf(val);
            } catch (Exception ignored) {}
        }
        return "";
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(AdminTheme.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.setColor(CARD_BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.dispose();
        super.paintComponent(g);
    }
}