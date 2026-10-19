
package gui.lecturer.CourseMaterialComponents.UploadMaterialComponents;

public class MaterialFormValidator {

    private MaterialFormValidator() {
        // Utility class
    }

    public static String validate(
            String title,
            CourseOption selectedCourse,
            String filePath,
            String externalUrl) {

        if (selectedCourse == null) {
            return "Please select a course.";
        }

        if (title == null || title.trim().isEmpty()) {
            return "Please enter a material title.";
        }

        boolean hasFile =
                filePath != null && !filePath.trim().isEmpty();

        boolean hasUrl =
                externalUrl != null
                        && !externalUrl.trim().isEmpty();

        if (!hasFile && !hasUrl) {
            return "Please select a file or enter an external URL.";
        }

        if (hasUrl && !isValidUrl(externalUrl.trim())) {
            return "Enter a valid URL starting with http:// or https://.";
        }

        return null;
    }

    private static boolean isValidUrl(String url) {
        return url.matches("(?i)^https?://[^\\s]+$");
    }
}
