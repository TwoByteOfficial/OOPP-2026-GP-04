package gui.undergraduate.dashboard.bottom;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * 1.1.5.2 - Undergraduate Notices Card
 */
public class LatestNoticesPanel extends JPanel {

    public LatestNoticesPanel(Consumer<String> navigationCallback) {
        setLayout(new BorderLayout(0, 12));
        setBackground(AdminTheme.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD2, 0xD7, 0xE1), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        // Header Section
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

        // Notices List
        JPanel listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        listPanel.add(createNoticeItem("Semester I Examination Schedule", "Draft examination timetable has been published.", "2026-09-10"));
        listPanel.add(Box.createVerticalStrut(8));
        listPanel.add(createNoticeItem("Medical Submissions Deadline", "Submit within 7 days of missed attendance sessions.", "2026-09-08"));

        add(listPanel, BorderLayout.CENTER);
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

        JLabel desc = new JLabel(snippet);
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