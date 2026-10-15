package gui.login;

import gui.admin.AdminDashboard;
import gui.lecturer.Lecturer;
import gui.technical.TechnicalOfficerDashboard;
import gui.undergraduate.UndergraduateDashboard;
import service.AuthService.AuthUser;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Entry point: builds the login frame and routes the user to their dashboard. */
public class Login {

    public static final String APP_TITLE =
            "UniMis - Faculty Academic Management Information System";

    /* ---- Role -> dashboard routing (add a role = add one line) ---- */
    @FunctionalInterface
    public interface DashboardFactory {
        JFrame create(long userId, String username, String fullName, String email, String role);
    }

    private static final Map<String, DashboardFactory> DASHBOARDS = new HashMap<>();
    static {
        DASHBOARDS.put("admin",            AdminDashboard::new);
        DASHBOARDS.put("lecturer",         Lecturer::new);
        DASHBOARDS.put("technicalofficer", TechnicalOfficerDashboard::new);
        DASHBOARDS.put("undergraduate",    UndergraduateDashboard::new);
    }

    private static String roleKey(String role) {
        return role == null ? "" : role.replaceAll("[\\s_-]+", "").toLowerCase(Locale.ROOT);
    }

    /* ---- Entry points ---- */

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
        root.add(new LeftPanel());
        root.add(new RightPanel(frame));
        frame.setContentPane(root);

        frame.setLocationRelativeTo(null);
        GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().setFullScreenWindow(frame);
    }

    /* ---- Dashboard routing ---- */

    public static void openDashboard(JFrame owner, AuthUser user) {
        owner.dispose();

        DashboardFactory factory = DASHBOARDS.get(roleKey(user.role()));
        if (factory == null) {
            LoginTheme.show(null,
                    "Unknown role: \"" + user.role() + "\".\nContact the administrator.",
                    "Login", JOptionPane.ERROR_MESSAGE);
            showLogin();
            return;
        }

        try {
            JFrame dashboard = factory.create(
                    user.id(), user.username(), user.fullName(), user.email(), user.role());
            dashboard.setVisible(true);
            GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().setFullScreenWindow(dashboard);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            LoginTheme.show(null,
                    "Could not open the dashboard for role \"" + user.role() + "\":\n" + ex,
                    "Login", JOptionPane.ERROR_MESSAGE);
            showLogin();
        }
    }
}