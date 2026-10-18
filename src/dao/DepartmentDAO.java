package dao;

import model.Department;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public boolean insert(Department d) {

        String sql = """
                INSERT INTO departments
                (department_code, department_name, description, status)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, d.getDepartmentCode());
            ps.setString(2, d.getDepartmentName());
            ps.setString(3, d.getDescription());
            ps.setString(4, d.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Department findById(Integer id) {

        String sql = "SELECT * FROM departments WHERE department_id = ?";

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

    public List<Department> findAll() {

        List<Department> list = new ArrayList<>();

        String sql = "SELECT * FROM departments ORDER BY department_id";

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

    public boolean update(Department d) {

        String sql = """
                UPDATE departments
                SET department_code = ?, department_name = ?,
                    description = ?, status = ?
                WHERE department_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, d.getDepartmentCode());
            ps.setString(2, d.getDepartmentName());
            ps.setString(3, d.getDescription());
            ps.setString(4, d.getStatus());
            ps.setInt(5, d.getDepartmentId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Integer id) {

        String sql = "DELETE FROM departments WHERE department_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Department mapResultSet(ResultSet rs) throws SQLException {

        return new Department(
                rs.getInt("department_id"),
                rs.getString("department_code"),
                rs.getString("department_name"),
                rs.getString("description"),
                rs.getString("status"),
                rs.getTimestamp("created_at")
        );
    }
}