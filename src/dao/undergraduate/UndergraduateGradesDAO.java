package dao.undergraduate;

import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Academic Transcript, GPV, SGPA/CGPA, and Honours Class evaluation.
 * Interfaces with MySQL analytics views (`v_final_results`, `v_cgpa`, `v_sgpa`) to retrieve
 * completed module scores, letter grades, credit weightings, and calculate degree class standing.
 */
public class UndergraduateGradesDAO {

    /**
     * Data Transfer Object representing individual course grade results and assessment performance.
     */
    public static class StudentCourseGradeDTO {
        public String courseCode;
        public String courseName;
        public int credits;
        public double caMark;
        public double finalExamMark;
        public double totalMark;
        public String grade;
        public double gpv;
        public String status;
    }

    /**
     * Data Transfer Object encapsulating cumulative grade point averages, credit totals,
     * and academic honours classification.
     */
    public static class TranscriptSummaryDTO {
        public double cgpa = 0.00;
        public double sgpa = 0.00;
        public int totalCredits = 0;
        public String academicStanding = "Good Standing";
    }

    /**
     * Queries finalized academic results for all completed modules registered to the specified student,
     * returning records sorted in ascending alphabetical order by course code.
     *
     * @param studentId Authenticated student primary key identifier
     * @return List of populated {@link StudentCourseGradeDTO} records
     */
    public static List<StudentCourseGradeDTO> getStudentGrades(long studentId) {
        List<StudentCourseGradeDTO> list = new ArrayList<>();
        String sql = "SELECT course_code, course_name, total_credits, " +
                "COALESCE(ca_mark, 0) AS ca_mark, " +
                "COALESCE(final_exam_mark, 0) AS final_exam_mark, " +
                "COALESCE(final_mark, 0) AS final_mark, " +
                "grade, grade_point " +
                "FROM v_final_results WHERE student_id = ? ORDER BY course_code ASC";

        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StudentCourseGradeDTO dto = new StudentCourseGradeDTO();
                    dto.courseCode = rs.getString("course_code");
                    dto.courseName = rs.getString("course_name");
                    dto.credits = (int) Math.round(rs.getDouble("total_credits"));
                    dto.caMark = rs.getDouble("ca_mark");
                    dto.finalExamMark = rs.getDouble("final_exam_mark");
                    dto.totalMark = rs.getDouble("final_mark");
                    dto.grade = rs.getString("grade");
                    dto.gpv = rs.getDouble("grade_point");
                    dto.status = "Completed";
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Queries cumulative GPA, current semester GPA, and earned semester credits from analytical database views,
     * and computes degree class classification or academic warning standing.
     *
     * @param studentId Authenticated student primary key identifier
     * @return Populated {@link TranscriptSummaryDTO} containing GPA metrics and academic standing
     */
    public static TranscriptSummaryDTO getTranscriptSummary(long studentId) {
        TranscriptSummaryDTO summary = new TranscriptSummaryDTO();
        String sql = "SELECT c.cgpa, s.sgpa, s.semester_credits " +
                "FROM v_cgpa c " +
                "JOIN v_sgpa s ON c.student_id = s.student_id " +
                "WHERE c.student_id = ?";

        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    summary.cgpa = rs.getDouble("cgpa");
                    summary.sgpa = rs.getDouble("sgpa");
                    summary.totalCredits = rs.getInt("semester_credits");

                    // -------------------------------------------------------------
                    // Degree Honours Classification & Academic Standing
                    // -------------------------------------------------------------
                    if (summary.cgpa >= 3.70) summary.academicStanding = "Dean's List / First Class";
                    else if (summary.cgpa >= 3.30) summary.academicStanding = "Second Class Upper";
                    else if (summary.cgpa >= 3.00) summary.academicStanding = "Second Class Lower";
                    else if (summary.cgpa >= 2.00) summary.academicStanding = "Pass";
                    else summary.academicStanding = "Academic Warning";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return summary;
    }
}