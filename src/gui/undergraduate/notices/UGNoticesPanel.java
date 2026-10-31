package gui.undergraduate.notices;

import dao.undergraduate.UndergraduateNoticeDAO;
import dao.undergraduate.UndergraduateNoticeDAO.NoticeItemDTO;
import gui.admin.AdminTheme;
import gui.undergraduate.notices.filter.NoticeTypeFilterBar;
import gui.undergraduate.notices.header.NoticesHeaderPanel;
import gui.undergraduate.notices.list.NoticesListPanel;
import gui.undergraduate.notices.model.NoticeData;
import gui.undergraduate.notices.reader.NoticeReaderDialog;
import gui.undergraduate.notices.summary.NoticesSummaryStripPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Module 1.7: Official Notices & Circulars view coordinator connected directly to database.
 */
public class UGNoticesPanel extends JPanel {

    private final long userId;
    private final String username;
    private final NoticesSummaryStripPanel summaryStrip;
    private final NoticesListPanel listPanel;
    private final List<NoticeData> masterNotices = new ArrayList<>();

    public UGNoticesPanel(long userId, String username) {
        this.userId = userId;
        this.username = username;

        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // 1. Top Section: Header + Metric Strip
        JPanel topWrapper = new JPanel(new BorderLayout(0, 14));
        topWrapper.setOpaque(false);
        topWrapper.add(new NoticesHeaderPanel(), BorderLayout.NORTH);

        summaryStrip = new NoticesSummaryStripPanel();
        topWrapper.add(summaryStrip, BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        // 2. Center Section: Filter Bar on Top + Notice Cards Feed
        JPanel centerWrapper = new JPanel(new BorderLayout(0, 12));
        centerWrapper.setOpaque(false);

        NoticeTypeFilterBar filterBar = new NoticeTypeFilterBar(this::filterNotices);
        centerWrapper.add(filterBar, BorderLayout.NORTH);

        listPanel = new NoticesListPanel(this::openNoticeReader);
        centerWrapper.add(listPanel, BorderLayout.CENTER);

        add(centerWrapper, BorderLayout.CENTER);

        // Fetch live database records
        loadDatabaseData();
    }

    private void loadDatabaseData() {
        summaryStrip.updateCounts(UndergraduateNoticeDAO.getNoticeCounts());

        List<NoticeItemDTO> notices = UndergraduateNoticeDAO.getActiveNotices();
        masterNotices.clear();

        for (NoticeItemDTO n : notices) {
            masterNotices.add(new NoticeData(
                    n.noticeId,
                    n.title,
                    n.content,
                    n.noticeType,
                    n.targetRole,
                    n.publishedAt,
                    n.publisher
            ));
        }

        filterNotices("All Notices");
    }

    private void filterNotices(String category) {
        if ("All Notices".equalsIgnoreCase(category)) {
            listPanel.setNotices(masterNotices);
        } else {
            List<NoticeData> filtered = masterNotices.stream()
                    .filter(n -> n.getNoticeType().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
            listPanel.setNotices(filtered);
        }
    }

    private void openNoticeReader(NoticeData data) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        NoticeReaderDialog dialog = new NoticeReaderDialog(owner, data);
        dialog.setVisible(true);
    }
}