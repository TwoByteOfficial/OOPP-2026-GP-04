package gui.admin.NoticeComponents;

import dao.admin.AdminNoticeDAO;
import gui.admin.AdminTheme;
import model.Notice;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class NoticeActions extends JPanel {

    private final AddNoticeComponent btnAdd;
    private final ViewNoticeComponent btnView;
    private final EditNoticeComponent btnEdit;
    private final DeleteNoticeComponent btnDelete;

    public NoticeActions(AdminNoticeDAO noticeDAO, long currentAdminUserId, Supplier<Notice> selectedNoticeSupplier, Runnable onRefreshNeeded) {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        setBackground(AdminTheme.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));

        btnAdd = new AddNoticeComponent(noticeDAO, currentAdminUserId, onRefreshNeeded);
        btnView = new ViewNoticeComponent(selectedNoticeSupplier);
        btnEdit = new EditNoticeComponent(selectedNoticeSupplier, noticeDAO, onRefreshNeeded);
        btnDelete = new DeleteNoticeComponent(selectedNoticeSupplier, noticeDAO, onRefreshNeeded);

        add(btnAdd);
        add(btnView);
        add(btnEdit);
        add(btnDelete);
    }
}
