package gui.admin.SettingsComponents;

import dao.admin.AdminUserDAO;

import javax.swing.*;
import java.awt.*;

public class AccountSettings extends JPanel {

    private String username;
    private AdminUserDAO userDAO;

    private JPasswordField txtCurrentPass;
    private JPasswordField txtNewPass;
    private JPasswordField txtConfirmNewPass;

    public AccountSettings(String username) {
        this(username, new AdminUserDAO());
    }

    public AccountSettings(String username, AdminUserDAO dao) {
        this.username = username;
        this.userDAO = dao;

        setLayout(null); // Absolute positioning with setBounds
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                        " Account Credentials & Security ",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 16),
                        new Color(0x1B, 0x5E, 0x20)
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Username (Locked)
        JLabel lblUser = createLabel("Username (Locked):");
        lblUser.setBounds(30, 45, 200, 35);
        add(lblUser);

        JTextField txtUser = new JTextField(username);
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUser.setEditable(false);
        txtUser.setBackground(new Color(0xE8, 0xF5, 0xE9));
        txtUser.setForeground(Color.BLACK);
        txtUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        txtUser.setBounds(240, 45, 460, 40);
        add(txtUser);

        // Current Password
        JLabel lblCurrent = createLabel("Current Password:");
        lblCurrent.setBounds(30, 105, 200, 35);
        add(lblCurrent);

        txtCurrentPass = createPasswordField();
        txtCurrentPass.setBounds(240, 105, 460, 40);
        add(txtCurrentPass);

        // New Password
        JLabel lblNew = createLabel("New Password:");
        lblNew.setBounds(30, 165, 200, 35);
        add(lblNew);

        txtNewPass = createPasswordField();
        txtNewPass.setBounds(240, 165, 460, 40);
        add(txtNewPass);

        // Re-enter New Password
        JLabel lblConfirm = createLabel("Re-enter New Password:");
        lblConfirm.setBounds(30, 225, 200, 35);
        add(lblConfirm);

        txtConfirmNewPass = createPasswordField();
        txtConfirmNewPass.setBounds(240, 225, 460, 40);
        add(txtConfirmNewPass);

        // Update Password Button
        JButton btnChangePass = new JButton("Update Password");
        btnChangePass.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnChangePass.setBackground(new Color(0x38, 0x8E, 0x3C));
        btnChangePass.setForeground(Color.WHITE);
        btnChangePass.setFocusPainted(false);
        btnChangePass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnChangePass.setBounds(240, 295, 240, 45);
        btnChangePass.addActionListener(e -> updatePassword());
        add(btnChangePass);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(Color.BLACK);
        return l;
    }

    private JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pf.setBackground(Color.WHITE);
        pf.setForeground(Color.BLACK);
        pf.setCaretColor(Color.BLACK);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return pf;
    }

    private void updatePassword() {
        String currentPass = new String(txtCurrentPass.getPassword()).trim();
        String newPass = new String(txtNewPass.getPassword()).trim();
        String confirmPass = new String(txtConfirmNewPass.getPassword()).trim();

        if (currentPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your current password.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtCurrentPass.requestFocus();
            return;
        }

        if (newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a new password.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtNewPass.requestFocus();
            return;
        }

        if (newPass.length() < 4) {
            JOptionPane.showMessageDialog(this, "New password must be at least 4 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtNewPass.requestFocus();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "New Password and Re-entered Password do not match. Please try again.", "Password Mismatch", JOptionPane.ERROR_MESSAGE);
            txtConfirmNewPass.requestFocus();
            return;
        }

        try {
            boolean updated = userDAO.updateUserPassword(username, currentPass, newPass);
            if (updated) {
                JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                txtCurrentPass.setText("");
                txtNewPass.setText("");
                txtConfirmNewPass.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Current password is incorrect. Please verify and try again.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating password: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
