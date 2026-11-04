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
 */
public class UGGradesPanel extends JPanel {

    private final long userId;
    private final String username;
    private final GradesSummaryStripPanel summaryStripPanel;
    private final GradesDirectoryPanel directoryPanel;
    private final GpaBreakdownPanel breakdownPanel;
    private final Map<String, StudentCourseGradeDTO> gradesMap = new HashMap<>();

    public UGGradesPanel(long userId, String username) {
        this.userId = userId;
        this.username = username;

        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // 1. Top Section: Header + Metric Strip
        JPanel topWrapper = new JPanel(new BorderLayout(0, 14));
        topWrapper.setOpaque(false);
        topWrapper.add(new GradesHeaderPanel(), BorderLayout.NORTH);

        summaryStripPanel = new GradesSummaryStripPanel();
        topWrapper.add(summaryStripPanel, BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        // 2. Center Section: Directory Table and Honours Details Split
        breakdownPanel = new GpaBreakdownPanel();
        directoryPanel = new GradesDirectoryPanel(courseCode -> {
            breakdownPanel.updateCourseSelection(gradesMap.get(courseCode));
        });

        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        splitPanel.setOpaque(false);
        splitPanel.add(directoryPanel);
        splitPanel.add(breakdownPanel);
        add(splitPanel, BorderLayout.CENTER);

        // Fetch live database records
        loadDatabaseData();
    }

    private void loadDatabaseData() {
        TranscriptSummaryDTO summary = UndergraduateGradesDAO.getTranscriptSummary(userId);
        summaryStripPanel.updateSummary(summary);
        breakdownPanel.updateHonoursBenchmarks(summary);

        List<StudentCourseGradeDTO> grades = UndergraduateGradesDAO.getStudentGrades(userId);
        gradesMap.clear();

        for (StudentCourseGradeDTO g : grades) {
            gradesMap.put(g.courseCode, g);
        }

        directoryPanel.setGradesData(grades);

        if (!grades.isEmpty()) {
            breakdownPanel.updateCourseSelection(grades.get(0));
        }
    }
}