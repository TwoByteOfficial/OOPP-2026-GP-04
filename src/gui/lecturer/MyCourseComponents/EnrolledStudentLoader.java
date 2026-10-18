
package gui.lecturer.MyCourseComponents;

import dao.Lecturer.CourseDAO;

import java.sql.SQLException;
import java.util.List;

public class EnrolledStudentLoader {

    private final CourseDAO courseDAO;

    public EnrolledStudentLoader() {
        courseDAO = new CourseDAO();
    }

    public List<Object[]> loadStudents(int courseId)
            throws SQLException {

        return courseDAO.getEnrolledStudentsByCourse(courseId);
    }
}
