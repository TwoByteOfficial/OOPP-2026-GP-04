package dao.TechnicalOfficer;

import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * [OOP - ABSTRACTION]: Data Access Object for the Technical Officer Dashboard.
 * Gathers and abstracts aggregated statistics and analytics (student counts, pending medical count,
 * today's sessions, and attendance status breakdown) filtered by the officer's department.
 */
public class TODashboardDAO {

    /**
     * [OOP - ABSTRACTION]: Abstracts complex SQL aggregate query counting department undergraduates.
     *
     * @param userId The user ID of the Technical Officer
     * @return Number of active undergraduate students in the same department
     * @throws SQLException if a query execution error occurs
     */
    public int getDepartmentUndergraduateCount(long userId)
            throws SQLException {

        String sql =
                "SELECT COUNT(*) " +
                        "FROM undergraduates ug " +
                        "JOIN users student ON ug.user_id = student.user_id " +
                        "JOIN users officer ON student.department_id = officer.department_id " +
                        "WHERE officer.user_id = ? " +
                        "AND officer.role = 'Technical Officer' " +
                        "AND student.role = 'Undergraduate' " +
                        "AND student.user_status = 'Active'";

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    /**
     * [OOP - ABSTRACTION]: Abstracts SQL query counting pending medical certificates.
     *
     * @param userId The user ID of the Technical Officer
     * @return Count of pending medical submissions
     * @throws SQLException if a database query error occurs
     */
    public int getPendingMedicalCount(long userId)
            throws SQLException {

        String sql =
                "SELECT COUNT(*) " +
                        "FROM medicals m " +
                        "JOIN users student ON m.student_id = student.user_id " +
                        "JOIN users officer ON student.department_id = officer.department_id " +
                        "WHERE officer.user_id = ? " +
                        "AND officer.role = 'Technical Officer' " +
                        "AND student.role = 'Undergraduate' " +
                        "AND m.approval_status = 'Pending'";

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    /**
     * Counts the number of distinct students with recorded attendance in the officer's department.
     *
     * @param userId The user ID of the Technical Officer
     * @return Number of distinct students with attendance entries
     * @throws SQLException if a database query error occurs
     */
    public int getAttendanceProfileCount(long userId)
            throws SQLException {

        String sql =
                "SELECT COUNT(DISTINCT ar.student_id) " +
                        "FROM attendance_records ar " +
                        "JOIN users student ON ar.student_id = student.user_id " +
                        "JOIN users officer ON student.department_id = officer.department_id " +
                        "WHERE officer.user_id = ? " +
                        "AND officer.role = 'Technical Officer' " +
                        "AND student.role = 'Undergraduate' " +
                        "AND student.user_status = 'Active'";

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    /**
     * Counts class or lab sessions scheduled for today in the timetable for the officer's department.
     *
     * @param userId The user ID of the Technical Officer
     * @return Count of scheduled timetable sessions today
     * @throws SQLException if a query error occurs
     */
    public int getTodaySessionsCount(long userId)
            throws SQLException {

        String sql =
                "SELECT COUNT(*) " +
                        "FROM timetable_entries te " +
                        "JOIN users officer ON te.department_id = officer.department_id " +
                        "WHERE officer.user_id = ? " +
                        "AND officer.role = 'Technical Officer' " +
                        "AND te.day_of_week = DAYNAME(CURDATE())";

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    /**
     * [OOP - ABSTRACTION]: Aggregates attendance statistics into an integer array
     * without exposing SQL conditional SUM expressions to the UI.
     *
     * @param userId The user ID of the Technical Officer
     * @return Array of 5 integers representing total and status breakdown
     * @throws SQLException if a query execution error occurs
     */
    public int[] getAttendanceOverview(long userId) throws SQLException {

        String sql =
                "SELECT COUNT(*) AS total, " +
                        "COALESCE(SUM(ar.attendance_status = 'PRESENT'), 0) AS present, " +
                        "COALESCE(SUM(ar.attendance_status = 'ABSENT'), 0) AS absent, " +
                        "COALESCE(SUM(ar.attendance_status = 'MEDICAL'), 0) AS medical, " +
                        "COALESCE(SUM(ar.attendance_status = 'LATE'), 0) AS late " +
                        "FROM attendance_records ar " +
                        "JOIN users student ON ar.student_id = student.user_id " +
                        "JOIN users officer ON student.department_id = officer.department_id " +
                        "WHERE officer.user_id = ? " +
                        "AND officer.role = 'Technical Officer' " +
                        "AND student.role = 'Undergraduate'";

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new int[] {
                            rs.getInt("total"),
                            rs.getInt("present"),
                            rs.getInt("absent"),
                            rs.getInt("medical"),
                            rs.getInt("late")
                    };
                }
            }
        }

        return new int[] {0, 0, 0, 0, 0};
    }

}
