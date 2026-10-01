package dao;

import model.Admin;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    public boolean insert(Admin admin) {

        String sql = """
                INSERT INTO admins
                (user_id, admin_code, designation)
                VALUES (?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, admin.getUserId());
            ps.setString(2, admin.getAdminCode());
            ps.setString(3, admin.getDesignation());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Admin findById(Long userId) {

        String sql = "SELECT * FROM admins WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Admin> findAll() {

        List<Admin> list = new ArrayList<>();

        String sql = "SELECT * FROM admins ORDER BY user_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean update(Admin admin) {

        String sql = """
                UPDATE admins
                SET admin_code = ?, designation = ?
                WHERE user_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, admin.getAdminCode());
            ps.setString(2, admin.getDesignation());
            ps.setLong(3, admin.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long userId) {

        String sql = "DELETE FROM admins WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Admin mapResultSet(ResultSet rs) throws SQLException {

        return new Admin(
                rs.getLong("user_id"),
                rs.getString("admin_code"),
                rs.getString("designation")
        );
    }
}