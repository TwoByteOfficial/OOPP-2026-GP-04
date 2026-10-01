package dao;

import model.Enrollment;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public boolean insert(Enrollment e) {

        String sql = """
                INSERT INTO enrollments
                (student_id, course_id, academic_year, semester,
                 enrollment_type, enrollment_status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, e.getStudentId());
            ps.setInt(2, e.getCourseId());
            ps.setString(3, e.getAcademicYear());
            ps.setString(4, e.getSemester());
            ps.setString(5, e.getEnrollmentType());
            ps.setString(6, e.getEnrollmentStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Enrollment findById(Long id) {

        String sql = "SELECT * FROM enrollments WHERE enrollment_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return mapResultSet(rs);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public List<Enrollment> findAll() {

        List<Enrollment> list = new ArrayList<>();

        String sql = "SELECT * FROM enrollments ORDER BY enrollment_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next())
                list.add(mapResultSet(rs));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public boolean update(Enrollment e) {

        String sql = """
                UPDATE enrollments
                SET student_id = ?, course_id = ?, academic_year = ?,
                    semester = ?, enrollment_type = ?,
                    enrollment_status = ?
                WHERE enrollment_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, e.getStudentId());
            ps.setInt(2, e.getCourseId());
            ps.setString(3, e.getAcademicYear());
            ps.setString(4, e.getSemester());
            ps.setString(5, e.getEnrollmentType());
            ps.setString(6, e.getEnrollmentStatus());
            ps.setLong(7, e.getEnrollmentId());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id) {

        String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private Enrollment mapResultSet(ResultSet rs) throws SQLException {

        return new Enrollment(
                rs.getLong("enrollment_id"),
                rs.getLong("student_id"),
                rs.getInt("course_id"),
                rs.getString("academic_year"),
                rs.getString("semester"),
                rs.getString("enrollment_type"),
                rs.getString("enrollment_status"),
                rs.getTimestamp("enrolled_at")
        );
    }
}