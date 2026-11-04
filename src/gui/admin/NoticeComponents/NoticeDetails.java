package gui.admin.NoticeComponents;

import model.Notice;
import javax.swing.*;
import java.awt.*;

public class NoticeDetails extends JDialog {

    public NoticeDetails(Frame parent, Notice notice) {
        super(parent, "Official Announcement Details", true);
        setSize(650, 520);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setBackground(new Color(0x1B, 0x5E, 0x20));
        JLabel lblHeader = new JLabel("Notice: " + notice.getTitle());
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setBackground(Color.WHITE);
        pnlContent.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        pnlContent.add(createMetaRow("Notice ID:", "NTC-" + notice.getNoticeId()));
        pnlContent.add(createMetaRow("Type:", notice.getNoticeType() != null ? notice.getNoticeType() : "General"));
        pnlContent.add(createMetaRow("Target Role:", notice.getTargetRole() != null ? notice.getTargetRole() : "All Roles"));
        pnlContent.add(createMetaRow("Published Date:", notice.getPublishedAt() != null ? notice.getPublishedAt().toString() : "N/A"));
        pnlContent.add(createMetaRow("Status:", notice.getStatus() != null ? notice.getStatus() : "Published"));
        pnlContent.add(Box.createVerticalStrut(12));

        JLabel lblBodyTitle = new JLabel("Announcement Body:");
        lblBodyTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblBodyTitle.setForeground(Color.BLACK);
        pnlContent.add(lblBodyTitle);
        pnlContent.add(Box.createVerticalStrut(6));

        JTextArea txtBody = new JTextArea(notice.getContent() != null ? notice.getContent() : "");
        txtBody.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtBody.setEditable(false);
        txtBody.setLineWrap(true);
        txtBody.setWrapStyleWord(true);
        txtBody.setBackground(new Color(0xF8, 0xF9, 0xFA));
        txtBody.setForeground(new Color(0x21, 0x25, 0x29));
        JScrollPane sp = new JScrollPane(txtBody);
        sp.setPreferredSize(new Dimension(580, 160));
        sp.setBorder(BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1));
        pnlContent.add(sp);

        add(pnlContent, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        footerPanel.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnClose.setBackground(new Color(0x38, 0x8E, 0x3C));
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> dispose());
        footerPanel.add(btnClose);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createMetaRow(String label, String value) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(Color.BLACK);
        l.setPreferredSize(new Dimension(140, 22));
        p.add(l);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        v.setForeground(new Color(0x1B, 0x5E, 0x20));
        p.add(v);
        return p;
    }
}
