package gui.admin.NoticeComponents;

import dao.admin.AdminNoticeDAO;
import gui.admin.AdminTheme;
import model.Notice;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class DeleteNoticeComponent extends JButton {

    private final Supplier<Notice> selectedNoticeSupplier;
    private final AdminNoticeDAO noticeDAO;
    private final Runnable onSuccess;

    public DeleteNoticeComponent(Supplier<Notice> selectedNoticeSupplier, AdminNoticeDAO noticeDAO, Runnable onSuccess) {
        super("Delete Notice");
        this.selectedNoticeSupplier = selectedNoticeSupplier;
        this.noticeDAO = noticeDAO;
        this.onSuccess = onSuccess;

        setFont(AdminTheme.FONT_BODY_BOLD);
        setBackground(AdminTheme.DANGER);
        setForeground(AdminTheme.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> openDeleteNoticeWindow());
    }

    private void openDeleteNoticeWindow() {
        Notice selected = selectedNoticeSupplier.get();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an announcement from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        Frame owner = (parentWindow instanceof Frame) ? (Frame) parentWindow : null;
        DeleteNoticeConfirmModal dialog = new DeleteNoticeConfirmModal(owner, noticeDAO, selected, onSuccess);
        dialog.setVisible(true);
    }

    // Modal Confirmation Window Class embedded in the same component file
    public static class DeleteNoticeConfirmModal extends JDialog {

        private final AdminNoticeDAO noticeDAO;
        private final Notice noticeToDelete;
        private final Runnable onSuccess;

        public DeleteNoticeConfirmModal(Frame parent, AdminNoticeDAO dao, Notice notice, Runnable onSuccess) {
            super(parent, "Confirm Notice Deletion", true);
            this.noticeDAO = dao;
            this.noticeToDelete = notice;
            this.onSuccess = onSuccess;

            setSize(520, 260);
            setLocationRelativeTo(parent);
            setResizable(false);
            getContentPane().setBackground(AdminTheme.WHITE);
            setLayout(new BorderLayout());

            // Header
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
            headerPanel.setBackground(AdminTheme.DANGER);
            JLabel lblHeader = new JLabel("Delete Announcement");
            lblHeader.setFont(AdminTheme.FONT_H3);
            lblHeader.setForeground(AdminTheme.WHITE);
            headerPanel.add(lblHeader);
            add(headerPanel, BorderLayout.NORTH);

            // Message content
            JPanel bodyPanel = new JPanel();
            bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
            bodyPanel.setBackground(AdminTheme.WHITE);
            bodyPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

            JLabel lblMsg = new JLabel("Are you sure you want to permanently delete this notice?");
            lblMsg.setFont(AdminTheme.FONT_BODY_BOLD);
            lblMsg.setForeground(AdminTheme.BLACK);

            JLabel lblNoticeTitle = new JLabel("" + noticeToDelete.getTitle() + "");
            lblNoticeTitle.setFont(AdminTheme.FONT_BODY);
            lblNoticeTitle.setForeground(AdminTheme.DEEP_GREEN);

            JLabel lblWarning = new JLabel("This action cannot be undone.");
            lblWarning.setFont(AdminTheme.FONT_SMALL);
            lblWarning.setForeground(AdminTheme.MUTED);

            bodyPanel.add(lblMsg);
            bodyPanel.add(Box.createVerticalStrut(8));
            bodyPanel.add(lblNoticeTitle);
            bodyPanel.add(Box.createVerticalStrut(8));
            bodyPanel.add(lblWarning);

            add(bodyPanel, BorderLayout.CENTER);

            // Footer Actions
            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
            footerPanel.setBackground(AdminTheme.WHITE);
            footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AdminTheme.BORDER));

            JButton btnConfirmDelete = new JButton("Yes, Delete");
            btnConfirmDelete.setFont(AdminTheme.FONT_BODY_BOLD);
            btnConfirmDelete.setBackground(AdminTheme.DANGER);
            btnConfirmDelete.setForeground(AdminTheme.WHITE);
            btnConfirmDelete.setFocusPainted(false);
            btnConfirmDelete.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
            btnConfirmDelete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnConfirmDelete.addActionListener(e -> executeDelete());

            JButton btnCancel = new JButton("Cancel");
            btnCancel.setFont(AdminTheme.FONT_BODY_BOLD);
            btnCancel.setBackground(AdminTheme.PASTEL_GREEN);
            btnCancel.setForeground(AdminTheme.DEEP_GREEN);
            btnCancel.setFocusPainted(false);
            btnCancel.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
            btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnCancel.addActionListener(e -> dispose());

            footerPanel.add(btnConfirmDelete);
            footerPanel.add(btnCancel);
            add(footerPanel, BorderLayout.SOUTH);
        }

        private void executeDelete() {
            try {
                boolean deleted = noticeDAO.deleteNotice(noticeToDelete.getNoticeId());
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Notice deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    if (onSuccess != null) onSuccess.run();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete notice from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting notice: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
