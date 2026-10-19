
package gui.lecturer.CourseMaterialComponents.MaterialActionComponents;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MaterialActionRenderer implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {

        return new MaterialActionPanel();
    }
}
