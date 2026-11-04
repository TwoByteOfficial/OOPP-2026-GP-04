package gui.undergraduate.medicals.table;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Dedicated cell renderer styling approval statuses (Approved, Pending, Rejected) with distinct badge colors.
 * Renders centered status values and applies semantic color coding matching the administrative design system
 * (Forest Green for Approved, Warning Amber for Pending, and Danger Red for Rejected).
 */
public class MedicalStatusBadgeRenderer extends DefaultTableCellRenderer {

    /**
     * Constructs the badge renderer with center alignment and horizontal cell padding.
     */
    public MedicalStatusBadgeRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(new EmptyBorder(2, 6, 2, 6));
    }

    /**
     * Configures typography and dynamic foreground color based on the medical record approval state.
     *
     * @param table      The parent JTable instance
     * @param value      The approval status string object
     * @param isSelected Whether the cell row is currently highlighted/selected
     * @param hasFocus   Whether the cell currently holds focus
     * @param row        The row index of the cell being drawn
     * @param column     The column index of the cell being drawn
     * @return Configured JLabel component for cell rendering
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        // Obtain default rendered label component
        JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        lbl.setFont(AdminTheme.font(Font.BOLD, 11));

        // Dynamic theme-based color-coding based on approval status
        String status = String.valueOf(value);
        if ("Approved".equalsIgnoreCase(status)) {
            lbl.setForeground(AdminTheme.FOREST_GREEN);
        } else if ("Pending".equalsIgnoreCase(status)) {
            lbl.setForeground(AdminTheme.WARNING);
        } else {
            lbl.setForeground(AdminTheme.DANGER);
        }
        return lbl;
    }
}