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
 */
public class UGMedicalsPanel extends JPanel {

    private final long userId;
    private final String username;
    private final MedicalsSummaryStripPanel summaryStripPanel;
    private final MedicalSubmissionPanel submissionPanel;
    private final MedicalHistoryPanel historyPanel;

    public UGMedicalsPanel(long userId, String username) {
        this.userId = userId;
        this.username = username;

        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // 1. Top Section: Header + Metric Strip
        JPanel topWrapper = new JPanel(new BorderLayout(0, 14));
        topWrapper.setOpaque(false);
        topWrapper.add(new MedicalsHeaderPanel(), BorderLayout.NORTH);

        summaryStripPanel = new MedicalsSummaryStripPanel();
        topWrapper.add(summaryStripPanel, BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        // 2. Center Section: Left History Table + Right URL Submission Form
        historyPanel = new MedicalHistoryPanel();
        submissionPanel = new MedicalSubmissionPanel(userId, this::loadDatabaseData);

        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        splitPanel.setOpaque(false);
        splitPanel.add(historyPanel);
        splitPanel.add(submissionPanel);
        add(splitPanel, BorderLayout.CENTER);

        // Fetch live database records
        loadDatabaseData();
    }

    private void loadDatabaseData() {
        MedicalMetricsDTO metrics = UndergraduateMedicalDAO.getMedicalMetrics(userId);
        summaryStripPanel.updateCounts(metrics);

        List<MedicalRecordDTO> list = UndergraduateMedicalDAO.getMedicalHistory(userId);
        historyPanel.setRecords(list);
    }
}