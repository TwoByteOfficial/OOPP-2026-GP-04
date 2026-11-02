package gui.undergraduate.dashboard.bottom;

import dao.undergraduate.UndergraduateTimetableDAO;
import dao.undergraduate.UndergraduateTimetableDAO.TimetableSlotDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

/**
 * Undergraduate Today's Timetable Card connected to database schedule.
 */
public class StudentTimetablePanel extends JPanel {

    private final JPanel listPanel;

    public StudentTimetablePanel(Consumer<String> navigationCallback) {
        setLayout(new BorderLayout(0, 12));
        setBackground(AdminTheme.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD2, 0xD7, 0xE1), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Today's Lecture Schedule");
        title.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 17));
        title.setForeground(AdminTheme.DEEP_GREEN);

        JButton viewAllBtn = new JButton("Full Timetable →");
        viewAllBtn.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 13));
        viewAllBtn.setForeground(AdminTheme.FOREST_GREEN);
        viewAllBtn.setBorder(null);
        viewAllBtn.setContentAreaFilled(false);
        viewAllBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewAllBtn.addActionListener(e -> {
            if (navigationCallback != null) navigationCallback.accept("timetables");
        });

        header.add(title, BorderLayout.WEST);
        header.add(viewAllBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        add(listPanel, BorderLayout.CENTER);
    }

    public void loadTodaySchedule(long studentId) {
        listPanel.removeAll();
        List<TimetableSlotDTO> slots = UndergraduateTimetableDAO.getStudentWeeklyTimetable(studentId);

        String today = LocalDate.now().getDayOfWeek().toString(); // e.g. MONDAY
        int added = 0;

        for (TimetableSlotDTO s : slots) {
            if (s.dayOfWeek.equalsIgnoreCase(today) || (added < 2 && slots.size() <= 2)) {
                listPanel.add(createScheduleItem(s.timeRange, s.courseCode + " - " + s.courseName, s.venue + " • " + s.lecturerName));
                listPanel.add(Box.createVerticalStrut(8));
                added++;
            }
        }

        // If no lectures today, display first 2 upcoming sessions
        if (added == 0) {
            for (TimetableSlotDTO s : slots) {
                if (added < 2) {
                    listPanel.add(createScheduleItem(s.dayOfWeek + " • " + s.timeRange, s.courseCode + " - " + s.courseName, s.venue + " • " + s.lecturerName));
                    listPanel.add(Box.createVerticalStrut(8));
                    added++;
                }
            }
        }

        if (added == 0) {
            JLabel empty = new JLabel("No scheduled lecture sessions found.");
            empty.setFont(AdminTheme.font(Font.PLAIN, 12));
            empty.setForeground(AdminTheme.MUTED);
            listPanel.add(empty);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createScheduleItem(String time, String subject, String venue) {
        JPanel p = new JPanel(new BorderLayout(10, 2));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 13));
        timeLabel.setForeground(AdminTheme.FOREST_GREEN);

        JLabel subj = new JLabel(subject);
        subj.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 14));
        subj.setForeground(AdminTheme.DEEP_GREEN);

        JLabel ven = new JLabel(venue);
        ven.setFont(new Font(AdminTheme.FONT_FAMILY, Font.PLAIN, 12));
        ven.setForeground(AdminTheme.MUTED);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(subj);
        info.add(Box.createVerticalStrut(2));
        info.add(ven);

        p.add(timeLabel, BorderLayout.WEST);
        p.add(info, BorderLayout.CENTER);
        return p;
    }
}