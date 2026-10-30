
package gui.lecturer.CourseMaterial.CourseMaterialComponents.MaterialActionComponents;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.function.IntConsumer;

public class MaterialActionEditor extends AbstractCellEditor
        implements TableCellEditor {

    private final MaterialActionPanel panel;
    private int editingRow = -1;

    public MaterialActionEditor(
            IntConsumer editAction,
            IntConsumer deleteAction) {

        panel = new MaterialActionPanel();

        panel.getEditButton().addActionListener(e -> {
            int row = editingRow;
            fireEditingStopped();

            if (row >= 0) {
                editAction.accept(row);
            }
        });

        panel.getDeleteButton().addActionListener(e -> {
            int row = editingRow;
            fireEditingStopped();

            if (row >= 0) {
                deleteAction.accept(row);
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table,
            Object value,
            boolean isSelected,
            int row,
            int column) {

        editingRow = row;
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "Actions";
    }

    @Override
    public boolean isCellEditable(EventObject event) {
        return event instanceof MouseEvent;
    }
}
