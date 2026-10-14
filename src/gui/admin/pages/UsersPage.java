package gui.admin.pages;

import dao.UserDAO;
import gui.admin.AdminDashboard;
import gui.admin.AdminTheme;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class UsersPage extends JPanel {

    private final AdminDashboard dash;
    private final UserDAO dao = new UserDAO();

    private final JTextField search = AdminTheme.field(18);
    private final JComboBox<String> roleFilter = AdminTheme.combo(
            "All Roles","Admin","Lecturer","Technical Officer","Undergraduate");
    private final JComboBox<String> statusFilter = AdminTheme.combo("All","Active","Inactive");
    private final JPanel tableHost = new JPanel(new BorderLayout());

    public UsersPage(AdminDashboard dash) {
        this.dash = dash;
        setLayout(new BorderLayout());
        setBackground(AdminTheme.LIGHT);
        setBorder(new EmptyBorder(24, 32, 24, 32));
        buildUI();
        reload();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(AdminTheme.label("Manage User Profiles", AdminTheme.F_H2, AdminTheme.PRIMARY));
        left.add(AdminTheme.label("Create, update and maintain system users.", AdminTheme.F_SMALL, AdminTheme.MUTED));
        header.add(left, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton add = AdminTheme.button("+ Add New User", AdminTheme.PRIMARY);
        add.addActionListener(e -> openUserModal(null));
        actions.add(add);
        header.add(actions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel filterBar = AdminTheme.card();
        filterBar.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));

        filterBar.add(fieldBlock("Search", search));
        filterBar.add(fieldBlock("Role", roleFilter));
        filterBar.add(fieldBlock("Status", statusFilter));

        JButton apply = AdminTheme.button("Search", AdminTheme.PRIMARY);
        apply.addActionListener(e -> reload());
        filterBar.add(apply);

        JPanel centre = new JPanel(new BorderLayout());
        centre.setOpaque(false);
        centre.setBorder(new EmptyBorder(16, 0, 0, 0));
        centre.add(filterBar, BorderLayout.NORTH);
        centre.add(tableHost, BorderLayout.CENTER);
        add(centre, BorderLayout.CENTER);
    }

    private JPanel fieldBlock(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(AdminTheme.label(label, AdminTheme.F_LABEL, AdminTheme.MUTED));
        p.add(Box.createVerticalStrut(4));
        field.setPreferredSize(new Dimension(180, 32));
        p.add(field);
        return p;
    }

    private void reload() {
        try {
            String q = search.getText().trim().toLowerCase();
            String r = (String) roleFilter.getSelectedItem();
            String s = (String) statusFilter.getSelectedItem();

            List<User> list = dao.getAllUsers().stream()
                    .filter(u -> r == null || "All Roles".equals(r) || r.equalsIgnoreCase(u.getRole()))
                    .filter(u -> s == null || "All".equals(s) || s.equalsIgnoreCase(u.getUserStatus()))
                    .filter(u -> q.isEmpty()
                            || (safe(u.getFullName()) + safe(u.getUsername()) + u.getUserId())
                            .toLowerCase().contains(q))
                    .collect(Collectors.toList());

            Object[][] rows = new Object[list.size()][7];
            for (int i = 0; i < list.size(); i++) {
                User u = list.get(i);
                rows[i] = new Object[]{
                        u.getUserId(), safe(u.getFullName()), safe(u.getUsername()),
                        safe(u.getRole()), safe(u.getDepartmentId()),
                        safe(u.getUserStatus()), "Edit / Toggle / Delete"
                };
            }
            JPanel wrap = AdminTheme.card();
            wrap.setLayout(new BorderLayout());
            wrap.add(AdminTheme.label(list.size() + " user(s) found", AdminTheme.F_SMALL, AdminTheme.MUTED),
                    BorderLayout.NORTH);

            JTable table = new JTable(rows, new String[]{"ID","Full Name","Username","Role","Dept","Status","Actions"});
            AdminTheme.styleTable(table);
            table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
            table.getColumn("Actions").setCellEditor(new ButtonEditor(this, list));
            JScrollPane sp = new JScrollPane(table);
            sp.setBorder(null);
            wrap.add(sp, BorderLayout.CENTER);

            tableHost.removeAll();
            tableHost.add(wrap, BorderLayout.CENTER);
            tableHost.revalidate();
            tableHost.repaint();
        } catch (Exception ex) {
            AdminTheme.showError(this, "Could not load users:\n" + ex.getMessage());
        }
    }

    /* ---------- Modal ---------- */

    void openUserModal(User u) {
        boolean isNew = u == null;
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isNew ? "Add New User" : "Edit User", true);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 10));
        form.setBorder(new EmptyBorder(20, 22, 20, 22));
        form.setBackground(AdminTheme.WHITE);

        JTextField id       = AdminTheme.field(15);
        JTextField username = AdminTheme.field(15);
        JTextField fullName = AdminTheme.field(15);
        JTextField email    = AdminTheme.field(15);
        JTextField contact  = AdminTheme.field(15);
        JPasswordField pass = AdminTheme.passwordField(15);
        JComboBox<String> roleCb = AdminTheme.combo("Admin","Lecturer","Technical Officer","Undergraduate");
        JComboBox<String> statusCb = AdminTheme.combo("Active","Inactive");

        if (u != null) {
            username.setText(safe(u.getUsername()));
            fullName.setText(safe(u.getFullName()));
            email.setText(safe(u.getEmail()));
            contact.setText(safe(u.getContactNo()));
            roleCb.setSelectedItem(u.getRole());
            statusCb.setSelectedItem(u.getUserStatus());
            username.setEditable(false);
            roleCb.setEnabled(false);
        }

        form.add(AdminTheme.label("Username *", AdminTheme.F_LABEL, AdminTheme.TEXT)); form.add(username);
        form.add(AdminTheme.label("Password *", AdminTheme.F_LABEL, AdminTheme.TEXT)); form.add(pass);
        form.add(AdminTheme.label("Full Name *", AdminTheme.F_LABEL, AdminTheme.TEXT)); form.add(fullName);
        form.add(AdminTheme.label("Role *", AdminTheme.F_LABEL, AdminTheme.TEXT)); form.add(roleCb);
        form.add(AdminTheme.label("Email", AdminTheme.F_LABEL, AdminTheme.TEXT)); form.add(email);
        form.add(AdminTheme.label("Contact", AdminTheme.F_LABEL, AdminTheme.TEXT)); form.add(contact);
        form.add(AdminTheme.label("Status", AdminTheme.F_LABEL, AdminTheme.TEXT)); form.add(statusCb);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bottom.setBackground(AdminTheme.LIGHT);
        JButton cancel = AdminTheme.button("Cancel", new Color(0xE9, 0xED, 0xF3));
        JButton save   = AdminTheme.button("Save", AdminTheme.PRIMARY);
        cancel.addActionListener(e -> dlg.dispose());
        save.addActionListener(e -> {
            try {
                User nu = isNew ? new User() : u;
                if (isNew) nu.setUsername(username.getText().trim());
                nu.setFullName(fullName.getText().trim());
                nu.setEmail(email.getText().trim());
                nu.setContactNo(contact.getText().trim());
                nu.setRole((String) roleCb.getSelectedItem());
                nu.setUserStatus((String) statusCb.getSelectedItem());

                if (isNew) {
                    // NOTE: hash the password before insert in real code
                    nu.setPasswordHash(new String(pass.getPassword()));
                    dao.createUser(nu);
                } else {
                    dao.updateUser(nu);
                }
                dlg.dispose();
                reload();
            } catch (Exception ex) {
                AdminTheme.showError(dlg, "Save failed:\n" + ex.getMessage());
            }
        });
        bottom.add(cancel);
        bottom.add(save);

        dlg.add(new JScrollPane(form), BorderLayout.CENTER);
        dlg.add(bottom, BorderLayout.SOUTH);
        dlg.setSize(520, 460);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    void toggleStatus(User u) {
        try {
            u.setUserStatus("Active".equalsIgnoreCase(u.getUserStatus()) ? "Inactive" : "Active");
            dao.updateUser(u);
            reload();
        } catch (Exception ex) {
            AdminTheme.showError(this, ex.getMessage());
        }
    }

    void deleteUser(User u) {
        if (!AdminTheme.confirm(this, "Delete user " + u.getFullName() + "?")) return;
        // UserDAO currently has no deleteUser; add one (see notes).
        try {
            new dao.UserDAO().deleteUser(u.getUserId());
            reload();
        } catch (Exception ex) {
            AdminTheme.showError(this, "Delete failed:\n" + ex.getMessage());
        }
    }

    private static String safe(Object o) { return o == null ? "" : String.valueOf(o); }

    /* ---------- Table button cell ---------- */

    private static class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        ButtonRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.LEFT, 4, 2));
            add(AdminTheme.button("Edit",   AdminTheme.PRIMARY));
            add(AdminTheme.button("Toggle", AdminTheme.WARNING));
            add(AdminTheme.button("Delete", AdminTheme.DANGER));
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            setBackground(sel ? t.getSelectionBackground() : AdminTheme.WHITE);
            return this;
        }
    }

    private static class ButtonEditor extends DefaultCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        ButtonEditor(UsersPage page, List<User> list) {
            super(new JCheckBox());
            panel.setOpaque(true);
            JButton edit   = AdminTheme.button("Edit",   AdminTheme.PRIMARY);
            JButton toggle = AdminTheme.button("Toggle", AdminTheme.WARNING);
            JButton del    = AdminTheme.button("Delete", AdminTheme.DANGER);
            panel.add(edit); panel.add(toggle); panel.add(del);
            edit.addActionListener(e -> { fireEditingStopped(); page.openUserModal(list.get(currentRow())); });
            toggle.addActionListener(e -> { fireEditingStopped(); page.toggleStatus(list.get(currentRow())); });
            del.addActionListener(e -> { fireEditingStopped(); page.deleteUser(list.get(currentRow())); });
        }
        private int currentRow;
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
            currentRow = t.convertRowIndexToModel(r);
            panel.setBackground(t.getSelectionBackground());
            return panel;
        }
        @Override public Object getCellEditorValue() { return "Actions"; }
    }
}