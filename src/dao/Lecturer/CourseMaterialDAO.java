package dao.Lecturer;

import model.CourseMaterial;
import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseMaterialDAO {

    // READ: Get materials uploaded by this lecturer.
    public List<CourseMaterial> getMaterialsByLecturer(
            long lecturerId) throws SQLException {

        List<CourseMaterial> materials = new ArrayList<>();

        String sql =
                "SELECT cm.material_id, cm.course_id, cm.lecturer_id, " +
                        "c.course_code, c.course_name, cm.title, cm.description, " +
                        "cm.material_type, cm.file_path, cm.external_url, " +
                        "cm.uploaded_at " +
                        "FROM course_materials cm " +
                        "INNER JOIN courses c ON cm.course_id = c.course_id " +
                        "WHERE cm.lecturer_id = ? " +
                        "ORDER BY cm.uploaded_at DESC";

        try (PreparedStatement statement =
                     DBConnection.LEC_CON.prepareStatement(sql)) {

            statement.setLong(1, lecturerId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    CourseMaterial material = new CourseMaterial();

                    material.setMaterialId(rs.getLong("material_id"));
                    material.setCourseId(rs.getInt("course_id"));
                    material.setLecturerId(rs.getLong("lecturer_id"));
                    material.setCourseCode(rs.getString("course_code"));
                    material.setCourseName(rs.getString("course_name"));
                    material.setTitle(rs.getString("title"));
                    material.setDescription(rs.getString("description"));
                    material.setMaterialType(rs.getString("material_type"));
                    material.setFilePath(rs.getString("file_path"));
                    material.setExternalUrl(rs.getString("external_url"));
                    material.setUploadedAt(rs.getTimestamp("uploaded_at"));

                    materials.add(material);
                }
            }
        }

        return materials;
    }

    // Check whether this course is assigned to this lecturer.
    private boolean isAssignedCourse(
            long lecturerId,
            int courseId) throws SQLException {

        String sql =
                "SELECT 1 FROM course_lecturers " +
                        "WHERE lecturer_id = ? AND course_id = ? LIMIT 1";

        try (PreparedStatement statement =
                     DBConnection.LEC_CON.prepareStatement(sql)) {

            statement.setLong(1, lecturerId);
            statement.setInt(2, courseId);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Basic input validation shared by CREATE and UPDATE.
    private void validateMaterial(CourseMaterial material) {

        if (material == null) {
            throw new IllegalArgumentException(
                    "Material details cannot be empty.");
        }

        if (material.getCourseId() <= 0) {
            throw new IllegalArgumentException(
                    "Please select a valid course.");
        }

        if (material.getLecturerId() <= 0) {
            throw new IllegalArgumentException(
                    "Invalid lecturer account.");
        }

        if (material.getTitle() == null ||
                material.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Material title is required.");
        }

        boolean hasFile = material.getFilePath() != null
                && !material.getFilePath().trim().isEmpty();

        boolean hasUrl = material.getExternalUrl() != null
                && !material.getExternalUrl().trim().isEmpty();

        if (!hasFile && !hasUrl) {
            throw new IllegalArgumentException(
                    "Select a file or enter an external URL.");
        }
    }

    // CREATE: Add a material only to a course assigned to the lecturer.
    public boolean addMaterial(CourseMaterial material)
            throws SQLException {

        validateMaterial(material);

        String sql =
                "INSERT INTO course_materials " +
                        "(course_id, lecturer_id, title, description, " +
                        "material_type, file_path, external_url) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        if (!isAssignedCourse(
                material.getLecturerId(),
                material.getCourseId())) {
            throw new SQLException(
                    "You are not assigned to the selected course.");
        }

        try (PreparedStatement statement =
                     DBConnection.LEC_CON.prepareStatement(sql)) {

            statement.setInt(1, material.getCourseId());
            statement.setLong(2, material.getLecturerId());
            statement.setString(3, material.getTitle().trim());
            statement.setString(4, material.getDescription());
            statement.setString(5, material.getMaterialType());
            statement.setString(6, material.getFilePath());
            statement.setString(7, material.getExternalUrl());

            return statement.executeUpdate() == 1;
        }
    }

    // UPDATE: Update only a material owned by this lecturer.
    public boolean updateMaterial(CourseMaterial material)
            throws SQLException {

        validateMaterial(material);

        if (material.getMaterialId() <= 0) {
            throw new IllegalArgumentException(
                    "Invalid material ID.");
        }

        String sql =
                "UPDATE course_materials SET " +
                        "course_id = ?, title = ?, description = ?, " +
                        "material_type = ?, file_path = ?, external_url = ? " +
                        "WHERE material_id = ? AND lecturer_id = ?";

        if (!isAssignedCourse(
                material.getLecturerId(),
                material.getCourseId())) {
            throw new SQLException(
                    "You are not assigned to the selected course.");
        }

        try (PreparedStatement statement =
                     DBConnection.LEC_CON.prepareStatement(sql)) {

            statement.setInt(1, material.getCourseId());
            statement.setString(2, material.getTitle().trim());
            statement.setString(3, material.getDescription());
            statement.setString(4, material.getMaterialType());
            statement.setString(5, material.getFilePath());
            statement.setString(6, material.getExternalUrl());
            statement.setLong(7, material.getMaterialId());
            statement.setLong(8, material.getLecturerId());

            return statement.executeUpdate() > 0;
        }
    }

    // DELETE: Delete only a material owned by this lecturer.
    public boolean deleteMaterial(
            long materialId,
            long lecturerId) throws SQLException {

        if (materialId <= 0 || lecturerId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid material or lecturer ID.");
        }

        String sql =
                "DELETE FROM course_materials " +
                        "WHERE material_id = ? AND lecturer_id = ?";

        try (PreparedStatement statement =
                     DBConnection.LEC_CON.prepareStatement(sql)) {

            statement.setLong(1, materialId);
            statement.setLong(2, lecturerId);

            return statement.executeUpdate() > 0;
        }
    }
}