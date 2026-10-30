package gui.undergraduate.medicals;

import gui.admin.AdminTheme;
import gui.undergraduate.medicals.form.MedicalSubmissionPanel;
import gui.undergraduate.medicals.header.MedicalsHeaderPanel;
import gui.undergraduate.medicals.model.MedicalRecordData;
import gui.undergraduate.medicals.summary.MedicalsSummaryStripPanel;
import gui.undergraduate.medicals.table.MedicalHistoryPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Module 1.5: Medical Records Submission & Status view coordinator.
 * Synchronizes medical submission form, verification table, and metric counters.
 */
public class UGMedicalsPanel extends JPanel {

    private final MedicalsSummaryStripPanel summaryStripPanel;
    private final MedicalSubmissionPanel submissionPanel;
    private final MedicalHistoryPanel historyPanel;
    private final List<MedicalRecordData> recordsList = new ArrayList<>();

    public UGMedicalsPanel(long userId, String username) {
        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        initSampleData();

        // 1. Top Section: Header + Metric Strip
        JPanel topWrapper = new JPanel(new BorderLayout(0, 14));
        topWrapper.setOpaque(false);
        topWrapper.add(new MedicalsHeaderPanel(), BorderLayout.NORTH);

        summaryStripPanel = new MedicalsSummaryStripPanel();
        recalculateCounts();
        topWrapper.add(summaryStripPanel, BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        // 2. Center Section: Form on Left, History Table on Right
        historyPanel = new MedicalHistoryPanel();
        historyPanel.setRecords(recordsList);

        submissionPanel = new MedicalSubmissionPanel(newRecord -> {
            recordsList.add(0, newRecord);
            historyPanel.addRecord(newRecord);
            recalculateCounts();
        });

        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        splitPanel.setOpaque(false);
        splitPanel.add(historyPanel);
        splitPanel.add(submissionPanel);
        add(splitPanel, BorderLayout.CENTER);
    }

    private void recalculateCounts() {
        int approved = 0, pending = 0, rejected = 0;
        for (MedicalRecordData r : recordsList) {
            if ("Approved".equalsIgnoreCase(r.getApprovalStatus())) approved++;
            else if ("Pending".equalsIgnoreCase(r.getApprovalStatus())) pending++;
            else if ("Rejected".equalsIgnoreCase(r.getApprovalStatus())) rejected++;
        }
        summaryStripPanel.updateCounts(recordsList.size(), approved, pending, rejected);
    }

    private void initSampleData() {
        recordsList.add(new MedicalRecordData(101, "ICT2132", "OOP Practicum", "2026-09-18", "Viral Fever certified by UHS", "scans/med101.pdf", "Approved", "Verified by TO"));
        recordsList.add(new MedicalRecordData(102, "ICT2142", "Database Management Systems", "2026-09-25", "Severe Migraine with prescription", "scans/med102.pdf", "Approved", "Verified by TO"));
        recordsList.add(new MedicalRecordData(103, "ICT2122", "Computer Networks", "2026-10-12", "Dental surgery recovery", "scans/med103.pdf", "Pending", "Under evaluation"));
    }
}