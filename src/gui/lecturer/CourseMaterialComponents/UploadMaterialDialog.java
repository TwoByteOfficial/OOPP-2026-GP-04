
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

    private CourseMaterial material;

    public UploadMaterialDialog(
            Window owner,
            long lecturerId,
            List<Object[]> courses) {

        super(owner, "Upload Course Material",
                ModalityType.APPLICATION_MODAL);

        this.lecturerId = lecturerId;
        this.formPanel = new MaterialFormPanel(courses);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(560, 650);
        setMinimumSize(new Dimension(500, 600));
        setLocationRelativeTo(owner);

        createLayout();
    }

    private void createLayout() {

        JPanel mainPanel = new JPanel(new BorderLayout(0, 18));
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
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel("Upload Learning Material");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(DEEP_GREEN);

        JLabel description = new JLabel(
                "Add learning resources for your assigned courses."
        );
        description.setFont(new Font("SansSerif", Font.PLAIN, 13));
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
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Save Material");
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

        material = new CourseMaterial();

        material.setCourseId(selectedCourse.getCourseId());
        material.setLecturerId(lecturerId);
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
