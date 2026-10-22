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
    private NoticeFilterSet filterSet;
    private NoticeActions actionToolbar;
    private NoticeList tableContainer;
    private NoticeTable noticeTable;

    public AdminNotices(long userId, String username, String fullName, String email, String role) {
        this.currentAdminUserId = userId;
        this.noticeDAO = new AdminNoticeDAO();

        setLayout(new BorderLayout(0, 12));
        setBackground(new Color(0xE8, 0xF5, 0xE9));
        setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

        initUI();
        loadNotices();
    }

    private void initUI() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        titleBar = new NoticeTitleBar();
        topPanel.add(titleBar);
        topPanel.add(Box.createVerticalStrut(10));

        filterSet = new NoticeFilterSet(this::onFilterChanged);
        topPanel.add(filterSet);
        topPanel.add(Box.createVerticalStrut(10));

        actionToolbar = new NoticeActions(
                this::onAddNotice,
                this::onViewDetails,
                this::onEditNotice,
                this::onDeleteNotice,
                this::onTogglePublish,
                this::loadNotices
        );
        topPanel.add(actionToolbar);

        add(topPanel, BorderLayout.NORTH);

        noticeTable = new NoticeTable();
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
        String query = filterSet.getSearchQuery().toLowerCase();
        String typeFilter = filterSet.getSelectedType();
        String roleFilter = filterSet.getSelectedRole();

        try {
            List<Notice> list = noticeDAO.getAllNotices();
            List<Notice> filtered = list.stream().filter(n -> {
                boolean mQuery = query.isEmpty() ||
                        (n.getTitle() != null && n.getTitle().toLowerCase().contains(query)) ||
                        (n.getContent() != null && n.getContent().toLowerCase().contains(query));
                boolean mType = ("All Types".equalsIgnoreCase(typeFilter) || typeFilter.equalsIgnoreCase(n.getNoticeType()));
                boolean mRole = ("All Roles".equalsIgnoreCase(roleFilter) || roleFilter.equalsIgnoreCase(n.getTargetRole()));
                return mQuery && mType && mRole;
            }).toList();

            noticeTable.setNotices(filtered);
            titleBar.updateCount(filtered.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onAddNotice() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        NoticeForm form = new NoticeForm(owner, noticeDAO, null, currentAdminUserId, this::loadNotices);
        form.setVisible(true);
    }

    private void onViewDetails() {
        Notice selected = noticeTable.getSelectedNotice();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an announcement from the table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        NoticeDetails details = new NoticeDetails(owner, selected);
        details.setVisible(true);
    }

    private void onEditNotice() {
        Notice selected = noticeTable.getSelectedNotice();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an announcement from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        NoticeForm form = new NoticeForm(owner, noticeDAO, selected, currentAdminUserId, this::loadNotices);
        form.setVisible(true);
    }

    private void onDeleteNotice() {
        Notice selected = noticeTable.getSelectedNotice();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an announcement from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete notice '" + selected.getTitle() + "'?",
                "Delete Notice", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                noticeDAO.deleteNotice(selected.getNoticeId());
                JOptionPane.showMessageDialog(this, "Notice deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadNotices();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting notice: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onTogglePublish() {
        Notice selected = noticeTable.getSelectedNotice();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an announcement from the table to toggle status.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentStatus = selected.getStatus();
        String newStatus = "Published".equalsIgnoreCase(currentStatus) ? "Draft" : "Published";

        try {
            noticeDAO.updateNoticeStatus(selected.getNoticeId(), newStatus);
            JOptionPane.showMessageDialog(this, "Notice status updated to '" + newStatus + "'.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadNotices();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating status: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
