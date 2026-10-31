
package gui.lecturer.Dashboard.DashboardComponents;

import static gui.lecturer.SharedComponents.Colors.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.Consumer;

public class RecentNotices extends JPanel {

    private final JPanel noticesList;
    private final Consumer<String> onNavigate;

    public RecentNotices(Consumer<String> onNavigate) {

        this.onNavigate = onNavigate;

        setLayout(new BorderLayout(0, 15));
        setBackground(WHITE);

        setBorder(new CompoundBorder(
                new LineBorder(BORDER_GREEN, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titleLabel = new JLabel("Recent Notices");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(DEEP_GREEN);

        JLabel subtitle = new JLabel("Latest faculty announcements");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(TEXT_GRAY);

        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);

        heading.add(titleLabel);
        heading.add(Box.createVerticalStrut(4));
        heading.add(subtitle);

        JButton viewAllButton = new JButton("View All →");
        viewAllButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        viewAllButton.setForeground(MEDIUM_GREEN);
        viewAllButton.setBackground(WHITE);
        viewAllButton.setBorderPainted(false);
        viewAllButton.setFocusPainted(false);
        viewAllButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        viewAllButton.addActionListener(e -> {
            if (this.onNavigate != null) {
                this.onNavigate.accept("Notices");
            }
        });

        header.add(heading, BorderLayout.WEST);
        header.add(viewAllButton, BorderLayout.EAST);

        // Notice list
        noticesList = new JPanel();
        noticesList.setLayout(
                new BoxLayout(noticesList, BoxLayout.Y_AXIS)
        );
        noticesList.setOpaque(false);

        add(header, BorderLayout.NORTH);
        add(noticesList, BorderLayout.CENTER);

        // No sample data. Actual data comes from the DAO.
        showNotices(List.of());
    }

    public void showNotices(List<Object[]> notices) {

        noticesList.removeAll();

        if (notices == null || notices.isEmpty()) {

            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(
                    new BoxLayout(emptyPanel, BoxLayout.Y_AXIS)
            );
            emptyPanel.setOpaque(false);
            emptyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel emptyIcon = new JLabel("♧");
            emptyIcon.setFont(new Font("SansSerif", Font.PLAIN, 30));
            emptyIcon.setForeground(BORDER_GREEN);
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel emptyText = new JLabel("No recent notices");
            emptyText.setFont(
                    new Font("SansSerif", Font.BOLD, 13)
            );
            emptyText.setForeground(TEXT_GRAY);
            emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyPanel.add(Box.createVerticalStrut(25));
            emptyPanel.add(emptyIcon);
            emptyPanel.add(Box.createVerticalStrut(8));
            emptyPanel.add(emptyText);
            emptyPanel.add(Box.createVerticalStrut(25));

            noticesList.add(emptyPanel);

        } else {

            for (Object[] notice : notices) {

                if (notice == null || notice.length < 6) {
                    continue;
                }

                long noticeId = ((Number) notice[0]).longValue();

                String title = notice[1] == null
                        ? "Untitled Notice"
                        : notice[1].toString();

                String content = notice[2] == null
                        ? ""
                        : notice[2].toString();

                String type = notice[3] == null
                        ? "General"
                        : notice[3].toString();

                Timestamp publishedAt =
                        (Timestamp) notice[4];

                String publisher = notice[5] == null
                        ? "Faculty Administration"
                        : notice[5].toString();

                noticesList.add(
                        createNoticeCard(
                                noticeId,
                                title,
                                content,
                                type,
                                publishedAt,
                                publisher
                        )
                );

                noticesList.add(Box.createVerticalStrut(12));
            }
        }

        noticesList.revalidate();
        noticesList.repaint();
    }

    private JPanel createNoticeCard(
            long noticeId,
            String title,
            String content,
            String type,
            Timestamp publishedAt,
            String publisher) {

        Color categoryColor = getCategoryColor(type);
        Color categoryBackground = getCategoryBackground(type);

        JPanel card = new JPanel(new BorderLayout(10, 0));

        card.setBackground(new Color(250, 252, 250));
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_GREEN, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 145)
        );

        // Category icon
        JLabel iconLabel = new JLabel(getCategoryIcon(type));
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        iconLabel.setForeground(categoryColor);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel iconPanel = new JPanel(new GridBagLayout());
        iconPanel.setPreferredSize(new Dimension(42, 42));
        iconPanel.setBackground(categoryBackground);
        iconPanel.setBorder(
                new LineBorder(categoryBackground, 1, true)
        );
        iconPanel.add(iconLabel);

        // Notice information
        JPanel information = new JPanel();
        information.setLayout(
                new BoxLayout(information, BoxLayout.Y_AXIS)
        );
        information.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel(
                "<html><b>" + escapeHtml(title) + "</b></html>"
        );
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        titleLabel.setForeground(DEEP_GREEN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Category label
        JLabel categoryLabel = new JLabel(type.toUpperCase());
        categoryLabel.setFont(
                new Font("SansSerif", Font.BOLD, 9)
        );
        categoryLabel.setForeground(categoryColor);
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Short description
        String shortContent = content.trim();

        if (shortContent.length() > 95) {
            shortContent = shortContent.substring(0, 95) + "...";
        }

        JLabel descriptionLabel = new JLabel(
                "<html><div style='width:230px;'>"
                        + escapeHtml(shortContent)
                        + "</div></html>"
        );

        descriptionLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 11)
        );
        descriptionLabel.setForeground(TEXT_GRAY);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Publisher and date
        String dateText = publishedAt == null
                ? "Date unavailable"
                : new SimpleDateFormat("dd MMM yyyy")
                .format(publishedAt);

        JLabel metadataLabel = new JLabel(
                "By " + escapeHtml(publisher) + "  •  " + dateText
        );

        metadataLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 10)
        );
        metadataLabel.setForeground(TEXT_GRAY);
        metadataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Read more button
        JButton readMoreButton = new JButton("Read more →");
        readMoreButton.setFont(
                new Font("SansSerif", Font.BOLD, 10)
        );
        readMoreButton.setForeground(MEDIUM_GREEN);
        readMoreButton.setBorderPainted(false);
        readMoreButton.setContentAreaFilled(false);
        readMoreButton.setFocusPainted(false);
        readMoreButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        readMoreButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        readMoreButton.addActionListener(e ->
                showNoticeDetails(
                        noticeId,
                        title,
                        content,
                        type,
                        publishedAt,
                        publisher
                )
        );

        information.add(categoryLabel);
        information.add(Box.createVerticalStrut(3));
        information.add(titleLabel);
        information.add(Box.createVerticalStrut(5));
        information.add(descriptionLabel);
        information.add(Box.createVerticalStrut(6));
        information.add(metadataLabel);
        information.add(Box.createVerticalStrut(3));
        information.add(readMoreButton);

        card.add(iconPanel, BorderLayout.WEST);
        card.add(information, BorderLayout.CENTER);

        return card;
    }

    private void showNoticeDetails(
            long noticeId,
            String title,
            String content,
            String type,
            Timestamp publishedAt,
            String publisher) {

        Window owner = SwingUtilities.getWindowAncestor(this);

        JDialog dialog = new JDialog(
                owner,
                "Notice Details",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(600, 480);
        dialog.setMinimumSize(new Dimension(500, 400));
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel categoryLabel = new JLabel(
                getCategoryIcon(type) + "  " + type.toUpperCase()
        );

        categoryLabel.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        categoryLabel.setForeground(getCategoryColor(type));

        JLabel titleLabel = new JLabel(
                "<html><div style='width:490px;'>"
                        + escapeHtml(title)
                        + "</div></html>"
        );

        titleLabel.setFont(
                new Font("SansSerif", Font.BOLD, 21)
        );
        titleLabel.setForeground(DEEP_GREEN);

        String dateText = publishedAt == null
                ? "Date unavailable"
                : new SimpleDateFormat("dd MMM yyyy, hh:mm a")
                .format(publishedAt);

        JLabel metadata = new JLabel(
                "Published by " + escapeHtml(publisher)
                        + "  •  " + dateText
        );

        metadata.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );
        metadata.setForeground(TEXT_GRAY);

        JPanel heading = new JPanel();
        heading.setLayout(
                new BoxLayout(heading, BoxLayout.Y_AXIS)
        );
        heading.setOpaque(false);

        heading.add(categoryLabel);
        heading.add(Box.createVerticalStrut(10));
        heading.add(titleLabel);
        heading.add(Box.createVerticalStrut(10));
        heading.add(metadata);

        JTextArea contentArea = new JTextArea(content);
        contentArea.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );
        contentArea.setForeground(Color.DARK_GRAY);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        contentArea.setOpaque(false);
        contentArea.setBorder(new EmptyBorder(5, 0, 5, 0));
        contentArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(
                new LineBorder(BORDER_GREEN, 1, true)
        );
        scrollPane.getViewport().setBackground(WHITE);

        JButton closeButton = new JButton("Close");
        closeButton.setBackground(DEEP_GREEN);
        closeButton.setForeground(WHITE);
        closeButton.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.add(closeButton);

        mainPanel.add(heading, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(footer, BorderLayout.SOUTH);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private Color getCategoryColor(String type) {

        switch (type.toLowerCase()) {
            case "emergency":
                return new Color(198, 40, 40);

            case "exam":
                return new Color(123, 31, 162);

            case "academic":
                return new Color(21, 101, 192);

            case "attendance":
                return new Color(230, 126, 34);

            case "event":
                return new Color(0, 121, 107);

            default:
                return MEDIUM_GREEN;
        }
    }

    private Color getCategoryBackground(String type) {

        switch (type.toLowerCase()) {
            case "emergency":
                return new Color(255, 235, 238);

            case "exam":
                return new Color(243, 229, 245);

            case "academic":
                return new Color(227, 242, 253);

            case "attendance":
                return new Color(255, 243, 224);

            case "event":
                return new Color(224, 242, 241);

            default:
                return PALE_GREEN;
        }
    }

    private String getCategoryIcon(String type) {

        switch (type.toLowerCase()) {
            case "emergency":
                return "⚠";

            case "exam":
                return "▤";

            case "academic":
                return "▣";

            case "attendance":
                return "◷";

            case "event":
                return "▦";

            default:
                return "●";
        }
    }

    private String escapeHtml(String text) {

        if (text == null) {
            return "";
        }

        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
