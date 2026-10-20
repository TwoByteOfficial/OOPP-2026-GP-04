
package gui.lecturer.CourseMaterialComponents;

import gui.lecturer.CourseMaterialComponents.UploadMaterialComponents.CourseOption;
import gui.lecturer.CourseMaterialComponents.UploadMaterialComponents.MaterialFormPanel;
import gui.lecturer.CourseMaterialComponents.UploadMaterialComponents.MaterialFormValidator;
import model.CourseMaterial;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UploadMaterialDialog extends JDialog {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private final long lecturerId;
    private final MaterialFormPanel formPanel;
    private final boolean editMode;

    private CourseMaterial material;

    // Upload a new material.
    public UploadMaterialDialog(
            Window owner,
            long lecturerId,
            List<Object[]> courses) {

        this(owner, lecturerId, courses, null);
    }

    // Upload or edit a material.
    public UploadMaterialDialog(
            Window owner,
            long lecturerId,
            List<Object[]> courses,
            CourseMaterial existingMaterial) {

        super(
                owner,
                existingMaterial == null
                        ? "Upload Course Material"
                        : "Edit Course Material",
                ModalityType.APPLICATION_MODAL
        );

        this.lecturerId = lecturerId;
        this.editMode = existingMaterial != null;
        this.material = existingMaterial;

        formPanel = new MaterialFormPanel(courses);

        if (editMode) {
            loadMaterialDetails(existingMaterial);
        }

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(560, 650);
        setMinimumSize(new Dimension(500, 600));
        createLayout();
        setLocationRelativeTo(owner);
    }

    private void createLayout() {

        JPanel mainPanel = new JPanel(
                new BorderLayout(0, 18)
        );

        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(22, 25, 22, 25)
        );

        mainPanel.add(createHeader(), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(createButtons(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createHeader() {

        JPanel header = new JPanel();
        header.setLayout(
                new BoxLayout(header, BoxLayout.Y_AXIS)
        );
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel(
                editMode
                        ? "Edit Learning Material"
                        : "Upload Learning Material"
        );

        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(DEEP_GREEN);

        JLabel description = new JLabel(
                editMode
                        ? "Update the details of this learning resource."
                        : "Add learning resources for your assigned courses."
        );

        description.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        description.setForeground(Color.GRAY);

        header.add(title);
        header.add(Box.createVerticalStrut(7));
        header.add(description);

        return header;
    }

    private JPanel createButtons() {

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );

        buttonPanel.setBackground(Color.WHITE);

        JButton cancelButton = new JButton("Cancel");

        cancelButton.addActionListener(e -> {
            material = null;
            dispose();
        });

        JButton saveButton = new JButton(
                editMode ? "Update Material" : "Save Material"
        );

        saveButton.setBackground(DEEP_GREEN);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);

        saveButton.addActionListener(e -> saveMaterial());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        return buttonPanel;
    }

    private void loadMaterialDetails(
            CourseMaterial existingMaterial) {

        formPanel.setSelectedCourseId(
                existingMaterial.getCourseId()
        );

        formPanel.setTitle(
                existingMaterial.getTitle()
        );

        formPanel.setDescription(
                existingMaterial.getDescription()
        );

        formPanel.setMaterialType(
                existingMaterial.getMaterialType()
        );

        formPanel.setFilePath(
                existingMaterial.getFilePath()
        );

        formPanel.setExternalUrl(
                existingMaterial.getExternalUrl()
        );
    }

    private void saveMaterial() {

        CourseOption selectedCourse =
                formPanel.getSelectedCourse();

        String title = formPanel.getTitle();
        String filePath = formPanel.getFilePath();
        String externalUrl = formPanel.getExternalUrl();

        String error = MaterialFormValidator.validate(
                title,
                selectedCourse,
                filePath,
                externalUrl
        );

        if (error != null) {
            JOptionPane.showMessageDialog(
                    this,
                    error,
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!editMode) {
            material = new CourseMaterial();
            material.setLecturerId(lecturerId);
        }

        // Preserve the existing material ID when editing.
        material.setCourseId(selectedCourse.getCourseId());
        material.setTitle(title);
        material.setDescription(formPanel.getDescription());
        material.setMaterialType(formPanel.getMaterialType());
        material.setFilePath(filePath);
        material.setExternalUrl(externalUrl);

        dispose();
    }

    public CourseMaterial getMaterial() {
        return material;
    }
}
