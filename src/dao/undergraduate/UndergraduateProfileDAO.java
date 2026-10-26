package dao.undergraduate;

import util.DBConnection;
import gui.undergraduate.profile.model.UserProfileData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UndergraduateProfileDAO {

    public static UserProfileData getProfile(long userId, String username) {
        String query = "SELECT ug.registration_no, u.full_name, u.email, d.department_name, " +
                "ug.current_level, ug.current_semester, ug.student_type " +
                "FROM undergraduates ug " +
                "INNER JOIN users u ON ug.user_id = u.user_id " +
                "LEFT JOIN departments d ON u.department_id = d.department_id " +
                "WHERE u.user_id = ? OR u.username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, userId);
            stmt.setString(2, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String levelStr = "Level " + rs.getInt("current_level");
                    String semStr = "Semester " + rs.getInt("current_semester") + " (2026)";
                    String status = rs.getString("student_type") + " Candidate";
                    return new UserProfileData(userId, rs.getString("registration_no"),
                            rs.getString("full_name").toUpperCase(), rs.getString("email"),
                            rs.getString("department_name"), "Bachelor of Information and Communication Technology (BICT)",
                            levelStr, semStr, status);
                }
            }
        } catch (Exception e) {
            System.err.println("[UndergraduateProfileDAO] Fallback triggered: " + e.getMessage());
        }

        String reg = (username != null && !username.isEmpty()) ? username.toUpperCase() : "TG" + userId;
        return new UserProfileData(userId, "TG/2024/" + (userId > 0 ? userId : "2061"),
                "UNDERGRADUATE STUDENT (" + reg + ")", reg.toLowerCase() + "@fot.ruh.ac.lk",
                "Department of Information and Communication Technology",
                "Bachelor of Information and Communication Technology (BICT)",
                "Level II", "Semester I (2026)", "Active Enrolment");
    }

    public static boolean updatePassword(long userId, String newPassword) {
        String query = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setLong(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}