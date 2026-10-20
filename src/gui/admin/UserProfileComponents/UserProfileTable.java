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
 * UserProfileTable - Custom JTable wrapper component.
 * Explicitly styled with high-contrast colors and custom cell renderers
 * to prevent OS Dark Theme contrast artifacts.
 */
public class UserProfileTable extends JPanel {

    private final Color WHITE        = Color.WHITE;
    private final Color MINT         = new Color(0xE8, 0xF5, 0xE9);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color DARK_TEXT    = new Color(0x21, 0x25, 0x29);
    private final Color DANGER       = new Color(0xDC, 0x35, 0x45);

    private JTable table;
    private DefaultTableModel tableModel;
    private List<User> currentUsersList = new ArrayList<>();

    public UserProfileTable() {
        setLayout(new BorderLayout());
        setBounds(0, 0, 1620, 670);
        setBackground(WHITE);
        setOpaque(true);

        String[] columns = { "User ID / Code", "Full Name", "Role", "Department", "Email / Contact", "Status" };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table cells are read-only
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(44); // Comfortably fits 16px font
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // 14 -> 16
        table.setBackground(WHITE);
        table.setForeground(DARK_TEXT);
        table.setGridColor(PASTEL_GREEN);
        table.setSelectionBackground(PASTEL_GREEN);
        table.setSelectionForeground(DEEP_GREEN);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(8, 8));

        // 1. Table Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16)); // 14 -> 16
        header.setBackground(MINT);
        header.setForeground(DEEP_GREEN);
        header.setPreferredSize(new Dimension(0, 46));
        header.setReorderingAllowed(false);

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                l.setBackground(MINT);
                l.setForeground(DEEP_GREEN);
                l.setFont(new Font("Segoe UI", Font.BOLD, 16));
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 1, PASTEL_GREEN),
                        BorderFactory.createEmptyBorder(6, 12, 6, 12)
                ));
                l.setOpaque(true);
                return l;
            }
        });

        // 2. Custom Table Cell Renderer (Row Alternating & Status Highlighting)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                l.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                l.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

                if (isSelected) {
                    l.setBackground(PASTEL_GREEN);
                    l.setForeground(DEEP_GREEN);
                } else {
                    if (row % 2 == 0) {
                        l.setBackground(WHITE);
                    } else {
                        l.setBackground(new Color(0xF9, 0xFB, 0xF9)); // Soft mint alternating row
                    }
                    l.setForeground(DARK_TEXT);
                }

                // ID / Code Column
                if (column == 0) {
                    l.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    l.setForeground(DEEP_GREEN);
                }

                // Status Column Highlighting
                if (column == 5) {
                    l.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    String statusVal = (value != null) ? value.toString() : "Active";
                    if ("Active".equalsIgnoreCase(statusVal)) {
                        l.setForeground(FOREST_GREEN);
                    } else {
                        l.setForeground(DANGER);
                    }
                }

                l.setOpaque(true);
                return l;
            }
        });

        // Column Width Allocations
        int[] widths = { 160, 280, 180, 140, 260, 120 };
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(WHITE);
        scrollPane.setBackground(WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(PASTEL_GREEN, 1));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setUsersData(List<User> users) {
        this.currentUsersList = users;
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
                deptText = "ET";
            } else if (u.getDepartmentId() != null && u.getDepartmentId() == 3) {
                deptText = "BST";
            }

            Object[] rowData = {
                    customIdentifier,
                    u.getFullName(),
                    u.getRole(),
                    deptText,
                    u.getEmail() != null ? u.getEmail() : u.getContactNo(),
                    u.getUserStatus() != null ? u.getUserStatus() : "Active"
            };

            tableModel.addRow(rowData);
        }
    }

    public User getSelectedUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < currentUsersList.size()) {
            return currentUsersList.get(selectedRow);
        }
        return null;
    }

    public JTable getTable() {
        return table;
    }
}
