package gui.admin.SettingsComponents;

import dao.admin.AdminUserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;

public class GeneralSettings extends JPanel {

    private AdminUserDAO userDAO;
    private long userId;

    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtContact;
    private JTextField txtNic;
    private JTextField txtAddress;

    public GeneralSettings(long userId, String fullName, String email, AdminUserDAO dao) {
        this.userId = userId;
        this.userDAO = dao;

        setLayout(null); // Absolute positioning with setBounds
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                        " Administrator Profile Settings ",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 16),
                        new Color(0x1B, 0x5E, 0x20)
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Full Name
        JLabel lblFullName = createLabel("Full Name:");
        lblFullName.setBounds(30, 45, 180, 35);
        add(lblFullName);

        txtFullName = createTextField(fullName);
        txtFullName.setBounds(220, 45, 480, 40);
        add(txtFullName);

        // Email Address
        JLabel lblEmail = createLabel("Email Address:");
        lblEmail.setBounds(30, 105, 180, 35);
        add(lblEmail);

        txtEmail = createTextField(email);
        txtEmail.setBounds(220, 105, 480, 40);
        add(txtEmail);

        // Contact Number
        JLabel lblContact = createLabel("Contact Number:");
        lblContact.setBounds(30, 165, 180, 35);
        add(lblContact);

        txtContact = createTextField("+94 77 123 4567");
        txtContact.setBounds(220, 165, 480, 40);
        add(txtContact);

        // NIC Number
        JLabel lblNic = createLabel("NIC Number:");
        lblNic.setBounds(30, 225, 180, 35);
        add(lblNic);

        txtNic = createTextField("199012345678");
        txtNic.setBounds(220, 225, 480, 40);
        add(txtNic);

        // Address
        JLabel lblAddress = createLabel("Address:");
        lblAddress.setBounds(30, 285, 180, 35);
        add(lblAddress);

        txtAddress = createTextField("Faculty of Technology, University of Ruhuna");
        txtAddress.setBounds(220, 285, 480, 40);
        add(txtAddress);

        // Save Button
        JButton btnSave = new JButton("Save Profile Changes");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSave.setBackground(new Color(0x38, 0x8E, 0x3C));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSave.setBounds(220, 355, 240, 45);
        btnSave.addActionListener(e -> saveProfile());
        add(btnSave);

        loadUserData();
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(Color.BLACK);
        return l;
    }

    private JTextField createTextField(String val) {
        JTextField tf = new JTextField(val);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tf.setBackground(Color.WHITE);
        tf.setForeground(Color.BLACK);
        tf.setCaretColor(Color.BLACK);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    private void loadUserData() {
        try {
            User u = userDAO.getUserById(userId);
            if (u != null) {
                if (u.getFullName() != null) txtFullName.setText(u.getFullName());
                if (u.getEmail() != null) txtEmail.setText(u.getEmail());
                if (u.getContactNo() != null) txtContact.setText(u.getContactNo());
                if (u.getNic() != null) txtNic.setText(u.getNic());
                if (u.getAddress() != null) txtAddress.setText(u.getAddress());
            }
        } catch (Exception ignored) {}
    }

    private void saveProfile() {
        try {
            boolean success = userDAO.updateAdminProfileSettings(
                    userId,
                    txtFullName.getText().trim(),
                    txtNic.getText().trim(),
                    txtEmail.getText().trim(),
                    txtContact.getText().trim(),
                    txtAddress.getText().trim()
            );
            if (success) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Could not update profile details.", "Update Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
