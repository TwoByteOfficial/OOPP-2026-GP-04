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
 */
public class UGTimetablePanel extends JPanel {

    private final long userId;
    private final String username;
    private final TimetableSummaryStripPanel summaryStripPanel;
    private final TimetableDirectoryPanel directoryPanel;
    private final UpcomingLectureCard detailsCard;
    private final List<TimetableEntryData> masterList = new ArrayList<>();
    private List<TimetableEntryData> currentFilteredList = new ArrayList<>();

    public UGTimetablePanel(long userId, String username) {
        this.userId = userId;
        this.username = username;

        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // 1. Top Section: Header + Metric Strip
        JPanel topWrapper = new JPanel(new BorderLayout(0, 14));
        topWrapper.setOpaque(false);
        topWrapper.add(new TimetableHeaderPanel(), BorderLayout.NORTH);

        summaryStripPanel = new TimetableSummaryStripPanel();
        topWrapper.add(summaryStripPanel, BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        // 2. Center Section: Left Table + Right Details Card
        detailsCard = new UpcomingLectureCard();
        detailsCard.setPreferredSize(new Dimension(340, 0));

        directoryPanel = new TimetableDirectoryPanel(rowIndex -> {
            if (rowIndex >= 0 && rowIndex < currentFilteredList.size()) {
                detailsCard.updateSession(currentFilteredList.get(rowIndex));
            }
        });

        DayFilterBar filterBar = new DayFilterBar(this::filterByDay);

        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setOpaque(false);
        leftPanel.add(filterBar, BorderLayout.NORTH);
        leftPanel.add(directoryPanel, BorderLayout.CENTER);

        JPanel splitPanel = new JPanel(new BorderLayout(16, 0));
        splitPanel.setOpaque(false);
        splitPanel.add(leftPanel, BorderLayout.CENTER);
        splitPanel.add(detailsCard, BorderLayout.EAST);
        add(splitPanel, BorderLayout.CENTER);

        // Fetch live database records
        loadDatabaseData();
    }

    private void loadDatabaseData() {
        summaryStripPanel.updateSummary(UndergraduateTimetableDAO.getTimetableSummary(userId));

        List<TimetableSlotDTO> slots = UndergraduateTimetableDAO.getStudentWeeklyTimetable(userId);
        masterList.clear();

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

        filterByDay("All Days");
    }

    private void filterByDay(String day) {
        if ("All Days".equals(day)) {
            currentFilteredList = new ArrayList<>(masterList);
        } else {
            currentFilteredList = masterList.stream()
                    .filter(e -> e.getDayOfWeek().equalsIgnoreCase(day))
                    .collect(Collectors.toList());
        }
        directoryPanel.setEntries(currentFilteredList);
        directoryPanel.selectFirstRow();
        if (!currentFilteredList.isEmpty()) {
            detailsCard.updateSession(currentFilteredList.get(0));
        }
    }
}