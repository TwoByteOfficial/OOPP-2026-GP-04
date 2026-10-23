package gui.undergraduate.notices.list;

import gui.admin.AdminTheme;
import gui.undergraduate.notices.model.NoticeData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Clean white card component representing a single official notice with preview and read button.
 */
public class NoticeCardItem extends JPanel {

    private static final int CORNER_RADIUS = 14;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    public NoticeCardItem(NoticeData data, Consumer<NoticeData> onReadMore) {
        setOpaque(false);
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(14, 16, 14, 16));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));
        setPreferredSize(new Dimension(0, 115));

        // 1. Top Row: Type Badge + Publisher + Date
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JPanel leftMeta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftMeta.setOpaque(false);

        JLabel typeBadge = createTypeBadge(data.getNoticeType());
        JLabel publisherLbl = new JLabel("•  " + data.getPublisher());
        publisherLbl.setFont(AdminTheme.font(Font.BOLD, 12));
        publisherLbl.setForeground(AdminTheme.MUTED);

        leftMeta.add(typeBadge);
        leftMeta.add(publisherLbl);

        JLabel dateLbl = new JLabel(data.getPublishedAt());
        dateLbl.setFont(AdminTheme.font(Font.PLAIN, 12));
        dateLbl.setForeground(AdminTheme.MUTED);

        topRow.add(leftMeta, BorderLayout.WEST);
        topRow.add(dateLbl, BorderLayout.EAST);
        add(topRow, BorderLayout.NORTH);

        // 2. Middle: Title
        JLabel titleLbl = new JLabel(data.getTitle());
        titleLbl.setFont(AdminTheme.font(Font.BOLD, 15));
        titleLbl.setForeground(AdminTheme.DEEP_GREEN);
        add(titleLbl, BorderLayout.CENTER);

        // 3. Bottom Row: Snippet + Read Button
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);

        String previewText = data.getContent().length() > 90
                ? data.getContent().substring(0, 90) + "..."
                : data.getContent();

        JLabel snippetLbl = new JLabel(previewText);
        snippetLbl.setFont(AdminTheme.font(Font.PLAIN, 12));
        snippetLbl.setForeground(new Color(0x55, 0x55, 0x55));

        JButton readBtn = new JButton("Read Full Notice →");
        readBtn.setFont(AdminTheme.font(Font.BOLD, 12));
        readBtn.setForeground(AdminTheme.FOREST_GREEN);
        readBtn.setContentAreaFilled(false);
        readBtn.setBorder(null);
        readBtn.setFocusPainted(false);
        readBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        readBtn.addActionListener(e -> {
            if (onReadMore != null) onReadMore.accept(data);
        });

        bottomRow.add(snippetLbl, BorderLayout.CENTER);
        bottomRow.add(readBtn, BorderLayout.EAST);
        add(bottomRow, BorderLayout.SOUTH);
    }

    private JLabel createTypeBadge(String type) {
        JLabel badge = new JLabel(type);
        badge.setFont(AdminTheme.font(Font.BOLD, 11));

        Color fg = AdminTheme.FOREST_GREEN;
        Color bg = AdminTheme.MINT;
        if ("Urgent".equalsIgnoreCase(type)) {
            fg = AdminTheme.DANGER;
            bg = new Color(0xFF, 0xEB, 0xEE);
        } else if ("Exam".equalsIgnoreCase(type)) {
            fg = new Color(0x15, 0x65, 0xC0);
            bg = new Color(0xE3, 0xF2, 0xFD);
        }

        badge.setForeground(fg);
        badge.setOpaque(true);
        badge.setBackground(bg);
        badge.setBorder(new EmptyBorder(2, 8, 2, 8));
        return badge;
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