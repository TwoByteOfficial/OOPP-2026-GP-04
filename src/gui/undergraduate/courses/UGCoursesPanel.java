package gui.undergraduate.courses;

import dao.undergraduate.UndergraduateCourseDAO;
import dao.undergraduate.UndergraduateCourseDAO.EnrolledCourseDTO;
import gui.admin.AdminTheme;
import gui.undergraduate.courses.directory.CourseDirectoryPanel;
import gui.undergraduate.courses.evaluation.CourseEvaluationPanel;
import gui.undergraduate.courses.header.CoursesHeaderPanel;
import gui.undergraduate.courses.summary.CoursesSummaryStripPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Module 1.2: My Courses view coordinator.
 * Binds header, metrics strip, directory table, and evaluation details with live database data.
 */
public class UGCoursesPanel extends JPanel {

    private final long userId;
    private final String username;
    private final CourseDirectoryPanel directoryPanel;
    private final CourseEvaluationPanel evaluationPanel;
    private final CoursesSummaryStripPanel summaryStripPanel;
    private final Map<String, EnrolledCourseDTO> courseMap = new HashMap<>();

    public UGCoursesPanel(long userId, String username) {
        this.userId = userId;
        this.username = username;

        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // 1. Top Section: Header + Metric Strip
        JPanel topWrapper = new JPanel(new BorderLayout(0, 14));
        topWrapper.setOpaque(false);
        topWrapper.add(new CoursesHeaderPanel(), BorderLayout.NORTH);

        summaryStripPanel = new CoursesSummaryStripPanel();
        topWrapper.add(summaryStripPanel, BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        // 2. Center Section: Directory Table and Evaluation Panel Split
        evaluationPanel = new CourseEvaluationPanel();
        directoryPanel = new CourseDirectoryPanel(courseCode -> {
            evaluationPanel.updateCourse(courseMap.get(courseCode));
        });

        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        splitPanel.setOpaque(false);
        splitPanel.add(directoryPanel);
        splitPanel.add(evaluationPanel);
        add(splitPanel, BorderLayout.CENTER);

        // Fetch live database records
        loadDatabaseData();
    }

    private void loadDatabaseData() {
        List<EnrolledCourseDTO> courses = UndergraduateCourseDAO.getEnrolledCoursesWithEvaluation(userId);
        courseMap.clear();

        int totalCredits = 0;
        int coreCount = 0;
        int electiveCount = 0;

        for (EnrolledCourseDTO c : courses) {
            courseMap.put(c.courseCode, c);
            totalCredits += c.totalCredits;
            if ("Normal".equalsIgnoreCase(c.enrollmentType)) {
                coreCount++;
            } else {
                electiveCount++;
            }
        }

        // Update Summary Strip & Directory
        summaryStripPanel.updateMetrics(totalCredits, coreCount, electiveCount);
        directoryPanel.setCourses(courses);

        if (!courses.isEmpty()) {
            evaluationPanel.updateCourse(courses.get(0));
        }
    }
}