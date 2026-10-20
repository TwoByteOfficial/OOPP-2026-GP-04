
package gui.lecturer.CourseMaterialComponents;

import gui.lecturer.CourseMaterialComponents.MaterialActionComponents.MaterialActionEditor;
import gui.lecturer.CourseMaterialComponents.MaterialActionComponents.MaterialActionRenderer;
import model.CourseMaterial;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class CourseMaterialTable extends JPanel {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private JTable materialsTable;
    private DefaultTableModel tableModel;

    private List<CourseMaterial> materials;

    public interface MaterialActionListener {
        void onEdit(CourseMaterial material);
        void onDelete(CourseMaterial material);
    }

    private MaterialActionListener actionListener;

    public CourseMaterialTable() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        createTable();
    }

    private void createTable() {

        String[] columns = {
                "Material Title",
                "Course ID",
                "Type",
                "Uploaded Date",
                "Action"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
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

        materialsTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        materialsTable.getTableHeader().setBackground(PALE_GREEN);
        materialsTable.getTableHeader().setForeground(DEEP_GREEN);
        materialsTable.getTableHeader().setPreferredSize(
                new Dimension(0, 40)
        );

        materialsTable.getColumnModel()
                .getColumn(4)
                .setCellRenderer(new MaterialActionRenderer());

        materialsTable.getColumnModel()
                .getColumn(4)
                .setCellEditor(new MaterialActionEditor(
                        this::editMaterial,
                        this::deleteMaterial
                ));

        materialsTable.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(170);

        JScrollPane scrollPane =
                new JScrollPane(materialsTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 230, 220)
                )
        );
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setActionListener(
            MaterialActionListener listener) {
        this.actionListener = listener;
    }

    public void setMaterials(List<CourseMaterial> materials) {

        this.materials = materials;

        tableModel.setRowCount(0);

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd");

        for (CourseMaterial material : materials) {

            String uploadedDate = "-";

            if (material.getUploadedAt() != null) {
                uploadedDate = dateFormat.format(
                        material.getUploadedAt()
                );
            }

            tableModel.addRow(new Object[]{
                    material.getTitle(),
                    material.getCourseId(),
                    material.getMaterialType(),
                    uploadedDate,
                    "Actions"
            });
        }
    }

    private CourseMaterial getMaterialAtRow(int viewRow) {

        if (materials == null
                || viewRow < 0
                || viewRow >= materialsTable.getRowCount()) {
            return null;
        }

        int modelRow =
                materialsTable.convertRowIndexToModel(viewRow);

        if (modelRow >= 0 && modelRow < materials.size()) {
            return materials.get(modelRow);
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

        if (selectedRow == -1 || materials == null) {
            return -1;
        }

        int modelRow =
                materialsTable.convertRowIndexToModel(selectedRow);

        if (modelRow >= 0 && modelRow < materials.size()) {
            return materials.get(modelRow).getMaterialId();
        }

        return -1;
    }
}
