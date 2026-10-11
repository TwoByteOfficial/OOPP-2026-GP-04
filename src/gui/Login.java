package gui;

import gui.admin.AdminDashboard;
import gui.lecturer.LecturerDashboard;
import gui.technical.TechnicalOfficerDashboard;
import gui.undergraduate.UndergraduateDashboard;
import util.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Login {

    /* =========================================================
       Constants
       ========================================================= */

    private static final String APP_TITLE = "UniMis - Faculty Academic Management Information System";
    private static final int MIN_PASSWORD_LENGTH = 6;

    /* Color palette (light green) */
    private static final Color MINT   = new Color(0xE8, 0xF5, 0xE9); // background
    private static final Color PASTEL = new Color(0xC8, 0xE6, 0xC9); // left panel / card
    private static final Color FRESH  = new Color(0xA5, 0xD6, 0xA7); // borders
    private static final Color FOREST = new Color(0x38, 0x8E, 0x3C); // buttons / headings
    private static final Color DEEP   = new Color(0x1B, 0x5E, 0x20); // text
    private static final Color WHITE  = Color.WHITE;

    /* Logos */
    private static final String ASSETS_DIR = "/home/dasindu-dilvan/Documents/8.OOPP Mini Project/OOPP-2026-GP-04/assets/";
    private static final String UNI_LOGO     = ASSETS_DIR + "university-logo.png";
    private static final String FACULTY_LOGO = ASSETS_DIR + "faculty-logo.png";

    /* Fonts */
    private static final String FONT = "Segoe UI";
    private static final Font F_TITLE  = new Font(FONT, Font.BOLD, 50);
    private static final Font F_TAG    = new Font(FONT, Font.PLAIN, 22);
    private static final Font F_LABEL  = new Font(FONT, Font.BOLD, 18);
    private static final Font F_FIELD  = new Font(FONT, Font.PLAIN, 19);
    private static final Font F_LINK   = new Font(FONT, Font.PLAIN, 16);
    private static final Font F_UNI    = new Font(FONT, Font.BOLD, 32);
    private static final Font F_FAC    = new Font(FONT, Font.PLAIN, 28);
    private static final Font F_BUTTON = new Font(FONT, Font.BOLD, 20);
    private static final Font F_TERMS  = new Font(FONT, Font.PLAIN, 14);

    private static final float LEFT   = Component.LEFT_ALIGNMENT;
    private static final float CENTER = Component.CENTER_ALIGNMENT;
    private static final Cursor HAND  = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    private static final String INVALID_CREDENTIALS = "Invalid username or password.";

    private static final String TERMS_TEXT = String.join("\n\n",
            "UniMis - Terms and Conditions",
            "1. Acceptance of Terms\n"
                    + "By logging in to UniMis you agree to abide by the rules and regulations "
                    + "of the University of Ruhuna and the Faculty of Technology.",
            "2. Authorised Use\n"
                    + "This system is provided only for registered students and staff of the faculty. "
                    + "Credentials are personal and must not be shared.",
            "3. Data Privacy\n"
                    + "All academic and personal data is treated as confidential. Access is logged.",
            "4. Misuse\n"
                    + "Unauthorised access, alteration of records, or disruption of service "
                    + "will result in disciplinary action.",
            "5. Changes\n"
                    + "The faculty reserves the right to modify these terms at any time.");

    /* Columns read for the logged-in user */
    private static final String USER_COLUMNS = "user_id, full_name, email, role";

    /* =========================================================
       Role -> dashboard routing (add a new role = add one line)
       ========================================================= */

    @FunctionalInterface
    private interface DashboardFactory {
        JFrame create(long userId, String username, String fullName, String email, String role);
    }

    /** Keys are normalized with {@link #roleKey(String)}: lower-case, no spaces / underscores / hyphens. */
    private static final Map<String, DashboardFactory> DASHBOARDS = new HashMap<>();
    static {
        DASHBOARDS.put("admin",            AdminDashboard::new);
        DASHBOARDS.put("lecturer",         LecturerDashboard::new);
        DASHBOARDS.put("technicalofficer", TechnicalOfficerDashboard::new);
        DASHBOARDS.put("undergraduate",    UndergraduateDashboard::new);
    }

    /** "Technical_Officer", "Technical Officer", "TechnicalOfficer", "technical-officer" -> "technicalofficer" */
    private static String roleKey(String role) {
        return role == null ? "" : role.replaceAll("[\\s_-]+", "").toLowerCase(Locale.ROOT);
    }

    /** The logged-in user's details, handed to the dashboard. */
    private static final class User {
        final long id;
        final String username, fullName, email, role;

        User(long id, String username, String fullName, String email, String role) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
        }

        static User from(ResultSet rs, String username) throws SQLException {
            return new User(rs.getLong("user_id"), username,
                    rs.getString("full_name"), rs.getString("email"), rs.getString("role"));
        }
    }

    /* =========================================================
       Entry points
       ========================================================= */

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::start);
    }

    /** Called by dashboards on Logout. */
    public static void showLogin() {
        SwingUtilities.invokeLater(Login::start);
    }

    private static void start() {
        JFrame frame = new JFrame(APP_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(buildLeftPanel());
        root.add(buildRightPanel(frame));
        frame.setContentPane(root);

        frame.setLocationRelativeTo(null);
        goFullScreen(frame);
    }

    private static void goFullScreen(JFrame frame) {
        GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().setFullScreenWindow(frame);
    }

    /* =========================================================
       LEFT PANEL : logos + university / faculty name
       ========================================================= */

    private static JPanel buildLeftPanel() {
        JPanel left = new JPanel();
        left.setBackground(PASTEL);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        addAll(left,
                Box.createVerticalGlue(),
                logoLabel(UNI_LOGO, "University Logo", 220, 220),
                gap(25),
                logoLabel(FACULTY_LOGO, "Faculty Logo", 180, 180),
                gap(30),
                label("University Of Ruhuna", F_UNI, DEEP, CENTER),
                gap(6),
                label("Faculty of Technology", F_FAC, FOREST, CENTER),
                Box.createVerticalGlue());
        return left;
    }

    /** Loads an image scaled to fit, or falls back to a labeled box. */
    private static JLabel logoLabel(String path, String fallback, int w, int h) {
        ImageIcon icon = loadIcon(path, w, h);
        JLabel label;
        if (icon != null) {
            label = new JLabel(icon);
        } else {
            label = new JLabel(fallback, SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBackground(WHITE);
            label.setForeground(DEEP);
            label.setFont(F_LABEL);
            label.setPreferredSize(new Dimension(w, h));
            label.setMaximumSize(new Dimension(w, h));
        }
        label.setAlignmentX(CENTER);
        return label;
    }

    /** Returns the image scaled to fit maxW x maxH, or null if it cannot be loaded. */
    private static ImageIcon loadIcon(String path, int maxW, int maxH) {
        ImageIcon original = new ImageIcon(path);   // width/height are -1 if the file is missing
        int w = original.getIconWidth(), h = original.getIconHeight();
        if (w <= 0 || h <= 0) return null;

        double scale = Math.min((double) maxW / w, (double) maxH / h);
        return new ImageIcon(original.getImage().getScaledInstance(
                (int) (w * scale), (int) (h * scale), Image.SCALE_SMOOTH));
    }

    /* =========================================================
       RIGHT PANEL : login form
       ========================================================= */

    private static JPanel buildRightPanel(JFrame owner) {
        JTextField     userField = loginField(new JTextField());
        JPasswordField passField = loginField(new JPasswordField());

        JButton loginBtn = button("Login", F_BUTTON);
        loginBtn.setAlignmentX(LEFT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.setPreferredSize(new Dimension(320, 44));
        loginBtn.addActionListener(e -> doLogin(owner, userField, passField));

        JPanel form = new JPanel();
        form.setBackground(MINT);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(0, 60, 0, 60));

        addAll(form,
                label("Login", F_TITLE, FOREST, LEFT),
                gap(6),
                label(APP_TITLE, F_TAG, DEEP, LEFT),
                gap(35),
                formLabel("Username"),
                gap(6),
                userField,
                gap(18),
                formLabel("Password"),
                gap(6),
                passField,
                gap(8),
                linkLabel("Forgot Password?", () -> forgotPassword(owner)),
                gap(24),
                loginBtn,
                gap(30),
                linkLabel("Terms and Conditions", () -> showTerms(owner)));

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(MINT);
        right.add(form);
        return right;
    }

    /* =========================================================
       Login : verify credentials against the users table
       ========================================================= */

    private static void doLogin(JFrame owner, JTextField userField, JPasswordField passField) {
        String username = userField.getText().trim();
        String password = readPassword(passField);

        if (username.isEmpty() || password.isEmpty()) {
            show(owner, "Please enter both username and password.", "Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = authenticate(owner, username, password, passField);
        if (user != null) openDashboard(owner, user);
    }

    /**
     * Checks the credentials and account status.
     * Shows the error dialog itself and returns null on any failure.
     */
    private static User authenticate(JFrame owner, String username, String password, JPasswordField passField) {
        String sql = "SELECT " + USER_COLUMNS + ", password_hash, user_status FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    loginFailed(owner, passField, INVALID_CREDENTIALS);
                    return null;
                }

                User user = User.from(rs, username);

                String blocked = blockedAccountMessage(rs.getString("user_status"));
                if (blocked != null) {
                    loginFailed(owner, passField, blocked);
                    return null;
                }

                if (!hashPassword(password).equalsIgnoreCase(rs.getString("password_hash"))) {
                    loginFailed(owner, passField, INVALID_CREDENTIALS);
                    passField.requestFocusInWindow();
                    return null;
                }

                touchLastLogin(con, user.id);
                return user;
            }

        } catch (SQLException ex) {
            dbError(owner, "Login", ex);
            return null;
        }
    }

    /** Returns the message to show for a Suspended / Inactive account, or null if it may log in. */
    private static String blockedAccountMessage(String status) {
        if ("Suspended".equalsIgnoreCase(status)) {
            return "This account has been suspended.\nPlease contact the Faculty IT Division.";
        }
        if ("Inactive".equalsIgnoreCase(status)) {
            return "This account is inactive.\nPlease contact the Faculty IT Division to activate it.";
        }
        return null;
    }

    private static void loginFailed(JFrame owner, JPasswordField passField, String message) {
        show(owner, message, "Login Failed", JOptionPane.ERROR_MESSAGE);
        passField.setText("");
    }

    /** Stamps last_login. A failure is logged but never blocks the login. */
    private static void touchLastLogin(Connection con, long userId) {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?")) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Could not update last_login: " + ex.getMessage());
        }
    }

    /** Routes the user to the dashboard that matches their role. */
    private static void openDashboard(JFrame owner, User user) {
        owner.dispose();

        DashboardFactory factory = DASHBOARDS.get(roleKey(user.role));
        if (factory == null) {
            show(null, "Unknown role: \"" + user.role + "\".\nContact the administrator.",
                    "Login", JOptionPane.ERROR_MESSAGE);
            showLogin();
            return;
        }

        try {
            JFrame dashboard = factory.create(user.id, user.username, user.fullName, user.email, user.role);
            dashboard.setVisible(true);
            goFullScreen(dashboard);
        } catch (RuntimeException ex) {
            // The dashboard itself failed to start (e.g. NullPointerException in its constructor).
            ex.printStackTrace();
            show(null, "Could not open the dashboard for role \"" + user.role + "\":\n" + ex,
                    "Login", JOptionPane.ERROR_MESSAGE);
            showLogin();
        }
    }

    /* =========================================================
       Forgot Password : one themed form (same look as Terms window)
       ========================================================= */

    private static void forgotPassword(JFrame owner) {
        final String title = "Forgot Password";

        JDialog dialog = new JDialog(owner, title, true);      // modal: blocks the login window
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JTextField     userField   = formField(new JTextField(),     420, 44);
        JTextField     emailField  = formField(new JTextField(),     420, 44);
        JTextField     nicField    = formField(new JTextField(),     420, 44);
        JPasswordField newPass     = formField(new JPasswordField(), 420, 44);
        JPasswordField confirmPass = formField(new JPasswordField(), 420, 44);

        /* --- Form (MINT background + FRESH border, like the Terms text area) --- */
        JPanel form = new JPanel();
        form.setBackground(MINT);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FRESH, 1),
                new EmptyBorder(20, 20, 20, 20)));

        addAll(form,
                label(title, F_UNI, FOREST, LEFT),
                gap(6),
                label("Verify your identity and choose a new password.", F_TERMS, DEEP, LEFT),
                gap(18),
                formLabel("Username"),         gap(6), userField,   gap(12),
                formLabel("Email"),            gap(6), emailField,  gap(12),
                formLabel("NIC"),              gap(6), nicField,    gap(12),
                formLabel("New Password"),     gap(6), newPass,     gap(12),
                formLabel("Re-enter Password"), gap(6), confirmPass);

        /* --- Button bar (PASTEL background, like the Terms window) --- */
        JButton cancelBtn = button("Cancel", F_LABEL);
        JButton resetBtn  = button("Reset Password", F_LABEL);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        bottom.setBackground(PASTEL);
        bottom.add(cancelBtn);
        bottom.add(resetBtn);

        cancelBtn.addActionListener(e -> dialog.dispose());

        resetBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String email    = emailField.getText().trim();
            String nic      = nicField.getText().trim();
            String p1       = readPassword(newPass);
            String p2       = readPassword(confirmPass);

            /* 1. Input checks (dialog stays open so the user can correct them) */
            if (username.isEmpty() || email.isEmpty() || nic.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
                show(dialog, "Please fill in all fields.", title, JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (p1.length() < MIN_PASSWORD_LENGTH) {
                show(dialog, "Password must be at least " + MIN_PASSWORD_LENGTH + " characters.",
                        title, JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!p1.equals(p2)) {
                show(dialog, "Passwords do not match.", title, JOptionPane.WARNING_MESSAGE);
                return;
            }

            /* 2. Update only if username + email + NIC all match one account */
            try {
                if (resetPassword(username, email, nic, p1)) {
                    show(dialog, "Password updated successfully.\nYou can now log in.",
                            title, JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    show(dialog, "No matching account found.\nPlease check your username, email and NIC.",
                            title, JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                dbError(dialog, title, ex);
            }
        });

        dialog.getRootPane().setDefaultButton(resetBtn);        // Enter = Reset Password
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);                                // blocks until the dialog is closed
    }

    /**
     * Saves the new password hash only when username, email and NIC all match
     * the same (non-suspended) account. Returns false when nothing matched.
     */
    private static boolean resetPassword(String username, String email, String nic, String newPassword)
            throws SQLException {
        String sql = "UPDATE users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE username = ? AND email = ? AND nic = ? AND user_status <> 'Suspended'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hashPassword(newPassword));
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, nic);
            return ps.executeUpdate() > 0;
        }
    }

    /** SHA-256 hex digest. */
    private static String hashPassword(String plain) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(plain.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /* =========================================================
       Terms & Conditions window
       ========================================================= */

    private static void showTerms(JFrame owner) {
        JFrame frame = new JFrame("Terms and Conditions");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        JTextArea area = new JTextArea(TERMS_TEXT);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(F_TERMS);
        area.setForeground(DEEP);
        area.setBackground(MINT);
        area.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(FRESH, 1));

        JButton close = button("Close", F_LABEL);
        close.addActionListener(e -> frame.dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        bottom.setBackground(PASTEL);
        bottom.add(close);

        frame.add(scroll, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);
        frame.setSize(640, 520);
        frame.setLocationRelativeTo(owner);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

    /* =========================================================
       Small UI helpers
       ========================================================= */

    private static void addAll(Container parent, Component... children) {
        for (Component c : children) parent.add(c);
    }

    private static Component gap(int height) {
        return Box.createVerticalStrut(height);
    }

    private static JLabel label(String text, Font font, Color color, float alignX) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(alignX);
        return l;
    }

    private static JLabel formLabel(String text) {
        return label(text, F_LABEL, DEEP, LEFT);
    }

    /** Underlined, clickable text. */
    private static JLabel linkLabel(String text, Runnable onClick) {
        JLabel l = label("<html><u>" + text + "</u></html>", F_LINK, FOREST, LEFT);
        l.setCursor(HAND);
        l.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });
        return l;
    }

    private static JButton button(String text, Font font) {
        JButton b = new JButton(text);
        b.setFont(font);
        b.setForeground(WHITE);
        b.setBackground(FOREST);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(HAND);
        return b;
    }

    /** Text field used on the login form. */
    private static <T extends JTextField> T loginField(T field) {
        return formField(field, 320, 50);
    }

    /** Text field that stretches to the form width and has a fixed height. */
    private static <T extends JTextField> T formField(T field, int width, int height) {
        styleField(field, 8, 10, width, height);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        field.setAlignmentX(LEFT);
        return field;
    }

    /** Password text, trimmed — login and reset must read it the same way. */
    private static String readPassword(JPasswordField field) {
        return new String(field.getPassword()).trim();
    }

    private static void styleField(JTextField field, int padV, int padH, int width, int height) {
        field.setFont(F_FIELD);
        field.setForeground(DEEP);
        field.setBackground(WHITE);
        field.setCaretColor(FOREST);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FRESH, 1),
                new EmptyBorder(padV, padH, padV, padH)));
        field.setPreferredSize(new Dimension(width, height));
    }

    private static void show(Component parent, String message, String title, int type) {
        JOptionPane.showMessageDialog(parent, message, title, type);
    }

    private static void dbError(Component parent, String title, SQLException ex) {
        show(parent, "Database error:\n" + ex.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }
}