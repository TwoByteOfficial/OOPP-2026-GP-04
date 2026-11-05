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
 * Properly wraps content while maintaining bounded height without excessive empty spaces.
 * Encapsulates dynamic styling per notice classification and hover interactions on the action link.
 */
public class NoticeCardItem extends JPanel {

    // Card styling constants for rounded corner geometry and subtle border stroke
    private static final int CORNER_RADIUS = 14;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    // UI elements displaying notice metadata, content, and interaction links
    private final JLabel categoryBadge;
    private final JLabel publisherLabel;
    private final JLabel dateLabel;
    private final JLabel titleLabel;
    private final JLabel contentPreviewLabel;
    private final JLabel readMoreLabel;

    /**
     * Constructs an individual notice card view and binds event listeners.
     *
     * @param notice     Notice data object holding announcement attributes
     * @param onReadMore Consumer callback triggered when the user clicks the read more link
     */
    public NoticeCardItem(NoticeData notice, Consumer<NoticeData> onReadMore) {
        // Base card layout configuration and inner padding
        setOpaque(false);
        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(12, 16, 12, 16));

        // -------------------------------------------------------------
        // 1. Top Meta Row: Badge + Publisher on Left, Date on Right
        // -------------------------------------------------------------
        JPanel topMeta = new JPanel(new BorderLayout());
        topMeta.setOpaque(false);

        JPanel leftMeta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftMeta.setOpaque(false);

        // Classification badge (e.g., Exam, Academic, Attendance)
        categoryBadge = new JLabel("General");
        categoryBadge.setFont(AdminTheme.font(Font.BOLD, 11));
        categoryBadge.setForeground(AdminTheme.FOREST_GREEN);
        categoryBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                new EmptyBorder(2, 8, 2, 8)
        ));

        // Publishing authority label
        publisherLabel = new JLabel("• Faculty Dean's Office");
        publisherLabel.setFont(AdminTheme.font(Font.PLAIN, 12));
        publisherLabel.setForeground(AdminTheme.MUTED);

        leftMeta.add(categoryBadge);
        leftMeta.add(publisherLabel);

        // Publication date label
        dateLabel = new JLabel("2026-10-30");
        dateLabel.setFont(AdminTheme.font(Font.PLAIN, 11));
        dateLabel.setForeground(AdminTheme.MUTED);

        topMeta.add(leftMeta, BorderLayout.WEST);
        topMeta.add(dateLabel, BorderLayout.EAST);
        add(topMeta, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Center Content Area: Title + Content Preview
        // -------------------------------------------------------------
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

        // -------------------------------------------------------------
        // 3. Bottom Action Row: Read Full Notice Link with Hover Feedback
        // -------------------------------------------------------------
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomRow.setOpaque(false);

        readMoreLabel = new JLabel("Read Full Notice →");
        readMoreLabel.setFont(AdminTheme.font(Font.BOLD, 12));
        readMoreLabel.setForeground(AdminTheme.FOREST_GREEN);
        readMoreLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Mouse listener handling click callback and hover color transitions
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

        // Populate component fields from the provided notice model
        bindData(notice);
    }

    /**
     * Binds notice model properties dynamically to UI elements using reflection.
     *
     * @param item NoticeData instance or data transfer object
     */
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

        String rawContent = !content.isEmpty() ? content : "";
        contentPreviewLabel.setText("<html><body style='width: 650px;'>" + rawContent + "</body></html>");
    }

    /**
     * Configures the border stroke and foreground tint of the category badge based on notice type.
     *
     * @param type Notice category type string
     */
    private void setCategoryBadgeColor(String type) {
        if ("Exam".equalsIgnoreCase(type)) {
            categoryBadge.setForeground(new Color(0x19, 0x76, 0xD2));
            categoryBadge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xBB, 0xDE, 0xFB), 1, true),
                    new EmptyBorder(2, 8, 2, 8)
            ));
        } else if ("Attendance".equalsIgnoreCase(type)) {
            categoryBadge.setForeground(new Color(0x2E, 0x7D, 0x32));
            categoryBadge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1, true),
                    new EmptyBorder(2, 8, 2, 8)
            ));
        } else if ("Emergency".equalsIgnoreCase(type) || "Urgent".equalsIgnoreCase(type)) {
            categoryBadge.setForeground(new Color(0xC6, 0x28, 0x28));
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

    /**
     * Inspects candidate getter method names and field identifiers via reflection to extract text values.
     *
     * @param obj                 Target data object
     * @param methodsOrFields     Ordered candidate names to inspect
     * @return Extracted string representation, or empty string if not found
     */
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

    /**
     * Paints an anti-aliased rounded white card background and outer perimeter border.
     */
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