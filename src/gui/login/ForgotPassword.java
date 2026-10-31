package gui.login;

import services.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/** Modal "Forgot Password" dialog. */
public final class ForgotPassword {

    private static final String TITLE = "Forgot Password";
    private static final int MIN_PASSWORD_LENGTH = 6;

    private ForgotPassword() {}

    public static void show(JFrame owner) {
        JDialog dialog = new JDialog(owner, TITLE, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JTextField     userField   = LoginTheme.formField(new JTextField(),     420, 44);
        JTextField     emailField  = LoginTheme.formField(new JTextField(),     420, 44);
        JTextField     nicField    = LoginTheme.formField(new JTextField(),     420, 44);
        JPasswordField newPass     = LoginTheme.formField(new JPasswordField(), 420, 44);
        JPasswordField confirmPass = LoginTheme.formField(new JPasswordField(), 420, 44);

        JPanel form = new JPanel();
        form.setBackground(LoginTheme.MINT);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LoginTheme.FRESH, 1),
                new EmptyBorder(20, 20, 20, 20)));

        LoginTheme.addAll(form,
                LoginTheme.label(TITLE, LoginTheme.F_UNI, LoginTheme.FOREST, LoginTheme.LEFT),
                LoginTheme.gap(6),
                LoginTheme.label("Verify your identity and choose a new password.",
                        LoginTheme.F_TERMS, LoginTheme.DEEP, LoginTheme.LEFT),
                LoginTheme.gap(18),
                LoginTheme.formLabel("Username"),          LoginTheme.gap(6), userField,   LoginTheme.gap(12),
                LoginTheme.formLabel("Email"),             LoginTheme.gap(6), emailField,  LoginTheme.gap(12),
                LoginTheme.formLabel("NIC"),               LoginTheme.gap(6), nicField,    LoginTheme.gap(12),
                LoginTheme.formLabel("New Password"),      LoginTheme.gap(6), newPass,     LoginTheme.gap(12),
                LoginTheme.formLabel("Re-enter Password"), LoginTheme.gap(6), confirmPass);

        JButton cancelBtn = LoginTheme.button("Cancel", LoginTheme.F_LABEL);
        JButton resetBtn  = LoginTheme.button("Reset Password", LoginTheme.F_LABEL);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        bottom.setBackground(LoginTheme.PASTEL);
        bottom.add(cancelBtn);
        bottom.add(resetBtn);

        cancelBtn.addActionListener(e -> dialog.dispose());
        resetBtn.addActionListener(e ->
                onReset(dialog, userField, emailField, nicField, newPass, confirmPass));

        dialog.getRootPane().setDefaultButton(resetBtn);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    private static void onReset(JDialog dialog,
                                JTextField userField, JTextField emailField, JTextField nicField,
                                JPasswordField newPass, JPasswordField confirmPass) {
        String username = userField.getText().trim();
        String email    = emailField.getText().trim();
        String nic      = nicField.getText().trim();
        String p1       = LoginTheme.readPassword(newPass);
        String p2       = LoginTheme.readPassword(confirmPass);

        if (username.isEmpty() || email.isEmpty() || nic.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
            LoginTheme.show(dialog, "Please fill in all fields.", TITLE, JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (p1.length() < MIN_PASSWORD_LENGTH) {
            LoginTheme.show(dialog, "Password must be at least " + MIN_PASSWORD_LENGTH + " characters.",
                    TITLE, JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!p1.equals(p2)) {
            LoginTheme.show(dialog, "Passwords do not match.", TITLE, JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (AuthService.resetPassword(username, email, nic, p1)) {
                LoginTheme.show(dialog, "Password updated successfully.\nYou can now log in.",
                        TITLE, JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                LoginTheme.show(dialog, "No matching account found.\nPlease check your username, email and NIC.",
                        TITLE, JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            LoginTheme.show(dialog, "Database error:\n" + ex.getMessage(),
                    TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }
}