package gui.undergraduate.medicals.form;

import dao.undergraduate.UndergraduateCourseDAO;
import dao.undergraduate.UndergraduateCourseDAO.EnrolledCourseDTO;
import dao.undergraduate.UndergraduateMedicalDAO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;
import java.util.List;

/**
 * Student medical certificate submission form complying strictly with Zero Local File Handling.
 * Dynamically binds absence dates to the selected course and student attendance history.
 * Collects diagnosis details and certified cloud document links (e.g., Google Drive)
 * for academic administration and Technical Officer (TO) verification.
 */
public class MedicalSubmissionPanel extends JPanel {

    // Card styling constants for rounded geometry and border strokes
    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);
    private static final Color INPUT_BORDER_COLOR = new Color(0xCF, 0xD8, 0xDC);

    // Authenticated user session state and form input components
    private final long userId;
    private final JComboBox<CourseDropdownItem> courseComboBox;
    private final JComboBox<String> dateComboBox;
    private final JTextField reasonField;
    private final JTextField urlField;
    private final Runnable onSubmittedCallback;

    /**
     * Helper model representing selectable course items inside the target course dropdown.
     */
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

    /**
     * Constructs the medical certificate submission form and binds event listeners.
     */
    public MedicalSubmissionPanel(long userId, Runnable onSubmittedCallback) {
        this.userId = userId;
        this.onSubmittedCallback = onSubmittedCallback;

        // Base container layout configuration and inner card padding
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(18, 20, 18, 20));

        // -------------------------------------------------------------
        // 1. Header Row (Form Title & Helper Guidance Caption)
        // -------------------------------------------------------------
        JPanel headerGroup = new JPanel();
        headerGroup.setOpaque(false);
        headerGroup.setLayout(new BoxLayout(headerGroup, BoxLayout.Y_AXIS));
        headerGroup.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel formTitle = new JLabel("Submit Medical Certificate");
        formTitle.setFont(AdminTheme.font(Font.BOLD, 15));
        formTitle.setForeground(AdminTheme.DEEP_GREEN);

        JLabel helperText = new JLabel("Provide cloud document URL certified by university medical officer.");
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

        // -------------------------------------------------------------
        // 2. Target Course Module Dropdown
        // -------------------------------------------------------------
        courseComboBox = new JComboBox<>();
        courseComboBox.setFont(AdminTheme.font(Font.PLAIN, 12));
        courseComboBox.setBackground(Color.WHITE);
        courseComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        add(createField("Target Course Module:", courseComboBox));
        add(Box.createVerticalStrut(12));

        // -------------------------------------------------------------
        // 3. Dynamic Absence Date Dropdown
        // -------------------------------------------------------------
        dateComboBox = new JComboBox<>();
        dateComboBox.setFont(AdminTheme.font(Font.PLAIN, 12));
        dateComboBox.setBackground(Color.WHITE);
        dateComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        add(createField("Absence Date (Lecture Session):", dateComboBox));
        add(Box.createVerticalStrut(12));

        // Event listener to reload dates whenever course selection changes
        courseComboBox.addActionListener(e -> {
            CourseDropdownItem selected = (CourseDropdownItem) courseComboBox.getSelectedItem();
            if (selected != null) {
                loadLectureDates(selected.courseId);
            }
        });

        // Load enrolled courses from database
        loadEnrolledCourses();

        // -------------------------------------------------------------
        // 4. Diagnosis / Absence Reason Input Field
        // -------------------------------------------------------------
        reasonField = new JTextField();
        reasonField.setFont(AdminTheme.font(Font.PLAIN, 12));
        reasonField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER_COLOR, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        reasonField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        add(createField("Diagnosis / Absence Reason:", reasonField));
        add(Box.createVerticalStrut(12));

        // -------------------------------------------------------------
        // 5. Cloud URL Input Field & Browser Test Link Action Button
        // -------------------------------------------------------------
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

        // -------------------------------------------------------------
        // 6. Medical Application Submission Button
        // -------------------------------------------------------------
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

    /**
     * Retrieves student's enrolled courses from database and populates the course dropdown.
     */
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

    /**
     * Loads lecture session dates for the selected course to populate the absence date dropdown.
     */
    private void loadLectureDates(int courseId) {
        dateComboBox.removeAllItems();
        try {
            List<String> dates = UndergraduateMedicalDAO.getCourseLectureDates(userId, courseId);
            for (String d : dates) {
                dateComboBox.addItem(d);
            }
        } catch (Exception e) {
            dateComboBox.addItem("No lecture dates available");
        }
    }

    /**
     * Validates student inputs, verifies cloud URL format, and submits the medical claim to database.
     */
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
            JOptionPane.showMessageDialog(this, "Please select a valid absence date.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (reason.isEmpty() || docUrl.isEmpty() || !docUrl.startsWith("http")) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all fields and provide a valid cloud document URL (http:// or https://)",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = UndergraduateMedicalDAO.submitMedical(userId, selected.courseId, selectedDate, reason, docUrl);
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Medical application submitted successfully!\nStatus set to 'Pending' for TO verification.",
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

    /**
     * Opens the provided URL in the student's default web browser for verification.
     */
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

    /**
     * Helper layout builder creating a vertical label-and-input field group.
     */
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

    /**
     * Paints an anti-aliased rounded white card background and outer perimeter border.
     */
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
}