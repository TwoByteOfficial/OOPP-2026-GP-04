package gui.admin.NoticeComponents;

import dao.admin.AdminNoticeDAO;
import gui.admin.AdminTheme;
import model.Notice;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class NoticeActions extends JPanel {

    public NoticeActions(Frame owner, AdminNoticeDAO dao, long adminUserId, Supplier<Notice> noticeSupplier, Runnable onSuccess) {
        setLayout(null);
        setPreferredSize(new Dimension(1620, 56));
        setBackground(AdminTheme.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));

        AddNoticeComponent btnAdd = new AddNoticeComponent(owner, dao, adminUserId, onSuccess);
        btnAdd.setBounds(980, 8, 170, 40); add(btnAdd);

        ViewNoticeComponent btnView = new ViewNoticeComponent(owner, noticeSupplier);
        btnView.setBounds(1160, 8, 140, 40); add(btnView);

        EditNoticeComponent btnEdit = new EditNoticeComponent(owner, dao, noticeSupplier, onSuccess);
        btnEdit.setBounds(1310, 8, 140, 40); add(btnEdit);

        DeleteNoticeComponent btnDelete = new DeleteNoticeComponent(owner, dao, noticeSupplier, onSuccess);
        btnDelete.setBounds(1460, 8, 150, 40); add(btnDelete);
    }
}
