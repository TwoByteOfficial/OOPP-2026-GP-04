package dao;

import model.SystemSetting;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SystemSettingDAO {

    public boolean insert(SystemSetting s) {

        String sql = """
                INSERT INTO system_settings
                (setting_key, setting_value, description)
                VALUES (?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getSettingKey());
            ps.setString(2, s.getSettingValue());
            ps.setString(3, s.getDescription());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public SystemSetting findByKey(String key) {

        String sql = """
                SELECT * FROM system_settings
                WHERE setting_key = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, key);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return mapResultSet(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<SystemSetting> findAll() {

        List<SystemSetting> list = new ArrayList<>();

        String sql = "SELECT * FROM system_settings ORDER BY setting_key";

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

    public boolean update(SystemSetting s) {

        String sql = """
                UPDATE system_settings
                SET setting_value = ?, description = ?
                WHERE setting_key = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getSettingValue());
            ps.setString(2, s.getDescription());
            ps.setString(3, s.getSettingKey());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String key) {

        String sql = """
                DELETE FROM system_settings
                WHERE setting_key = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, key);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private SystemSetting mapResultSet(ResultSet rs) throws SQLException {

        return new SystemSetting(
                rs.getString("setting_key"),
                rs.getString("setting_value"),
                rs.getString("description")
        );
    }
}