package gui.admin;

import gui.login.Login;
import javax.swing.*;
import java.awt.*;

/**
 * Main Administrator window layout engine.
 * Utilizes absolute bounds management mapping for fixed full-screen display layers.
 */
public class Administrator extends JFrame {

    /* ---- Color Theme Variables matching Sidebar Layout ---- */
    private final Color MINT = new Color(0xE8, 0xF5, 0xE9);

    /* ---- Logged-in user data fields ---- */
    private final long   userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    /* ---- Active Subcomponents ---- */
    private AdminSidebar sidebar;
    private JPanel       contentArea;
    private CardLayout   contentLayout;

    /* ------------ Pages ----------- */
    private AdminDashboard pageDashboard;

    public Administrator() {
        this(0L, "-", "-", "-", "admin");
    }

    public Administrator(long userId, String username, String fullName, String email, String role) {
        super(Login.APP_TITLE + "  —  Administrator");

        this.userId   = userId;
        this.username = username;
        this.fullName = fullName;
        this.email    = email;
        this.role     = role;

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1. Enforce absolute positioning boundaries for parent components
        getContentPane().setLayout(null);
        getContentPane().setBackground(MINT); // Fixed frame background layer

        // 2. Instantiate and map the matching sidebar panel boundaries
        sidebar = new AdminSidebar();
        // Force the sidebar component coordinates to explicitly pin inside full-screen frames
        sidebar.setBounds(0, 0, 260, 1080);
        sidebar.setOnNavigate(this::navigateTo);
        sidebar.setOnLogout(this::onLogout);
        getContentPane().add(sidebar);

        // 3. Configure Central Display Pages container layout boundaries
        contentLayout = new CardLayout();
        contentArea   = new JPanel(contentLayout);
        contentArea.setOpaque(false); // Let the MINT background framework show through

        // Offset starting X by 260 so content fills the frame width (e.g. 1920 - 260 = 1660)
        contentArea.setBounds(260, 0, 1660, 1080);
        getContentPane().add(contentArea);

        buildPages();

        // 4. Force specific window initialization constraints for execution
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizes frame to full monitor dimension boundaries
        setUndecorated(true); // Optional: Clears native OS menu headers for true immersion layout

        setVisible(true);

        // Default routing target
        navigateTo(AdminSidebar.PAGE_DASHBOARD);
    }

    private void buildPages() {
        // Instantiate and assign directly to the private class field
        pageDashboard = new AdminDashboard(userId, username, fullName, email, role);

        contentArea.add(pageDashboard, AdminSidebar.PAGE_DASHBOARD);

        // Keep your other temporary placeholder panels
        contentArea.add(new JPanel(), AdminSidebar.PAGE_USERS);
        contentArea.add(new JPanel(), AdminSidebar.PAGE_COURSES);
        contentArea.add(new JPanel(), AdminSidebar.PAGE_NOTICES);
        contentArea.add(new JPanel(), AdminSidebar.PAGE_TIMETABLE);
        contentArea.add(new JPanel(), AdminSidebar.PAGE_SETTINGS);
    }


    private void navigateTo(String key) {
        contentLayout.show(contentArea, key);
        sidebar.setActive(key);

        // Force rendering updates down the component tree
        getContentPane().repaint();
    }

    private void onLogout() {
        int choice = JOptionPane.showConfirmDialog(
                this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            Login.showLogin();
        }
    }
}
