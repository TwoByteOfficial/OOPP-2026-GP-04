package dao;

import model.Undergraduate;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database operations used only by the Undergraduate module.
 * Does not modify other role modules.
 */
public class UndergraduateDAO {

    public Undergraduate getProfile(long userId) throws SQLException {
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.contact_no, " +
                "u.address, u.profile_picture, u.department_id, u.user_status, " +
                "u.created_at, u.updated_at, " +
                "ug.registration_no, ug.index_no, ug.batch_year, ug.academic_year, " +
                "ug.student_type, ug.intake, ug.current_level, ug.current_semester, " +
                "ug.previous_credits, ug.previous_grade_points, ug.admission_date, " +
                "ug.guardian_name, ug.guardian_contact " +
                "FROM users u JOIN undergraduates ug ON ug.user_id = u.user_id " +
                "WHERE u.user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Undergraduate s = new Undergraduate();
                s.setUserId(rs.getLong("user_id"));
                s.setUsername(rs.getString("username"));
                s.setFullName(rs.getString("full_name"));
                s.setEmail(rs.getString("email"));
                s.setContactNo(rs.getString("contact_no"));
                s.setAddress(rs.getString("address"));
                s.setProfilePicture(rs.getString("profile_picture"));
                Object departmentId = rs.getObject("department_id");
                s.setDepartmentId(departmentId == null ? null : ((Number) departmentId).intValue());
                s.setUserStatus(rs.getString("user_status"));
                // The user's existing local database version does not contain
                // nic/date_of_birth/gender columns, so those optional model fields
                // intentionally remain unset.
                s.setCreatedAt(rs.getTimestamp("created_at"));
                s.setUpdatedAt(rs.getTimestamp("updated_at"));
                s.setRegistrationNo(rs.getString("registration_no"));
                s.setIndexNo(rs.getString("index_no"));
                s.setBatchYear(rs.getInt("batch_year"));
                s.setAcademicYear(rs.getString("academic_year"));
                s.setStudentType(rs.getString("student_type"));
                s.setIntake(rs.getString("intake"));
                s.setCurrentLevel(rs.getInt("current_level"));
                s.setCurrentSemester(rs.getInt("current_semester"));
                s.setPreviousCredits(rs.getBigDecimal("previous_credits"));
                s.setPreviousGradePoints(rs.getBigDecimal("previous_grade_points"));
                s.setAdmissionDate(rs.getDate("admission_date"));
                s.setGuardianName(rs.getString("guardian_name"));
                s.setGuardianContact(rs.getString("guardian_contact"));
                return s;
            }
        }
    }

    /** Only the four profile fields allowed by the SRS are updated. */
    public boolean updateContactDetails(long userId, String email, String contactNo,
                                        String address, String profilePicture) throws SQLException {
        String sql = "UPDATE users SET email = ?, contact_no = ?, address = ?, profile_picture = ? " +
                "WHERE user_id = ? AND EXISTS (SELECT 1 FROM undergraduates ug WHERE ug.user_id = users.user_id)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ps.setString(2, contactNo.trim());
            ps.setString(3, address.trim());
            if (profilePicture == null || profilePicture.trim().isEmpty()) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, profilePicture.trim());
            }
            ps.setLong(5, userId);
            return ps.executeUpdate() == 1;
        }
    }

    public List<String[]> getCourses(long studentId) throws SQLException {
        String sql = "SELECT c.course_code, c.course_name, c.theory_credits, c.practical_credits, " +
                "c.total_credits, c.academic_year, c.semester, " +
                "COALESCE(GROUP_CONCAT(DISTINCT CONCAT(COALESCE(l.title,''),' ',u.full_name) " +
                "ORDER BY u.full_name SEPARATOR ', '), 'Not assigned') AS lecturers " +
                "FROM enrollments e JOIN courses c ON c.course_id=e.course_id " +
                "LEFT JOIN course_lecturers cl ON cl.course_id=c.course_id " +
                "LEFT JOIN lecturers l ON l.user_id=cl.lecturer_id " +
                "LEFT JOIN users u ON u.user_id=l.user_id " +
                "WHERE e.student_id=? AND e.enrollment_status='Enrolled' " +
                "GROUP BY c.course_id, c.course_code, c.course_name, c.theory_credits, " +
                "c.practical_credits, c.total_credits, c.academic_year, c.semester " +
                "ORDER BY c.course_code";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                List<String[]> rows = new ArrayList<>();
                while (rs.next()) rows.add(new String[]{
                        rs.getString("course_code"), rs.getString("course_name"),
                        rs.getString("theory_credits"), rs.getString("practical_credits"),
                        rs.getString("total_credits"), rs.getString("lecturers"),
                        rs.getString("academic_year"), rs.getString("semester")
                });
                return rows;
            }
        }
    }

    public List<String[]> getAttendance(long studentId) throws SQLException {
        String sql = "SELECT c.course_code, c.course_name, s.component, " +
                "COUNT(s.attendance_session_id) AS total_sessions, " +
                "SUM(CASE WHEN ar.attendance_status IN ('PRESENT','LATE') " +
                "OR (ar.attendance_status='MEDICAL' AND m.approval_status='Approved') THEN 1 ELSE 0 END) AS attended, " +
                "SUM(CASE WHEN ar.attendance_status='MEDICAL' THEN 1 ELSE 0 END) AS medical_sessions, " +
                "ROUND(100.0 * SUM(CASE WHEN ar.attendance_status IN ('PRESENT','LATE') " +
                "OR (ar.attendance_status='MEDICAL' AND m.approval_status='Approved') THEN 1 ELSE 0 END) / " +
                "NULLIF(COUNT(s.attendance_session_id),0), 2) AS attendance_percentage " +
                "FROM enrollments e JOIN courses c ON c.course_id=e.course_id " +
                "JOIN attendance_sessions s ON s.course_id=c.course_id " +
                "LEFT JOIN attendance_records ar ON ar.attendance_session_id=s.attendance_session_id AND ar.student_id=e.student_id " +
                "LEFT JOIN medicals m ON m.attendance_record_id=ar.attendance_record_id " +
                "WHERE e.student_id=? AND e.enrollment_status='Enrolled' " +
                "GROUP BY c.course_id, c.course_code, c.course_name, s.component ORDER BY c.course_code, s.component";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                List<String[]> rows = new ArrayList<>();
                while (rs.next()) rows.add(new String[]{
                        rs.getString("course_code"), rs.getString("course_name"), rs.getString("component"),
                        rs.getString("total_sessions"), rs.getString("attended"),
                        rs.getString("medical_sessions"), rs.getString("attendance_percentage") == null
                        ? "N/A" : rs.getString("attendance_percentage") + "%"
                });
                return rows;
            }
        }
    }

    public List<String[]> getMedicals(long studentId) throws SQLException {
        String sql = "SELECT m.medical_date, c.course_code, c.course_name, m.reason, " +
                "m.approval_status, m.document_path, m.review_comment " +
                "FROM medicals m JOIN courses c ON c.course_id=m.course_id " +
                "WHERE m.student_id=? ORDER BY m.medical_date DESC, m.submitted_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                List<String[]> rows = new ArrayList<>();
                while (rs.next()) rows.add(new String[]{
                        String.valueOf(rs.getDate("medical_date")), rs.getString("course_code"),
                        rs.getString("course_name"), rs.getString("reason"),
                        rs.getString("approval_status"), rs.getString("document_path"),
                        rs.getString("review_comment")
                });
                return rows;
            }
        }
    }

    public List<String[]> getResults(long studentId) throws SQLException {
        String sql = "SELECT c.course_code, c.course_name, a.assessment_name, a.assessment_category, " +
                "a.weight_percent, m.mark, gs.grade_code, gs.grade_point " +
                "FROM enrollments e JOIN courses c ON c.course_id=e.course_id " +
                "LEFT JOIN assessments a ON a.course_id=c.course_id " +
                "LEFT JOIN marks m ON m.assessment_id=a.assessment_id AND m.student_id=e.student_id " +
                "LEFT JOIN grade_scales gs ON gs.grade_id = (" +
                "SELECT gs2.grade_id FROM grade_scales gs2 WHERE m.mark >= gs2.minimum_mark " +
                "ORDER BY gs2.minimum_mark DESC LIMIT 1) " +
                "WHERE e.student_id=? AND e.enrollment_status IN ('Enrolled','Completed') " +
                "ORDER BY c.course_code, a.assessment_category, a.assessment_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                List<String[]> rows = new ArrayList<>();
                while (rs.next()) rows.add(new String[]{
                        rs.getString("course_code"), rs.getString("course_name"),
                        rs.getString("assessment_name") == null ? "No results available" : rs.getString("assessment_name"),
                        rs.getString("assessment_category") == null ? "-" : rs.getString("assessment_category"),
                        rs.getString("weight_percent") == null ? "-" : rs.getString("weight_percent") + "%",
                        rs.getString("mark") == null ? "Pending" : rs.getString("mark"),
                        rs.getString("grade_code") == null ? "-" : rs.getString("grade_code"),
                        rs.getString("grade_point") == null ? "-" : rs.getString("grade_point")
                });
                return rows;
            }
        }
    }

    /** Current semester GPA and cumulative GPA from the database views. */
    public List<String[]> getGPA(long studentId) throws SQLException {
        String sql = "SELECT 'SGPA' AS metric, sgpa AS value, semester_credits AS credits " +
                "FROM v_sgpa WHERE student_id=? UNION ALL " +
                "SELECT 'CGPA' AS metric, cgpa AS value, " +
                "(previous_credits + semester_credits) AS credits " +
                "FROM v_cgpa WHERE student_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setLong(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                List<String[]> rows = new ArrayList<>();
                while (rs.next()) {
                    Object value = rs.getObject("value");
                    Object credits = rs.getObject("credits");
                    rows.add(new String[]{rs.getString("metric"), value == null ? "Not available" : value.toString(),
                            credits == null ? "0" : credits.toString()});
                }
                return rows;
            }
        }
    }

    public List<String[]> getTimetable(long studentId) throws SQLException {
        String sql = "SELECT t.day_of_week, t.start_time, t.end_time, c.course_code, c.course_name, " +
                "t.component, t.venue, t.semester, t.academic_year " +
                "FROM undergraduates ug JOIN timetable_entries t ON t.batch_year=ug.batch_year " +
                "JOIN courses c ON c.course_id=t.course_id " +
                "WHERE ug.user_id=? AND t.department_id=COALESCE((SELECT department_id FROM users WHERE user_id=?),t.department_id) " +
                "AND t.semester = CASE WHEN ug.current_semester=1 THEN 'Semester I' ELSE 'Semester II' END " +
                "ORDER BY FIELD(t.day_of_week,'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'), t.start_time";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setLong(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                List<String[]> rows = new ArrayList<>();
                while (rs.next()) rows.add(new String[]{
                        rs.getString("day_of_week"), String.valueOf(rs.getTime("start_time")),
                        String.valueOf(rs.getTime("end_time")), rs.getString("course_code"),
                        rs.getString("course_name"), rs.getString("component"),
                        rs.getString("venue"), rs.getString("academic_year"), rs.getString("semester")
                });
                return rows;
            }
        }
    }

    public List<String[]> getNotices() throws SQLException {
        String sql = "SELECT title, notice_type, content, published_at, expires_at " +
                "FROM notices WHERE status='Published' AND (target_role='All' OR target_role='Undergraduate') " +
                "AND (expires_at IS NULL OR expires_at >= NOW()) ORDER BY published_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<String[]> rows = new ArrayList<>();
            while (rs.next()) rows.add(new String[]{
                    rs.getString("title"), rs.getString("notice_type"), rs.getString("content"),
                    String.valueOf(rs.getTimestamp("published_at")),
                    rs.getTimestamp("expires_at") == null ? "No expiry" : String.valueOf(rs.getTimestamp("expires_at"))
            });
            return rows;
        }
    }
}
