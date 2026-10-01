package dao;

import model.Lecturer;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LecturerDAO {

    public boolean insert(Lecturer lecturer) {

        String sql = """
                INSERT INTO lecturers
                (user_id, lecturer_code, title, specialization)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, lecturer.getUserId());
            ps.setString(2, lecturer.getLecturerCode());
            ps.setString(3, lecturer.getTitle());
            ps.setString(4, lecturer.getSpecialization());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Lecturer findById(Long userId) {

        String sql = "SELECT * FROM lecturers WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return mapResultSet(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Lecturer> findAll() {

        List<Lecturer> list = new ArrayList<>();

        String sql = "SELECT * FROM lecturers ORDER BY user_id";

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

    public boolean update(Lecturer lecturer) {

        String sql = """
                UPDATE lecturers
                SET lecturer_code = ?, title = ?, specialization = ?
                WHERE user_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lecturer.getLecturerCode());
            ps.setString(2, lecturer.getTitle());
            ps.setString(3, lecturer.getSpecialization());
            ps.setLong(4, lecturer.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long userId) {

        String sql = "DELETE FROM lecturers WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Lecturer mapResultSet(ResultSet rs) throws SQLException {

        return new Lecturer(
                rs.getLong("user_id"),
                rs.getString("lecturer_code"),
                rs.getString("title"),
                rs.getString("specialization")
        );
    }
}