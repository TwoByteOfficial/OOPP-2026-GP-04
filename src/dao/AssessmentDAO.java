package dao;

import model.Assessment;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssessmentDAO {

    public boolean insert(Assessment a) {

        String sql = """
                INSERT INTO assessments
                (course_id, assessment_name, assessment_category,
                 assessment_type, component, weight_percent,
                 max_mark, assessment_date, created_by)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, a.getCourseId());
            ps.setString(2, a.getAssessmentName());
            ps.setString(3, a.getAssessmentCategory());
            ps.setString(4, a.getAssessmentType());
            ps.setString(5, a.getComponent());
            ps.setBigDecimal(6, a.getWeightPercent());
            ps.setBigDecimal(7, a.getMaxMark());

            if (a.getAssessmentDate() != null)
                ps.setDate(8, a.getAssessmentDate());
            else
                ps.setNull(8, Types.DATE);

            if (a.getCreatedBy() != null)
                ps.setLong(9, a.getCreatedBy());
            else
                ps.setNull(9, Types.BIGINT);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Assessment findById(Long id) {

        String sql = "SELECT * FROM assessments WHERE assessment_id = ?";

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

    public List<Assessment> findAll() {

        List<Assessment> list = new ArrayList<>();

        String sql = "SELECT * FROM assessments ORDER BY assessment_id";

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

    public boolean update(Assessment a) {

        String sql = """
                UPDATE assessments
                SET course_id = ?, assessment_name = ?,
                    assessment_category = ?, assessment_type = ?,
                    component = ?, weight_percent = ?, max_mark = ?,
                    assessment_date = ?, created_by = ?
                WHERE assessment_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, a.getCourseId());
            ps.setString(2, a.getAssessmentName());
            ps.setString(3, a.getAssessmentCategory());
            ps.setString(4, a.getAssessmentType());
            ps.setString(5, a.getComponent());
            ps.setBigDecimal(6, a.getWeightPercent());
            ps.setBigDecimal(7, a.getMaxMark());

            if (a.getAssessmentDate() != null)
                ps.setDate(8, a.getAssessmentDate());
            else
                ps.setNull(8, Types.DATE);

            if (a.getCreatedBy() != null)
                ps.setLong(9, a.getCreatedBy());
            else
                ps.setNull(9, Types.BIGINT);

            ps.setLong(10, a.getAssessmentId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id) {

        String sql = "DELETE FROM assessments WHERE assessment_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Assessment mapResultSet(ResultSet rs) throws SQLException {

        return new Assessment(
                rs.getLong("assessment_id"),
                rs.getInt("course_id"),
                rs.getString("assessment_name"),
                rs.getString("assessment_category"),
                rs.getString("assessment_type"),
                rs.getString("component"),
                rs.getBigDecimal("weight_percent"),
                rs.getBigDecimal("max_mark"),
                rs.getDate("assessment_date"),
                (Long) rs.getObject("created_by")
        );
    }
}