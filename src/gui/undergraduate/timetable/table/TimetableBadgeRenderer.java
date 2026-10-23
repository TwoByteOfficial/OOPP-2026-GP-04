package gui.undergraduate.timetable.table;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Dedicated cell renderer for Theory (Deep Green) and Practical (Mint Green) badges.
 */
public class TimetableBadgeRenderer extends DefaultTableCellRenderer {

    public TimetableBadgeRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(new EmptyBorder(2, 6, 2, 6));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        lbl.setFont(AdminTheme.font(Font.BOLD, 11));

        String comp = String.valueOf(value);
        if ("Practical".equalsIgnoreCase(comp)) {
            lbl.setForeground(new Color(0x1B, 0x7E, 0x5A));
        } else {
            lbl.setForeground(AdminTheme.FOREST_GREEN);
        }
        return lbl;
    }
}