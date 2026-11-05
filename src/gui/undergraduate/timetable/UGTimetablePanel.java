package gui.undergraduate.timetable;

import dao.undergraduate.UndergraduateTimetableDAO;
import dao.undergraduate.UndergraduateTimetableDAO.TimetableSlotDTO;
import gui.admin.AdminTheme;
import gui.undergraduate.timetable.details.UpcomingLectureCard;
import gui.undergraduate.timetable.filter.DayFilterBar;
import gui.undergraduate.timetable.header.TimetableHeaderPanel;
import gui.undergraduate.timetable.model.TimetableEntryData;
import gui.undergraduate.timetable.summary.TimetableSummaryStripPanel;
import gui.undergraduate.timetable.table.TimetableDirectoryPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Module 1.6: Weekly Academic Timetable view coordinator connected directly to database.
 * Anchored to NORTH with synchronized 310px wrapper for optimal lecture detail presentation.
 * Integrates weekly schedule summaries, weekday filtering tabs, interactive directory
 * tables, and real-time logistics cards for selected lecture sessions.
 */
public class UGTimetablePanel extends JPanel {

    // Authenticated student session identifiers
    private final long userId;
    private final String username;

    // Sub-view component references
    private final TimetableSummaryStripPanel summaryStripPanel;
    private final TimetableDirectoryPanel directoryPanel;
    private final UpcomingLectureCard detailsCard;

    // In-memory data caches for master schedule records and active filtered view entries
    private final List<TimetableEntryData> masterList = new ArrayList<>();
    private List<TimetableEntryData> currentFilteredList = new ArrayList<>();

    /**
     * Constructs the weekly academic timetable coordinator panel, configures layouts,
     * registers sub-component event callbacks, and hydrates timetable records from the database.
     *
     * @param userId   Logged-in student account primary key identifier
     * @param username Logged-in student institutional username
     */
    public UGTimetablePanel(long userId, String username) {
        this.userId = userId;
        this.username = username;

        // Base container layout configuration and standard view margin padding
        setLayout(new BorderLayout(0, 14));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 14, 22));

        // -------------------------------------------------------------
        // 1. Top Section: Header + Metric Strip + Weekday Filter Bar
        // -------------------------------------------------------------
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setOpaque(false);

        // Academic schedule title and semester descriptor banner
        TimetableHeaderPanel headerPanel = new TimetableHeaderPanel();
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topWrapper.add(headerPanel);

        topWrapper.add(Box.createVerticalStrut(16));

        // Four-column summary metric cards strip
        summaryStripPanel = new TimetableSummaryStripPanel();
        summaryStripPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryStripPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        topWrapper.add(summaryStripPanel);

        topWrapper.add(Box.createVerticalStrut(14));

        // Weekday selection tabs filter bar
        DayFilterBar filterBar = new DayFilterBar(this::filterByDay);
        filterBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        topWrapper.add(filterBar);

        add(topWrapper, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Center Section: Left Directory Table + Right Details Card
        // -------------------------------------------------------------
        detailsCard = new UpcomingLectureCard();

        // Directory table with row selection listener updating the right logistics card
        directoryPanel = new TimetableDirectoryPanel(rowIndex -> {
            if (rowIndex >= 0 && rowIndex < currentFilteredList.size()) {
                detailsCard.updateSession(currentFilteredList.get(rowIndex));
            }
        });

        // Left wrapper containing the directory table anchored to NORTH
        JPanel leftWrapper = new JPanel(new BorderLayout());
        leftWrapper.setOpaque(false);
        leftWrapper.add(directoryPanel, BorderLayout.NORTH);

        // Right wrapper set to 310px to accommodate expanded section spacing without clipping
        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.setPreferredSize(new Dimension(340, 310));
        rightWrapper.add(detailsCard, BorderLayout.NORTH);

        // Split panel combining left directory and right detail card
        JPanel splitPanel = new JPanel(new BorderLayout(16, 0));
        splitPanel.setOpaque(false);
        splitPanel.add(leftWrapper, BorderLayout.CENTER);
        splitPanel.add(rightWrapper, BorderLayout.EAST);

        // Anchored to NORTH to strictly confine vertical layout and eliminate bottom stretching
        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.setOpaque(false);
        contentContainer.add(splitPanel, BorderLayout.NORTH);

        add(contentContainer, BorderLayout.CENTER);

        // Fetch live database records
        loadDatabaseData();
    }

    /**
     * Retrieves weekly timetable summaries and scheduled lecture slots from the database DAO,
     * maps data transfer objects into domain entities, and applies default weekday filtering.
     */
    private void loadDatabaseData() {
        // Query workload hours, course counts, and practical lab metrics
        summaryStripPanel.updateSummary(UndergraduateTimetableDAO.getTimetableSummary(userId));

        // Query student-specific timetabled sessions
        List<TimetableSlotDTO> slots = UndergraduateTimetableDAO.getStudentWeeklyTimetable(userId);
        masterList.clear();

        // Convert DTOs to UI domain model entities
        for (TimetableSlotDTO s : slots) {
            masterList.add(new TimetableEntryData(
                    s.timetableId,
                    s.dayOfWeek,
                    s.timeRange,
                    s.courseCode,
                    s.courseName,
                    s.component,
                    s.venue,
                    s.lecturerName
            ));
        }

        // Apply default filter to present the full weekly schedule initially
        filterByDay("All Days");
    }

    /**
     * Filters cached schedule sessions by the specified weekday name, updates directory rows,
     * auto-selects the first entry, or renders the empty state when no lectures are scheduled.
     *
     * @param day Selected day string (e.g., 'All Days', 'Monday', 'Friday')
     */
    private void filterByDay(String day) {
        if ("All Days".equalsIgnoreCase(day)) {
            currentFilteredList = new ArrayList<>(masterList);
        } else {
            currentFilteredList = masterList.stream()
                    .filter(e -> e.getDayOfWeek().equalsIgnoreCase(day))
                    .collect(Collectors.toList());
        }
        directoryPanel.setEntries(currentFilteredList);

        // Automatically synchronize the details card with the first visible entry or display free day
        if (!currentFilteredList.isEmpty()) {
            directoryPanel.selectFirstRow();
            detailsCard.updateSession(currentFilteredList.get(0));
        } else {
            detailsCard.showEmptyState();
        }
    }
}