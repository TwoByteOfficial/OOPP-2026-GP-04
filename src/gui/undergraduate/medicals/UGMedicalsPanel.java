package gui.undergraduate.medicals;

import dao.undergraduate.UndergraduateMedicalDAO;
import dao.undergraduate.UndergraduateMedicalDAO.MedicalMetricsDTO;
import dao.undergraduate.UndergraduateMedicalDAO.MedicalRecordDTO;
import gui.admin.AdminTheme;
import gui.undergraduate.medicals.form.MedicalSubmissionPanel;
import gui.undergraduate.medicals.header.MedicalsHeaderPanel;
import gui.undergraduate.medicals.summary.MedicalsSummaryStripPanel;
import gui.undergraduate.medicals.table.MedicalHistoryPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Module 1.5: Medical Records Submission & Status view coordinator connected directly to database.
 * Anchored to NORTH with balanced equal-height cards for history and submission form.
 * Coordinates medical KPI metrics, student submission requests, and historical certificate records.
 */
public class UGMedicalsPanel extends JPanel {

    // Authenticated user session credentials
    private final long userId;
    private final String username;

    // Sub-view panel components
    private final MedicalsSummaryStripPanel summaryStripPanel;
    private final MedicalSubmissionPanel submissionPanel;
    private final MedicalHistoryPanel historyPanel;

    /**
     * Constructs the primary medical records view and organizes layout structures.
     *
     * @param userId   Logged-in student identifier
     * @param username Logged-in student username
     */
    public UGMedicalsPanel(long userId, String username) {
        this.userId = userId;
        this.username = username;

        // Base container layout and calibrated outer padding
        setLayout(new BorderLayout(0, 14));
        setBackground(AdminTheme.LIGHT_BG);
        // Calibrated outer padding matching Dashboard, Attendance, and Grades tabs (Top 18px, Sides 22px)
        setBorder(new EmptyBorder(18, 22, 14, 22));

        // -------------------------------------------------------------
        // 1. Top Section: Header + Metric Strip
        // -------------------------------------------------------------
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setOpaque(false);

        MedicalsHeaderPanel headerPanel = new MedicalsHeaderPanel();
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topWrapper.add(headerPanel);

        // Exact 16px vertical strut between Header and Summary Strip (Zero jumping across tabs)
        topWrapper.add(Box.createVerticalStrut(16));

        summaryStripPanel = new MedicalsSummaryStripPanel();
        summaryStripPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryStripPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        topWrapper.add(summaryStripPanel);

        add(topWrapper, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Center Section: Left History Table + Right URL Submission Form
        // -------------------------------------------------------------
        historyPanel = new MedicalHistoryPanel();
        submissionPanel = new MedicalSubmissionPanel(userId, this::loadDatabaseData);

        // GridLayout ensures both cards have identical height and align on the same bottom line
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        splitPanel.setOpaque(false);
        splitPanel.add(historyPanel);
        splitPanel.add(submissionPanel);

        // Anchored to NORTH so neither card stretches downward into empty space
        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.setOpaque(false);
        contentContainer.add(splitPanel, BorderLayout.NORTH);

        add(contentContainer, BorderLayout.CENTER);

        // Fetch live database records
        loadDatabaseData();
    }

    /**
     * Queries database for submission summary counts and historical records,
     * updating the summary strip and history table accordingly.
     */
    private void loadDatabaseData() {
        // Query application metric totals
        MedicalMetricsDTO metrics = UndergraduateMedicalDAO.getMedicalMetrics(userId);
        summaryStripPanel.updateCounts(metrics);

        // Query historical certificate submissions and populate table
        List<MedicalRecordDTO> list = UndergraduateMedicalDAO.getMedicalHistory(userId);
        historyPanel.setRecords(list);
    }
}