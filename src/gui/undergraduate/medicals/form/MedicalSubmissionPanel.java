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
import java.util.List;

/**
 * Student medical certificate submission form complying strictly with Zero Local File Handling.
 */
public class MedicalSubmissionPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final long userId;
    private final JComboBox<CourseDropdownItem> courseComboBox;
    private final JTextField dateField;
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

        JLabel formTitle = new JLabel("Submit Medical Certificate");
        formTitle.setFont(AdminTheme.font(Font.BOLD, 16));
        formTitle.setForeground(AdminTheme.FOREST_GREEN);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel helperText = new JLabel("Provide cloud document URL certified by university medical officer.");
        helperText.setFont(AdminTheme.font(Font.PLAIN, 12));
        helperText.setForeground(AdminTheme.MUTED);
        helperText.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(formTitle);
        add(Box.createVerticalStrut(2));
        add(helperText);
        add(Box.createVerticalStrut(14));

        courseComboBox = new JComboBox<>();
        courseComboBox.setFont(AdminTheme.font(Font.PLAIN, 13));
        courseComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        loadEnrolledCourses();
        add(createField("Target Course Module:", courseComboBox));
        add(Box.createVerticalStrut(10));

        dateField = new JTextField(LocalDate.now().toString());
        dateField.setFont(AdminTheme.font(Font.PLAIN, 13));
        dateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        add(createField("Absence Date (YYYY-MM-DD):", dateField));
        add(Box.createVerticalStrut(10));

        reasonField = new JTextField();
        reasonField.setFont(AdminTheme.font(Font.PLAIN, 13));
        reasonField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        add(createField("Diagnosis / Absence Reason:", reasonField));
        add(Box.createVerticalStrut(10));

        // Cloud URL Input & Test Link Button (Zero Local File Handling)
        urlField = new JTextField("https://drive.google.com/...");
        urlField.setFont(AdminTheme.font(Font.PLAIN, 12));

        JButton testBtn = new JButton("Test Link");
        testBtn.setFont(AdminTheme.font(Font.BOLD, 11));
        testBtn.setBackground(new Color(0xF1, 0xF5, 0xF9));
        testBtn.setFocusPainted(false);
        testBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        testBtn.addActionListener(e -> testUrlInBrowser(urlField.getText().trim()));

        JPanel urlInputBox = new JPanel(new BorderLayout(8, 0));
        urlInputBox.setOpaque(false);
        urlInputBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        urlInputBox.add(urlField, BorderLayout.CENTER);
        urlInputBox.add(testBtn, BorderLayout.EAST);

        add(createField("Medical Certificate Link (Google Drive / Cloud URL):", urlInputBox));
        add(Box.createVerticalStrut(16));

        JButton submitBtn = new JButton("Submit Medical Application");
        submitBtn.setFont(AdminTheme.font(Font.BOLD, 13));
        submitBtn.setBackground(AdminTheme.FOREST_GREEN);
        submitBtn.setForeground(AdminTheme.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.addActionListener(e -> handleSubmission());

        add(submitBtn);
    }

    private void loadEnrolledCourses() {
        courseComboBox.removeAllItems();
        List<EnrolledCourseDTO> list = UndergraduateCourseDAO.getEnrolledCoursesWithEvaluation(userId);
        for (EnrolledCourseDTO c : list) {
            courseComboBox.addItem(new CourseDropdownItem(c.courseId, c.courseCode, c.courseName));
        }
    }

    private void handleSubmission() {
        CourseDropdownItem selected = (CourseDropdownItem) courseComboBox.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an enrolled course module.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String date = dateField.getText().trim();
        String reason = reasonField.getText().trim();
        String docUrl = urlField.getText().trim();

        if (date.isEmpty() || reason.isEmpty() || docUrl.isEmpty() || !docUrl.startsWith("http")) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all fields and provide a valid cloud document URL (http:// or https://)",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = UndergraduateMedicalDAO.submitMedical(userId, selected.courseId, date, reason, docUrl);
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
            JOptionPane.showMessageDialog(this, "Failed to submit medical record to database. Verify date format (YYYY-MM-DD).", "Error", JOptionPane.ERROR_MESSAGE);
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
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(AdminTheme.font(Font.BOLD, 12));
        lbl.setForeground(new Color(0x44, 0x44, 0x44));

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
}