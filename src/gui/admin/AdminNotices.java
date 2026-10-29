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

        setLayout(new BorderLayout(0, 12));
        setBackground(AdminTheme.MINT);
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        initUI();
        loadNotices();
    }

    private void initUI() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        // 1. Title Bar
        titleBar = new NoticeTitleBar();
        topPanel.add(titleBar);
        topPanel.add(Box.createVerticalStrut(12));

        // 2. Search & Filter Bar (search + type filter + role filter + reset button [left] + refresh button [right])
        filterBar = new NoticeFilterBar(this::onFilterChanged, this::loadNotices);
        topPanel.add(filterBar);
        topPanel.add(Box.createVerticalStrut(12));

        noticeTable = new NoticeTable();

        // 3. Action Toolbar (Post, View, Edit, Delete notice components)
        actionToolbar = new NoticeActions(
                noticeDAO,
                currentAdminUserId,
                noticeTable::getSelectedNotice,
                this::loadNotices
        );
        topPanel.add(actionToolbar);

        add(topPanel, BorderLayout.NORTH);

        // 4. Notice Table List Container
        tableContainer = new NoticeList(noticeTable);
        add(tableContainer, BorderLayout.CENTER);
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

                // Filter matching: "All Roles" notice matches any target role filter, and "All Roles" filter matches any notice
                String targetRole = n.getTargetRole();
                boolean isNoticeAllRoles = (targetRole == null || targetRole.trim().isEmpty() || "All Roles".equalsIgnoreCase(targetRole) || "All".equalsIgnoreCase(targetRole));
                boolean isFilterAllRoles = ("All Roles".equalsIgnoreCase(roleFilter) || "All".equalsIgnoreCase(roleFilter));

                boolean mRole = isFilterAllRoles || isNoticeAllRoles || roleFilter.equalsIgnoreCase(targetRole);

                return mQuery && mType && mRole;
            }).toList();

            noticeTable.setNotices(filtered);
            titleBar.updateCount(filtered.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}