package gui.admin;

import gui.login.Login;
import service.AuthService.AuthUser;   // if you need to fetch fresh user data

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Administrator dashboard.
 * Only users whose role resolves to "admin" are allowed to open this frame.
 */
public class Administrator extends JFrame {

    /* ---- Logged-in user info ---- */
    private final long   userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    /* ---- UI components that display the user details ---- */
    private JLabel welcomeLabel;
    private JLabel idValueLabel;
    private JLabel userValueLabel;
    private JLabel nameValueLabel;
    private JLabel emailValueLabel;
    private JLabel roleValueLabel;

    /* ------------------------------------------------------------------ */
    /*  Constructors                                                      */
    /* ------------------------------------------------------------------ */

    /** No-arg constructor kept for compatibility (e.g. form previews). */
    public Administrator() {
        this(0L, "-", "-", "-", "admin");
    }

    /** Main constructor used by Login routing. */
    public Administrator(long userId, String username, String fullName,
                         String email, String role) {

        super(Login.APP_TITLE + "  —  Administrator");

        this.userId   = userId;
        this.username = username;
        this.fullName = fullName;
        this.email    = email;
        this.role     = role;

        // Role-based authorisation: refuse anyone who is not an admin.
        if (!isAdmin(role)) {
            showAccessDeniedAndLogout(role);
            return; // frame is disposed; do not build the rest of the UI
        }

        initUI();
    }

    /* ------------------------------------------------------------------ */
    /*  Role check                                                        */
    /* ------------------------------------------------------------------ */

    private static boolean isAdmin(String role) {
        if (role == null) return false;
        String key = role.replaceAll("[\\s_-]+", "").toLowerCase();
        return key.equals("admin") || key.equals("administrator");
    }

    private void showAccessDeniedAndLogout(String actualRole) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    null,
                    "Access denied.\nThis dashboard is restricted to Administrators.\n" +
                            "Your role: \"" + actualRole + "\"",
                    "Unauthorised",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            Login.showLogin();
        });
    }

    /* ------------------------------------------------------------------ */
    /*  UI                                                                */
    /* ------------------------------------------------------------------ */

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 60, 120));
        header.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel title = new JLabel("Administrator Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        welcomeLabel = new JLabel("Welcome, " + safe(fullName));
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeLabel.setForeground(Color.WHITE);

        header.add(title,        BorderLayout.WEST);
        header.add(welcomeLabel, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(20, 20));
        body.setBorder(new EmptyBorder(30, 40, 30, 40));
        body.setBackground(new Color(245, 247, 250));

        // Left: user details card
        body.add(buildUserDetailsCard(), BorderLayout.WEST);

        // Center: admin actions
        body.add(buildActionsCard(), BorderLayout.CENTER);

        return body;
    }

    private JPanel buildUserDetailsCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225)),
                new EmptyBorder(20, 25, 20, 25)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel heading = new JLabel("Your Account");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(heading, gbc);
        gbc.gridwidth = 1;

        idValueLabel   = new JLabel();
        userValueLabel = new JLabel();
        nameValueLabel = new JLabel();
        emailValueLabel= new JLabel();
        roleValueLabel = new JLabel();

        int row = 1;
        addRow(card, gbc, row++, "User ID:",   idValueLabel);
        addRow(card, gbc, row++, "Username:",  userValueLabel);
        addRow(card, gbc, row++, "Full name:", nameValueLabel);
        addRow(card, gbc, row++, "Email:",     emailValueLabel);
        addRow(card, gbc, row,   "Role:",      roleValueLabel);

        refreshUserDetails();
        return card;
    }

    private void addRow(JPanel parent, GridBagConstraints gbc, int row,
                        String labelText, JLabel valueLabel) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));

        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueLabel.setForeground(new Color(50, 55, 70));

        gbc.gridx = 0; gbc.gridy = row; parent.add(label,      gbc);
        gbc.gridx = 1; gbc.gridy = row; parent.add(valueLabel, gbc);
    }

    /** Fill the labels with the currently-stored user info. */
    private void refreshUserDetails() {
        idValueLabel   .setText(String.valueOf(userId));
        userValueLabel .setText(safe(username));
        nameValueLabel .setText(safe(fullName));
        emailValueLabel.setText(safe(email));
        roleValueLabel .setText(safe(role));
        if (welcomeLabel != null) welcomeLabel.setText("Welcome, " + safe(fullName));
    }

    private JPanel buildActionsCard() {
        JPanel card = new JPanel(new GridLayout(0, 1, 10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225)),
                new EmptyBorder(20, 25, 20, 25)));

        JLabel heading = new JLabel("Administrative Actions");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        card.add(heading);

        card.add(actionButton("Manage Users",        this::onManageUsers));
        card.add(actionButton("Manage Courses",      this::onManageCourses));
        card.add(actionButton("Assign Lecturers",    this::onAssignLecturers));
        card.add(actionButton("View Reports",        this::onViewReports));
        card.add(actionButton("System Settings",     this::onSystemSettings));

        // push buttons to top
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(245, 247, 250));
        wrapper.add(card, BorderLayout.NORTH);
        return wrapper;
    }

    private JButton actionButton(String text, Runnable onClick) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(220, 40));
        b.addActionListener(e -> onClick.run());
        return b;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(new Color(240, 242, 246));
        footer.setBorder(new EmptyBorder(8, 15, 8, 15));

        JLabel signedIn = new JLabel("Signed in as " + safe(username) + "  |  ");
        signedIn.setForeground(new Color(80, 85, 100));

        JButton logout = new JButton("Logout");
        logout.setFocusPainted(false);
        logout.addActionListener(e -> onLogout());

        footer.add(signedIn);
        footer.add(logout);
        return footer;
    }

    /* ------------------------------------------------------------------ */
    /*  Action handlers                                                   */
    /* ------------------------------------------------------------------ */

    private void onManageUsers()     { placeholder("Manage Users"); }
    private void onManageCourses()   { placeholder("Manage Courses"); }
    private void onAssignLecturers() { placeholder("Assign Lecturers"); }
    private void onViewReports()     { placeholder("View Reports"); }
    private void onSystemSettings()  { placeholder("System Settings"); }

    private void placeholder(String action) {
        JOptionPane.showMessageDialog(this,
                action + " — not implemented yet.",
                "Administrator", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onLogout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Logout",
                JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        dispose();
        Login.showLogin();
    }

    /* ------------------------------------------------------------------ */
    /*  Helpers                                                           */
    /* ------------------------------------------------------------------ */

    private static String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}