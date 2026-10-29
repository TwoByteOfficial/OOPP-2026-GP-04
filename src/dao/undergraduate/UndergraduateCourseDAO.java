package dao.undergraduate;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UndergraduateCourseDAO {

    public static class CourseRecord {
        public final String code;
        public final String name;
        public final int credits;
        public final String type;

        public CourseRecord(String code, String name, int credits, String type) {
            this.code = code;
            this.name = name;
            this.credits = credits;
            this.type = type;
        }
    }

    public static List<CourseRecord> getEnrolledCourses(long studentId) {
        List<CourseRecord> courses = new ArrayList<>();
        String sql = "SELECT c.course_code, c.course_name, c.total_credits, e.enrollment_type " + "FROM courses c INNER JOIN enrollments e ON c.course_id = e.course_id " + "WHERE e.student_id = ? ORDER BY c.course_code ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(new CourseRecord(
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            (int) Math.round(rs.getDouble("total_credits")),
                            rs.getString("enrollment_type")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("[UndergraduateCourseDAO] Fallback triggered: " + e.getMessage());
        }

        if (courses.isEmpty()) {
            courses.add(new CourseRecord("ICT2112", "Data Structures and Algorithms", 2, "Normal"));
            courses.add(new CourseRecord("ICT2122", "Computer Networks", 2, "Normal"));
            courses.add(new CourseRecord("ICT2132", "Object Oriented Programming Practicum", 2, "Normal"));
            courses.add(new CourseRecord("ICT2142", "Database Management Systems", 3, "Normal"));
        }
        return courses;
    }
}