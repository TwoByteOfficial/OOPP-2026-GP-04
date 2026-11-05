package gui.technical.dashboard.summary;

import dao.TechnicalOfficer.TODashboardDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * [OOP - INHERITANCE]: Extends javax.swing.JPanel.
 * 
 * [OOP - POLYMORPHISM]: Holds and renders different SummaryCardPanel subclasses polymorphically.
 * Containers accept specialized card instances via the polymorphic Component type.
 */
public class DashboardSummaryPanel extends JPanel {

    /**
     * Constructs the summary cards container and triggers database count queries.
     *
     * @param userId The database user ID of the Technical Officer
     */
    public DashboardSummaryPanel(long userId) {

        setLayout(new GridLayout(1, 4, 15, 0));
        setOpaque(false);

        // [OOP - POLYMORPHISM]: Instantiating specific subclasses of SummaryCardPanel
        UndergraduateSummaryCard undergraduateCard = new UndergraduateSummaryCard("—");
        AttendanceSummaryCard attendanceCard = new AttendanceSummaryCard("—");
        PendingMedicalCard medicalCard = new PendingMedicalCard("—");
        TodaySessionsCard sessionsCard = new TodaySessionsCard("—");

        add(undergraduateCard);
        add(attendanceCard);
        add(medicalCard);
        add(sessionsCard);

        loadSummaryCounts(
                userId,
                undergraduateCard,
                attendanceCard,
                medicalCard,
                sessionsCard
        );
    }

    /**
     * [OOP - ENCAPSULATION & ABSTRACTION]: Queries TODashboardDAO for metrics and updates card values.
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

            undergraduateCard.setValue("—");
            attendanceCard.setValue("—");
            medicalCard.setValue("—");
            sessionsCard.setValue("—");
        }
    }
}
