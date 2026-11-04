package gui.admin.UserProfileComponents;

import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * UserProfileTable - Custom JTable component for displaying user profile datasets.
 */
public class UserProfileTable extends JTable {

    private final Color WHITE        = Color.WHITE;
    private final Color SOFT_MINT    = new Color(0xF9, 0xFB, 0xF9);
    private final Color MINT         = new Color(0xE8, 0xF5, 0xE9);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color DARK_TEXT    = new Color(0x21, 0x25, 0x29);
    private final Color DANGER       = new Color(0xDC, 0x35, 0x45);

    private final DefaultTableModel tableModel;
    private List<User> currentDisplayedUsers = new ArrayList<>();

    public UserProfileTable() {
        String[] columnNames = { "User ID / Reg Code", "Full Name", "Role", "Email", "Department", "Status" };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table cells are read-only
            }
        };

        setModel(tableModel);
        setRowHeight(44);
        setFont(new Font("Segoe UI", Font.PLAIN, 15));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));

        // Header Styling
        JTableHeader header = getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(MINT);
        header.setForeground(DEEP_GREEN);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 46));
        header.setReorderingAllowed(false);

        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // Column Widths Allocation
        getColumnModel().getColumn(0).setPreferredWidth(200);
        getColumnModel().getColumn(1).setPreferredWidth(280);
        getColumnModel().getColumn(2).setPreferredWidth(180);
        getColumnModel().getColumn(3).setPreferredWidth(260);
        getColumnModel().getColumn(4).setPreferredWidth(180);
        getColumnModel().getColumn(5).setPreferredWidth(140);

        // Custom Cell Renderer
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                c.setFont(new Font("Segoe UI", Font.PLAIN, 15));

                if (isSelected) {
                    c.setBackground(PASTEL_GREEN);
                    c.setForeground(DEEP_GREEN);
                } else {
                    c.setBackground(row % 2 == 0 ? WHITE : SOFT_MINT);
                    c.setForeground(DARK_TEXT);
                }

                if (column == 5) { // Status Tag Column
                    String status = String.valueOf(value);
                    setFont(new Font("Segoe UI", Font.BOLD, 15));
                    if ("Active".equalsIgnoreCase(status)) {
                        setForeground(FOREST_GREEN);
                    } else {
                        setForeground(DANGER);
                    }
                }

                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                return c;
            }
        });
    }

    public void setUsersData(List<User> users) {
        this.currentDisplayedUsers = users;
        tableModel.setRowCount(0);

        for (User u : users) {
            String customIdentifier = "UID-" + u.getUserId();
            if (u instanceof Undergraduate) {
                customIdentifier = ((Undergraduate) u).getRegistrationNo();
            } else if (u instanceof Lecturer) {
                customIdentifier = ((Lecturer) u).getLecturerCode();
            } else if (u instanceof TechnicalOfficer) {
                customIdentifier = ((TechnicalOfficer) u).getTechnicalOfficerCode();
            } else if (u instanceof Admin) {
                customIdentifier = ((Admin) u).getAdminCode();
            }

            if (customIdentifier == null || customIdentifier.trim().isEmpty()) {
                customIdentifier = "UID-" + u.getUserId();
            }

            String deptText = (u.getDepartmentId() != null) ? "Dept " + u.getDepartmentId() : "General";
            if (u.getDepartmentId() != null && u.getDepartmentId() == 1) {
                deptText = "ICT";
            } else if (u.getDepartmentId() != null && u.getDepartmentId() == 2) {
                deptText = "Engineering Tech";
            } else if (u.getDepartmentId() != null && u.getDepartmentId() == 3) {
                deptText = "Bio Systems Tech";
            }

            Object[] rowData = {
                    customIdentifier,
                    u.getFullName(),
                    u.getRole(),
                    u.getEmail() != null ? u.getEmail() : "-",
                    deptText,
                    u.getUserStatus() != null ? u.getUserStatus() : "Active"
            };

            tableModel.addRow(rowData);
        }
    }

    public User getSelectedUser() {
        int selectedRow = getSelectedRow();
        if (selectedRow >= 0 && selectedRow < currentDisplayedUsers.size()) {
            return currentDisplayedUsers.get(selectedRow);
        }
        return null;
    }
}
