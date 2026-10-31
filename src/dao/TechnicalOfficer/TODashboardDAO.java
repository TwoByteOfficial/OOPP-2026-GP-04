
package dao.TechnicalOfficer;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TODashboardDAO {

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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }




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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }



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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }



    public int getTodaySessionsCount(long userId)
            throws SQLException {

        String sql =
                "SELECT COUNT(*) " +
                        "FROM timetable_entries te " +
                        "JOIN users officer ON te.department_id = officer.department_id " +
                        "WHERE officer.user_id = ? " +
                        "AND officer.role = 'Technical Officer' " +
                        "AND te.day_of_week = DAYNAME(CURDATE())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }







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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
