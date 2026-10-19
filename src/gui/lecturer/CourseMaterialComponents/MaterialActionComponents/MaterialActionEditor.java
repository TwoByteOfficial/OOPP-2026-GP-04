
package gui.lecturer.CourseMaterialComponents.MaterialActionComponents;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class MaterialActionEditor extends AbstractCellEditor
        implements TableCellEditor {

    private final MaterialActionPanel panel;

    private Runnable editAction;
    private Runnable deleteAction;

    public MaterialActionEditor(
            Runnable editAction,
            Runnable deleteAction) {

        this.editAction = editAction;
        this.deleteAction = deleteAction;

        panel = new MaterialActionPanel();

        panel.getEditButton().addActionListener(e -> {
            Runnable action = this.editAction;
            fireEditingStopped();

            if (action != null) {
                action.run();
            }
        });

        panel.getDeleteButton().addActionListener(e -> {
            Runnable action = this.deleteAction;
            fireEditingStopped();

            if (action != null) {
                action.run();
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
