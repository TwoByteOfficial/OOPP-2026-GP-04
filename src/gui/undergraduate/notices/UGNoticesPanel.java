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
 * Serves as the primary controller view for the notices ecosystem, orchestrating summary metrics,
 * category filter tabs, interactive card lists, and popup reader dialogs.
 */
public class UGNoticesPanel extends JPanel {

    // Authenticated session state attributes
    private final long userId;
    private final String username;

    // Sub-view component references
    private final NoticesSummaryStripPanel summaryStrip;
    private final NoticesListPanel listPanel;

    // Master in-memory cache of retrieved notice items for client-side category filtering
    private final List<NoticeData> masterNotices = new ArrayList<>();

    /**
     * Constructs the main notices dashboard view and sets up container layout structures.
     *
     * @param userId   Logged-in undergraduate student identifier
     * @param username Logged-in undergraduate student username
     */
    public UGNoticesPanel(long userId, String username) {
        this.userId = userId;
        this.username = username;

        // Base container layout configuration and standard view margins
        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // -------------------------------------------------------------
        // 1. Top Section: Header + Metric Strip
        // -------------------------------------------------------------
        JPanel topWrapper = new JPanel(new BorderLayout(0, 14));
        topWrapper.setOpaque(false);
        topWrapper.add(new NoticesHeaderPanel(), BorderLayout.NORTH);

        summaryStrip = new NoticesSummaryStripPanel();
        topWrapper.add(summaryStrip, BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Center Section: Filter Bar on Top + Notice Cards Feed
        // -------------------------------------------------------------
        JPanel centerWrapper = new JPanel(new BorderLayout(0, 12));
        centerWrapper.setOpaque(false);

        // Category filter buttons bar linked with the filter callback
        NoticeTypeFilterBar filterBar = new NoticeTypeFilterBar(this::filterNotices);
        centerWrapper.add(filterBar, BorderLayout.NORTH);

        // Scrollable vertical notice list linked with modal reader preview
        listPanel = new NoticesListPanel(this::openNoticeReader);
        centerWrapper.add(listPanel, BorderLayout.CENTER);

        add(centerWrapper, BorderLayout.CENTER);

        // Fetch live database records and populate the view
        loadDatabaseData();
    }

    /**
     * Loads live notices and statistical metrics from the database layer,
     * caches them into the master list, and sets the default view to display all notices.
     */
    private void loadDatabaseData() {
        // Query and update summary counter badges
        summaryStrip.updateCounts(UndergraduateNoticeDAO.getNoticeCounts());

        // Fetch active announcements from database
        List<NoticeItemDTO> notices = UndergraduateNoticeDAO.getActiveNotices();
        masterNotices.clear();

        // Map data transfer objects (DTO) to domain model entities
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

        // Apply default filter to show all records initially
        filterNotices("All Notices");
    }

    /**
     * Filters the cached master notices list by the specified category and refreshes the card feed.
     *
     * @param category Selected category filter name (e.g., 'All Notices', 'Academic', 'Exam')
     */
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

    /**
     * Spawns a modal dialog allowing the student to inspect the complete notice description and publisher metadata.
     *
     * @param data Notice data model containing the selected announcement
     */
    private void openNoticeReader(NoticeData data) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        NoticeReaderDialog dialog = new NoticeReaderDialog(owner, data);
        dialog.setVisible(true);
    }
}