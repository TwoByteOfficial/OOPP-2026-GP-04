
package gui.technical.dashboard.overview;

import dao.TODashboardDAO;
import gui.theme.UniMisTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class AttendanceOverviewPanel extends JPanel {

    public AttendanceOverviewPanel(long userId) {

        setLayout(new BorderLayout(0, 12));
        setBackground(UniMisTheme.WHITE);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel heading = new JLabel("Attendance Overview");
        heading.setFont(UniMisTheme.FONT_H3);
        heading.setForeground(UniMisTheme.TEXT);

        add(heading, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        try {
            TODashboardDAO dao = new TODashboardDAO();
            int[] counts = dao.getAttendanceOverview(userId);

            addStat(content, "Total attendance records: ", counts[0]);
            addStat(content, "Present: ", counts[1]);
            addStat(content, "Absent: ", counts[2]);
            addStat(content, "Medical: ", counts[3]);
            addStat(content, "Late: ", counts[4]);

        } catch (SQLException e) {
            e.printStackTrace();

            JLabel error = new JLabel(
                    "Unable to load attendance statistics.");
            error.setFont(UniMisTheme.FONT_BODY_BOLD);
            error.setForeground(UniMisTheme.PRIMARY_DARK);

            content.add(error);
        }

        add(content, BorderLayout.CENTER);
    }

    private void addStat(JPanel panel, String label, int count) {

        JLabel stat = new JLabel(label + count);
        stat.setFont(UniMisTheme.FONT_BODY_BOLD);
        stat.setForeground(UniMisTheme.PRIMARY_DARK);
        stat.setBorder(new EmptyBorder(2, 0, 2, 0));

        panel.add(stat);
    }
}
