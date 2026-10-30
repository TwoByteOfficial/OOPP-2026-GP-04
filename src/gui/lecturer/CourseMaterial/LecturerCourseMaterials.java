package gui.lecturer.CourseMaterial;

import dao.Lecturer.CourseDAO;
import dao.Lecturer.CourseMaterialDAO;
import gui.lecturer.CourseMaterial.CourseMaterialComponents.CourseMaterialTable;
import gui.lecturer.CourseMaterial.CourseMaterialComponents.CourseMaterialTitleBar;
import gui.lecturer.CourseMaterial.CourseMaterialComponents.UploadMaterialDialog;
import gui.lecturer.SharedComponents.Colors;
import model.CourseMaterial;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static gui.lecturer.SharedComponents.Colors.*;


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
        setBackground(APP_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        createComponents();
        loadCourses();
        loadMaterials();
    }

    private void createComponents() {

        titleBar = new CourseMaterialTitleBar();
        materialTable = new CourseMaterialTable();

        titleBar.getUploadButton().addActionListener(e -> openUploadDialog());

        // Keep the existing Edit and Delete functionality.
        materialTable.setActionListener(
                new CourseMaterialTable.MaterialActionListener() {
                    @Override
                    public void onEdit(CourseMaterial material) {
                        editMaterial(material);
                    }

                    @Override
                    public void onDelete(CourseMaterial material) {
                        confirmDelete(material);
                    }
                }
        );

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Main content background matches the other Lecturer pages.
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(APP_BACKGROUND);

        JLabel sectionTitle = new JLabel("Learning Materials");
        sectionTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        sectionTitle.setForeground(new Color(27, 94, 32));

        // White background for the table only.
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(
                BorderFactory.createLineBorder(BORDER_GREEN)
        );

        materialTable.setBackground(Color.WHITE);
        tableContainer.add(materialTable, BorderLayout.CENTER);

        centerPanel.add(sectionTitle, BorderLayout.NORTH);
        centerPanel.add(tableContainer, BorderLayout.CENTER);
        centerPanel.add(statusLabel, BorderLayout.SOUTH);

        add(titleBar, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }


    private void loadCourses() {

        try {
            lecturerCourses =
                    courseDAO.getCoursesByLecturer(lecturerId);

        } catch (SQLException ex) {
            showDatabaseError("load courses", ex);
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
                            : materials.size() + " material(s) available"
            );

        } catch (SQLException ex) {
            showDatabaseError("load materials", ex);
        }
    }

    // Upload a new material.
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

        if (material != null) {
            saveMaterial(material);
        }
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

    // Open the dialog with the selected material's existing details.
    private void editMaterial(CourseMaterial material) {

        if (lecturerCourses.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No courses are available for editing.",
                    "No Courses Available",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Window owner = SwingUtilities.getWindowAncestor(this);

        UploadMaterialDialog dialog =
                new UploadMaterialDialog(
                        owner,
                        lecturerId,
                        lecturerCourses,
                        material
                );

        dialog.setVisible(true);

        CourseMaterial updatedMaterial = dialog.getMaterial();

        // The user cancelled the dialog.
        if (updatedMaterial == null) {
            return;
        }

        try {
            boolean updated =
                    materialDAO.updateMaterial(updatedMaterial);

            if (updated) {
                JOptionPane.showMessageDialog(
                        this,
                        "Course material updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadMaterials();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "The material could not be updated.",
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException ex) {
            showDatabaseError("update material", ex);
        }
    }

    // Ask for confirmation before deleting.
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
            deleteMaterial(material);
        }
    }

    private void deleteMaterial(CourseMaterial material) {

        try {
            boolean deleted = materialDAO.deleteMaterial(
                    material.getMaterialId(),
                    lecturerId
            );

            if (deleted) {
                JOptionPane.showMessageDialog(
                        this,
                        "Course material deleted successfully.",
                        "Deleted",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadMaterials();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "The material could not be deleted.",
                        "Delete Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException ex) {
            showDatabaseError("delete material", ex);
        }
    }

    private void showDatabaseError(
            String operation,
            SQLException ex) {

        ex.printStackTrace();

        if (statusLabel != null) {
            statusLabel.setText("Unable to " + operation + ".");
        }

        JOptionPane.showMessageDialog(
                this,
                "A database error occurred while trying to "
                        + operation + ".\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}