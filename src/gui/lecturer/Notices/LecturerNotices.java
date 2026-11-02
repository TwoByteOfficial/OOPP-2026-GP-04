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
import java.util.List;

import static gui.lecturer.SharedComponents.Colors.NOTICES_BACKGROUND;
import static gui.lecturer.SharedComponents.Colors.NOTICES_BORDER;
import static gui.lecturer.SharedComponents.Colors.NOTICES_DEEP_GREEN;
import static gui.lecturer.SharedComponents.Colors.NOTICES_DARK_GRAY;
import static gui.lecturer.SharedComponents.Colors.NOTICES_TEXT_GRAY;
import static gui.lecturer.SharedComponents.Colors.NOTICES_WHITE;

public class LecturerNotices extends JPanel {
    private final NoticeDAO noticeDAO = new NoticeDAO();

    private final JComboBox<String> typeFilterCombo = new JComboBox<>(new String[]{
            "All", "General", "Academic", "Exam", "Attendance", "Event", "Emergency"
    });
    private final JTextField searchField = new JTextField(18);

    private final JPanel noticeContainer = new JPanel();
    private final JLabel statusLabel = new JLabel("Ready");

    public LecturerNotices() {
        setLayout(new BorderLayout(0, 20));
        setBackground(NOTICES_BACKGROUND);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Heading
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);

        JLabel title = new JLabel("Faculty Announcements & Notices");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(NOTICES_DEEP_GREEN);

        JLabel subtitle = new JLabel("View active academic, examination, and administrative notices published by faculty management.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(NOTICES_TEXT_GRAY);

        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        controlPanel.setOpaque(false);

        JLabel typeLabel = new JLabel("Notice Type:");
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        typeLabel.setForeground(NOTICES_DEEP_GREEN);

        typeFilterCombo.setPreferredSize(new Dimension(160, 36));
        typeFilterCombo.addActionListener(e -> loadNotices());

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        searchLabel.setForeground(NOTICES_DEEP_GREEN);

        searchField.setPreferredSize(new Dimension(220, 36));

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        searchBtn.setBackground(NOTICES_DEEP_GREEN);
        searchBtn.setForeground(NOTICES_WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> loadNotices());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadNotices());

        controlPanel.add(typeLabel);
        controlPanel.add(typeFilterCombo);
        controlPanel.add(searchLabel);
        controlPanel.add(searchField);
        controlPanel.add(searchBtn);
        controlPanel.add(refreshBtn);

        JPanel topContainer = new JPanel(new BorderLayout(0, 15));
        topContainer.setOpaque(false);
        topContainer.add(heading, BorderLayout.NORTH);
        topContainer.add(controlPanel, BorderLayout.SOUTH);

        // Notice list container inside scroll pane
        noticeContainer.setLayout(new BoxLayout(noticeContainer, BoxLayout.Y_AXIS));
        noticeContainer.setBackground(NOTICES_BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(noticeContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(statusLabel, BorderLayout.WEST);

        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        loadNotices();
    }

    private void loadNotices() {
        noticeContainer.removeAll();
        statusLabel.setText("Loading notices...");

        String typeFilter = (String) typeFilterCombo.getSelectedItem();
        String query = searchField.getText().trim();

        try {
            List<Object[]> notices = noticeDAO.getAllNotices(typeFilter, query);

            if (notices.isEmpty()) {
                JLabel emptyLabel = new JLabel("No notices found matching your search criteria.");
                emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
                emptyLabel.setForeground(NOTICES_TEXT_GRAY);
                emptyLabel.setBorder(new EmptyBorder(20, 10, 20, 10));
                noticeContainer.add(emptyLabel);
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                for (Object[] n : notices) {
                    String titleStr = (String) n[1];
                    String contentStr = (String) n[2];
                    String typeStr = (String) n[3];
                    String targetStr = (String) n[4];
                    Timestamp pubAt = (Timestamp) n[5];

                    JPanel card = new JPanel(new BorderLayout(0, 10));
                    card.setBackground(NOTICES_WHITE);
                    card.setBorder(new CompoundBorder(
                            new LineBorder(NOTICES_BORDER, 1, true),
                            new EmptyBorder(16, 20, 16, 20)
                    ));
                    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
                    card.setAlignmentX(Component.LEFT_ALIGNMENT);

                    // Card Header
                    JPanel cardHeader = new JPanel(new BorderLayout());
                    cardHeader.setOpaque(false);

                    JLabel cardTitle = new JLabel(titleStr);
                    cardTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
                    cardTitle.setForeground(NOTICES_DEEP_GREEN);

                    String metaText = "[" + typeStr + "] • Target: " + targetStr + " • Published: " + (pubAt != null ? dateFormat.format(pubAt) : "N/A");
                    JLabel cardMeta = new JLabel(metaText);
                    cardMeta.setFont(new Font("SansSerif", Font.ITALIC, 12));
                    cardMeta.setForeground(NOTICES_TEXT_GRAY);

                    cardHeader.add(cardTitle, BorderLayout.WEST);
                    cardHeader.add(cardMeta, BorderLayout.EAST);

                    // Card Content
                    JTextArea contentArea = new JTextArea(contentStr);
                    contentArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    contentArea.setForeground(NOTICES_DARK_GRAY);
                    contentArea.setLineWrap(true);
                    contentArea.setWrapStyleWord(true);
                    contentArea.setEditable(false);
                    contentArea.setOpaque(false);

                    card.add(cardHeader, BorderLayout.NORTH);
                    card.add(contentArea, BorderLayout.CENTER);

                    noticeContainer.add(card);
                    noticeContainer.add(Box.createVerticalStrut(15));
                }
            }

            statusLabel.setText("Total Published Notices: " + notices.size());

        } catch (SQLException e) {
            statusLabel.setText("Error loading notices.");
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        noticeContainer.revalidate();
        noticeContainer.repaint();
    }
}
