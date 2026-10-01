package dao;

import model.Undergraduate;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UndergraduateDAO {

    public boolean insert(Undergraduate s) {

        String sql = """
                INSERT INTO undergraduates
                (user_id, registration_no, index_no, batch_year,
                 academic_year, student_type, intake, current_level,
                 current_semester, previous_credits, previous_grade_points,
                 admission_date, guardian_name, guardian_contact)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, s.getUserId());
            ps.setString(2, s.getRegistrationNo());
            ps.setString(3, s.getIndexNo());
            ps.setInt(4, s.getBatchYear());
            ps.setString(5, s.getAcademicYear());
            ps.setString(6, s.getStudentType());
            ps.setString(7, s.getIntake());

            if (s.getCurrentLevel() != null)
                ps.setInt(8, s.getCurrentLevel());
            else
                ps.setNull(8, Types.TINYINT);

            if (s.getCurrentSemester() != null)
                ps.setInt(9, s.getCurrentSemester());
            else
                ps.setNull(9, Types.TINYINT);

            ps.setBigDecimal(10, s.getPreviousCredits());
            ps.setBigDecimal(11, s.getPreviousGradePoints());

            if (s.getAdmissionDate() != null)
                ps.setDate(12, s.getAdmissionDate());
            else
                ps.setNull(12, Types.DATE);

            ps.setString(13, s.getGuardianName());
            ps.setString(14, s.getGuardianContact());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Undergraduate findById(Long userId) {

        String sql = "SELECT * FROM undergraduates WHERE user_id = ?";

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

    public List<Undergraduate> findAll() {

        List<Undergraduate> list = new ArrayList<>();

        String sql = "SELECT * FROM undergraduates ORDER BY user_id";

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

    public boolean update(Undergraduate s) {

        String sql = """
                UPDATE undergraduates
                SET registration_no = ?, index_no = ?, batch_year = ?,
                    academic_year = ?, student_type = ?, intake = ?,
                    current_level = ?, current_semester = ?,
                    previous_credits = ?, previous_grade_points = ?,
                    admission_date = ?, guardian_name = ?,
                    guardian_contact = ?
                WHERE user_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getRegistrationNo());
            ps.setString(2, s.getIndexNo());
            ps.setInt(3, s.getBatchYear());
            ps.setString(4, s.getAcademicYear());
            ps.setString(5, s.getStudentType());
            ps.setString(6, s.getIntake());

            if (s.getCurrentLevel() != null)
                ps.setInt(7, s.getCurrentLevel());
            else
                ps.setNull(7, Types.TINYINT);

            if (s.getCurrentSemester() != null)
                ps.setInt(8, s.getCurrentSemester());
            else
                ps.setNull(8, Types.TINYINT);

            ps.setBigDecimal(9, s.getPreviousCredits());
            ps.setBigDecimal(10, s.getPreviousGradePoints());

            if (s.getAdmissionDate() != null)
                ps.setDate(11, s.getAdmissionDate());
            else
                ps.setNull(11, Types.DATE);

            ps.setString(12, s.getGuardianName());
            ps.setString(13, s.getGuardianContact());
            ps.setLong(14, s.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long userId) {

        String sql = "DELETE FROM undergraduates WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Undergraduate mapResultSet(ResultSet rs) throws SQLException {

        return new Undergraduate(
                rs.getLong("user_id"),
                rs.getString("registration_no"),
                rs.getString("index_no"),
                rs.getInt("batch_year"),
                rs.getString("academic_year"),
                rs.getString("student_type"),
                rs.getString("intake"),
                (Integer) rs.getObject("current_level"),
                (Integer) rs.getObject("current_semester"),
                rs.getBigDecimal("previous_credits"),
                rs.getBigDecimal("previous_grade_points"),
                rs.getDate("admission_date"),
                rs.getString("guardian_name"),
                rs.getString("guardian_contact")
        );
    }
}