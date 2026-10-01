package dao;

import model.CourseLecturer;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseLecturerDAO {

    public boolean insert(CourseLecturer cl) {

        String sql = """
                INSERT INTO course_lecturers
                (course_id, lecturer_id, responsibility)
                VALUES (?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cl.getCourseId());
            ps.setLong(2, cl.getLecturerId());
            ps.setString(3, cl.getResponsibility());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public CourseLecturer findById(Integer courseId, Long lecturerId) {

        String sql = """
                SELECT * FROM course_lecturers
                WHERE course_id = ? AND lecturer_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setLong(2, lecturerId);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return mapResultSet(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<CourseLecturer> findAll() {

        List<CourseLecturer> list = new ArrayList<>();

        String sql = "SELECT * FROM course_lecturers";

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

    public boolean update(CourseLecturer cl) {

        String sql = """
                UPDATE course_lecturers
                SET responsibility = ?
                WHERE course_id = ? AND lecturer_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cl.getResponsibility());
            ps.setInt(2, cl.getCourseId());
            ps.setLong(3, cl.getLecturerId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Integer courseId, Long lecturerId) {

        String sql = """
                DELETE FROM course_lecturers
                WHERE course_id = ? AND lecturer_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setLong(2, lecturerId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private CourseLecturer mapResultSet(ResultSet rs) throws SQLException {

        return new CourseLecturer(
                rs.getInt("course_id"),
                rs.getLong("lecturer_id"),
                rs.getString("responsibility"),
                rs.getTimestamp("assigned_at")
        );
    }
}