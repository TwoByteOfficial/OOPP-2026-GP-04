package dao;

import model.GradeScale;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeScaleDAO {

    public boolean insert(GradeScale g) {

        String sql = """
                INSERT INTO grade_scales
                (grade_code, minimum_mark, grade_point, description)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, g.getGradeCode());
            ps.setBigDecimal(2, g.getMinimumMark());
            ps.setBigDecimal(3, g.getGradePoint());
            ps.setString(4, g.getDescription());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public GradeScale findById(Integer id) {

        String sql = "SELECT * FROM grade_scales WHERE grade_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return mapResultSet(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<GradeScale> findAll() {

        List<GradeScale> list = new ArrayList<>();

        String sql = "SELECT * FROM grade_scales ORDER BY grade_id";

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

    public boolean update(GradeScale g) {

        String sql = """
                UPDATE grade_scales
                SET grade_code = ?, minimum_mark = ?,
                    grade_point = ?, description = ?
                WHERE grade_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, g.getGradeCode());
            ps.setBigDecimal(2, g.getMinimumMark());
            ps.setBigDecimal(3, g.getGradePoint());
            ps.setString(4, g.getDescription());
            ps.setInt(5, g.getGradeId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Integer id) {

        String sql = "DELETE FROM grade_scales WHERE grade_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private GradeScale mapResultSet(ResultSet rs) throws SQLException {

        return new GradeScale(
                rs.getInt("grade_id"),
                rs.getString("grade_code"),
                rs.getBigDecimal("minimum_mark"),
                rs.getBigDecimal("grade_point"),
                rs.getString("description")
        );
    }
}