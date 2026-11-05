package gui.undergraduate.dashboard.bottom;

import dao.undergraduate.UndergraduateTimetableDAO;
import dao.undergraduate.UndergraduateTimetableDAO.TimetableSlotDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Today's Lecture Schedule widget anchored to NORTH to wrap cards tightly.
 * Header typography and colors match Attendance Distribution exactly (15px Bold, AdminTheme.PRIMARY).
 * Fetches and displays today's scheduled lectures or fallback slots for the undergraduate student.
 */
public class StudentTimetablePanel extends JPanel {

    // Cards container panel and navigation action consumer
    private final JPanel cardsContainer;
    private final Consumer<String> onNavigate;

    public StudentTimetablePanel(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        setLayout(new BorderLayout());
        setOpaque(false);

        // -------------------------------------------------------------
        // Outer Rounded Card Container
        // -------------------------------------------------------------
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw rounded white background card
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Draw subtle card border
                g2.setColor(new Color(225, 235, 228));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        // -------------------------------------------------------------
        // Top Header Row (Title & Full Timetable Navigation Button)
        // -------------------------------------------------------------
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);

        // Header title (15px Bold & AdminTheme.PRIMARY) matching dashboard design standards
        JLabel titleLbl = new JLabel("Today's Lecture Schedule");
        titleLbl.setFont(AdminTheme.font(Font.BOLD, 15));
        titleLbl.setForeground(AdminTheme.PRIMARY);

        // "Full Timetable" button directing users to the complete timetable module view
        JButton viewAllBtn = new JButton("Full Timetable →");
        viewAllBtn.setFont(AdminTheme.font(Font.BOLD, 11));
        viewAllBtn.setForeground(AdminTheme.PRIMARY);
        viewAllBtn.setBorderPainted(false);
        viewAllBtn.setContentAreaFilled(false);
        viewAllBtn.setFocusPainted(false);
        viewAllBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewAllBtn.addActionListener(e -> {
            if (onNavigate != null) onNavigate.accept("timetables");
        });

        headerRow.add(titleLbl, BorderLayout.WEST);
        headerRow.add(viewAllBtn, BorderLayout.EAST);
        card.add(headerRow, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // Cards List Container
        // -------------------------------------------------------------
        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);
        card.add(cardsContainer, BorderLayout.CENTER);

        // Anchored to NORTH so white background tightly terminates after the last card
        add(card, BorderLayout.NORTH);
    }

    /**
     * Asynchronously fetches weekly timetable slots via DAO, filters for today's day of week,
     * and handles fallback cases using SwingWorker.
     */
    public void loadTodaySchedule(long studentId) {
        SwingWorker<List<TimetableSlotDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<TimetableSlotDTO> doInBackground() {
                try {
                    // Fetch full weekly schedule slots for the student
                    List<TimetableSlotDTO> allSlots = UndergraduateTimetableDAO.getStudentWeeklyTimetable(studentId);
                    String currentDay = LocalDate.now().getDayOfWeek().toString();

                    // Filter slots matching today's system day of week
                    List<TimetableSlotDTO> todaySlots = new ArrayList<>();
                    for (TimetableSlotDTO s : allSlots) {
                        if (s.dayOfWeek != null && s.dayOfWeek.equalsIgnoreCase(currentDay)) {
                            todaySlots.add(s);
                        }
                    }

                    // Fallback to all slots if none found for today
                    return todaySlots.isEmpty() ? allSlots : todaySlots;
                } catch (Exception e) {
                    return new ArrayList<>();
                }
            }

            @Override
            protected void done() {
                try {
                    updateTimetable(get());
                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    /**
     * Populates timetable card items or empty state text into the UI container.
     */
    public void updateTimetable(List<TimetableSlotDTO> slots) {
        cardsContainer.removeAll();

        if (slots == null || slots.isEmpty()) {
            JLabel emptyLbl = new JLabel("No scheduled lectures for today.");
            emptyLbl.setFont(AdminTheme.font(Font.PLAIN, 12));
            emptyLbl.setForeground(new Color(120, 140, 130));
            emptyLbl.setBorder(new EmptyBorder(10, 4, 10, 4));
            cardsContainer.add(emptyLbl);
        } else {
            // Display a maximum of 3 scheduled items on the dashboard widget view
            int displayCount = Math.min(slots.size(), 3);
            for (int i = 0; i < displayCount; i++) {
                if (i > 0) cardsContainer.add(Box.createVerticalStrut(6));
                cardsContainer.add(createCompactLectureCard(slots.get(i)));
            }
        }
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    /**
     * Factory method creating a compact lecture card showing time range, course code, venue, and instructor.
     */
    private JPanel createCompactLectureCard(TimetableSlotDTO slot) {
        JPanel rowCard = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw rounded white sub-card background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                // Draw subtle card border
                g2.setColor(new Color(215, 235, 222));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        rowCard.setOpaque(false);
        rowCard.setBorder(new EmptyBorder(6, 12, 6, 12));
        rowCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // Left: Time Range label
        JLabel timeLbl = new JLabel(slot.timeRange != null ? slot.timeRange : "00:00 - 00:00");
        timeLbl.setFont(AdminTheme.font(Font.BOLD, 12));
        timeLbl.setForeground(AdminTheme.PRIMARY);
        timeLbl.setPreferredSize(new Dimension(90, 0));
        rowCard.add(timeLbl, BorderLayout.WEST);

        // Center: Course Title & Venue/Lecturer metadata stack
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel courseLbl = new JLabel(slot.courseCode + " - " + slot.courseName);
        courseLbl.setFont(AdminTheme.font(Font.BOLD, 12));
        courseLbl.setForeground(new Color(30, 41, 59));

        String venueStr = slot.venue != null ? slot.venue : "Lecture Hall";
        String lecturerStr = slot.lecturerName != null ? slot.lecturerName : "Academic Staff";
        JLabel locationLbl = new JLabel(venueStr + " • " + lecturerStr);
        locationLbl.setFont(AdminTheme.font(Font.PLAIN, 11));
        locationLbl.setForeground(new Color(110, 130, 120));

        infoPanel.add(courseLbl);
        infoPanel.add(Box.createVerticalStrut(1));
        infoPanel.add(locationLbl);

        rowCard.add(infoPanel, BorderLayout.CENTER);
        return rowCard;
    }
}