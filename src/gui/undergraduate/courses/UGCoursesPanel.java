package gui.undergraduate.courses;

import gui.admin.AdminTheme;
import gui.undergraduate.courses.directory.CourseDirectoryPanel;
import gui.undergraduate.courses.evaluation.CourseEvaluationPanel;
import gui.undergraduate.courses.header.CoursesHeaderPanel;
import gui.undergraduate.courses.model.CourseEvaluationData;
import gui.undergraduate.courses.summary.CoursesSummaryStripPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Module 1.2: My Courses view coordinator.
 * Binds header, metrics strip, directory table, and evaluation details in a clean modular architecture.
 */
public class UGCoursesPanel extends JPanel {

    private final CourseDirectoryPanel directoryPanel;
    private final CourseEvaluationPanel evaluationPanel;
    private final Map<String, CourseEvaluationData> courseDataMap = new HashMap<>();

    public UGCoursesPanel(long userId, String username) {
        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        initSampleData();

        // 1. Top Section: Header + Metric Strip
        JPanel topWrapper = new JPanel(new BorderLayout(0, 14));
        topWrapper.setOpaque(false);
        topWrapper.add(new CoursesHeaderPanel(), BorderLayout.NORTH);
        topWrapper.add(new CoursesSummaryStripPanel(), BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        // 2. Center Section: Directory Table and Evaluation Panel Split
        evaluationPanel = new CourseEvaluationPanel();
        directoryPanel = new CourseDirectoryPanel(courseCode -> {
            evaluationPanel.updateData(courseCode, courseDataMap.get(courseCode));
        });

        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        splitPanel.setOpaque(false);
        splitPanel.add(directoryPanel);
        splitPanel.add(evaluationPanel);
        add(splitPanel, BorderLayout.CENTER);

        // Pre-select first course
        directoryPanel.selectFirstRow();
        evaluationPanel.updateData("ICT2112", courseDataMap.get("ICT2112"));
    }

    private void initSampleData() {
        courseDataMap.put("ICT2112", new CourseEvaluationData("Object Oriented Programming", "Dr. Kamal Perera", "Department of ICT", 3, 10, 15, 15, 60));
        courseDataMap.put("ICT2123", new CourseEvaluationData("Database Management Systems", "Prof. Nimal Silva", "Department of ICT", 3, 10, 20, 10, 60));
        courseDataMap.put("ICT2133", new CourseEvaluationData("Software Engineering Essentials", "Dr. Sumith Bandara", "Department of ICT", 3, 15, 15, 10, 60));
        courseDataMap.put("ICT2142", new CourseEvaluationData("Data Communication & Networks", "Ms. Ruwani Fernando", "Department of ICT", 2, 10, 10, 20, 60));
        courseDataMap.put("ICT2152", new CourseEvaluationData("Operating Systems", "Mr. Asela Weerasinghe", "Department of ICT", 2, 10, 15, 15, 60));
        courseDataMap.put("ENG2113", new CourseEvaluationData("Technical Communication", "Dr. Priyantha Dissanayake", "Department of Languages", 3, 20, 20, 0, 60));
    }
}