package gui.admin.UserProfileComponents;

import dao.admin.AdminUserDAO;
import gui.admin.AdminTheme;
import model.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Date;
import java.util.function.Supplier;

/**
 * EditUserButton - Single component file containing the Edit User Profile action button
 * AND its on-click edit modal dialog window.
 *
 * All theme colors and typography are retrieved directly from AdminTheme.
 */
public class EditUserButton extends JButton {

    public EditUserButton(Component parentComponent, AdminUserDAO userDAO, Supplier<User> userSupplier, Runnable onSuccess) {
        super("✏️ Edit User");
        setFont(AdminTheme.font(Font.BOLD, 15));
        setBackground(AdminTheme.WARNING);
        setForeground(AdminTheme.DEEP_GREEN);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        addActionListener(e -> {
            User selectedUser = userSupplier != null ? userSupplier.get() : null;
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(this, "Please select a user profile row to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(parentComponent != null ? parentComponent : this);
            EditUserDialog dialog = new EditUserDialog(parentFrame, userDAO, selectedUser, onSuccess);
            dialog.setVisible(true);
        });
    }

    public EditUserButton(Runnable onEdit) {
        super("✏️ Edit User");
        setFont(AdminTheme.font(Font.BOLD, 15));
        setBackground(AdminTheme.WARNING);
        setForeground(AdminTheme.DEEP_GREEN);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        addActionListener(e -> {
            if (onEdit != null) onEdit.run();
        });
    }

    /**
     * Modal Edit User Profile Dialog
     */
    public static class EditUserDialog extends JDialog {

        private final AdminUserDAO userDAO;
        private final User editUser;
        private final Runnable onSuccess;

        private JTextField txtFullName;
        private JTextField txtNic;
        private JTextField txtDob;
        private JComboBox<String> cmbGender;
        private JTextField txtEmail;
        private JTextField txtContactNo;
        private JTextField txtAddress;
        private JComboBox<String> cmbDepartment;
        private JComboBox<String> cmbStatus;

        public EditUserDialog(Frame parent, AdminUserDAO dao, User userToEdit, Runnable onSuccess) {
            super(parent, "Modify User Profile Details", true);
            this.userDAO = dao;
            this.editUser = userToEdit;
            this.onSuccess = onSuccess;

            setSize(820, 560);
            setLocationRelativeTo(parent);
            setResizable(false);
            getContentPane().setBackground(AdminTheme.MINT);
            setLayout(new BorderLayout());

            initUI();
            populateFormForEdit();
        }

        private void initUI() {
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
            headerPanel.setBackground(AdminTheme.DEEP_GREEN);

            JLabel lblHeader = new JLabel("Modify User Account Details - " + (editUser != null ? editUser.getFullName() : ""));
            lblHeader.setFont(AdminTheme.font(Font.BOLD, 18));
            lblHeader.setForeground(AdminTheme.WHITE);
            headerPanel.add(lblHeader);

            add(headerPanel, BorderLayout.NORTH);

            JPanel mainContainer = new JPanel();
            mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
            mainContainer.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
            mainContainer.setBackground(AdminTheme.MINT);

            JPanel pnlFields = new JPanel(new GridBagLayout());
            pnlFields.setBackground(AdminTheme.WHITE);
            pnlFields.setOpaque(true);
            pnlFields.setBorder(createTitledSectionBorder("User Account Information"));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 10, 6, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Row 0: Full Name & NIC
            addGridRow(pnlFields, gbc, 0, "Full Name *:", txtFullName = createStyledTextField(),
                    "NIC Number:", txtNic = createStyledTextField());

            // Row 1: DOB & Gender
            addGridRow(pnlFields, gbc, 1, "Date of Birth (YYYY-MM-DD):", txtDob = createStyledTextField(),
                    "Gender:", cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"}));
            cmbGender.setFont(AdminTheme.font(Font.PLAIN, 15));
            cmbGender.setBackground(AdminTheme.WHITE);

            // Row 2: Email & Contact
            addGridRow(pnlFields, gbc, 2, "Email Address:", txtEmail = createStyledTextField(),
                    "Contact Phone:", txtContactNo = createStyledTextField());

            // Row 3: Department & Status
            addGridRow(pnlFields, gbc, 3, "Department:", cmbDepartment = new JComboBox<>(new String[]{"1 - ICT", "2 - Engineering Tech", "3 - Bio Systems Tech"}),
                    "Account Status:", cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"}));
            cmbDepartment.setFont(AdminTheme.font(Font.PLAIN, 15));
            cmbDepartment.setBackground(AdminTheme.WHITE);
            cmbStatus.setFont(AdminTheme.font(Font.PLAIN, 15));
            cmbStatus.setBackground(AdminTheme.WHITE);

            // Row 4: Address
            addGridRow(pnlFields, gbc, 4, "Residential Address:", txtAddress = createStyledTextField(), "", new JLabel());

            mainContainer.add(pnlFields);

            JScrollPane scrollPane = new JScrollPane(mainContainer);
            scrollPane.setBorder(null);
            add(scrollPane, BorderLayout.CENTER);

            // Footer Panel
            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
            footerPanel.setBackground(AdminTheme.WHITE);
            footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AdminTheme.PASTEL_GREEN));

            JButton btnCancel = new JButton("Cancel");
            btnCancel.setFont(AdminTheme.font(Font.BOLD, 15));
            btnCancel.setBackground(AdminTheme.PASTEL_GREEN);
            btnCancel.setForeground(AdminTheme.DEEP_GREEN);
            btnCancel.setFocusPainted(false);
            btnCancel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            btnCancel.addActionListener(e -> dispose());

            JButton btnUpdate = new JButton("Update Profile");
            btnUpdate.setFont(AdminTheme.font(Font.BOLD, 15));
            btnUpdate.setBackground(AdminTheme.FOREST_GREEN);
            btnUpdate.setForeground(AdminTheme.WHITE);
            btnUpdate.setFocusPainted(false);
            btnUpdate.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            btnUpdate.addActionListener(e -> updateUser());

            footerPanel.add(btnCancel);
            footerPanel.add(btnUpdate);

            add(footerPanel, BorderLayout.SOUTH);
        }

        private void populateFormForEdit() {
            if (editUser == null) return;

            txtFullName.setText(editUser.getFullName() != null ? editUser.getFullName() : "");
            txtNic.setText(editUser.getNic() != null ? editUser.getNic() : "");
            txtDob.setText(editUser.getDateOfBirth() != null ? editUser.getDateOfBirth().toString() : "");

            if (editUser.getGender() != null) {
                cmbGender.setSelectedItem(editUser.getGender());
            }

            txtEmail.setText(editUser.getEmail() != null ? editUser.getEmail() : "");
            txtContactNo.setText(editUser.getContactNo() != null ? editUser.getContactNo() : "");
            txtAddress.setText(editUser.getAddress() != null ? editUser.getAddress() : "");

            if (editUser.getDepartmentId() != null && editUser.getDepartmentId() >= 1 && editUser.getDepartmentId() <= 3) {
                cmbDepartment.setSelectedIndex(editUser.getDepartmentId() - 1);
            }

            if (editUser.getUserStatus() != null) {
                cmbStatus.setSelectedItem(editUser.getUserStatus());
            }
        }

        private void addGridRow(JPanel panel, GridBagConstraints gbc, int row, String lbl1Text, Component comp1, String lbl2Text, Component comp2) {
            gbc.gridy = row;

            gbc.gridx = 0; gbc.weightx = 0.15;
            JLabel l1 = new JLabel(lbl1Text);
            l1.setFont(AdminTheme.font(Font.BOLD, 14));
            l1.setForeground(AdminTheme.DEEP_GREEN);
            panel.add(l1, gbc);

            gbc.gridx = 1; gbc.weightx = 0.35;
            panel.add(comp1, gbc);

            if (!lbl2Text.isEmpty()) {
                gbc.gridx = 2; gbc.weightx = 0.15;
                JLabel l2 = new JLabel(lbl2Text);
                l2.setFont(AdminTheme.font(Font.BOLD, 14));
                l2.setForeground(AdminTheme.DEEP_GREEN);
                panel.add(l2, gbc);

                gbc.gridx = 3; gbc.weightx = 0.35;
                panel.add(comp2, gbc);
            }
        }

        private JTextField createStyledTextField() {
            JTextField tf = new JTextField();
            tf.setFont(AdminTheme.font(Font.PLAIN, 15));
            tf.setBackground(AdminTheme.WHITE);
            tf.setForeground(AdminTheme.BLACK);
            tf.setCaretColor(AdminTheme.BLACK);
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            return tf;
        }

        private Border createTitledSectionBorder(String title) {
            TitledBorder tb = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                    " " + title + " ",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    AdminTheme.font(Font.BOLD, 15),
                    AdminTheme.DEEP_GREEN
            );
            return BorderFactory.createCompoundBorder(tb, BorderFactory.createEmptyBorder(8, 14, 12, 14));
        }

        private void updateUser() {
            try {
                String fullName = txtFullName.getText().trim();
                if (fullName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Full Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Date dob = null;
                String dobText = txtDob.getText().trim();
                if (!dobText.isEmpty()) {
                    try { dob = Date.valueOf(dobText); }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Invalid Date format. Use YYYY-MM-DD.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                int deptId = cmbDepartment.getSelectedIndex() + 1;

                editUser.setFullName(fullName);
                editUser.setNic(txtNic.getText().trim());
                editUser.setDateOfBirth(dob);
                editUser.setGender((String) cmbGender.getSelectedItem());
                editUser.setEmail(txtEmail.getText().trim());
                editUser.setContactNo(txtContactNo.getText().trim());
                editUser.setAddress(txtAddress.getText().trim());
                editUser.setUserStatus((String) cmbStatus.getSelectedItem());
                editUser.setDepartmentId(deptId);

                if (userDAO != null) {
                    userDAO.updateUser(editUser);
                }

                JOptionPane.showMessageDialog(this, "User profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                if (onSuccess != null) onSuccess.run();
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating user profile: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
