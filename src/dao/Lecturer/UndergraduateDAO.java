package dao.Lecturer;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UndergraduateDAO {

    // Retrieve undergraduates enrolled in courses assigned to the specified lecturer
    public List<Object[]> getUndergraduatesByLecturer(long lecturerUserId, Integer courseIdFilter, String searchQuery)
            throws SQLException {

        List<Object[]> rows = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT " +
                "ug.user_id, " +
                "ug.registration_no, " +
                "ug.index_no, " +
                "usr.full_name, " +
                "ug.student_type, " +
                "ug.intake, " +
                "ug.batch_year, " +
                "ug.current_level, " +
                "ug.current_semester, " +
                "usr.email, " +
                "usr.contact_no " +
                "FROM enrollments e " +
                "JOIN undergraduates ug ON e.student_id = ug.user_id " +
                "JOIN users usr ON ug.user_id = usr.user_id " +
                "JOIN courses c ON e.course_id = c.course_id " +
                "JOIN course_lecturers cl ON c.course_id = cl.course_id " +
                "WHERE cl.lecturer_id = ? " +
                "AND c.status = 'Active' " +
                "AND e.enrollment_status = 'Enrolled' "
        );

        if (courseIdFilter != null && courseIdFilter > 0) {
            sql.append("AND c.course_id = ? ");
        }

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (LOWER(ug.registration_no) LIKE ? OR LOWER(usr.full_name) LIKE ? OR LOWER(ug.index_no) LIKE ?) ");
        }

        sql.append("ORDER BY ug.registration_no");

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            statement.setLong(paramIndex++, lecturerUserId);

            if (courseIdFilter != null && courseIdFilter > 0) {
                statement.setInt(paramIndex++, courseIdFilter);
            }

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String searchPattern = "%" + searchQuery.trim().toLowerCase() + "%";
                statement.setString(paramIndex++, searchPattern);
                statement.setString(paramIndex++, searchPattern);
                statement.setString(paramIndex++, searchPattern);
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                            rs.getLong("user_id"),
                            rs.getString("registration_no"),
                            rs.getString("index_no") == null ? "N/A" : rs.getString("index_no"),
                            rs.getString("full_name"),
                            rs.getString("student_type"),
                            rs.getString("intake"),
                            "L" + rs.getInt("current_level") + " S" + rs.getInt("current_semester"),
                            rs.getString("email"),
                            rs.getString("contact_no") == null ? "N/A" : rs.getString("contact_no")
                    });
                }
            }
        }

        return rows;
    }

    // Get detailed academic profile for a single undergraduate
    public Object[] getUndergraduateDetails(long studentUserId) throws SQLException {
        String sql =
                "SELECT ug.user_id, ug.registration_no, ug.index_no, usr.full_name, " +
                "usr.email, usr.contact_no, usr.address, ug.student_type, ug.batch_year, " +
                "ug.current_level, ug.current_semester, ug.guardian_name, ug.guardian_contact, " +
                "sg.sgpa, cg.cgpa " +
                "FROM undergraduates ug " +
                "JOIN users usr ON ug.user_id = usr.user_id " +
                "LEFT JOIN v_sgpa sg ON sg.student_id = ug.user_id " +
                "LEFT JOIN v_cgpa cg ON cg.student_id = ug.user_id " +
                "WHERE ug.user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, studentUserId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                            rs.getLong("user_id"),
                            rs.getString("registration_no"),
                            rs.getString("index_no"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("contact_no"),
                            rs.getString("address"),
                            rs.getString("student_type"),
                            rs.getInt("batch_year"),
                            rs.getInt("current_level"),
                            rs.getInt("current_semester"),
                            rs.getString("guardian_name"),
                            rs.getString("guardian_contact"),
                            rs.getBigDecimal("sgpa"),
                            rs.getBigDecimal("cgpa")
                    };
                }
            }
        }

        return null;
    }
}
