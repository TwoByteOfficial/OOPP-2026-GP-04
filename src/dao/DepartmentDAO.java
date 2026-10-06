package dao;

import util.DBConnection;
import model.Department;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public List<Department> getAllDepartments() throws SQLException {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM departments ORDER BY department_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Department d = new Department();
                d.setDepartmentId(rs.getInt("department_id"));
                d.setDepartmentCode(rs.getString("department_code"));
                d.setDepartmentName(rs.getString("department_name"));
                d.setDescription(rs.getString("description"));
                d.setStatus(rs.getString("status"));
                d.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(d);
            }
        }
        return list;
    }
}
