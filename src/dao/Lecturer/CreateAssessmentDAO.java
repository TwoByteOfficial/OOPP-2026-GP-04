package dao.Lecturer;

import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateAssessmentDAO {

    public List<Object[]> getAssignedCourses(long lecturerId)
            throws SQLException {

        List<Object[]> courses = new ArrayList<>();

        String sql =
                "SELECT c.course_id, c.course_code, c.course_name, " +
                        "c.ca_weight, c.quiz_weight, c.assignment_weight, " +
                        "c.mid_theory_weight, c.mid_practical_weight, " +
                        "c.end_theory_weight, c.end_practical_weight " +
                        "FROM courses c " +
                        "JOIN course_lecturers cl ON c.course_id = cl.course_id " +
                        "WHERE cl.lecturer_id = ? AND c.status = 'Active' " +
                        "ORDER BY c.course_code";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, lecturerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    courses.add(new Object[]{
                            rs.getInt("course_id"),
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getBigDecimal("ca_weight"),
                            rs.getBigDecimal("quiz_weight"),
                            rs.getBigDecimal("assignment_weight"),
                            rs.getBigDecimal("mid_theory_weight"),
                            rs.getBigDecimal("mid_practical_weight"),
                            rs.getBigDecimal("end_theory_weight"),
                            rs.getBigDecimal("end_practical_weight")
                    });
                }
            }
        }

        return courses;
    }

    public List<Object[]> getAssessments(
            long lecturerId,
            int courseId
    ) throws SQLException {

        List<Object[]> assessments = new ArrayList<>();

        String sql =
                "SELECT a.assessment_id, a.assessment_name, " +
                        "a.assessment_category, a.assessment_type, " +
                        "a.component, a.weight_percent, a.max_mark, " +
                        "a.assessment_date " +
                        "FROM assessments a " +
                        "WHERE a.course_id = ? " +
                        "AND EXISTS (" +
                        " SELECT 1 FROM course_lecturers cl " +
                        " WHERE cl.course_id = a.course_id " +
                        " AND cl.lecturer_id = ?" +
                        ") ORDER BY a.assessment_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setLong(2, lecturerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    assessments.add(new Object[]{
                            rs.getLong("assessment_id"),
                            rs.getString("assessment_name"),
                            rs.getString("assessment_category"),
                            rs.getString("assessment_type"),
                            rs.getString("component"),
                            rs.getBigDecimal("weight_percent"),
                            rs.getBigDecimal("max_mark"),
                            rs.getDate("assessment_date")
                    });
                }
            }
        }

        return assessments;
    }

    /**
     * Returns assessment types that:
     * 1. Have a configured weight in the selected course.
     * 2. Have not already been created for that course.
     */
    public List<AssessmentOption> getMissingAssessmentTypes(
            long lecturerId,
            int courseId
    ) throws SQLException {

        List<AssessmentOption> options = new ArrayList<>();
        Set<String> existingTypes = new HashSet<>();

        String existingSql =
                "SELECT a.assessment_type " +
                        "FROM assessments a " +
                        "JOIN course_lecturers cl ON cl.course_id = a.course_id " +
                        "WHERE a.course_id = ? AND cl.lecturer_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            // Read existing assessment types.
            try (PreparedStatement ps =
                         con.prepareStatement(existingSql)) {

                ps.setInt(1, courseId);
                ps.setLong(2, lecturerId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        existingTypes.add(rs.getString("assessment_type"));
                    }
                }
            }

            // Read the course's configured assessment weights.
            String courseSql =
                    "SELECT c.quiz_weight, c.assignment_weight, " +
                            "c.mid_theory_weight, c.mid_practical_weight, " +
                            "c.end_theory_weight, c.end_practical_weight " +
                            "FROM courses c " +
                            "JOIN course_lecturers cl ON cl.course_id = c.course_id " +
                            "WHERE c.course_id = ? " +
                            "AND cl.lecturer_id = ? " +
                            "AND c.status = 'Active'";

            try (PreparedStatement ps =
                         con.prepareStatement(courseSql)) {

                ps.setInt(1, courseId);
                ps.setLong(2, lecturerId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        throw new SQLException(
                                "Course not found or you are not assigned to it."
                        );
                    }

                    addOption(options, existingTypes,
                            "Quiz1", "CA", "Theory",
                            rs.getBigDecimal("quiz_weight"));

                    addOption(options, existingTypes,
                            "Quiz2", "CA", "Theory",
                            rs.getBigDecimal("quiz_weight"));

                    addOption(options, existingTypes,
                            "Quiz3", "CA", "Theory",
                            rs.getBigDecimal("quiz_weight"));

                    addOption(options, existingTypes,
                            "Assignment", "CA", "Theory",
                            rs.getBigDecimal("assignment_weight"));

                    addOption(options, existingTypes,
                            "Mid Exam Theory", "CA", "Theory",
                            rs.getBigDecimal("mid_theory_weight"));

                    addOption(options, existingTypes,
                            "Mid Exam Practical", "CA", "Practical",
                            rs.getBigDecimal("mid_practical_weight"));

                    addOption(options, existingTypes,
                            "End Exam Theory", "FINAL", "Theory",
                            rs.getBigDecimal("end_theory_weight"));

                    addOption(options, existingTypes,
                            "End Exam Practical", "FINAL", "Practical",
                            rs.getBigDecimal("end_practical_weight"));
                }
            }
        }

        return options;
    }

    private void addOption(
            List<AssessmentOption> options,
            Set<String> existingTypes,
            String type,
            String category,
            String component,
            BigDecimal weight
    ) {

        if (weight != null
                && weight.compareTo(BigDecimal.ZERO) > 0
                && !existingTypes.contains(type)) {

            options.add(new AssessmentOption(
                    type,
                    category,
                    component,
                    weight
            ));
        }
    }

    public void createAssessment(
            long lecturerId,
            int courseId,
            String assessmentName,
            String assessmentType,
            Date assessmentDate
    ) throws SQLException {

        if (assessmentName == null || assessmentName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Assessment name is required."
            );
        }

        String category;
        String component;
        String weightColumn;

        switch (assessmentType) {

            case "Quiz1":
            case "Quiz2":
            case "Quiz3":
                category = "CA";
                component = "Theory";
                weightColumn = "quiz_weight";
                break;

            case "Assignment":
                category = "CA";
                component = "Theory";
                weightColumn = "assignment_weight";
                break;

            case "Mid Exam Theory":
                category = "CA";
                component = "Theory";
                weightColumn = "mid_theory_weight";
                break;

            case "Mid Exam Practical":
                category = "CA";
                component = "Practical";
                weightColumn = "mid_practical_weight";
                break;

            case "End Exam Theory":
                category = "FINAL";
                component = "Theory";
                weightColumn = "end_theory_weight";
                break;

            case "End Exam Practical":
                category = "FINAL";
                component = "Practical";
                weightColumn = "end_practical_weight";
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported assessment type."
                );
        }

        // Only the known database column names above can be used here.
        String courseSql =
                "SELECT c." + weightColumn + " AS configured_weight " +
                        "FROM courses c " +
                        "JOIN course_lecturers cl ON c.course_id = cl.course_id " +
                        "WHERE c.course_id = ? " +
                        "AND cl.lecturer_id = ? " +
                        "AND c.status = 'Active' " +
                        "FOR UPDATE";

        String duplicateSql =
                "SELECT COUNT(*) FROM assessments " +
                        "WHERE course_id = ? AND assessment_type = ?";

        String insertSql =
                "INSERT INTO assessments " +
                        "(course_id, assessment_name, assessment_category, " +
                        "assessment_type, component, weight_percent, " +
                        "max_mark, assessment_date, created_by) " +
                        "VALUES (?, ?, ?, ?, ?, ?, 100, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            boolean oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);

            try {
                BigDecimal configuredWeight;

                // Verify course assignment and configured weight.
                try (PreparedStatement ps =
                             con.prepareStatement(courseSql)) {

                    ps.setInt(1, courseId);
                    ps.setLong(2, lecturerId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                    "Course not found or you are not assigned to it."
                            );
                        }

                        configuredWeight =
                                rs.getBigDecimal("configured_weight");
                    }
                }

                if (configuredWeight == null
                        || configuredWeight.compareTo(BigDecimal.ZERO) <= 0) {

                    throw new IllegalArgumentException(
                            "This assessment component has no configured weight."
                    );
                }

                // Prevent duplicate assessment types.
                try (PreparedStatement ps =
                             con.prepareStatement(duplicateSql)) {

                    ps.setInt(1, courseId);
                    ps.setString(2, assessmentType);

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();

                        if (rs.getInt(1) > 0) {
                            throw new IllegalArgumentException(
                                    assessmentType +
                                            " already exists for this course."
                            );
                        }
                    }
                }

                // The quiz group weight is used by the best-two-of-three view.
                BigDecimal assessmentWeight =
                        assessmentType.startsWith("Quiz")
                                ? BigDecimal.ZERO
                                : configuredWeight;

                try (PreparedStatement ps =
                             con.prepareStatement(insertSql)) {

                    ps.setInt(1, courseId);
                    ps.setString(2, assessmentName.trim());
                    ps.setString(3, category);
                    ps.setString(4, assessmentType);
                    ps.setString(5, component);
                    ps.setBigDecimal(6, assessmentWeight);

                    if (assessmentDate == null) {
                        ps.setNull(7, Types.DATE);
                    } else {
                        ps.setDate(7, assessmentDate);
                    }

                    ps.setLong(8, lecturerId);
                    ps.executeUpdate();
                }

                con.commit();

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;

            } finally {
                con.setAutoCommit(oldAutoCommit);
            }
        }
    }

    public static class AssessmentOption {

        private final String type;
        private final String category;
        private final String component;
        private final BigDecimal weight;

        public AssessmentOption(
                String type,
                String category,
                String component,
                BigDecimal weight
        ) {
            this.type = type;
            this.category = category;
            this.component = component;
            this.weight = weight;
        }

        public String getType() {
            return type;
        }

        public String getCategory() {
            return category;
        }

        public String getComponent() {
            return component;
        }

        public BigDecimal getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return getDisplayName(type);
        }

        private String getDisplayName(String value) {
            switch (value) {
                case "Quiz1": return "Quiz 01";
                case "Quiz2": return "Quiz 02";
                case "Quiz3": return "Quiz 03";
                case "Mid Exam Theory": return "Mid Examination - Theory";
                case "Mid Exam Practical": return "Mid Examination - Practical";
                case "End Exam Theory": return "End Examination - Theory";
                case "End Exam Practical": return "End Examination - Practical";
                default: return value;
            }
        }
    }
}