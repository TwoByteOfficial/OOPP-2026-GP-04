
package dao.Lecturer;

import model.CourseMaterial;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseMaterialDAO {

    // Get materials uploaded by this lecturer.
    public List<CourseMaterial> getMaterialsByLecturer(
            long lecturerId) throws SQLException {

        List<CourseMaterial> materials = new ArrayList<>();

        String sql =
                "SELECT material_id, course_id, lecturer_id, title, " +
                        "description, material_type, file_path, external_url, " +
                        "uploaded_at " +
                        "FROM course_materials " +
                        "WHERE lecturer_id = ? " +
                        "ORDER BY uploaded_at DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, lecturerId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    CourseMaterial material = new CourseMaterial();

                    material.setMaterialId(
                            rs.getLong("material_id"));
                    material.setCourseId(
                            rs.getInt("course_id"));
                    material.setLecturerId(
                            rs.getLong("lecturer_id"));
                    material.setTitle(
                            rs.getString("title"));
                    material.setDescription(
                            rs.getString("description"));
                    material.setMaterialType(
                            rs.getString("material_type"));
                    material.setFilePath(
                            rs.getString("file_path"));
                    material.setExternalUrl(
                            rs.getString("external_url"));
                    material.setUploadedAt(
                            rs.getTimestamp("uploaded_at"));

                    materials.add(material);
                }
            }
        }

        return materials;
    }

    // Add a new material record.
    public boolean addMaterial(CourseMaterial material)
            throws SQLException {

        String sql =
                "INSERT INTO course_materials " +
                        "(course_id, lecturer_id, title, description, " +
                        "material_type, file_path, external_url) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, material.getCourseId());
            statement.setLong(2, material.getLecturerId());
            statement.setString(3, material.getTitle());
            statement.setString(4, material.getDescription());
            statement.setString(5, material.getMaterialType());
            statement.setString(6, material.getFilePath());
            statement.setString(7, material.getExternalUrl());

            return statement.executeUpdate() > 0;
        }
    }

    // Delete a material belonging to this lecturer.
    public boolean deleteMaterial(
            long materialId, long lecturerId) throws SQLException {

        String sql =
                "DELETE FROM course_materials " +
                        "WHERE material_id = ? AND lecturer_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, materialId);
            statement.setLong(2, lecturerId);

            return statement.executeUpdate() > 0;
        }
    }


    // Update a material belonging to this lecturer.
    public boolean updateMaterial(CourseMaterial material)
            throws SQLException {

        String sql =
                "UPDATE course_materials SET " +
                        "course_id = ?, title = ?, description = ?, " +
                        "material_type = ?, file_path = ?, external_url = ? " +
                        "WHERE material_id = ? AND lecturer_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, material.getCourseId());
            statement.setString(2, material.getTitle());
            statement.setString(3, material.getDescription());
            statement.setString(4, material.getMaterialType());
            statement.setString(5, material.getFilePath());
            statement.setString(6, material.getExternalUrl());
            statement.setLong(7, material.getMaterialId());
            statement.setLong(8, material.getLecturerId());

            return statement.executeUpdate() > 0;
        }
    }

}
