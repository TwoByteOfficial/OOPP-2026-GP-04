package dao;

import util.DBConnection;
import model.Mark;
import java.sql.*;
import java.util.List;

public class AssessmentMarkDAO {

    public boolean saveMarksBatch(List<Mark> marks) throws SQLException {
        String sql = "INSERT INTO marks (assessment_id, student_id, mark, entered_by) " +
                     "VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE mark = VALUES(mark), entered_by = VALUES(entered_by)";
        Connection conn = DBConnection.getConnection();
        boolean success = false;
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Mark m : marks) {
                    stmt.setLong(1, m.getAssessmentId());
                    stmt.setLong(2, m.getStudentId());
                    stmt.setBigDecimal(3, m.getMark());
                    if (m.getEnteredBy() != null) stmt.setLong(4, m.getEnteredBy()); else stmt.setNull(4, Types.BIGINT);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            conn.commit();
            success = true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
        return success;
    }
}
