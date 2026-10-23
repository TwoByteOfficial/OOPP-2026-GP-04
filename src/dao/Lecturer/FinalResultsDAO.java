
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
                        "fr.course_code, " +
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

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    results.add(new Object[]{
                            rs.getString("registration_no"),
                            rs.getString("full_name"),
                            rs.getObject("total_credits"),
                            rs.getObject("ca_mark"),
                            rs.getObject("final_exam_mark"),
                            rs.getObject("final_mark"),
                            rs.getObject("attendance_percent"),
                            rs.getString("eligibility"),
                            rs.getString("grade"),
                            rs.getObject("grade_point"),
                            rs.getObject("sgpa"),
                            rs.getObject("semester_credits"),
                            rs.getObject("cgpa")
                    });
                }
            }
        }

        return results;
    }
}
