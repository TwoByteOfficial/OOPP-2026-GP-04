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
 * Anchored to NORTH with balanced equal-height cards for directory and eligibility details.
 */
public class UGAttendancePanel extends JPanel {

    // Current authenticated user session details
    private final long userId;
    private final String username;

    // Sub-panels for directory table, eligibility breakdown, and metrics summary
    private final AttendanceDirectoryPanel directoryPanel;
    private final AttendanceEligibilityPanel eligibilityPanel;
    private final AttendanceSummaryStripPanel summaryStripPanel;

    // Fast in-memory lookup cache mapping courseCode -> AttendanceRecordDTO
    private final Map<String, AttendanceRecordDTO> attendanceMap = new HashMap<>();

    public UGAttendancePanel(long userId, String username) {
        this.userId = userId;
        this.username = username;

        // Main layout configuration with standard theme padding and background
        setLayout(new BorderLayout(0, 14));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 14, 22));

        // -------------------------------------------------------------
        // 1. Top Section: Header + Metric Strip
        // -------------------------------------------------------------
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setOpaque(false);

        // Header panel showing module title and academic regulations
        AttendanceHeaderPanel headerPanel = new AttendanceHeaderPanel();
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topWrapper.add(headerPanel);

        // Standard 16px vertical strut between Header and Summary Strip
        topWrapper.add(Box.createVerticalStrut(16));

        // 4-card metric strip showing aggregate attendance statistics
        summaryStripPanel = new AttendanceSummaryStripPanel();
        summaryStripPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryStripPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        topWrapper.add(summaryStripPanel);

        add(topWrapper, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Center Section: Attendance Table and Eligibility Details Split
        // -------------------------------------------------------------
        eligibilityPanel = new AttendanceEligibilityPanel();

        // Connect directory table row selection callback to update the right eligibility breakdown
        directoryPanel = new AttendanceDirectoryPanel(courseCode -> {
            eligibilityPanel.updateData(attendanceMap.get(courseCode));
        });

        // 1x2 GridLayout ensures both cards share equal width, equal height, and align horizontally
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        splitPanel.setOpaque(false);
        splitPanel.add(directoryPanel);
        splitPanel.add(eligibilityPanel);

        // Anchored to NORTH within center container to prevent vertical stretching into dead space
        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.setOpaque(false);
        contentContainer.add(splitPanel, BorderLayout.NORTH);

        add(contentContainer, BorderLayout.CENTER);

        // Fetch live database records on initial load
        loadDatabaseData();
    }

    /**
     * Queries database via DAO layer to fetch attendance directory and summary strip records.
     * Caches records into hashmap and triggers initial UI view synchronization.
     */
    private void loadDatabaseData() {
        // Query database for all enrolled attendance records for the active undergraduate
        List<AttendanceRecordDTO> records = UndergraduateAttendanceDAO.getStudentAttendanceDirectory(userId);
        attendanceMap.clear();

        // Populate in-memory lookup cache keyed by course code
        for (AttendanceRecordDTO r : records) {
            attendanceMap.put(r.courseCode, r);
        }

        // Fetch and display aggregate attendance strip statistics (Overall %, Eligible count, Hours, Medicals)
        summaryStripPanel.updateMetrics(UndergraduateAttendanceDAO.getAttendanceStripMetrics(userId));

        // Bind course records list into directory table
        directoryPanel.setAttendanceData(records);

        // Automatically populate eligibility details with the first course record by default
        if (!records.isEmpty()) {
            eligibilityPanel.updateData(records.get(0));
        }
    }
}