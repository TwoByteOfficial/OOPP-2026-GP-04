
package dao.Lecturer;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FinalResultsDAO {

    public List<Object[]> getFinalResults(int courseId)
            throws SQLException {

        List<Object[]> results = new ArrayList<>();

        String sql =
                "SELECT " +
                        "fr.registration_no, " +
                        "fr.full_name, " +
                        "fr.total_credits, " +
                        "fr.ca_mark, " +
                        "fr.final_exam_mark, " +
                        "fr.final_mark, " +
                        "fr.attendance_percent, " +
                        "fr.eligibility, " +
                        "fr.grade, " +
                        "fr.grade_point, " +
                        "sg.sgpa, " +
                        "sg.semester_credits, " +
                        "cg.cgpa " +
                        "FROM v_final_results fr " +
                        "LEFT JOIN v_sgpa sg " +
                        "ON sg.student_id = fr.student_id " +
                        "LEFT JOIN v_cgpa cg " +
                        "ON cg.student_id = fr.student_id " +
                        "WHERE fr.course_id = ? " +
                        "ORDER BY fr.registration_no";

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    results.add(new Object[]{
                            rs.getString("registration_no"), // 0
                            rs.getString("full_name"),       // 1
                            rs.getObject("total_credits"),   // 2
                            rs.getObject("ca_mark"),         // 3
                            rs.getObject("final_exam_mark"), // 4
                            rs.getObject("final_mark"),      // 5
                            rs.getObject("attendance_percent"), // 6
                            rs.getString("eligibility"),     // 7
                            rs.getString("grade"),           // 8
                            rs.getObject("grade_point"),     // 9
                            rs.getObject("sgpa"),            // 10
                            rs.getObject("semester_credits"),// 11
                            rs.getObject("cgpa")             // 12
                    });
                }
            }
        }

        return results;
    }
}
