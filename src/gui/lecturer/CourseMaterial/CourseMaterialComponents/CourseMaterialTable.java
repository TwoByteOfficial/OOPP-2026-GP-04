
package gui.lecturer.CourseMaterial.CourseMaterialComponents;

import gui.lecturer.CourseMaterial.CourseMaterialComponents.MaterialActionComponents.MaterialActionEditor;
import gui.lecturer.CourseMaterial.CourseMaterialComponents.MaterialActionComponents.MaterialActionRenderer;
import model.CourseMaterial;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CourseMaterialTable extends JPanel {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private JTable materialsTable;
    private DefaultTableModel tableModel;

    private JComboBox<CourseOption> courseFilter;

    // All materials loaded from the database.
    private List<CourseMaterial> materials = new ArrayList<>();

    // Materials currently displayed after filtering.
    private List<CourseMaterial> visibleMaterials = new ArrayList<>();

    private MaterialActionListener actionListener;

    public interface MaterialActionListener {
        void onEdit(CourseMaterial material);
        void onDelete(CourseMaterial material);
    }

    public CourseMaterialTable() {
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);

        createTable();
    }

    private void createTable() {

        // Table columns.
        String[] columns = {
                "Course Code",
                "Course Name",
                "Title",
                "Type",
                "Uploaded Date",
                "Action"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        materialsTable = new JTable(tableModel);
        materialsTable.setRowHeight(42);
        materialsTable.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        materialsTable.setSelectionBackground(PALE_GREEN);
        materialsTable.setSelectionForeground(Color.BLACK);
        materialsTable.setShowVerticalLines(false);
        materialsTable.setGridColor(new Color(230, 230, 230));
        materialsTable.setAutoCreateRowSorter(true);

        materialsTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        materialsTable.getTableHeader().setBackground(PALE_GREEN);
        materialsTable.getTableHeader().setForeground(DEEP_GREEN);
        materialsTable.getTableHeader().setPreferredSize(
                new Dimension(0, 40)
        );

        // Action column: index 5.
        materialsTable.getColumnModel()
                .getColumn(5)
                .setCellRenderer(new MaterialActionRenderer());

        materialsTable.getColumnModel()
                .getColumn(5)
                .setCellEditor(new MaterialActionEditor(
                        this::editMaterial,
                        this::deleteMaterial
                ));

        // Column widths.
        materialsTable.getColumnModel()
                .getColumn(0).setPreferredWidth(110);

        materialsTable.getColumnModel()
                .getColumn(1).setPreferredWidth(220);

        materialsTable.getColumnModel()
                .getColumn(2).setPreferredWidth(200);

        materialsTable.getColumnModel()
                .getColumn(3).setPreferredWidth(100);

        materialsTable.getColumnModel()
                .getColumn(4).setPreferredWidth(130);

        materialsTable.getColumnModel()
                .getColumn(5).setPreferredWidth(170);

        // Course filter.
        JPanel filterPanel = new JPanel(new FlowLayout(
                FlowLayout.LEFT, 10, 8
        ));
        filterPanel.setBackground(Color.WHITE);

        JLabel filterLabel = new JLabel("Filter by Course:");
        filterLabel.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        filterLabel.setForeground(DEEP_GREEN);

        courseFilter = new JComboBox<>();
        courseFilter.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        courseFilter.setPreferredSize(new Dimension(320, 34));
        courseFilter.setBackground(Color.WHITE);

        courseFilter.addActionListener(e -> applyCourseFilter());

        filterPanel.add(filterLabel);
        filterPanel.add(courseFilter);

        JScrollPane scrollPane = new JScrollPane(materialsTable);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 230, 220)
                )
        );
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setActionListener(
            MaterialActionListener listener) {
        this.actionListener = listener;
    }

    // Load materials and refresh the course filter.
    public void setMaterials(List<CourseMaterial> materials) {

        CourseOption previousSelection =
                (CourseOption) courseFilter.getSelectedItem();

        int previousCourseId = previousSelection == null
                ? -1
                : previousSelection.courseId;

        this.materials = materials == null
                ? new ArrayList<>()
                : new ArrayList<>(materials);

        refreshCourseFilter(previousCourseId);
        applyCourseFilter();
    }

    private void refreshCourseFilter(int previousCourseId) {

        courseFilter.removeAllItems();

        courseFilter.addItem(
                new CourseOption(-1, "All Courses")
        );

        // Keep each course only once.
        Map<Integer, CourseOption> uniqueCourses =
                new LinkedHashMap<>();

        for (CourseMaterial material : materials) {

            int courseId = material.getCourseId();

            String courseCode = material.getCourseCode();
            String courseName = material.getCourseName();

            if (courseCode == null || courseCode.isBlank()) {
                courseCode = "Course " + courseId;
            }

            if (courseName == null || courseName.isBlank()) {
                courseName = "Unnamed Course";
            }

            String label = courseCode + " - " + courseName;

            uniqueCourses.putIfAbsent(
                    courseId,
                    new CourseOption(courseId, label)
            );
        }

        for (CourseOption option : uniqueCourses.values()) {
            courseFilter.addItem(option);
        }

        // Keep the previous selection after refreshing, if possible.
        int selectedIndex = 0;

        for (int i = 0; i < courseFilter.getItemCount(); i++) {
            CourseOption option = courseFilter.getItemAt(i);

            if (option.courseId == previousCourseId) {
                selectedIndex = i;
                break;
            }
        }

        courseFilter.setSelectedIndex(selectedIndex);
    }

    // Display all materials or only the selected course's materials.
    private void applyCourseFilter() {

        if (tableModel == null || courseFilter == null) {
            return;
        }

        CourseOption selected =
                (CourseOption) courseFilter.getSelectedItem();

        int selectedCourseId = selected == null
                ? -1
                : selected.courseId;

        visibleMaterials = new ArrayList<>();
        tableModel.setRowCount(0);

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd");

        for (CourseMaterial material : materials) {

            if (selectedCourseId != -1
                    && material.getCourseId() != selectedCourseId) {
                continue;
            }

            visibleMaterials.add(material);

            String uploadedDate = "-";

            if (material.getUploadedAt() != null) {
                uploadedDate = dateFormat.format(
                        material.getUploadedAt()
                );
            }

            tableModel.addRow(new Object[]{
                    material.getCourseCode(),
                    material.getCourseName(),
                    material.getTitle(),
                    material.getMaterialType(),
                    uploadedDate,
                    "Actions"
            });
        }
    }

    private CourseMaterial getMaterialAtRow(int viewRow) {

        if (viewRow < 0
                || viewRow >= materialsTable.getRowCount()) {
            return null;
        }

        int modelRow =
                materialsTable.convertRowIndexToModel(viewRow);

        if (modelRow >= 0 && modelRow < visibleMaterials.size()) {
            return visibleMaterials.get(modelRow);
        }

        return null;
    }

    private void editMaterial(int row) {

        CourseMaterial material = getMaterialAtRow(row);

        if (material != null && actionListener != null) {
            actionListener.onEdit(material);
        }
    }

    private void deleteMaterial(int row) {

        CourseMaterial material = getMaterialAtRow(row);

        if (material != null && actionListener != null) {
            actionListener.onDelete(material);
        }
    }

    public JTable getMaterialsTable() {
        return materialsTable;
    }

    public long getSelectedMaterialId() {

        int selectedRow = materialsTable.getSelectedRow();

        if (selectedRow == -1) {
            return -1;
        }

        CourseMaterial material = getMaterialAtRow(selectedRow);

        return material == null
                ? -1
                : material.getMaterialId();
    }

    // Represents one course in the dropdown.
    private static class CourseOption {

        private final int courseId;
        private final String label;

        private CourseOption(int courseId, String label) {
            this.courseId = courseId;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
