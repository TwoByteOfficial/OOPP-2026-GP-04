package gui.admin.NoticeComponents;

import dao.admin.AdminNoticeDAO;
import gui.admin.AdminTheme;
import model.Notice;

import javax.swing.*;
import java.awt.*;

public class AddNoticeComponent extends JButton {

    public AddNoticeComponent(Frame owner, AdminNoticeDAO dao, long adminUserId, Runnable onSuccess) {
        super("+ Post New Notice");
        setFont(AdminTheme.FONT_BODY_BOLD);
        setBackground(AdminTheme.FOREST_GREEN);
        setForeground(AdminTheme.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> {
            AddNoticeModal dialog = new AddNoticeModal(owner, dao, adminUserId, onSuccess);
            dialog.setVisible(true);
        });
    }

    public static class AddNoticeModal extends JDialog {
        private JTextField txtTitle;
        private JTextArea txtContent;
        private JComboBox<String> cmbType;
        private JComboBox<String> cmbRole;

        public AddNoticeModal(Frame owner, AdminNoticeDAO dao, long adminUserId, Runnable onSuccess) {
            super(owner, "Post New Official Notice", true);
            setSize(680, 560);
            setLocationRelativeTo(owner);
            setResizable(false);
            setLayout(null);
            getContentPane().setBackground(AdminTheme.WHITE);

            JLabel lblHeader = new JLabel("Post Announcement");
            lblHeader.setFont(AdminTheme.FONT_H2);
            lblHeader.setForeground(AdminTheme.DEEP_GREEN);
            lblHeader.setBounds(24, 20, 400, 32);
            add(lblHeader);

            JLabel l1 = new JLabel("Title:"); l1.setFont(AdminTheme.FONT_TINY); l1.setBounds(24, 70, 120, 28); add(l1);
            txtTitle = new JTextField(); txtTitle.setFont(AdminTheme.FONT_SMALL); txtTitle.setBounds(150, 70, 480, 32); add(txtTitle);

            JLabel l2 = new JLabel("Content:"); l2.setFont(AdminTheme.FONT_TINY); l2.setBounds(24, 120, 120, 28); add(l2);
            txtContent = new JTextArea(); txtContent.setFont(AdminTheme.FONT_SMALL); txtContent.setLineWrap(true); txtContent.setWrapStyleWord(true);
            JScrollPane sp = new JScrollPane(txtContent); sp.setBounds(150, 120, 480, 160); add(sp);

            JLabel l3 = new JLabel("Notice Type:"); l3.setFont(AdminTheme.FONT_TINY); l3.setBounds(24, 300, 120, 28); add(l3);
            cmbType = new JComboBox<>(new String[]{ "General", "Academic", "Exam", "Emergency" });
            cmbType.setFont(AdminTheme.FONT_SMALL); cmbType.setBounds(150, 300, 480, 32); add(cmbType);

            JLabel l4 = new JLabel("Target Role:"); l4.setFont(AdminTheme.FONT_TINY); l4.setBounds(24, 350, 120, 28); add(l4);
            cmbRole = new JComboBox<>(new String[]{ "All Roles", "Undergraduate", "Lecturer", "Technical Officer", "Admin" });
            cmbRole.setFont(AdminTheme.FONT_SMALL); cmbRole.setBounds(150, 350, 480, 32); add(cmbRole);

            JButton btnSave = new JButton("Post Notice");
            btnSave.setFont(AdminTheme.FONT_BODY_BOLD); btnSave.setBackground(AdminTheme.FOREST_GREEN); btnSave.setForeground(AdminTheme.WHITE);
            btnSave.setBounds(370, 440, 130, 40);
            btnSave.addActionListener(ev -> {
                try {
                    String title = txtTitle.getText().trim();
                    String content = txtContent.getText().trim();
                    if (title.isEmpty() || content.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Title and Content are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Notice n = new Notice();
                    n.setTitle(title);
                    n.setContent(content);
                    n.setNoticeType((String) cmbType.getSelectedItem());
                    n.setTargetRole((String) cmbRole.getSelectedItem());
                    n.setPublishedBy(adminUserId);
                    n.setStatus("Published");

                    dao.createNotice(n);
                    JOptionPane.showMessageDialog(this, "Notice published successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    if (onSuccess != null) onSuccess.run();
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            add(btnSave);

            JButton btnCancel = new JButton("Cancel");
            btnCancel.setFont(AdminTheme.FONT_BODY_BOLD); btnCancel.setBackground(AdminTheme.PASTEL_GREEN); btnCancel.setForeground(AdminTheme.DEEP_GREEN);
            btnCancel.setBounds(510, 440, 120, 40);
            btnCancel.addActionListener(ev -> dispose());
            add(btnCancel);
        }
    }
}
