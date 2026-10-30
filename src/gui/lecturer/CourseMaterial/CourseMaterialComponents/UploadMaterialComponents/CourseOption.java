
package gui.lecturer.CourseMaterial.CourseMaterialComponents.UploadMaterialComponents;

public class CourseOption {

    private final int courseId;
    private final String courseName;

    public CourseOption(int courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    @Override
    public String toString() {
        return courseName;
    }
}
