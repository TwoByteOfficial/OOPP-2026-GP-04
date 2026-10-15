package dao;

import util.DBConnection;
import model.Course;
import model.CourseMaterial;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE status = 'Active' ORDER BY course_code";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToCourse(rs));
            }
        }
        return list;
    }

    public List<Course> getCoursesByLecturer(long lecturerId) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.* FROM courses c JOIN course_lecturers cl ON cl.course_id = c.course_id WHERE cl.lecturer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, lecturerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCourse(rs));
                }
            }
        }
        return list;
    }

    public List<CourseMaterial> getMaterialsByCourse(int courseId) throws SQLException {
        List<CourseMaterial> list = new ArrayList<>();
        String sql = "SELECT * FROM course_materials WHERE course_id = ? ORDER BY uploaded_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    public boolean addCourseMaterial(CourseMaterial material)
            throws SQLException {

        String sql = """
            INSERT INTO course_materials
            (
                course_id,
                lecturer_id,
                title,
                description,
                material_type,
                file_path,
                external_url
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setInt(
                    1,
                    material.getCourseId()
            );

            stmt.setLong(
                    2,
                    material.getLecturerId()
            );

            stmt.setString(
                    3,
                    material.getTitle()
            );

            stmt.setString(
                    4,
                    material.getDescription()
            );

            stmt.setString(
                    5,
                    material.getMaterialType()
            );

            stmt.setString(
                    6,
                    material.getFilePath()
            );

            stmt.setString(
                    7,
                    material.getExternalUrl()
            );

            return stmt.executeUpdate() > 0;
        }
    }


    public boolean updateCourseMaterial(
            CourseMaterial material
    ) throws SQLException {

        String sql = """
            UPDATE course_materials
            SET
                title = ?,
                description = ?,
                material_type = ?,
                file_path = ?,
                external_url = ?
            WHERE material_id = ?
              AND lecturer_id = ?
            """;

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setString(
                    1,
                    material.getTitle()
            );

            stmt.setString(
                    2,
                    material.getDescription()
            );

            stmt.setString(
                    3,
                    material.getMaterialType()
            );

            stmt.setString(
                    4,
                    material.getFilePath()
            );

            stmt.setString(
                    5,
                    material.getExternalUrl()
            );

            stmt.setLong(
                    6,
                    material.getMaterialId()
            );

            stmt.setLong(
                    7,
                    material.getLecturerId()
            );

            return stmt.executeUpdate() > 0;
        }
    }

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
