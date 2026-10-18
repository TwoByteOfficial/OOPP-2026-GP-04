
package gui.lecturer.MyCourseComponents;

import dao.Lecturer.CourseDAO;

import java.sql.SQLException;
import java.util.List;

public class MyCourseLoader {

    private final CourseDAO courseDAO;

    public MyCourseLoader() {
        courseDAO = new CourseDAO();
    }

    public List<Object[]> loadCourses(long lecturerUserId)
            throws SQLException {

        return courseDAO.getCoursesByLecturer(lecturerUserId);
    }
}
