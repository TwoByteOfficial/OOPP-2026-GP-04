package dao;

import model.Medical;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {

    public boolean insert(Medical m) {

        String sql = """
                INSERT INTO medicals
                (student_id, course_id, attendance_record_id,
                 medical_date, reason, document_path, approval_status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, m.getStudentId());
            ps.setInt(2, m.getCourseId());

            if (m.getAttendanceRecordId() != null)
                ps.setLong(3, m.getAttendanceRecordId());
            else
                ps.setNull(3, Types.BIGINT);

            ps.setDate(4, m.getMedicalDate());
            ps.setString(5, m.getReason());
            ps.setString(6, m.getDocumentPath());
            ps.setString(7, m.getApprovalStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Medical findById(Long id) {

        String sql = "SELECT * FROM medicals WHERE medical_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return mapResultSet(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Medical> findAll() {

        List<Medical> list = new ArrayList<>();

        String sql = "SELECT * FROM medicals ORDER BY medical_id";

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

    public boolean update(Medical m) {

        String sql = """
                UPDATE medicals
                SET student_id = ?, course_id = ?,
                    attendance_record_id = ?, medical_date = ?,
                    reason = ?, document_path = ?,
                    approval_status = ?, reviewed_by = ?,
                    reviewed_at = ?, review_comment = ?
                WHERE medical_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, m.getStudentId());
            ps.setInt(2, m.getCourseId());

            if (m.getAttendanceRecordId() != null)
                ps.setLong(3, m.getAttendanceRecordId());
            else
                ps.setNull(3, Types.BIGINT);

            ps.setDate(4, m.getMedicalDate());
            ps.setString(5, m.getReason());
            ps.setString(6, m.getDocumentPath());
            ps.setString(7, m.getApprovalStatus());

            if (m.getReviewedBy() != null)
                ps.setLong(8, m.getReviewedBy());
            else
                ps.setNull(8, Types.BIGINT);

            if (m.getReviewedAt() != null)
                ps.setTimestamp(9, m.getReviewedAt());
            else
                ps.setNull(9, Types.TIMESTAMP);

            ps.setString(10, m.getReviewComment());
            ps.setLong(11, m.getMedicalId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id) {

        String sql = "DELETE FROM medicals WHERE medical_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Medical mapResultSet(ResultSet rs) throws SQLException {

        return new Medical(
                rs.getLong("medical_id"),
                rs.getLong("student_id"),
                rs.getInt("course_id"),
                (Long) rs.getObject("attendance_record_id"),
                rs.getDate("medical_date"),
                rs.getString("reason"),
                rs.getString("document_path"),
                rs.getString("approval_status"),
                rs.getTimestamp("submitted_at"),
                (Long) rs.getObject("reviewed_by"),
                rs.getTimestamp("reviewed_at"),
                rs.getString("review_comment")
        );
    }
}