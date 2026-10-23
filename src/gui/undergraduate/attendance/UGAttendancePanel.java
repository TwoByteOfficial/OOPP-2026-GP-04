package gui.undergraduate.attendance;

import gui.admin.AdminTheme;
import gui.undergraduate.attendance.details.AttendanceEligibilityPanel;
import gui.undergraduate.attendance.header.AttendanceHeaderPanel;
import gui.undergraduate.attendance.model.CourseAttendanceData;
import gui.undergraduate.attendance.summary.AttendanceSummaryStripPanel;
import gui.undergraduate.attendance.table.AttendanceDirectoryPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Module 1.3: Attendance & Exam Eligibility coordinator panel.
 * Connects header, metric strip, directory table, and eligibility breakdown in a modular structure.
 */
public class UGAttendancePanel extends JPanel {

    private final AttendanceDirectoryPanel directoryPanel;
    private final AttendanceEligibilityPanel eligibilityPanel;
    private final Map<String, CourseAttendanceData> attendanceMap = new HashMap<>();
    private final List<CourseAttendanceData> attendanceList = new ArrayList<>();

    public UGAttendancePanel(long userId, String username) {
        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        initSampleData();

        // 1. Top Section: Header + Metric Strip
        JPanel topWrapper = new JPanel(new BorderLayout(0, 14));
        topWrapper.setOpaque(false);
        topWrapper.add(new AttendanceHeaderPanel(), BorderLayout.NORTH);
        topWrapper.add(new AttendanceSummaryStripPanel(), BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        // 2. Center Section: Attendance Table and Eligibility Details Split
        eligibilityPanel = new AttendanceEligibilityPanel();
        directoryPanel = new AttendanceDirectoryPanel(courseCode -> {
            eligibilityPanel.updateData(attendanceMap.get(courseCode));
        });

        directoryPanel.setCourseData(attendanceList);

        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        splitPanel.setOpaque(false);
        splitPanel.add(directoryPanel);
        splitPanel.add(eligibilityPanel);
        add(splitPanel, BorderLayout.CENTER);

        // Pre-select first course
        directoryPanel.selectFirstRow();
        if (!attendanceList.isEmpty()) {
            eligibilityPanel.updateData(attendanceList.get(0));
        }
    }

    private void initSampleData() {
        addRecord(new CourseAttendanceData("ICT2112", "Object Oriented Programming", 30, 26, 15, 14, 0));
        addRecord(new CourseAttendanceData("ICT2123", "Database Management Systems", 32, 28, 16, 15, 0));
        addRecord(new CourseAttendanceData("ICT2133", "Software Engineering Essentials", 30, 22, 15, 10, 2)); // Medical applied
        addRecord(new CourseAttendanceData("ICT2142", "Data Communication & Networks", 28, 20, 12, 8, 0)); // At risk
        addRecord(new CourseAttendanceData("ICT2152", "Operating Systems", 28, 25, 14, 13, 0));
        addRecord(new CourseAttendanceData("ENG2113", "Technical Communication", 30, 27, 0, 0, 0));
    }

    private void addRecord(CourseAttendanceData data) {
        attendanceList.add(data);
        attendanceMap.put(data.getCourseCode(), data);
    }
}