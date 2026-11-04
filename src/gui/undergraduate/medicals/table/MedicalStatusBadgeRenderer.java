package gui.undergraduate.medicals.table;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Dedicated cell renderer styling approval statuses (Approved, Pending, Rejected) with distinct badge colors.
 */
public class MedicalStatusBadgeRenderer extends DefaultTableCellRenderer {

    public MedicalStatusBadgeRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(new EmptyBorder(2, 6, 2, 6));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        lbl.setFont(AdminTheme.font(Font.BOLD, 11));

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