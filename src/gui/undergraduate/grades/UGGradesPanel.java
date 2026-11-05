package gui.undergraduate.grades;

import dao.undergraduate.UndergraduateGradesDAO;
import dao.undergraduate.UndergraduateGradesDAO.StudentCourseGradeDTO;
import dao.undergraduate.UndergraduateGradesDAO.TranscriptSummaryDTO;
import gui.admin.AdminTheme;
import gui.undergraduate.grades.details.GpaBreakdownPanel;
import gui.undergraduate.grades.header.GradesHeaderPanel;
import gui.undergraduate.grades.summary.GradesSummaryStripPanel;
import gui.undergraduate.grades.table.GradesDirectoryPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Module 1.4: Academic Grades, SGPA & CGPA Records view coordinator connected directly to database.
 * Anchored to NORTH with balanced equal-height cards for directory and honours details.
 * Manages transcript summary updates, grade table synchronization, and honours benchmark calculations.
 */
public class UGGradesPanel extends JPanel {

    // Authenticated user session state
    private final long userId;
    private final String username;

    // Sub-view component references
    private final GradesSummaryStripPanel summaryStripPanel;
    private final GradesDirectoryPanel directoryPanel;
    private final GpaBreakdownPanel breakdownPanel;

    // Fast-lookup mapping of course codes to student grade DTOs
    private final Map<String, StudentCourseGradeDTO> gradesMap = new HashMap<>();

    public UGGradesPanel(long userId, String username) {
        this.userId = userId;
        this.username = username;

        // Base container styling and exact padding matching other portal modules
        setLayout(new BorderLayout(0, 14));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 14, 22));

        // -------------------------------------------------------------
        // 1. Top Section: Header + Summary Metric Strip
        // -------------------------------------------------------------
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setOpaque(false);

        // Header containing title and academic level/semester badge
        GradesHeaderPanel headerPanel = new GradesHeaderPanel();
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topWrapper.add(headerPanel);

        // Exact 16px vertical strut between Header and Summary Strip (Zero jumping across tabs)
        topWrapper.add(Box.createVerticalStrut(16));

        // 4-card metric strip (CGPA, SGPA, Credits, Standing)
        summaryStripPanel = new GradesSummaryStripPanel();
        summaryStripPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryStripPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        topWrapper.add(summaryStripPanel);

        add(topWrapper, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Center Section: Directory Table and Honours Details Split
        // -------------------------------------------------------------
        breakdownPanel = new GpaBreakdownPanel();
        directoryPanel = new GradesDirectoryPanel(courseCode -> {
            // Synchronize selected module details with the breakdown panel
            breakdownPanel.updateCourseSelection(gradesMap.get(courseCode));
        });

        // GridLayout ensures both cards have identical height and align on the same bottom line
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        splitPanel.setOpaque(false);
        splitPanel.add(directoryPanel);
        splitPanel.add(breakdownPanel);

        // Anchored to NORTH so neither card stretches downward into empty space
        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.setOpaque(false);
        contentContainer.add(splitPanel, BorderLayout.NORTH);

        add(contentContainer, BorderLayout.CENTER);

        // Initial database fetch to populate summary cards, directory table, and benchmarks
        loadDatabaseData();
    }

    /**
     * Retrieves live academic records, transcript totals, and module grades from the database,
     * populates UI components, and selects the first grade item by default.
     */
    private void loadDatabaseData() {
        // Query cumulative GPA, current semester SGPA, and credit progression metrics
        TranscriptSummaryDTO summary = UndergraduateGradesDAO.getTranscriptSummary(userId);
        summaryStripPanel.updateSummary(summary);
        breakdownPanel.updateHonoursBenchmarks(summary);

        // Query course grades and populate internal lookup cache
        List<StudentCourseGradeDTO> grades = UndergraduateGradesDAO.getStudentGrades(userId);
        gradesMap.clear();

        for (StudentCourseGradeDTO g : grades) {
            gradesMap.put(g.courseCode, g);
        }

        // Bind dataset to directory table
        directoryPanel.setGradesData(grades);

        // Default display the first course module in the detail inspector
        if (!grades.isEmpty()) {
            breakdownPanel.updateCourseSelection(grades.get(0));
        }
    }
}