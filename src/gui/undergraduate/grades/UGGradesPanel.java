package gui.undergraduate.grades;

import gui.admin.AdminTheme;
import gui.undergraduate.grades.details.GpaBreakdownPanel;
import gui.undergraduate.grades.header.GradesHeaderPanel;
import gui.undergraduate.grades.model.CourseGradeData;
import gui.undergraduate.grades.model.GpaSummaryData;
import gui.undergraduate.grades.summary.GradesSummaryStripPanel;
import gui.undergraduate.grades.table.GradesDirectoryPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Module 1.4: Academic Grades, SGPA & CGPA Records view coordinator.
 * Integrates transcript metrics, directory table, and honours classification panels.
 */
public class UGGradesPanel extends JPanel {

    private final GradesSummaryStripPanel summaryStripPanel;
    private final GradesDirectoryPanel directoryPanel;
    private final GpaBreakdownPanel breakdownPanel;
    private final Map<String, CourseGradeData> gradesMap = new HashMap<>();
    private final List<CourseGradeData> gradesList = new ArrayList<>();

    public UGGradesPanel(long userId, String username) {
        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        initSampleData();

        // 1. Top Section: Header + Metric Strip
        JPanel topWrapper = new JPanel(new BorderLayout(0, 14));
        topWrapper.setOpaque(false);
        topWrapper.add(new GradesHeaderPanel(), BorderLayout.NORTH);

        summaryStripPanel = new GradesSummaryStripPanel();
        summaryStripPanel.updateSummary(new GpaSummaryData(3.65, 3.72, 48, "Dean's List"));
        topWrapper.add(summaryStripPanel, BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        // 2. Center Section: Directory Table and Honours Details Split
        breakdownPanel = new GpaBreakdownPanel();
        directoryPanel = new GradesDirectoryPanel(courseCode -> {
            breakdownPanel.updateCourseSelection(gradesMap.get(courseCode));
        });

        directoryPanel.setGradesData(gradesList);

        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        splitPanel.setOpaque(false);
        splitPanel.add(directoryPanel);
        splitPanel.add(breakdownPanel);
        add(splitPanel, BorderLayout.CENTER);

        // Pre-select first course
        directoryPanel.selectFirstRow();
        if (!gradesList.isEmpty()) {
            breakdownPanel.updateCourseSelection(gradesList.get(0));
        }
    }

    private void initSampleData() {
        addRecord(new CourseGradeData("ICT2112", "Object Oriented Programming", 3, "A", 4.00, "Completed"));
        addRecord(new CourseGradeData("ICT2123", "Database Management Systems", 3, "A-", 3.70, "Completed"));
        addRecord(new CourseGradeData("ICT2133", "Software Engineering Essentials", 3, "B+", 3.30, "Completed"));
        addRecord(new CourseGradeData("ICT2142", "Data Communication & Networks", 2, "A", 4.00, "Completed"));
        addRecord(new CourseGradeData("ICT2152", "Operating Systems", 2, "B", 3.00, "Completed"));
        addRecord(new CourseGradeData("ENG2113", "Technical Communication", 3, "A+", 4.00, "Completed"));
    }

    private void addRecord(CourseGradeData data) {
        gradesList.add(data);
        gradesMap.put(data.getCourseCode(), data);
    }
}