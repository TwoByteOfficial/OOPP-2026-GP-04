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

        setLayout(new GridLayout(6, 2, 14, 12));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                        " Administrator Profile Settings ",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 16),
                        new Color(0x1B, 0x5E, 0x20)
                ),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        addLabel("Full Name:");
        txtFullName = createTextField(fullName);
        add(txtFullName);

        addLabel("Email Address:");
        txtEmail = createTextField(email);
        add(txtEmail);

        addLabel("Contact Number:");
        txtContact = createTextField("+94 77 123 4567");
        add(txtContact);

        addLabel("NIC Number:");
        txtNic = createTextField("199012345678");
        add(txtNic);

        addLabel("Address:");
        txtAddress = createTextField("Faculty of Technology, University of Ruhuna");
        add(txtAddress);

        JButton btnSave = new JButton("Save Profile Changes");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSave.setBackground(new Color(0x38, 0x8E, 0x3C));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> saveProfile());
        add(new JLabel()); // spacer
        add(btnSave);

        loadUserData();
    }

    private void addLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(Color.BLACK);
        add(l);
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
            userDAO.updateAdminProfileSettings(
                    userId,
                    txtFullName.getText().trim(),
                    txtNic.getText().trim(),
                    txtEmail.getText().trim(),
                    txtContact.getText().trim(),
                    txtAddress.getText().trim()
            );
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
