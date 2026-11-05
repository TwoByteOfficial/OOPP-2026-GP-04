package gui.undergraduate.timetable.table;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Dedicated cell renderer for Theory (Deep Green) and Practical (Mint Green) badges.
 * Customizes the display of timetable session delivery modes within JTable columns,
 * centering the text and applying distinct semantic font coloring based on session type.
 */
public class TimetableBadgeRenderer extends DefaultTableCellRenderer {

    /**
     * Constructs the badge renderer, setting center alignment and standard cell margins.
     */
    public TimetableBadgeRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(new EmptyBorder(2, 6, 2, 6));
    }

    /**
     * Configures and returns the rendering component for the specified table cell.
     * Applies bold typography and evaluates session category ('Practical' vs 'Theory')
     * to assign appropriate status foreground tints.
     *
     * @param table      The parent JTable requesting the renderer
     * @param value      The cell value representing the session component string
     * @param isSelected Whether the cell is currently selected
     * @param hasFocus   Whether the cell currently holds focus
     * @param row        The row index of the cell being drawn
     * @param column     The column index of the cell being drawn
     * @return Configured JLabel component for cell painting
     */
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