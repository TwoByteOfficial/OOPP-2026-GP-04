package gui.admin;

import dao.admin.AdminNoticeDAO;
import gui.admin.NoticeComponents.*;
import model.Notice;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class AdminNotices extends JPanel {

    private final long userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    private AdminNoticeDAO noticeDAO;
    private List<Notice> masterList = new ArrayList<>();

    private NoticeTitleBar titleBar;
    private NoticeFilterSet filterSet;
    private NoticeActions actionToolbar;
    private NoticeList noticeListView;

    public AdminNotices(long userId, String username, String fullName, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;

        setLayout(null);
        setBounds(0, 0, 1660, 1080);
        setBackground(new Color(0xE8, 0xF5, 0xE9));

        noticeDAO = new AdminNoticeDAO();

        // Title Bar
        titleBar = new NoticeTitleBar();
        titleBar.setLocation(20, 20);
        add(titleBar);

        // Filter Bar
        filterSet = new NoticeFilterSet(this::applyFilters);
        filterSet.setBounds(20, 120, 1620, 60);
        add(filterSet);

        // Actions Toolbar
        actionToolbar = new NoticeActions(
                this::openAddDialog,
                this::openViewDialog,
                this::openEditDialog,
                this::deleteNotice,
                this::togglePublishStatus,
                this::loadNoticesFromDB
        );
        actionToolbar.setBounds(20, 190, 1620, 60);
        add(actionToolbar);

        // Notice Table View
        noticeListView = new NoticeList();
        noticeListView.setBounds(20, 260, 1620, 780);
        add(noticeListView);

        loadNoticesFromDB();
    }

    private void loadNoticesFromDB() {
        try {
            masterList = noticeDAO.getAllNotices();
            titleBar.updateNoticeCount(masterList.size());
            applyFilters();
        } catch (Exception e) {
            System.err.println("[AdminNotices Error] Could not load notices: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        String query = filterSet.getSearchQuery().toLowerCase();
        String type = filterSet.getSelectedType();
        String targetRole = filterSet.getSelectedRole();

        List<Notice> filtered = new ArrayList<>();
        for (Notice n : masterList) {
            boolean matchesSearch = query.isEmpty()
                    || (n.getTitle() != null && n.getTitle().toLowerCase().contains(query))
                    || (n.getContent() != null && n.getContent().toLowerCase().contains(query));

            boolean matchesType = "All Types".equalsIgnoreCase(type)
                    || (n.getNoticeType() != null && n.getNoticeType().equalsIgnoreCase(type));

            boolean matchesRole = "All Roles".equalsIgnoreCase(targetRole)
                    || (n.getTargetRole() != null && n.getTargetRole().equalsIgnoreCase(targetRole));

            if (matchesSearch && matchesType && matchesRole) {
                filtered.add(n);
            }
        }
        noticeListView.setNotices(filtered);
    }

    private void openAddDialog() {
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        NoticeForm form = new NoticeForm(topFrame, noticeDAO, null, userId, this::loadNoticesFromDB);
        form.setVisible(true);
    }

    private void openViewDialog() {
        Notice selected = noticeListView.getSelectedNotice();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a notice to view.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        NoticeDetails details = new NoticeDetails(topFrame, selected);
        details.setVisible(true);
    }

    private void openEditDialog() {
        Notice selected = noticeListView.getSelectedNotice();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a notice to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        NoticeForm form = new NoticeForm(topFrame, noticeDAO, selected, userId, this::loadNoticesFromDB);
        form.setVisible(true);
    }

    private void togglePublishStatus() {
        Notice selected = noticeListView.getSelectedNotice();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a notice to toggle status.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String current = selected.getStatus();
        String nextStatus = "Published".equalsIgnoreCase(current) ? "Draft" : "Published";
        try {
            noticeDAO.updateNoticeStatus(selected.getNoticeId(), nextStatus);
            JOptionPane.showMessageDialog(this, "Notice status updated to " + nextStatus + "!", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
            loadNoticesFromDB();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error toggling status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteNotice() {
        Notice selected = noticeListView.getSelectedNotice();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a notice to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete notice ID NTC-" + selected.getNoticeId() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            try {
                noticeDAO.deleteNotice(selected.getNoticeId());
                JOptionPane.showMessageDialog(this, "Notice deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadNoticesFromDB();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting notice: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}