package dao.undergraduate;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Enrolled Courses and Assessment Breakdown.
 * Directly mapped to UniMis database schema.
 */
public class UndergraduateCourseDAO {

    public static class EnrolledCourseDTO {
        public int courseId;
        public String courseCode;
        public String courseName;
        public int totalCredits;
        public String enrollmentType;
        public String departmentName;
        public String coordinatorName;

        // Sub-weights from courses table
        public double quizWeight;
        public double assignmentWeight;
        public double midTheoryWeight;
        public double midPracticalWeight;
        public double caWeight;
        public double endTheoryWeight;
        public double endPracticalWeight;
        public double finalWeight;
    }

    public static List<EnrolledCourseDTO> getEnrolledCoursesWithEvaluation(long studentId) {
        List<EnrolledCourseDTO> list = new ArrayList<>();
        String sql = "SELECT c.course_id, c.course_code, c.course_name, c.total_credits, e.enrollment_type, " +
                "d.department_name, COALESCE(u.full_name, 'Staff Coordinator') AS coordinator_name, " +
                "c.quiz_weight, c.assignment_weight, c.mid_theory_weight, c.mid_practical_weight, " +
                "c.ca_weight, c.end_theory_weight, c.end_practical_weight, c.final_weight " +
                "FROM enrollments e " +
                "JOIN courses c ON e.course_id = c.course_id " +
                "LEFT JOIN departments d ON c.department_id = d.department_id " +
                "LEFT JOIN course_lecturers cl ON c.course_id = cl.course_id AND cl.responsibility = 'Coordinator' " +
                "LEFT JOIN users u ON cl.lecturer_id = u.user_id " +
                "WHERE e.student_id = ? " +
                "ORDER BY c.course_code ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EnrolledCourseDTO dto = new EnrolledCourseDTO();
                    dto.courseId = rs.getInt("course_id");
                    dto.courseCode = rs.getString("course_code");
                    dto.courseName = rs.getString("course_name");
                    dto.totalCredits = (int) Math.round(rs.getDouble("total_credits"));
                    dto.enrollmentType = rs.getString("enrollment_type");
                    dto.departmentName = rs.getString("department_name");
                    dto.coordinatorName = rs.getString("coordinator_name");

                    dto.quizWeight = rs.getDouble("quiz_weight");
                    dto.assignmentWeight = rs.getDouble("assignment_weight");
                    dto.midTheoryWeight = rs.getDouble("mid_theory_weight");
                    dto.midPracticalWeight = rs.getDouble("mid_practical_weight");
                    dto.caWeight = rs.getDouble("ca_weight");
                    dto.endTheoryWeight = rs.getDouble("end_theory_weight");
                    dto.endPracticalWeight = rs.getDouble("end_practical_weight");
                    dto.finalWeight = rs.getDouble("final_weight");

                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}