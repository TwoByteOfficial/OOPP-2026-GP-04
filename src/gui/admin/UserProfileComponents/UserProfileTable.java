package gui.admin.UserProfileComponents;

import gui.admin.AdminTheme;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserProfileTable - Single-file component representing the complete user profile data table.
 * All colors and fonts are retrieved directly from AdminTheme.
 */
public class UserProfileTable extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private List<User> currentDisplayedUsers = new ArrayList<>();

    public UserProfileTable() {
        setLayout(new BorderLayout());
        setBackground(AdminTheme.WHITE);

        String[] columnNames = {
                "User ID / Identifier", "Full Name", "System Role", "Email Address", "Department", "Account Status"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(48);
        table.setFont(AdminTheme.font(Font.PLAIN, 15));
        table.setShowVerticalLines(false);
        table.setGridColor(AdminTheme.PASTEL_GREEN);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(AdminTheme.font(Font.BOLD, 16));
        header.setBackground(AdminTheme.PASTEL_GREEN);
        header.setForeground(AdminTheme.DEEP_GREEN);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 46));
        header.setReorderingAllowed(false);

        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(280);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(260);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);
        table.getColumnModel().getColumn(5).setPreferredWidth(140);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                c.setFont(AdminTheme.font(Font.PLAIN, 15));

                if (isSelected) {
                    c.setBackground(AdminTheme.PASTEL_GREEN);
                    c.setForeground(AdminTheme.DEEP_GREEN);
                } else {
                    c.setBackground(row % 2 == 0 ? AdminTheme.WHITE : AdminTheme.MINT);
                    c.setForeground(AdminTheme.TEXT);
                }

                if (column == 5) { // Status Tag Column
                    String status = String.valueOf(value);
                    setFont(AdminTheme.font(Font.BOLD, 15));
                    if ("Active".equalsIgnoreCase(status)) {
                        setForeground(AdminTheme.FOREST_GREEN);
                    } else {
                        setForeground(AdminTheme.DANGER);
                    }
                }

                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(AdminTheme.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setUsersData(List<User> users) {
        this.currentDisplayedUsers = users != null ? users : new ArrayList<>();
        tableModel.setRowCount(0);

        for (User u : currentDisplayedUsers) {
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

    public void updateData(List<User> users) {
        setUsersData(users);
    }

    public User getSelectedUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < currentDisplayedUsers.size()) {
            return currentDisplayedUsers.get(selectedRow);
        }
        return null;
    }

    public JTable getTable() {
        return table;
    }
}
