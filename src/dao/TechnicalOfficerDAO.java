package dao;

import model.TechnicalOfficer;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TechnicalOfficerDAO {

    public boolean insert(TechnicalOfficer officer) {

        String sql = """
                INSERT INTO technical_officers
                (user_id, technical_officer_code, designation)
                VALUES (?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, officer.getUserId());
            ps.setString(2, officer.getTechnicalOfficerCode());
            ps.setString(3, officer.getDesignation());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public TechnicalOfficer findById(Long userId) {

        String sql = "SELECT * FROM technical_officers WHERE user_id = ?";

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

    public List<TechnicalOfficer> findAll() {

        List<TechnicalOfficer> list = new ArrayList<>();

        String sql = "SELECT * FROM technical_officers ORDER BY user_id";

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

    public boolean update(TechnicalOfficer officer) {

        String sql = """
                UPDATE technical_officers
                SET technical_officer_code = ?, designation = ?
                WHERE user_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, officer.getTechnicalOfficerCode());
            ps.setString(2, officer.getDesignation());
            ps.setLong(3, officer.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long userId) {

        String sql = "DELETE FROM technical_officers WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private TechnicalOfficer mapResultSet(ResultSet rs) throws SQLException {

        return new TechnicalOfficer(
                rs.getLong("user_id"),
                rs.getString("technical_officer_code"),
                rs.getString("designation")
        );
    }
}