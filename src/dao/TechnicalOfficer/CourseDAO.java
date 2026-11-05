package dao.TechnicalOfficer;

import util.DBConnection;
import model.Course;
import model.CourseMaterial;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * [OOP - ABSTRACTION]: Data Access Object for retrieving course information.
 * Encapsulates the SQL logic required to query course catalogs and course materials.
 */
public class CourseDAO {

    /**
     * [OOP - ABSTRACTION]: Retrieves all active courses and transforms raw database tuples
     * into a high-level List of Course objects.
     *
     * @return List of active Course objects ordered by course code
     * @throws SQLException if a database query error occurs
     */
    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE status = 'Active' ORDER BY course_code";
        try (Statement stmt = DBConnection.TO_CON.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToCourse(rs));
            }
        }
        return list;
    }

    /**
     * Retrieves all courses assigned to a specific lecturer.
     *
     * @param lecturerId The user ID of the lecturer
     * @return List of Course objects taught by this lecturer
     * @throws SQLException if a query error occurs
     */
    public List<Course> getCoursesByLecturer(long lecturerId) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.* FROM courses c JOIN course_lecturers cl ON cl.course_id = c.course_id WHERE cl.lecturer_id = ?";
        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {
            stmt.setLong(1, lecturerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCourse(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves learning materials uploaded for a course, ordered with newest uploads first.
     *
     * @param courseId The course ID
     * @return List of CourseMaterial objects
     * @throws SQLException if a database error occurs
     */
    public List<CourseMaterial> getMaterialsByCourse(int courseId) throws SQLException {
        List<CourseMaterial> list = new ArrayList<>();
        String sql = "SELECT * FROM course_materials WHERE course_id = ? ORDER BY uploaded_at DESC";
        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CourseMaterial cm = new CourseMaterial();
                    cm.setMaterialId(rs.getLong("material_id"));
                    cm.setCourseId(rs.getInt("course_id"));
                    cm.setLecturerId(rs.getLong("lecturer_id"));
                    cm.setTitle(rs.getString("title"));
                    cm.setDescription(rs.getString("description"));
                    cm.setMaterialType(rs.getString("material_type"));
                    cm.setFilePath(rs.getString("file_path"));
                    cm.setExternalUrl(rs.getString("external_url"));
                    cm.setUploadedAt(rs.getTimestamp("uploaded_at"));
                    list.add(cm);
                }
            }
        }
        return list;
    }

    /**
     * [OOP - ENCAPSULATION]: Private helper method encapsulating the mapping logic
     * from a ResultSet row to a Course domain model.
     *
     * @param rs The active ResultSet positioned at a valid row
     * @return A populated Course model object
     * @throws SQLException if reading column values fails
     */
    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setCourseCode(rs.getString("course_code"));
        c.setCourseName(rs.getString("course_name"));
        c.setDescription(rs.getString("description"));
        c.setTheoryCredits(rs.getBigDecimal("theory_credits"));
        c.setPracticalCredits(rs.getBigDecimal("practical_credits"));
        c.setTotalCredits(rs.getBigDecimal("total_credits"));
        c.setCaWeight(rs.getBigDecimal("ca_weight"));
        c.setFinalWeight(rs.getBigDecimal("final_weight"));
        c.setDepartmentId(rs.getInt("department_id"));
        c.setAcademicYear(rs.getString("academic_year"));
        c.setSemester(rs.getString("semester"));
        c.setStatus(rs.getString("status"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}
