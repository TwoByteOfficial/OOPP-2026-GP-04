package gui.admin.NoticeComponents;

import dao.admin.AdminNoticeDAO;
import gui.admin.AdminTheme;
import model.Notice;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class DeleteNoticeComponent extends JButton {

    public DeleteNoticeComponent(Component parent, AdminNoticeDAO dao, Supplier<Notice> noticeSupplier, Runnable onSuccess) {
        super("Delete Notice");
        setFont(AdminTheme.FONT_BODY_BOLD);
        setBackground(AdminTheme.DANGER);
        setForeground(AdminTheme.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> {
            Notice selected = noticeSupplier.get();
            if (selected == null) {
                JOptionPane.showMessageDialog(parent, "Please select an announcement to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int choice = JOptionPane.showConfirmDialog(parent,
                    "Are you sure you want to delete notice '" + selected.getTitle() + "'?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                try {
                    dao.deleteNotice(selected.getNoticeId());
                    JOptionPane.showMessageDialog(parent, "Notice deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    if (onSuccess != null) onSuccess.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parent, "Error deleting: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
