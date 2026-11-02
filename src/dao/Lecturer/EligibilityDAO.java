package dao.Lecturer;

import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EligibilityDAO {

    public List<Object[]> getEligibilityRows(int courseId)
            throws SQLException {

        List<Object[]> rows = new ArrayList<>();

        String sql =
                "SELECT registration_no, full_name, " +
                        "attendance_percent, ca_mark, " +
                        "attendance_requirement, ca_requirement, " +
                        "final_exam_eligibility " +
                        "FROM v_eligibility " +
                        "WHERE course_id = ? " +
                        "ORDER BY registration_no";

        try (PreparedStatement statement =
                     DBConnection.LEC_CON.prepareStatement(sql)) {

            statement.setInt(1, courseId);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {

                    rows.add(new Object[]{
                            rs.getString("registration_no"),
                            rs.getString("full_name"),
                            rs.getBigDecimal("attendance_percent"),
                            rs.getBigDecimal("ca_mark"),
                            rs.getString("attendance_requirement"),
                            rs.getString("ca_requirement"),
                            rs.getString("final_exam_eligibility")
                    });
                }
            }
        }

        return rows;
    }
}