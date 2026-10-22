
package gui.lecturer.MyCourse.MyCourseComponents;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.function.IntConsumer;

public class ShowButtonEditor extends AbstractCellEditor
        implements TableCellEditor {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private static final Color BORDER_GREEN =
            new Color(200, 230, 201);

    private final JButton button = new JButton("Show Details");
    private final IntConsumer onShowCourse;

    private int modelRow;

    public ShowButtonEditor(IntConsumer onShowCourse) {

        this.onShowCourse = onShowCourse;

        button.setFocusPainted(false);
        button.setBackground(PALE_GREEN);
        button.setForeground(DEEP_GREEN);

        button.setBorder(
                BorderFactory.createLineBorder(BORDER_GREEN)
        );

        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        button.addActionListener(event -> {

            int selectedModelRow = modelRow;

            fireEditingStopped();

            this.onShowCourse.accept(selectedModelRow);
        });
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table,
            Object value,
            boolean isSelected,
            int row,
            int column) {

        modelRow = table.convertRowIndexToModel(row);

        button.setText("Show Details");

        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "Show Details";
    }
}
