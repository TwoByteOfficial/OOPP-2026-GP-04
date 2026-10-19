
package gui.lecturer;

import dao.Lecturer.CourseDAO;
import dao.Lecturer.CourseMaterialDAO;
import gui.lecturer.CourseMaterialComponents.CourseMaterialTable;
import gui.lecturer.CourseMaterialComponents.CourseMaterialTitleBar;
import gui.lecturer.CourseMaterialComponents.UploadMaterialDialog;
import model.CourseMaterial;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LecturerCourseMaterials extends JPanel {

    private final long lecturerId;

    private final CourseDAO courseDAO;
    private final CourseMaterialDAO materialDAO;

    private CourseMaterialTitleBar titleBar;
    private CourseMaterialTable materialTable;

    private List<Object[]> lecturerCourses = new ArrayList<>();
    private List<CourseMaterial> materials = new ArrayList<>();

    private JLabel statusLabel;

    public LecturerCourseMaterials(long lecturerId) {

        this.lecturerId = lecturerId;

        courseDAO = new CourseDAO();
        materialDAO = new CourseMaterialDAO();

        setLayout(new BorderLayout(0, 18));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(
                25, 25, 25, 25
        ));

        createComponents();
        loadCourses();
        loadMaterials();
    }

    private void createComponents() {

        titleBar = new CourseMaterialTitleBar();
        materialTable = new CourseMaterialTable();

        titleBar.getUploadButton().addActionListener(
                e -> openUploadDialog()
        );

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setFont(new Font(
                "SansSerif", Font.PLAIN, 12
        ));

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(Color.WHITE);

        JLabel sectionTitle = new JLabel("Learning Materials");
        sectionTitle.setFont(new Font(
                "SansSerif", Font.BOLD, 17
        ));
        sectionTitle.setForeground(new Color(27, 94, 32));

        centerPanel.add(sectionTitle, BorderLayout.NORTH);
        centerPanel.add(materialTable, BorderLayout.CENTER);
        centerPanel.add(statusLabel, BorderLayout.SOUTH);

        add(titleBar, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void loadCourses() {

        try {
            lecturerCourses =
                    courseDAO.getCoursesByLecturer(lecturerId);

        } catch (SQLException ex) {
            showDatabaseError("courses", ex);
        }
    }

    private void loadMaterials() {

        try {
            materials =
                    materialDAO.getMaterialsByLecturer(lecturerId);

            materialTable.setMaterials(materials);

            statusLabel.setText(
                    materials.isEmpty()
                            ? "No course materials found."
                            : materials.size() +
                            " material(s) available"
            );

        } catch (SQLException ex) {
            showDatabaseError("materials", ex);
        }
    }

    private void openUploadDialog() {

        if (lecturerCourses.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No active courses are assigned to you.",
                    "No Courses Available",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        Window owner = SwingUtilities.getWindowAncestor(this);

        UploadMaterialDialog dialog =
                new UploadMaterialDialog(
                        owner,
                        lecturerId,
                        lecturerCourses
                );

        dialog.setVisible(true);

        CourseMaterial material = dialog.getMaterial();

        if (material == null) {
            return;
        }

        saveMaterial(material);
    }

    private void saveMaterial(CourseMaterial material) {

        try {
            boolean saved = materialDAO.addMaterial(material);

            if (saved) {
                JOptionPane.showMessageDialog(
                        this,
                        "Course material saved successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadMaterials();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "The material could not be saved.",
                        "Save Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException ex) {
            showDatabaseError("save material", ex);
        }
    }

    private void showDatabaseError(
            String operation,
            SQLException ex) {

        ex.printStackTrace();

        statusLabel.setText("Unable to load " + operation + ".");

        JOptionPane.showMessageDialog(
                this,
                "A database error occurred while trying to " +
                        operation + ".\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }


    private void confirmDelete(CourseMaterial material) {

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete \""
                        + material.getTitle() + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            // We will add the database delete operation here.
        }
    }

}
