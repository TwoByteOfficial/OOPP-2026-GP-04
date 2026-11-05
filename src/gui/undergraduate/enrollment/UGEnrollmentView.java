package gui.undergraduate.enrollment;

import dao.undergraduate.UndergraduateEnrollmentDAO;
import gui.undergraduate.enrollment.directory.EnrollmentDirectoryPanel;
import gui.undergraduate.enrollment.evaluation.EnrollmentEvaluationPanel;
import gui.undergraduate.enrollment.model.EnrollmentCourseData;
import gui.undergraduate.enrollment.summary.EnrollmentSummaryStripPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Body component for the Course Registration sub-view containing
 * curriculum metrics, directory table, and evaluation inspector.
 *
 * Responsibilities:
 * - Coordinates sub-panels for course registration (summary strip, directory table, evaluation details).
 * - Manages GridBagLayout split ratio (57% directory listing, 43% course evaluation inspector).
 * - Queries and synchronizes student course availability via {@link UndergraduateEnrollmentDAO}.
 * - Prompts confirmation dialogs and submits formal course enrollment requests for admin approval.
 */
public class UGEnrollmentView extends JPanel {

    // Authenticated student identifier
    private final long userId;

    // Data access object for course query and enrollment request dispatching
    private final UndergraduateEnrollmentDAO enrollmentDAO;

    // UI Sub-panels
    private final EnrollmentSummaryStripPanel summaryStripPanel; // Top statistics strip (credits, enrolled, pending)
    private final EnrollmentDirectoryPanel directoryPanel;       // Left directory table listing courses
    private final EnrollmentEvaluationPanel evaluationPanel;     // Right details and assessment breakdown panel
    private final Runnable onDataChanged;                        // External callback to notify parent views on updates

    /**
     * Constructs the enrollment view panel, initializes sub-components,
     * arranges split layouts, and triggers initial asynchronous data load.
     *
     * @param userId        The unique identifier of the authenticated undergraduate
     * @param onDataChanged Callback triggered when an enrollment request succeeds to refresh parent counters
     */
    public UGEnrollmentView(long userId, Runnable onDataChanged) {
        this.userId = userId;
        this.onDataChanged = onDataChanged;
        this.enrollmentDAO = new UndergraduateEnrollmentDAO();

        // Use BorderLayout with vertical spacing between top strip and center workspace
        setLayout(new BorderLayout(0, 14));
        setOpaque(false);

        // -------------------------------------------------------------
        // 1. Top Section: Enrollment Summary KPI Strip
        // -------------------------------------------------------------
        summaryStripPanel = new EnrollmentSummaryStripPanel();
        add(summaryStripPanel, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Center Section: Split Layout (Directory 57% & Evaluation 43%)
        // -------------------------------------------------------------
        JPanel centerSplit = new JPanel(new GridBagLayout());
        centerSplit.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Instantiate evaluation panel with enroll button action listener
        evaluationPanel = new EnrollmentEvaluationPanel(this::handleEnrollRequest);

        // Instantiate directory table with row selection listener linking to evaluation inspector
        directoryPanel = new EnrollmentDirectoryPanel(evaluationPanel::displayCourse);

        // Position Directory Panel on Left (57% proportional width)
        gbc.gridx = 0;
        gbc.weightx = 0.57;
        gbc.insets = new Insets(0, 0, 0, 14);
        centerSplit.add(directoryPanel, gbc);

        // Position Evaluation Details Panel on Right (43% proportional width)
        gbc.gridx = 1;
        gbc.weightx = 0.43;
        gbc.insets = new Insets(0, 0, 0, 0);
        centerSplit.add(evaluationPanel, gbc);

        add(centerSplit, BorderLayout.CENTER);

        // Load courses into directory table on construction
        refreshData();
    }

    /**
     * Asynchronously queries the database for available courses,
     * recalculates summary statistics, populates directory table rows,
     * and auto-selects the first course entry for the evaluation inspector.
     */
    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            // Fetch curriculum courses along with student-specific status (ENROLLED, PENDING, AVAILABLE)
            List<EnrollmentCourseData> list = enrollmentDAO.getAvailableCoursesForStudent(userId);
            directoryPanel.setCourses(list);

            // Metric tracking counters
            int totalCredits = 0;
            int enrolled = 0;
            int pending = 0;
            int available = 0;

            // Iterate courses to compute cumulative credit totals and status counts
            for (EnrollmentCourseData c : list) {
                totalCredits += (int) c.getTotalCredits();
                if ("ENROLLED".equals(c.getEnrollmentStatus())) {
                    enrolled++;
                } else if ("PENDING".equals(c.getEnrollmentStatus())) {
                    pending++;
                } else {
                    available++;
                }
            }

            // Update top summary cards with computed totals
            summaryStripPanel.updateStats(totalCredits, enrolled, pending, available);

            // Display details of the first course in the list by default
            if (!list.isEmpty()) {
                evaluationPanel.displayCourse(list.get(0));
            }
        });
    }

    /**
     * Handles student enrollment submission workflow.
     * Displays confirmation popup, executes request via DAO, and informs user of status.
     *
     * @param c The selected course item requesting enrollment
     */
    private void handleEnrollRequest(EnrollmentCourseData c) {
        // Confirmation prompt before submitting request to database
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Do you want to submit an enrollment request for:\n" +
                        c.getCourseCode() + " - " + c.getCourseName() + " (" + (int) c.getTotalCredits() + " Credits)?",
                "Confirm Course Enrollment",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            // Submit enrollment request to database with 'Pending' status
            boolean ok = enrollmentDAO.requestEnrollment(userId, c.getCourseId(), c.getSemester(), "Enrollment request submitted by undergraduate student");
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Enrollment request submitted successfully!\nAwaiting Admin approval.",
                        "Request Sent",
                        JOptionPane.INFORMATION_MESSAGE);
                // Refresh local view data
                refreshData();
                // Notify parent container/dashboard if callback registered
                if (onDataChanged != null) {
                    onDataChanged.run();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to submit request. You may already have a pending request for this module.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}