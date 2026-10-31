
package gui.technical.dashboard.summary;

import dao.TechnicalOfficer.TODashboardDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class DashboardSummaryPanel extends JPanel {

    public DashboardSummaryPanel(long userId) {

        setLayout(new GridLayout(1, 4, 15, 0));
        setOpaque(false);

        UndergraduateSummaryCard undergraduateCard =
                new UndergraduateSummaryCard("—");

        AttendanceSummaryCard attendanceCard =
                new AttendanceSummaryCard("—");

        PendingMedicalCard medicalCard =
                new PendingMedicalCard("—");

        TodaySessionsCard sessionsCard =
                new TodaySessionsCard("—");

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

    private void loadSummaryCounts(
            long userId,
            UndergraduateSummaryCard undergraduateCard,
            AttendanceSummaryCard attendanceCard,
            PendingMedicalCard medicalCard,
            TodaySessionsCard sessionsCard) {

        TODashboardDAO dashboardDAO = new TODashboardDAO();

        try {
            undergraduateCard.setValue(
                    String.valueOf(
                            dashboardDAO.getDepartmentUndergraduateCount(userId)
                    )
            );

            attendanceCard.setValue(
                    String.valueOf(
                            dashboardDAO.getAttendanceProfileCount(userId)
                    )
            );

            medicalCard.setValue(
                    String.valueOf(
                            dashboardDAO.getPendingMedicalCount(userId)
                    )
            );

            int todaySessions =
                    dashboardDAO.getTodaySessionsCount(userId);

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
