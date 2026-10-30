package gui.admin.NoticeComponents;

import gui.admin.AdminTheme;
import model.Notice;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class ViewNoticeComponent extends JButton {

    public ViewNoticeComponent(Frame owner, Supplier<Notice> noticeSupplier) {
        super("View Notice");
        setFont(AdminTheme.FONT_BODY_BOLD);
        setBackground(AdminTheme.PASTEL_GREEN);
        setForeground(AdminTheme.DEEP_GREEN);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> {
            Notice selected = noticeSupplier.get();
            if (selected == null) {
                JOptionPane.showMessageDialog(owner, "Please select an announcement to view.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ViewNoticeModal dialog = new ViewNoticeModal(owner, selected);
            dialog.setVisible(true);
        });
    }

    public static class ViewNoticeModal extends JDialog {
        public ViewNoticeModal(Frame owner, Notice notice) {
            super(owner, "Announcement Details", true);
            setSize(620, 460);
            setLocationRelativeTo(owner);
            setLayout(null);
            getContentPane().setBackground(AdminTheme.WHITE);

            JLabel lblTitle = new JLabel("Title: " + notice.getTitle());
            lblTitle.setFont(AdminTheme.FONT_H3);
            lblTitle.setForeground(AdminTheme.DEEP_GREEN);
            lblTitle.setBounds(24, 20, 560, 30);
            add(lblTitle);

            JLabel l1 = new JLabel("Type: " + (notice.getNoticeType() != null ? notice.getNoticeType() : "General"));
            l1.setFont(AdminTheme.FONT_SMALL); l1.setBounds(24, 65, 260, 24); add(l1);

            JLabel l2 = new JLabel("Target Role: " + (notice.getTargetRole() != null ? notice.getTargetRole() : "All Roles"));
            l2.setFont(AdminTheme.FONT_SMALL); l2.setBounds(300, 65, 260, 24); add(l2);

            JTextArea txtBody = new JTextArea(notice.getContent() != null ? notice.getContent() : "");
            txtBody.setFont(AdminTheme.FONT_SMALL); txtBody.setEditable(false); txtBody.setLineWrap(true); txtBody.setWrapStyleWord(true);
            JScrollPane sp = new JScrollPane(txtBody);
            sp.setBounds(24, 105, 550, 240);
            add(sp);

            JButton btnClose = new JButton("Close");
            btnClose.setFont(AdminTheme.FONT_BODY_BOLD); btnClose.setBackground(AdminTheme.FOREST_GREEN); btnClose.setForeground(AdminTheme.WHITE);
            btnClose.setBounds(450, 365, 120, 38);
            btnClose.addActionListener(ev -> dispose());
            add(btnClose);
        }
    }
}
