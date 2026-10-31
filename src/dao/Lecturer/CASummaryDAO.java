package dao.Lecturer;

import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CASummaryDAO {

    public List<Object[]> getCASummary(int courseId)
            throws SQLException {

        List<Object[]> rows = new ArrayList<>();

        String sql =
                "SELECT registration_no, full_name, ca_mark, ca_weight, " +
                        "ROUND((ca_mark / NULLIF(ca_weight, 0)) * 100, 2) AS ca_percentage, " +
                        "ca_eligibility " +
                        "FROM v_ca_summary " +
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
                            rs.getBigDecimal("ca_mark"),
                            rs.getBigDecimal("ca_weight"),
                            rs.getBigDecimal("ca_percentage"),
                            rs.getString("ca_eligibility")
                    });
                }
            }
        }

        return rows;
    }
}