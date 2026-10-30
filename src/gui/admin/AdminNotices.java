package gui.admin;

import dao.admin.AdminNoticeDAO;
import gui.admin.NoticeComponents.*;
import model.Notice;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminNotices extends JPanel {

    private final AdminNoticeDAO noticeDAO;
    private final long currentAdminUserId;

    private NoticeTitleBar titleBar;
    private NoticeFilterBar filterBar;
    private NoticeActions actionToolbar;
    private NoticeList tableContainer;
    private NoticeTable noticeTable;

    public AdminNotices(long userId, String username, String fullName, String email, String role) {
        this.currentAdminUserId = userId;
        this.noticeDAO = new AdminNoticeDAO();

        setLayout(null);
        setBackground(AdminTheme.LIGHT_BG);
        setBounds(30, 30, 1600, 900);

        initUI();
        loadNotices();
    }

    private void initUI() {
        titleBar = new NoticeTitleBar();
        titleBar.setBounds(20, 20, 1620, 80);
        add(titleBar);

        filterBar = new NoticeFilterBar(this::onFilterChanged, this::loadNotices);
        filterBar.setBounds(20, 110, 1620, 60);
        add(filterBar);

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);

        noticeTable = new NoticeTable();
        tableContainer = new NoticeList(noticeTable);
        tableContainer.setBounds(20, 250, 1620, 680);
        add(tableContainer);

        actionToolbar = new NoticeActions(owner, noticeDAO, currentAdminUserId, noticeTable::getSelectedNotice, this::loadNotices);
        actionToolbar.setBounds(20, 180, 1620, 56);
        add(actionToolbar);
    }

    private void loadNotices() {
        try {
            List<Notice> list = noticeDAO.getAllNotices();
            noticeTable.setNotices(list);
            titleBar.updateCount(list.size());
        } catch (Exception e) {
            System.err.println("[AdminNotices] Error loading notices: " + e.getMessage());
        }
    }

    private void onFilterChanged() {
        String query = filterBar.getSearchQuery().toLowerCase();
        String typeFilter = filterBar.getSelectedType();
        String roleFilter = filterBar.getSelectedRole();

        try {
            List<Notice> list = noticeDAO.getAllNotices();
            List<Notice> filtered = list.stream().filter(n -> {
                boolean mQuery = query.isEmpty() ||
                        (n.getTitle() != null && n.getTitle().toLowerCase().contains(query)) ||
                        (n.getContent() != null && n.getContent().toLowerCase().contains(query));
                boolean mType = ("All Types".equalsIgnoreCase(typeFilter) || typeFilter.equalsIgnoreCase(n.getNoticeType()));
                boolean mRole = ("All Roles".equalsIgnoreCase(roleFilter) || "All Roles".equalsIgnoreCase(n.getTargetRole()) || roleFilter.equalsIgnoreCase(n.getTargetRole()));
                return mQuery && mType && mRole;
            }).toList();

            noticeTable.setNotices(filtered);
            titleBar.updateCount(filtered.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}