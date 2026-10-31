package gui.undergraduate.attendance;

import dao.undergraduate.UndergraduateAttendanceDAO;
import dao.undergraduate.UndergraduateAttendanceDAO.AttendanceRecordDTO;
import gui.admin.AdminTheme;
import gui.undergraduate.attendance.details.AttendanceEligibilityPanel;
import gui.undergraduate.attendance.header.AttendanceHeaderPanel;
import gui.undergraduate.attendance.summary.AttendanceSummaryStripPanel;
import gui.undergraduate.attendance.table.AttendanceDirectoryPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Module 1.3: Attendance & Exam Eligibility coordinator panel connected directly to database.
 */
public class UGAttendancePanel extends JPanel {

    private final long userId;
    private final String username;
    private final AttendanceDirectoryPanel directoryPanel;
    private final AttendanceEligibilityPanel eligibilityPanel;
    private final AttendanceSummaryStripPanel summaryStripPanel;
    private final Map<String, AttendanceRecordDTO> attendanceMap = new HashMap<>();

    public UGAttendancePanel(long userId, String username) {
        this.userId = userId;
        this.username = username;

        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // 1. Top Section: Header + Metric Strip
        JPanel topWrapper = new JPanel(new BorderLayout(0, 14));
        topWrapper.setOpaque(false);
        topWrapper.add(new AttendanceHeaderPanel(), BorderLayout.NORTH);

        summaryStripPanel = new AttendanceSummaryStripPanel();
        topWrapper.add(summaryStripPanel, BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        // 2. Center Section: Attendance Table and Eligibility Details Split
        eligibilityPanel = new AttendanceEligibilityPanel();
        directoryPanel = new AttendanceDirectoryPanel(courseCode -> {
            eligibilityPanel.updateData(attendanceMap.get(courseCode));
        });

        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        splitPanel.setOpaque(false);
        splitPanel.add(directoryPanel);
        splitPanel.add(eligibilityPanel);
        add(splitPanel, BorderLayout.CENTER);

        // Fetch live database records
        loadDatabaseData();
    }

    private void loadDatabaseData() {
        List<AttendanceRecordDTO> records = UndergraduateAttendanceDAO.getStudentAttendanceDirectory(userId);
        attendanceMap.clear();

        for (AttendanceRecordDTO r : records) {
            attendanceMap.put(r.courseCode, r);
        }

        summaryStripPanel.updateMetrics(UndergraduateAttendanceDAO.getAttendanceStripMetrics(userId));
        directoryPanel.setAttendanceData(records);

        if (!records.isEmpty()) {
            eligibilityPanel.updateData(records.get(0));
        }
    }
}