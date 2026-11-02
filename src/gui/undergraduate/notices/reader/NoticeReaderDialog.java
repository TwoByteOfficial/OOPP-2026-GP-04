package gui.undergraduate.notices.reader;

import gui.admin.AdminTheme;
import gui.undergraduate.notices.model.NoticeData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Clean modal dialog displaying full notice body and publication metadata.
 */
public class NoticeReaderDialog extends JDialog {

    public NoticeReaderDialog(Window owner, NoticeData data) {
        super(owner, "Official Notice Details", ModalityType.APPLICATION_MODAL);
        setSize(560, 430);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(0, 14));
        main.setBackground(AdminTheme.WHITE);
        main.setBorder(new EmptyBorder(22, 24, 18, 24));

        // 1. Header Area
        JPanel head = new JPanel(new BorderLayout(0, 6));
        head.setOpaque(false);

        JLabel catLbl = new JLabel(data.getNoticeType().toUpperCase() + " BULLETIN");
        catLbl.setFont(AdminTheme.font(Font.BOLD, 12));
        catLbl.setForeground(AdminTheme.FOREST_GREEN);

        JLabel titleLbl = new JLabel("<html><b>" + data.getTitle() + "</b></html>");
        titleLbl.setFont(AdminTheme.font(Font.BOLD, 16));
        titleLbl.setForeground(AdminTheme.DEEP_GREEN);

        JLabel metaLbl = new JLabel("Published by: " + data.getPublisher() + "  •  Date: " + data.getPublishedAt());
        metaLbl.setFont(AdminTheme.font(Font.PLAIN, 12));
        metaLbl.setForeground(AdminTheme.MUTED);

        head.add(catLbl, BorderLayout.NORTH);
        head.add(titleLbl, BorderLayout.CENTER);
        head.add(metaLbl, BorderLayout.SOUTH);
        main.add(head, BorderLayout.NORTH);

        // 2. Body Text
        JTextArea contentArea = new JTextArea(data.getContent());
        contentArea.setFont(AdminTheme.font(Font.PLAIN, 13));
        contentArea.setForeground(new Color(0x2B, 0x2B, 0x2B));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        contentArea.setOpaque(false);
        contentArea.setBorder(new EmptyBorder(8, 0, 8, 0));

        JScrollPane scroll = new JScrollPane(contentArea);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0xEAEAEA)));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        main.add(scroll, BorderLayout.CENTER);

        // 3. Footer Close Button
        JPanel foot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        foot.setOpaque(false);

        JButton closeBtn = new JButton("Close Bulletin");
        closeBtn.setFont(AdminTheme.font(Font.BOLD, 13));
        closeBtn.setBackground(AdminTheme.FOREST_GREEN);
        closeBtn.setForeground(AdminTheme.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setPreferredSize(new Dimension(130, 34));
        closeBtn.addActionListener(e -> dispose());
        foot.add(closeBtn);

        main.add(foot, BorderLayout.SOUTH);
        setContentPane(main);
    }
}