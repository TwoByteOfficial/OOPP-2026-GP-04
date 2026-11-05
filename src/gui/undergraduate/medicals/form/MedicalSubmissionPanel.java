package gui.undergraduate.medicals.form;

import dao.undergraduate.UndergraduateCourseDAO;
import dao.undergraduate.UndergraduateCourseDAO.EnrolledCourseDTO;
import dao.undergraduate.UndergraduateMedicalDAO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Student medical certificate submission form with an interactive Graphical Calendar Dialog.
 * Enforces a strict 14-day maximum medical coverage limit by disabling out-of-range dates.
 */
public class MedicalSubmissionPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);
    private static final Color INPUT_BORDER_COLOR = new Color(0xCF, 0xD8, 0xDC);

    private final long userId;
    private final JComboBox<CourseDropdownItem> courseComboBox;
    private final JComboBox<String> dateComboBox;
    private final JTextField startDateField;
    private final JTextField endDateField;
    private final JTextField reasonField;
    private final JTextField urlField;
    private final Runnable onSubmittedCallback;

    public static class CourseDropdownItem {
        public final int courseId;
        public final String courseCode;
        public final String courseName;

        public CourseDropdownItem(int courseId, String courseCode, String courseName) {
            this.courseId = courseId;
            this.courseCode = courseCode;
            this.courseName = courseName;
        }

        @Override
        public String toString() {
            return courseCode + " - " + courseName;
        }
    }

    public MedicalSubmissionPanel(long userId, Runnable onSubmittedCallback) {
        this.userId = userId;
        this.onSubmittedCallback = onSubmittedCallback;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(18, 20, 18, 20));

        // 1. Header Group
        JPanel headerGroup = new JPanel();
        headerGroup.setOpaque(false);
        headerGroup.setLayout(new BoxLayout(headerGroup, BoxLayout.Y_AXIS));
        headerGroup.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel formTitle = new JLabel("Submit Medical Certificate");
        formTitle.setFont(AdminTheme.font(Font.BOLD, 15));
        formTitle.setForeground(AdminTheme.DEEP_GREEN);

        JLabel helperText = new JLabel("Provide cloud document URL. Certificates must be submitted within 14 days of absence.");
        helperText.setFont(AdminTheme.font(Font.PLAIN, 12));
        helperText.setForeground(AdminTheme.MUTED);

        headerGroup.add(formTitle);
        headerGroup.add(Box.createVerticalStrut(3));
        headerGroup.add(helperText);
        add(headerGroup);

        add(Box.createVerticalStrut(14));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(0xEAEAEA));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(sep);
        add(Box.createVerticalStrut(14));

        // 2. Course Dropdown
        courseComboBox = new JComboBox<>();
        courseComboBox.setFont(AdminTheme.font(Font.PLAIN, 12));
        courseComboBox.setBackground(Color.WHITE);
        courseComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        add(createField("Target Course Module:", courseComboBox));
        add(Box.createVerticalStrut(12));

        // 3. Absence Date Dropdown
        dateComboBox = new JComboBox<>();
        dateComboBox.setFont(AdminTheme.font(Font.PLAIN, 12));
        dateComboBox.setBackground(Color.WHITE);
        dateComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        add(createField("Absence Date (Lecture Session - Within Past 14 Days):", dateComboBox));
        add(Box.createVerticalStrut(12));

        courseComboBox.addActionListener(e -> {
            CourseDropdownItem selected = (CourseDropdownItem) courseComboBox.getSelectedItem();
            if (selected != null) {
                loadLectureDates(selected.courseId);
            }
        });

        // 4. Graphical Interactive Calendar Fields (Start Date & End Date)
        LocalDate today = LocalDate.now();
        startDateField = createReadOnlyDateField(today.toString());
        endDateField = createReadOnlyDateField(today.toString());

        JButton startCalBtn = createCalendarButton();
        startCalBtn.addActionListener(e -> {
            LocalDate initial = parseFieldDate(startDateField.getText(), today);
            // Start date can be picked within past 14 days up to today
            LocalDate picked = CalendarDialog.showPicker(
                    SwingUtilities.getWindowAncestor(this),
                    initial,
                    today.minusDays(14),
                    today,
                    "Select Medical Start Date"
            );
            if (picked != null) {
                startDateField.setText(picked.toString());
                // Validate if current end date is now before start date or exceeds 14 days
                LocalDate curEnd = parseFieldDate(endDateField.getText(), picked);
                if (curEnd.isBefore(picked) || ChronoUnit.DAYS.between(picked, curEnd) >= 14) {
                    endDateField.setText(picked.toString());
                }
            }
        });

        JButton endCalBtn = createCalendarButton();
        endCalBtn.addActionListener(e -> {
            LocalDate start = parseFieldDate(startDateField.getText(), today);
            LocalDate initial = parseFieldDate(endDateField.getText(), start);
            // Strict Rule: End Date cannot be before Start Date, and cannot exceed Start Date + 13 days (Max 14 Days)
            LocalDate maxAllowedEnd = start.plusDays(13);

            LocalDate picked = CalendarDialog.showPicker(
                    SwingUtilities.getWindowAncestor(this),
                    initial,
                    start,
                    maxAllowedEnd,
                    "Select Medical End Date (Max 14 Days Allowed)"
            );
            if (picked != null) {
                endDateField.setText(picked.toString());
            }
        });

        JPanel startPickerPanel = new JPanel(new BorderLayout(5, 0));
        startPickerPanel.setOpaque(false);
        startPickerPanel.add(startDateField, BorderLayout.CENTER);
        startPickerPanel.add(startCalBtn, BorderLayout.EAST);

        JPanel endPickerPanel = new JPanel(new BorderLayout(5, 0));
        endPickerPanel.setOpaque(false);
        endPickerPanel.add(endDateField, BorderLayout.CENTER);
        endPickerPanel.add(endCalBtn, BorderLayout.EAST);

        JPanel dateRangeGroup = new JPanel(new GridLayout(1, 2, 12, 0));
        dateRangeGroup.setOpaque(false);
        dateRangeGroup.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        dateRangeGroup.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateRangeGroup.add(createField("Medical Start Date (Calendar):", startPickerPanel));
        dateRangeGroup.add(createField("Medical End Date (Max 14 Days):", endPickerPanel));
        add(dateRangeGroup);
        add(Box.createVerticalStrut(12));

        // Auto-sync start date when absence date changes
        dateComboBox.addActionListener(e -> {
            String selectedDate = (String) dateComboBox.getSelectedItem();
            if (selectedDate != null && !selectedDate.startsWith("No ")) {
                try {
                    LocalDate ld = LocalDate.parse(selectedDate);
                    startDateField.setText(ld.toString());
                    endDateField.setText(ld.toString());
                } catch (Exception ignored) {}
            }
        });

        loadEnrolledCourses();

        // 5. Diagnosis / Reason
        reasonField = new JTextField();
        reasonField.setFont(AdminTheme.font(Font.PLAIN, 12));
        reasonField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER_COLOR, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        reasonField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        add(createField("Diagnosis / Absence Reason:", reasonField));
        add(Box.createVerticalStrut(12));

        // 6. Cloud Link Field & Test Button
        urlField = new JTextField("https://drive.google.com/...");
        urlField.setFont(AdminTheme.font(Font.PLAIN, 12));
        urlField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER_COLOR, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JButton testBtn = new JButton("Test Link");
        testBtn.setFont(AdminTheme.font(Font.BOLD, 11));
        testBtn.setForeground(AdminTheme.DEEP_GREEN);
        testBtn.setBackground(AdminTheme.MINT);
        testBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                new EmptyBorder(5, 12, 5, 12)
        ));
        testBtn.setFocusPainted(false);
        testBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        testBtn.addActionListener(e -> testUrlInBrowser(urlField.getText().trim()));

        JPanel urlInputBox = new JPanel(new BorderLayout(8, 0));
        urlInputBox.setOpaque(false);
        urlInputBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        urlInputBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        urlInputBox.add(urlField, BorderLayout.CENTER);
        urlInputBox.add(testBtn, BorderLayout.EAST);

        add(createField("Medical Certificate Link (Google Drive / Cloud URL):", urlInputBox));
        add(Box.createVerticalStrut(18));

        // 7. Submit Button
        JButton submitBtn = new JButton("Submit Medical Application") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? AdminTheme.DEEP_GREEN : AdminTheme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        submitBtn.setFont(AdminTheme.font(Font.BOLD, 13));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setContentAreaFilled(false);
        submitBtn.setBorderPainted(false);
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.addActionListener(e -> handleSubmission());

        add(submitBtn);
    }

    private JTextField createReadOnlyDateField(String text) {
        JTextField tf = new JTextField(text);
        tf.setEditable(false);
        tf.setBackground(Color.WHITE);
        tf.setFont(AdminTheme.font(Font.BOLD, 12));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER_COLOR, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return tf;
    }

    private JButton createCalendarButton() {
        JButton btn = new JButton("📅 Pick");
        btn.setFont(AdminTheme.font(Font.BOLD, 11));
        btn.setBackground(AdminTheme.MINT);
        btn.setForeground(AdminTheme.DEEP_GREEN);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private LocalDate parseFieldDate(String text, LocalDate fallback) {
        try {
            return LocalDate.parse(text.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    private void loadEnrolledCourses() {
        courseComboBox.removeAllItems();
        List<EnrolledCourseDTO> list = UndergraduateCourseDAO.getEnrolledCoursesWithEvaluation(userId);
        for (EnrolledCourseDTO c : list) {
            courseComboBox.addItem(new CourseDropdownItem(c.courseId, c.courseCode, c.courseName));
        }
        if (courseComboBox.getItemCount() > 0) {
            CourseDropdownItem first = courseComboBox.getItemAt(0);
            loadLectureDates(first.courseId);
        }
    }

    private void loadLectureDates(int courseId) {
        dateComboBox.removeAllItems();
        try {
            List<String> dates = UndergraduateMedicalDAO.getCourseLectureDates(userId, courseId);
            LocalDate today = LocalDate.now();
            LocalDate fourteenDaysAgo = today.minusDays(14);
            int eligibleCount = 0;

            for (String d : dates) {
                try {
                    LocalDate ld = LocalDate.parse(d);
                    if (!ld.isBefore(fourteenDaysAgo) && !ld.isAfter(today)) {
                        dateComboBox.addItem(d);
                        eligibleCount++;
                    }
                } catch (Exception ex) {
                    dateComboBox.addItem(d);
                    eligibleCount++;
                }
            }

            if (eligibleCount == 0) {
                dateComboBox.addItem("No eligible absence dates within past 14 days");
            }
        } catch (Exception e) {
            dateComboBox.addItem("No lecture dates available");
        }
    }

    private void handleSubmission() {
        CourseDropdownItem selected = (CourseDropdownItem) courseComboBox.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an enrolled course module.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedDate = (String) dateComboBox.getSelectedItem();
        String reason = reasonField.getText().trim();
        String docUrl = urlField.getText().trim();

        if (selectedDate == null || selectedDate.isEmpty() || selectedDate.startsWith("No ")) {
            JOptionPane.showMessageDialog(this,
                    "Cannot submit medical: No eligible absence date within the past 14 days selected.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate startLocal = LocalDate.parse(startDateField.getText().trim());
        LocalDate endLocal = LocalDate.parse(endDateField.getText().trim());

        if (endLocal.isBefore(startLocal)) {
            JOptionPane.showMessageDialog(this, "Medical End Date cannot be before Start Date.", "Date Range Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long durationDays = ChronoUnit.DAYS.between(startLocal, endLocal) + 1;
        if (durationDays > 14) {
            JOptionPane.showMessageDialog(this,
                    "Faculty Regulation Limit Exceeded:\nSelected medical duration is " + durationDays + " days.\nMaximum allowed duration is strictly 14 days (2 weeks).",
                    "Limit Exceeded", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (reason.isEmpty() || docUrl.isEmpty() || !docUrl.startsWith("http")) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all fields and provide a valid cloud document URL (http:// or https://)",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String enrichedReason = reason + " [Coverage: " + startLocal + " to " + endLocal + " (" + durationDays + " Days)]";

        boolean success = UndergraduateMedicalDAO.submitMedical(userId, selected.courseId, selectedDate, enrichedReason, docUrl);
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Medical application submitted successfully!\n" +
                            "Duration: " + durationDays + " Day(s) (" + startLocal + " to " + endLocal + ")\n" +
                            "Status: Pending administrative review.",
                    "Submission Successful", JOptionPane.INFORMATION_MESSAGE);
            reasonField.setText("");
            urlField.setText("");
            if (onSubmittedCallback != null) {
                onSubmittedCallback.run();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to submit medical record to database. Verify date format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void testUrlInBrowser(String url) {
        if (url == null || url.trim().isEmpty() || !url.startsWith("http")) {
            JOptionPane.showMessageDialog(this, "Please provide a valid web URL starting with http:// or https://", "Invalid Link", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url.trim()));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to launch default browser: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createField(String labelText, JComponent input) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(AdminTheme.font(Font.BOLD, 12));
        lbl.setForeground(new Color(0x33, 0x33, 0x33));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        input.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        p.add(input);
        return p;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(AdminTheme.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.setColor(CARD_BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.dispose();
        super.paintComponent(g);
    }

    // =========================================================================
    // CUSTOM INTERACTIVE CALENDAR DIALOG WITH 14-DAY STRICT RANGE LIMITATION
    // =========================================================================
    public static class CalendarDialog extends JDialog {
        private YearMonth currentYearMonth;
        private LocalDate selectedDate;
        private final LocalDate minDate;
        private final LocalDate maxDate;
        private final JLabel monthLabel;
        private final JPanel daysPanel;

        public static LocalDate showPicker(Window parent, LocalDate initial, LocalDate min, LocalDate max, String title) {
            CalendarDialog dialog = new CalendarDialog(parent, initial, min, max, title);
            dialog.setVisible(true);
            return dialog.selectedDate;
        }

        private CalendarDialog(Window parent, LocalDate initial, LocalDate min, LocalDate max, String title) {
            super(parent, title, ModalityType.APPLICATION_MODAL);
            this.selectedDate = null;
            this.minDate = min;
            this.maxDate = max;
            this.currentYearMonth = YearMonth.from(initial != null ? initial : LocalDate.now());

            setSize(360, 360);
            setLocationRelativeTo(parent);
            setResizable(false);
            setLayout(new BorderLayout(10, 10));
            getContentPane().setBackground(Color.WHITE);

            // Month navigation bar
            JPanel navPanel = new JPanel(new BorderLayout());
            navPanel.setBackground(AdminTheme.PRIMARY);
            navPanel.setBorder(new EmptyBorder(8, 12, 8, 12));

            JButton prevBtn = new JButton("◀");
            prevBtn.setForeground(Color.WHITE);
            prevBtn.setContentAreaFilled(false);
            prevBtn.setBorderPainted(false);
            prevBtn.setFocusPainted(false);
            prevBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            prevBtn.addActionListener(e -> {
                currentYearMonth = currentYearMonth.minusMonths(1);
                updateCalendar();
            });

            JButton nextBtn = new JButton("▶");
            nextBtn.setForeground(Color.WHITE);
            nextBtn.setContentAreaFilled(false);
            nextBtn.setBorderPainted(false);
            nextBtn.setFocusPainted(false);
            nextBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            nextBtn.addActionListener(e -> {
                currentYearMonth = currentYearMonth.plusMonths(1);
                updateCalendar();
            });

            monthLabel = new JLabel("", SwingConstants.CENTER);
            monthLabel.setFont(AdminTheme.font(Font.BOLD, 14));
            monthLabel.setForeground(Color.WHITE);

            navPanel.add(prevBtn, BorderLayout.WEST);
            navPanel.add(monthLabel, BorderLayout.CENTER);
            navPanel.add(nextBtn, BorderLayout.EAST);
            add(navPanel, BorderLayout.NORTH);

            // Week header + Day grid
            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.setOpaque(false);

            JPanel weekHeader = new JPanel(new GridLayout(1, 7, 2, 2));
            weekHeader.setOpaque(false);
            String[] days = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
            for (String d : days) {
                JLabel l = new JLabel(d, SwingConstants.CENTER);
                l.setFont(AdminTheme.font(Font.BOLD, 12));
                l.setForeground(AdminTheme.DEEP_GREEN);
                weekHeader.add(l);
            }
            centerPanel.add(weekHeader, BorderLayout.NORTH);

            daysPanel = new JPanel(new GridLayout(6, 7, 3, 3));
            daysPanel.setOpaque(false);
            daysPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
            centerPanel.add(daysPanel, BorderLayout.CENTER);
            add(centerPanel, BorderLayout.CENTER);

            updateCalendar();
        }

        private void updateCalendar() {
            monthLabel.setText(currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            daysPanel.removeAll();

            LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
            int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0
            int daysInMonth = currentYearMonth.lengthOfMonth();

            // Leading blanks
            for (int i = 0; i < dayOfWeek; i++) {
                daysPanel.add(new JLabel(""));
            }

            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate date = currentYearMonth.atDay(day);
                JButton dayBtn = new JButton(String.valueOf(day));
                dayBtn.setFont(AdminTheme.font(Font.PLAIN, 11));
                dayBtn.setMargin(new Insets(2, 2, 2, 2));
                dayBtn.setFocusPainted(false);

                // STRICT RULE: Disable dates outside [minDate, maxDate]
                boolean isAllowed = true;
                if (minDate != null && date.isBefore(minDate)) isAllowed = false;
                if (maxDate != null && date.isAfter(maxDate)) isAllowed = false;

                if (isAllowed) {
                    dayBtn.setBackground(new Color(0xE8, 0xF5, 0xE9));
                    dayBtn.setForeground(AdminTheme.DEEP_GREEN);
                    dayBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    dayBtn.addActionListener(e -> {
                        selectedDate = date;
                        dispose();
                    });
                } else {
                    // Visually disabled and unclickable for dates exceeding 14-day rule
                    dayBtn.setEnabled(false);
                    dayBtn.setBackground(new Color(0xF5, 0xF5, 0xF5));
                    dayBtn.setForeground(Color.LIGHT_GRAY);
                }

                daysPanel.add(dayBtn);
            }

            // Fill trailing grid slots
            int totalSlots = dayOfWeek + daysInMonth;
            for (int i = totalSlots; i < 42; i++) {
                daysPanel.add(new JLabel(""));
            }

            daysPanel.revalidate();
            daysPanel.repaint();
        }
    }
}