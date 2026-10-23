package dao;

import model.CourseMaterial;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseMaterialDAO {

    public boolean insert(CourseMaterial m) {

        String sql = """
                INSERT INTO course_materials
                (course_id, lecturer_id, title, description,
                 material_type, file_path, external_url)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, m.getCourseId());
            ps.setLong(2, m.getLecturerId());
            ps.setString(3, m.getTitle());
            ps.setString(4, m.getDescription());
            ps.setString(5, m.getMaterialType());
            ps.setString(6, m.getFilePath());
            ps.setString(7, m.getExternalUrl());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public CourseMaterial findById(Long id) {

        String sql = "SELECT * FROM course_materials WHERE material_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return mapResultSet(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<CourseMaterial> findAll() {

        List<CourseMaterial> list = new ArrayList<>();

        String sql = "SELECT * FROM course_materials ORDER BY material_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next())
                list.add(mapResultSet(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean update(CourseMaterial m) {

        String sql = """
                UPDATE course_materials
                SET course_id = ?, lecturer_id = ?, title = ?,
                    description = ?, material_type = ?, file_path = ?,
                    external_url = ?
                WHERE material_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, m.getCourseId());
            ps.setLong(2, m.getLecturerId());
            ps.setString(3, m.getTitle());
            ps.setString(4, m.getDescription());
            ps.setString(5, m.getMaterialType());
            ps.setString(6, m.getFilePath());
            ps.setString(7, m.getExternalUrl());
            ps.setLong(8, m.getMaterialId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id) {

        String sql = "DELETE FROM course_materials WHERE material_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private CourseMaterial mapResultSet(ResultSet rs) throws SQLException {

        return new CourseMaterial(
                rs.getLong("material_id"),
                rs.getInt("course_id"),
                rs.getLong("lecturer_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("material_type"),
                rs.getString("file_path"),
                rs.getString("external_url"),
                rs.getTimestamp("uploaded_at"),
                rs.getTimestamp("updated_at")
        );
    }
}