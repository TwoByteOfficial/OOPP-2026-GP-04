package dao;

import model.Mark;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarkDAO {

    public boolean insert(Mark m) {

        String sql = """
                INSERT INTO marks
                (assessment_id, student_id, mark, entered_by)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, m.getAssessmentId());
            ps.setLong(2, m.getStudentId());
            ps.setBigDecimal(3, m.getMark());

            if (m.getEnteredBy() != null)
                ps.setLong(4, m.getEnteredBy());
            else
                ps.setNull(4, Types.BIGINT);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Mark findById(Long id) {

        String sql = "SELECT * FROM marks WHERE mark_id = ?";

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

    public List<Mark> findAll() {

        List<Mark> list = new ArrayList<>();

        String sql = "SELECT * FROM marks ORDER BY mark_id";

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

    public boolean update(Mark m) {

        String sql = """
                UPDATE marks
                SET assessment_id = ?, student_id = ?,
                    mark = ?, entered_by = ?
                WHERE mark_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, m.getAssessmentId());
            ps.setLong(2, m.getStudentId());
            ps.setBigDecimal(3, m.getMark());

            if (m.getEnteredBy() != null)
                ps.setLong(4, m.getEnteredBy());
            else
                ps.setNull(4, Types.BIGINT);

            ps.setLong(5, m.getMarkId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id) {

        String sql = "DELETE FROM marks WHERE mark_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Mark mapResultSet(ResultSet rs) throws SQLException {

        return new Mark(
                rs.getLong("mark_id"),
                rs.getLong("assessment_id"),
                rs.getLong("student_id"),
                rs.getBigDecimal("mark"),
                (Long) rs.getObject("entered_by"),
                rs.getTimestamp("entered_at"),
                rs.getTimestamp("updated_at")
        );
    }
}