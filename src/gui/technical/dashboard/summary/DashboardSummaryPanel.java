package gui.technical.dashboard.summary;

import dao.TechnicalOfficer.TODashboardDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Summary cards container row on the Technical Officer Dashboard.
 * Arranges 4 key metric indicator cards horizontally:
 * 1. UndergraduateSummaryCard - Count of active undergraduates in the department
 * 2. AttendanceSummaryCard    - Count of distinct student attendance profiles
 * 3. PendingMedicalCard        - Count of pending medical records awaiting review
 * 4. TodaySessionsCard        - Count of class/lab sessions scheduled for today
 * Fetches metrics asynchronously from TODashboardDAO and updates each card.
 */
public class DashboardSummaryPanel extends JPanel {

    /**
     * Constructs the summary cards container and triggers database count queries.
     *
     * @param userId The database user ID of the Technical Officer
     */
    public DashboardSummaryPanel(long userId) {

        // 1 row, 4 equal columns with 15px horizontal spacing
        setLayout(new GridLayout(1, 4, 15, 0));
        setOpaque(false);

        // Initialize cards with placeholder values
        UndergraduateSummaryCard undergraduateCard = new UndergraduateSummaryCard("—");
        AttendanceSummaryCard attendanceCard = new AttendanceSummaryCard("—");
        PendingMedicalCard medicalCard = new PendingMedicalCard("—");
        TodaySessionsCard sessionsCard = new TodaySessionsCard("—");

        add(undergraduateCard);
        add(attendanceCard);
        add(medicalCard);
        add(sessionsCard);

        // Load live statistics from database
        loadSummaryCounts(
                userId,
                undergraduateCard,
                attendanceCard,
                medicalCard,
                sessionsCard
        );
    }

    /**
     * Queries TODashboardDAO for the latest department counts and updates each card's display value.
     *
     * @param userId            Technical Officer user ID
     * @param undergraduateCard Target undergraduate card
     * @param attendanceCard    Target attendance card
     * @param medicalCard       Target medical card
     * @param sessionsCard      Target today's sessions card
     */
    private void loadSummaryCounts(
            long userId,
            UndergraduateSummaryCard undergraduateCard,
            AttendanceSummaryCard attendanceCard,
            PendingMedicalCard medicalCard,
            TodaySessionsCard sessionsCard) {

        TODashboardDAO dashboardDAO = new TODashboardDAO();

        try {
            undergraduateCard.setValue(
                    String.valueOf(dashboardDAO.getDepartmentUndergraduateCount(userId))
            );

            attendanceCard.setValue(
                    String.valueOf(dashboardDAO.getAttendanceProfileCount(userId))
            );

            medicalCard.setValue(
                    String.valueOf(dashboardDAO.getPendingMedicalCount(userId))
            );

            int todaySessions = dashboardDAO.getTodaySessionsCount(userId);
            sessionsCard.setValue(String.valueOf(todaySessions));

        } catch (SQLException e) {
            e.printStackTrace();

            // Fallback display if database query fails
            undergraduateCard.setValue("—");
            attendanceCard.setValue("—");
            medicalCard.setValue("—");
            sessionsCard.setValue("—");
        }
    }
}
