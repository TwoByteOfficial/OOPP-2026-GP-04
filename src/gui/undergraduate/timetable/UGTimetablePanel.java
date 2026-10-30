package gui.undergraduate.timetable;

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
 * Module 1.6: Weekly Academic Timetable view coordinator.
 * Coordinates weekly schedule filtering, table directory, and classroom inspection card.
 */
public class UGTimetablePanel extends JPanel {

    private final TimetableDirectoryPanel directoryPanel;
    private final UpcomingLectureCard detailsCard;
    private final List<TimetableEntryData> masterList = new ArrayList<>();
    private List<TimetableEntryData> currentFilteredList = new ArrayList<>();

    public UGTimetablePanel(long userId, String username) {
        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        initSampleData();

        // 1. Top Section: Header + Metric Strip
        JPanel topWrapper = new JPanel(new BorderLayout(0, 14));
        topWrapper.setOpaque(false);
        topWrapper.add(new TimetableHeaderPanel(), BorderLayout.NORTH);
        topWrapper.add(new TimetableSummaryStripPanel(), BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        // 2. Center Section: Left Panel (Table) gets full remaining width, Details Card gets fixed 330px
        detailsCard = new UpcomingLectureCard();
        detailsCard.setPreferredSize(new Dimension(340, 0)); // 340px

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

        // Initial populate
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

    private void initSampleData() {
        // Matches DB courses: ICT2112 to ICT2182 & Lecturers: Dr. N. Perera, Ms. S. Fernando, etc.
        masterList.add(new TimetableEntryData(1, "Monday", "08:00 - 10:00", "ICT2112", "Data Structures & Algorithms", "Theory", "Lecture Hall 01", "Dr. N. Perera"));
        masterList.add(new TimetableEntryData(2, "Monday", "10:15 - 12:15", "ICT2162", "Software Engineering", "Theory", "Lecture Hall 02", "Dr. R. Jayasuriya"));
        masterList.add(new TimetableEntryData(3, "Tuesday", "08:00 - 10:00", "ICT2142", "Database Systems", "Theory", "Lecture Hall 01", "Ms. S. Fernando"));
        masterList.add(new TimetableEntryData(4, "Tuesday", "13:00 - 15:00", "ICT2142", "Database Systems Lab", "Practical", "Specialized Lab 02", "Ms. S. Fernando"));
        masterList.add(new TimetableEntryData(5, "Wednesday", "08:00 - 10:00", "ICT2132", "OOP Practicum", "Theory", "Lecture Hall 03", "Dr. N. Perera"));
        masterList.add(new TimetableEntryData(6, "Wednesday", "13:00 - 15:00", "ICT2132", "OOP Practicum Lab", "Practical", "Software Lab 01", "Dr. N. Perera"));
        masterList.add(new TimetableEntryData(7, "Thursday", "08:00 - 10:00", "ICT2122", "Computer Networks", "Theory", "Lecture Hall 02", "Mr. A. Gunawardena"));
        masterList.add(new TimetableEntryData(8, "Thursday", "13:00 - 15:00", "ICT2122", "Computer Networks Lab", "Practical", "Hardware/Net Lab", "Mr. A. Gunawardena"));
        masterList.add(new TimetableEntryData(9, "Friday", "08:00 - 10:00", "ICT2172", "Probability & Statistics", "Theory", "Lecture Hall 01", "Dr. R. Jayasuriya"));
        masterList.add(new TimetableEntryData(10, "Friday", "10:15 - 12:15", "ICT2182", "Technical Communication", "Theory", "Main Auditorium", "Ms. T. Karunaratne"));
    }
}