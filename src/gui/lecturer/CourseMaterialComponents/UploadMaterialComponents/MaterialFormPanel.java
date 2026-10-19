
package gui.lecturer.CourseMaterialComponents.UploadMaterialComponents;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MaterialFormPanel extends JPanel {

    private JComboBox<CourseOption> courseComboBox;
    private JTextField titleField;
    private JComboBox<String> typeComboBox;
    private JTextArea descriptionArea;
    private MaterialFilePicker filePicker;
    private JTextField externalUrlField;

    public MaterialFormPanel(List<Object[]> courses) {

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        createFields(courses);
    }

    private void createFields(List<Object[]> courses) {

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(7, 0, 7, 0);

        courseComboBox = new JComboBox<>();

        for (Object[] course : courses) {

            int courseId = ((Number) course[0]).intValue();
            String courseCode = String.valueOf(course[1]);
            String courseName = String.valueOf(course[2]);

            courseComboBox.addItem(
                    new CourseOption(
                            courseId,
                            courseCode + " - " + courseName
                    )
            );
        }

        titleField = new JTextField(25);

        typeComboBox = new JComboBox<>(new String[]{
                "Lecture Note",
                "Slide",
                "PDF",
                "Document",
                "Video",
                "Link",
                "Other"
        });

        descriptionArea = new JTextArea(4, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        filePicker = new MaterialFilePicker();

        externalUrlField = new JTextField(25);

        addField("Course", courseComboBox, gbc);
        addField("Material Title", titleField, gbc);
        addField("Material Type", typeComboBox, gbc);
        addField(
                "Description",
                new JScrollPane(descriptionArea),
                gbc
        );
        addField("Select File (optional)", filePicker, gbc);
        addField("External URL (optional)", externalUrlField, gbc);
    }

    private void addField(
            String labelText,
            JComponent field,
            GridBagConstraints gbc) {

        gbc.gridy++;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(new Color(27, 94, 32));

        add(label, gbc);

        gbc.gridy++;
        add(field, gbc);
    }

    // Existing getter methods

    public CourseOption getSelectedCourse() {
        return (CourseOption) courseComboBox.getSelectedItem();
    }

    public String getTitle() {
        return titleField.getText().trim();
    }

    public String getMaterialType() {
        return (String) typeComboBox.getSelectedItem();
    }

    public String getDescription() {
        return descriptionArea.getText().trim();
    }

    public String getFilePath() {
        return filePicker.getSelectedFilePath();
    }

    public String getExternalUrl() {
        String url = externalUrlField.getText().trim();
        return url.isEmpty() ? null : url;
    }

    // New setter methods for Edit Material

    public void setSelectedCourseId(int courseId) {

        for (int i = 0; i < courseComboBox.getItemCount(); i++) {

            CourseOption option = courseComboBox.getItemAt(i);

            if (option.getCourseId() == courseId) {
                courseComboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    public void setTitle(String title) {
        titleField.setText(title == null ? "" : title);
    }

    public void setDescription(String description) {
        descriptionArea.setText(
                description == null ? "" : description
        );
    }

    public void setMaterialType(String materialType) {
        if (materialType != null) {
            typeComboBox.setSelectedItem(materialType);
        }
    }

    public void setExternalUrl(String externalUrl) {
        externalUrlField.setText(
                externalUrl == null ? "" : externalUrl
        );
    }

    public void setFilePath(String filePath) {
        if (filePath != null && !filePath.trim().isEmpty()) {
            filePicker.setSelectedFilePath(filePath);
        } else {
            filePicker.clearSelection();
        }
    }

    public void clearForm() {

        titleField.setText("");
        descriptionArea.setText("");
        filePicker.clearSelection();
        externalUrlField.setText("");

        if (courseComboBox.getItemCount() > 0) {
            courseComboBox.setSelectedIndex(0);
        }

        typeComboBox.setSelectedIndex(0);
    }
}
