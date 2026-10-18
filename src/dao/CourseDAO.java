package dao;

import model.Course;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public boolean insert(Course c) {

        String sql = """
                INSERT INTO courses
                (course_code, course_name, description,
                 theory_credits, practical_credits, ca_weight,
                 department_id, academic_year, semester, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getCourseCode());
            ps.setString(2, c.getCourseName());
            ps.setString(3, c.getDescription());
            ps.setBigDecimal(4, c.getTheoryCredits());
            ps.setBigDecimal(5, c.getPracticalCredits());
            ps.setBigDecimal(6, c.getCaWeight());
            ps.setInt(7, c.getDepartmentId());
            ps.setString(8, c.getAcademicYear());
            ps.setString(9, c.getSemester());
            ps.setString(10, c.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Course findById(Integer id) {

        String sql = "SELECT * FROM courses WHERE course_id = ?";

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

    public List<Course> findAll() {

        List<Course> list = new ArrayList<>();

        String sql = "SELECT * FROM courses ORDER BY course_id";

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

    public boolean update(Course c) {

        String sql = """
                UPDATE courses
                SET course_code = ?, course_name = ?, description = ?,
                    theory_credits = ?, practical_credits = ?,
                    ca_weight = ?, department_id = ?,
                    academic_year = ?, semester = ?, status = ?
                WHERE course_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getCourseCode());
            ps.setString(2, c.getCourseName());
            ps.setString(3, c.getDescription());
            ps.setBigDecimal(4, c.getTheoryCredits());
            ps.setBigDecimal(5, c.getPracticalCredits());
            ps.setBigDecimal(6, c.getCaWeight());
            ps.setInt(7, c.getDepartmentId());
            ps.setString(8, c.getAcademicYear());
            ps.setString(9, c.getSemester());
            ps.setString(10, c.getStatus());
            ps.setInt(11, c.getCourseId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Integer id) {

        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Course mapResultSet(ResultSet rs) throws SQLException {

        return new Course(
                rs.getInt("course_id"),
                rs.getString("course_code"),
                rs.getString("course_name"),
                rs.getString("description"),
                rs.getBigDecimal("theory_credits"),
                rs.getBigDecimal("practical_credits"),
                rs.getBigDecimal("total_credits"),
                rs.getBigDecimal("ca_weight"),
                rs.getBigDecimal("final_weight"),
                rs.getInt("department_id"),
                rs.getString("academic_year"),
                rs.getString("semester"),
                rs.getString("status"),
                rs.getTimestamp("created_at")
        );
    }
}