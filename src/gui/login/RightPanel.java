package gui.login;

import service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Right side panel rendering the student/staff authentication form.
 * Optimized with HTML nowrap rendering to eliminate subtitle truncation.
 */
public class RightPanel extends JPanel {

    private final JFrame owner;
    private final JTextField userField = LoginTheme.loginField(new JTextField());
    private final JPasswordField passField = LoginTheme.loginField(new JPasswordField());

    public RightPanel(JFrame owner) {
        this.owner = owner;
        setBackground(LoginTheme.MINT);
        setLayout(new GridBagLayout());
        add(buildForm());
    }

    private JPanel buildForm() {
        JButton loginBtn = LoginTheme.button("Login", LoginTheme.F_BUTTON);
        loginBtn.setAlignmentX(LoginTheme.LEFT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.setPreferredSize(new Dimension(340, 44));
        loginBtn.addActionListener(e -> doLogin());

        // Ensure input fields share identical left alignment in BoxLayout
        userField.setAlignmentX(LoginTheme.LEFT);
        passField.setAlignmentX(LoginTheme.LEFT);

        // Enter key listeners for smooth keyboard navigation
        userField.addActionListener(e -> passField.requestFocusInWindow());
        passField.addActionListener(e -> doLogin());

        JPanel form = new JPanel();
        form.setBackground(LoginTheme.MINT);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        // Balanced horizontal padding to allow ample horizontal breathing room
        form.setBorder(new EmptyBorder(0, 40, 0, 40));

        // Format app title with HTML nowrap to strictly prevent ellipsis truncation
        String rawTitle = (Login.APP_TITLE != null && !Login.APP_TITLE.trim().isEmpty())
                ? Login.APP_TITLE.trim()
                : "UniMis - Faculty Academic Management Information System";
        String wrappedSubtitle = "<html><nobr>" + rawTitle + "</nobr></html>";

        JLabel titleLabel = LoginTheme.label("Login", LoginTheme.F_TITLE, LoginTheme.FOREST, LoginTheme.LEFT);
        JLabel subtitleLabel = LoginTheme.label(wrappedSubtitle, LoginTheme.F_TAG, LoginTheme.DEEP, LoginTheme.LEFT);

        LoginTheme.addAll(form,
                titleLabel,
                LoginTheme.gap(6),
                subtitleLabel,
                LoginTheme.gap(35),
                LoginTheme.formLabel("Username"),
                LoginTheme.gap(6),
                userField,
                LoginTheme.gap(18),
                LoginTheme.formLabel("Password"),
                LoginTheme.gap(6),
                passField,
                LoginTheme.gap(8),
                LoginTheme.linkLabel("Forgot Password?", () -> ForgotPassword.show(owner)),
                LoginTheme.gap(24),
                loginBtn,
                LoginTheme.gap(30),
                LoginTheme.linkLabel("Terms and Conditions", () -> TermsDialog.show(owner)));

        return form;
    }

    private void doLogin() {
        String username = userField.getText().trim();
        String password = LoginTheme.readPassword(passField);

        if (username.isEmpty() || password.isEmpty()) {
            LoginTheme.show(owner, "Please enter both username and password.",
                    "Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            AuthService.LoginResult result = AuthService.authenticate(username, password);
            if (result.ok()) {
                Login.openDashboard(owner, result.user());
            } else {
                LoginTheme.show(owner, result.error(), "Login Failed", JOptionPane.ERROR_MESSAGE);
                passField.setText("");
            }
        } catch (SQLException ex) {
            LoginTheme.show(owner, "Database error:\n" + ex.getMessage(),
                    "Login", JOptionPane.ERROR_MESSAGE);
        }
    }
}