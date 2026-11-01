
package gui.lecturer.Notices;

import dao.Lecturer.NoticeDAO;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static gui.lecturer.SharedComponents.Colors.NOTICES_BACKGROUND;
import static gui.lecturer.SharedComponents.Colors.NOTICES_BORDER;
import static gui.lecturer.SharedComponents.Colors.NOTICES_DEEP_GREEN;
import static gui.lecturer.SharedComponents.Colors.NOTICES_DARK_GRAY;
import static gui.lecturer.SharedComponents.Colors.NOTICES_TEXT_GRAY;
import static gui.lecturer.SharedComponents.Colors.NOTICES_WHITE;

public class LecturerNotices extends JPanel {

    private final long lecturerId;

    private final NoticeDAO noticeDAO = new NoticeDAO();

    private final JComboBox<String> typeFilterCombo =
            new JComboBox<>(new String[]{
                    "All",
                    "General",
                    "Academic",
                    "Exam",
                    "Attendance",
                    "Event",
                    "Emergency"
            });

    private final JTextField searchField = new JTextField(18);

    private final JPanel noticeContainer = new JPanel();

    private final JLabel statusLabel = new JLabel("Ready");

    private SwingWorker<List<Object[]>, Void> loadWorker;


    // Constructor
    public LecturerNotices(long lecturerId) {

        this.lecturerId = lecturerId;

        setLayout(new BorderLayout(0, 20));

        setBackground(NOTICES_BACKGROUND);

        setBorder(new EmptyBorder(30, 30, 30, 30));


        // ---------------- HEADING ----------------

        JPanel heading = new JPanel();

        heading.setLayout(
                new BoxLayout(heading, BoxLayout.Y_AXIS)
        );

        heading.setOpaque(false);

        JLabel title = new JLabel(
                "Faculty Announcements & Notices"
        );

        title.setFont(
                new Font("SansSerif", Font.BOLD, 28)
        );

        title.setForeground(NOTICES_DEEP_GREEN);

        JLabel subtitle = new JLabel(
                "Stay updated with the latest faculty announcements."
        );

        subtitle.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        subtitle.setForeground(NOTICES_TEXT_GRAY);

        heading.add(title);

        heading.add(Box.createVerticalStrut(6));

        heading.add(subtitle);


        // ---------------- FILTER CONTROLS ----------------

        JPanel controlPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 12, 5)
        );

        controlPanel.setOpaque(false);


        JLabel typeLabel = new JLabel("Notice Type:");

        typeLabel.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        typeLabel.setForeground(NOTICES_DEEP_GREEN);


        typeFilterCombo.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        typeFilterCombo.setBackground(NOTICES_WHITE);

        typeFilterCombo.setPreferredSize(
                new Dimension(155, 38)
        );

        typeFilterCombo.addActionListener(
                e -> loadNotices()
        );


        JLabel searchLabel = new JLabel("Search:");

        searchLabel.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        searchLabel.setForeground(NOTICES_DEEP_GREEN);


        searchField.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        searchField.setPreferredSize(
                new Dimension(220, 38)
        );

        searchField.setBackground(NOTICES_WHITE);

        searchField.setForeground(NOTICES_DARK_GRAY);

        searchField.setCaretColor(NOTICES_DEEP_GREEN);

        searchField.setToolTipText(
                "Search notices by title or content"
        );

        searchField.setBorder(
                new CompoundBorder(
                        new LineBorder(NOTICES_BORDER, 1, true),
                        new EmptyBorder(5, 10, 5, 10)
                )
        );


        // Search button
        JButton searchButton = createButton(
                "Search",
                NOTICES_DEEP_GREEN,
                NOTICES_WHITE
        );

        searchButton.addActionListener(
                e -> loadNotices()
        );

        // Pressing Enter also searches.
        searchField.addActionListener(
                e -> loadNotices()
        );


        // Refresh button
        JButton refreshButton = createButton(
                "Refresh",
                new Color(200, 230, 201),
                NOTICES_DEEP_GREEN
        );

        refreshButton.addActionListener(
                e -> loadNotices()
        );


        // Clear filters button
        JButton clearButton = createButton(
                "Clear Filters",
                new Color(238, 245, 238),
                NOTICES_DEEP_GREEN
        );

        clearButton.addActionListener(e -> {

            searchField.setText("");

            if (!"All".equals(typeFilterCombo.getSelectedItem())) {

                typeFilterCombo.setSelectedItem("All");

                // The combo action listener loads notices.
            } else {

                loadNotices();
            }
        });


        controlPanel.add(typeLabel);
        controlPanel.add(typeFilterCombo);

        controlPanel.add(searchLabel);
        controlPanel.add(searchField);

        controlPanel.add(searchButton);
        controlPanel.add(refreshButton);
        controlPanel.add(clearButton);


        // ---------------- TOP CONTAINER ----------------

        JPanel topContainer = new JPanel(
                new BorderLayout(0, 18)
        );

        topContainer.setOpaque(false);

        topContainer.add(
                heading,
                BorderLayout.NORTH
        );

        topContainer.add(
                controlPanel,
                BorderLayout.SOUTH
        );


        // ---------------- NOTICE CONTAINER ----------------

        noticeContainer.setLayout(
                new BoxLayout(
                        noticeContainer,
                        BoxLayout.Y_AXIS
                )
        );

        noticeContainer.setBackground(
                NOTICES_BACKGROUND
        );

        noticeContainer.setBorder(
                new EmptyBorder(5, 3, 5, 3)
        );


        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(
                noticeContainer
        );

        scrollPane.setBorder(null);

        scrollPane.setBackground(NOTICES_BACKGROUND);

        scrollPane.getViewport().setBackground(
                NOTICES_BACKGROUND
        );

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.getVerticalScrollBar().setUnitIncrement(18);


        // ---------------- FOOTER ----------------

        JPanel footer = new JPanel(
                new BorderLayout()
        );

        footer.setOpaque(false);

        statusLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );

        statusLabel.setForeground(NOTICES_TEXT_GRAY);

        footer.add(
                statusLabel,
                BorderLayout.WEST
        );


        // ---------------- ADD COMPONENTS ----------------

        add(topContainer, BorderLayout.NORTH);

        add(scrollPane, BorderLayout.CENTER);

        add(footer, BorderLayout.SOUTH);


        // Initial loading
        loadNotices();
    }


    // =====================================================
    // BUTTON DESIGN
    // =====================================================

    private JButton createButton(
            String text,
            Color background,
            Color foreground
    ) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        button.setBackground(background);

        button.setForeground(foreground);

        button.setFocusPainted(false);

        button.setBorderPainted(false);

        button.setOpaque(true);

        button.setBorder(
                new EmptyBorder(9, 15, 9, 15)
        );

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        return button;
    }


    // =====================================================
    // LOAD NOTICES
    // =====================================================

    private void loadNotices() {

        // Cancel an older request if a new filter is selected.
        if (loadWorker != null && !loadWorker.isDone()) {

            loadWorker.cancel(true);
        }


        noticeContainer.removeAll();

        noticeContainer.add(
                createMessageLabel("Loading notices...")
        );

        noticeContainer.revalidate();

        noticeContainer.repaint();

        statusLabel.setText("Loading notices...");


        String typeFilter =
                (String) typeFilterCombo.getSelectedItem();

        String query =
                searchField.getText().trim();


        loadWorker = new SwingWorker<List<Object[]>, Void>() {

            @Override
            protected List<Object[]> doInBackground()
                    throws Exception {

                return noticeDAO.getAllNotices(
                        lecturerId,
                        typeFilter,
                        query
                );
            }


            @Override
            protected void done() {

                // Ignore an older request.
                if (this != loadWorker || isCancelled()) {
                    return;
                }

                try {

                    List<Object[]> notices = get();

                    displayNotices(notices);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    statusLabel.setText(
                            "Notice loading was interrupted."
                    );

                } catch (ExecutionException e) {

                    Throwable cause = e.getCause();

                    statusLabel.setText(
                            "Error loading notices."
                    );

                    noticeContainer.removeAll();

                    noticeContainer.add(
                            createMessageLabel(
                                    "Unable to load notices. Please try again."
                            )
                    );

                    noticeContainer.revalidate();

                    noticeContainer.repaint();

                    JOptionPane.showMessageDialog(
                            LecturerNotices.this,
                            "Database error: " +
                                    (cause != null
                                            ? cause.getMessage()
                                            : e.getMessage()),
                            "Notice Loading Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        loadWorker.execute();
    }


    // =====================================================
    // DISPLAY NOTICES
    // =====================================================

    private void displayNotices(List<Object[]> notices) {

        noticeContainer.removeAll();

        if (notices.isEmpty()) {

            noticeContainer.add(
                    createMessageLabel(
                            "No notices found matching your search criteria."
                    )
            );

            statusLabel.setText("No notices found.");

        } else {

            String previousDateGroup = null;

            for (Object[] notice : notices) {

                Timestamp publishedAt =
                        notice[5] instanceof Timestamp
                                ? (Timestamp) notice[5]
                                : null;


                String currentDateGroup =
                        getDateGroup(publishedAt);


                // Add a date heading when the date changes.
                if (!currentDateGroup.equals(previousDateGroup)) {

                    noticeContainer.add(
                            createDateHeading(currentDateGroup)
                    );

                    previousDateGroup = currentDateGroup;
                }


                JPanel card = createNoticeCard(notice);

                noticeContainer.add(card);

                noticeContainer.add(
                        Box.createVerticalStrut(15)
                );
            }

            statusLabel.setText(
                    "Total Published Notices: " + notices.size()
            );
        }

        noticeContainer.revalidate();

        noticeContainer.repaint();
    }


    // =====================================================
    // DATE GROUPING
    // =====================================================

    private String getDateGroup(Timestamp timestamp) {

        if (timestamp == null) {
            return "Date Unavailable";
        }

        LocalDate noticeDate =
                timestamp.toLocalDateTime().toLocalDate();

        LocalDate today = LocalDate.now();

        if (noticeDate.equals(today)) {

            return "Today";

        } else if (noticeDate.equals(today.minusDays(1))) {

            return "Yesterday";

        } else {

            return noticeDate.format(
                    DateTimeFormatter.ofPattern(
                            "EEEE, dd MMMM yyyy"
                    )
            );
        }
    }


    private JPanel createDateHeading(String dateText) {

        JPanel panel = new JPanel(
                new BorderLayout(12, 0)
        );

        panel.setOpaque(false);

        panel.setBorder(
                new EmptyBorder(12, 2, 12, 2)
        );


        JLabel label = new JLabel(dateText);

        label.setFont(
                new Font("SansSerif", Font.BOLD, 15)
        );

        label.setForeground(NOTICES_DEEP_GREEN);


        JSeparator separator = new JSeparator();

        separator.setForeground(NOTICES_BORDER);

        panel.add(label, BorderLayout.WEST);

        panel.add(separator, BorderLayout.CENTER);

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 45)
        );

        return panel;
    }


    // =====================================================
    // NOTICE CARD
    // =====================================================

    private JPanel createNoticeCard(Object[] notice) {

        String titleText = getValue(
                notice, 1, "Untitled Notice"
        );

        String contentText = getValue(
                notice, 2, "No notice content available."
        );

        String typeText = getValue(
                notice, 3, "General"
        );

        String targetText = getValue(
                notice, 4, "All"
        );

        String publisherText = getValue(
                notice, 6, "Faculty Administration"
        );

        Timestamp publishedAt =
                notice[5] instanceof Timestamp
                        ? (Timestamp) notice[5]
                        : null;


        String publishedText;

        if (publishedAt != null) {

            publishedText = new SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a"
            ).format(publishedAt);

        } else {

            publishedText = "Date unavailable";
        }


        // Main card
        JPanel card = new JPanel(
                new BorderLayout(0, 12)
        );

        card.setBackground(NOTICES_WHITE);

        card.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                NOTICES_BORDER,
                                1,
                                true
                        ),
                        new EmptyBorder(17, 20, 17, 20)
                )
        );

        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE
                )
        );


        // ---------------- CARD HEADER ----------------

        JPanel cardHeader = new JPanel(
                new BorderLayout(12, 7)
        );

        cardHeader.setOpaque(false);


        JPanel titlePanel = new JPanel(
                new BorderLayout(10, 0)
        );

        titlePanel.setOpaque(false);


        JLabel cardTitle = new JLabel(titleText);

        cardTitle.setFont(
                new Font("SansSerif", Font.BOLD, 16)
        );

        cardTitle.setForeground(NOTICES_DEEP_GREEN);


        JLabel typeBadge = createTypeBadge(typeText);


        titlePanel.add(
                cardTitle,
                BorderLayout.CENTER
        );

        titlePanel.add(
                typeBadge,
                BorderLayout.EAST
        );


        // ---------------- NOTICE INFORMATION ----------------

        JPanel metaPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 0, 0)
        );

        metaPanel.setOpaque(false);


        JLabel targetLabel = createMetaLabel(
                "Target: " + targetText
        );

        JLabel publisherLabel = createMetaLabel(
                "   •   Published by: " + publisherText
        );

        JLabel dateLabel = createMetaLabel(
                "   •   " + publishedText
        );


        metaPanel.add(targetLabel);

        metaPanel.add(publisherLabel);

        metaPanel.add(dateLabel);


        cardHeader.add(
                titlePanel,
                BorderLayout.NORTH
        );

        cardHeader.add(
                metaPanel,
                BorderLayout.SOUTH
        );


        // ---------------- NOTICE CONTENT ----------------

        JTextArea contentArea = new JTextArea(contentText);

        contentArea.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        contentArea.setForeground(NOTICES_DARK_GRAY);

        contentArea.setLineWrap(true);

        contentArea.setWrapStyleWord(true);

        contentArea.setEditable(false);

        contentArea.setFocusable(false);

        contentArea.setOpaque(false);

        contentArea.setRows(3);

        contentArea.setColumns(70);

        contentArea.setBorder(
                new EmptyBorder(2, 0, 2, 0)
        );


        JScrollPane contentScroll = new JScrollPane(
                contentArea
        );

        contentScroll.setBorder(null);

        contentScroll.setOpaque(false);

        contentScroll.getViewport().setOpaque(false);

        contentScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        contentScroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_NEVER
        );

        contentScroll.setPreferredSize(
                new Dimension(500, 65)
        );


        // ---------------- READ MORE BUTTON ----------------

        JButton expandButton = new JButton("Read more");

        expandButton.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        expandButton.setForeground(NOTICES_DEEP_GREEN);

        expandButton.setContentAreaFilled(false);

        expandButton.setBorderPainted(false);

        expandButton.setFocusPainted(false);

        expandButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        expandButton.setAlignmentX(Component.LEFT_ALIGNMENT);


        expandButton.addActionListener(e -> {

            boolean currentlyExpanded =
                    expandButton.getText().equals("Read less");


            if (currentlyExpanded) {

                // Collapse the notice.
                contentArea.setRows(3);

                contentScroll.setPreferredSize(
                        new Dimension(500, 65)
                );

                contentScroll.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_NEVER
                );

                expandButton.setText("Read more");

            } else {

                // Expand the notice.
                int rows = estimateRows(contentText);

                contentArea.setRows(rows);

                int height = Math.min(
                        230,
                        Math.max(65, rows * 19)
                );

                contentScroll.setPreferredSize(
                        new Dimension(500, height)
                );

                contentScroll.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
                );

                expandButton.setText("Read less");
            }

            card.revalidate();

            card.repaint();

            noticeContainer.revalidate();
        });


        JPanel contentPanel = new JPanel();

        contentPanel.setLayout(
                new BoxLayout(
                        contentPanel,
                        BoxLayout.Y_AXIS
                )
        );

        contentPanel.setOpaque(false);

        contentPanel.add(contentScroll);

        contentPanel.add(
                Box.createVerticalStrut(3)
        );

        contentPanel.add(expandButton);


        // Add sections to card.
        card.add(
                cardHeader,
                BorderLayout.NORTH
        );

        card.add(
                contentPanel,
                BorderLayout.CENTER
        );

        return card;
    }


    // =====================================================
    // NOTICE TYPE BADGES
    // =====================================================

    private JLabel createTypeBadge(String type) {

        JLabel badge = new JLabel(type);

        badge.setFont(
                new Font("SansSerif", Font.BOLD, 11)
        );

        badge.setOpaque(true);

        badge.setBorder(
                new EmptyBorder(5, 10, 5, 10)
        );


        String value = type.toLowerCase();


        if (value.equals("emergency")) {

            badge.setBackground(new Color(255, 235, 238));

            badge.setForeground(new Color(183, 28, 28));

        } else if (value.equals("exam")) {

            badge.setBackground(new Color(227, 242, 253));

            badge.setForeground(new Color(21, 101, 192));

        } else if (value.equals("academic")) {

            badge.setBackground(new Color(232, 245, 233));

            badge.setForeground(new Color(27, 94, 32));

        } else if (value.equals("attendance")) {

            badge.setBackground(new Color(255, 243, 224));

            badge.setForeground(new Color(230, 126, 34));

        } else if (value.equals("event")) {

            badge.setBackground(new Color(243, 229, 245));

            badge.setForeground(new Color(106, 27, 154));

        } else {

            badge.setBackground(new Color(238, 242, 239));

            badge.setForeground(NOTICES_DARK_GRAY);
        }

        return badge;
    }


    // =====================================================
    // SMALL UI HELPERS
    // =====================================================

    private JLabel createMetaLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(
                new Font("SansSerif", Font.PLAIN, 11)
        );

        label.setForeground(NOTICES_TEXT_GRAY);

        return label;
    }


    private JLabel createMessageLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        label.setForeground(NOTICES_TEXT_GRAY);

        label.setBorder(
                new EmptyBorder(30, 15, 30, 15)
        );

        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }


    private String getValue(
            Object[] values,
            int index,
            String defaultValue
    ) {

        if (values == null
                || index >= values.length
                || values[index] == null) {

            return defaultValue;
        }

        return String.valueOf(values[index]);
    }


    private int estimateRows(String content) {

        if (content == null || content.isEmpty()) {
            return 3;
        }

        int estimatedRows = (int) Math.ceil(
                content.length() / 85.0
        );

        int actualLines = content.split("\\R", -1).length;

        return Math.max(
                3,
                Math.max(estimatedRows, actualLines)
        );
    }
}
