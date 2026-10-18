package gui.admin;

/* ---- Explicit Sub-Component Import Framework Declarations ---- */
import gui.admin.DashboardComponents.*;

/* ---- Core Java GUI System Libraries ---- */
import javax.swing.*;
import java.awt.*;

/**
 * Main coordinated layout manager panel mapping grid components side-by-side.
 * Sub-panels are declared as private instance variables with explicit import definitions.
 */
public class AdminDashboard extends JPanel {

    /* ---- Private Sub-Panel Component Fields ---- */
    private DashboardTitleBar   titleBar;
    private SimpleAnalyticView  analyticsRow;
    private StatusSummary       statusPanel;
    private AttendanceSummary   attendancePanel;
    private UserSummary         userPanel;
    private NoticesSummary      noticesPanel;

    public AdminDashboard(long userId, String username, String fullName, String email, String role) {
        // Enforce absolute layouts to map coordinates precisely side-by-side
        setLayout(null);

        // Total view canvas footprint space (Frame width 1920 - Sidebar 260 = 1660 Content window)
        setBounds(0, 0, 1660, 1080);
        setOpaque(false);

        /* 1. Row 1: Header Context Title Bar Component */
        titleBar = new DashboardTitleBar(fullName);
        titleBar.setLocation(20, 20); // Pin grid offsets
        add(titleBar);

        /* 2. Row 2: Analytics Cards Module Component Block */
        analyticsRow = new SimpleAnalyticView();
        analyticsRow.setLocation(20, 130);
        add(analyticsRow);

        /* 3. Row 3: Middle Grid Cards Stacked Side-by-Side */
        // Card A: Status summary log details
        statusPanel = new StatusSummary();
        statusPanel.setLocation(20, 290);
        add(statusPanel);

        // Card B: Attendance tracking panel overview
        attendancePanel = new AttendanceSummary();
        attendancePanel.setLocation(550, 290); // Left offset (20 + 510 width + 20 spacing)
        add(attendancePanel);

        // Card C: User database role breakdown tracking directory
        userPanel = new UserSummary();
        userPanel.setLocation(1080, 290); // Left offset (550 + 510 width + 20 spacing)
        add(userPanel);

        /* 4. Row 4: Wide Announcement Board Component Container */
        noticesPanel = new NoticesSummary();
        noticesPanel.setLocation(20, 560);
        add(noticesPanel);
    }

}
