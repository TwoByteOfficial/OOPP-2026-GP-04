package dao.Lecturer;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<Object[]> getCoursesByLecturer(long lecturerUserId)
            throws SQLException {

        List<Object[]> courses = new ArrayList<>();

        String sql =
                "SELECT c.course_code, " +
                        "c.course_name, " +
                        "c.total_credits, " +
                        "c.ca_weight, " +
                        "COUNT(DISTINCT e.student_id) AS student_count " +
                        "FROM courses c " +
                        "JOIN course_lecturers cl " +
                        "ON c.course_id = cl.course_id " +
                        "LEFT JOIN enrollments e " +
                        "ON c.course_id = e.course_id " +
                        "AND c.academic_year = e.academic_year " +
                        "AND c.semester = e.semester " +
                        "AND e.enrollment_status = 'Enrolled' " +
                        "WHERE cl.lecturer_id = ? " +
                        "AND c.status = 'Active' " +
                        "GROUP BY c.course_id, c.course_code, " +
                        "c.course_name, c.total_credits, c.ca_weight " +
                        "ORDER BY c.course_code";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, lecturerUserId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Object[] course = {
                            resultSet.getString("course_code"),
                            resultSet.getString("course_name"),
                            resultSet.getBigDecimal("total_credits"),
                            resultSet.getBigDecimal("ca_weight"),
                            resultSet.getInt("student_count")
                    };

                    courses.add(course);
                }
            }
        }

        return courses;
    }
}