package dao;

import util.DBConnection;
import model.EligibilityResult;
import model.StudentGPA;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EligibilityGradeDAO {

    public List<EligibilityResult> getEligibilityByCourse(int courseId) throws SQLException {
        List<EligibilityResult> list = new ArrayList<>();
        String sql = "SELECT * FROM v_eligibility WHERE course_id = ? ORDER BY registration_no";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EligibilityResult er = new EligibilityResult();
                    er.setStudentId(rs.getLong("student_id"));
                    er.setRegistrationNo(rs.getString("registration_no"));
                    er.setFullName(rs.getString("full_name"));
                    er.setCourseId(rs.getInt("course_id"));
                    er.setCourseCode(rs.getString("course_code"));
                    er.setCourseName(rs.getString("course_name"));
                    er.setAttendancePercent(rs.getBigDecimal("attendance_percent"));
                    er.setCaMark(rs.getBigDecimal("ca_mark"));
                    er.setFinalExamEligibility(rs.getString("final_exam_eligibility"));
                    er.setAttendanceRequirement(rs.getString("attendance_requirement"));
                    er.setCaRequirement(rs.getString("ca_requirement"));
                    list.add(er);
                }
            }
        }
        return list;
    }

    public StudentGPA getStudentCGPA(long studentId) throws SQLException {
        String sql = "SELECT * FROM v_cgpa WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    StudentGPA gpa = new StudentGPA();
                    gpa.setStudentId(rs.getLong("student_id"));
                    gpa.setRegistrationNo(rs.getString("registration_no"));
                    gpa.setFullName(rs.getString("full_name"));
                    gpa.setPreviousCredits(rs.getBigDecimal("previous_credits"));
                    gpa.setPreviousGradePoints(rs.getBigDecimal("previous_grade_points"));
                    gpa.setCurrentSgpa(rs.getBigDecimal("current_sgpa"));
                    gpa.setSemesterCredits(rs.getBigDecimal("semester_credits"));
                    gpa.setTotalGradePoints(rs.getBigDecimal("total_grade_points"));
                    gpa.setCgpa(rs.getBigDecimal("cgpa"));
                    return gpa;
                }
            }
        }
        return null;
    }
}
