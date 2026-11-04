
package gui.lecturer.MyCourse.MyCourseComponents;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ShowButtonRenderer extends JButton
        implements TableCellRenderer {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private static final Color BORDER_GREEN =
            new Color(200, 230, 201);

    public ShowButtonRenderer() {

        setText("Show Details");
        setOpaque(true);
        setFocusPainted(false);
        setBackground(PALE_GREEN);
        setForeground(DEEP_GREEN);

        setBorder(
                BorderFactory.createLineBorder(BORDER_GREEN)
        );

        setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {

        setText(value == null ? "Show Details" : value.toString());

        return this;
    }
}
