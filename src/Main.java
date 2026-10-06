import util.DBConnection;
import dao.*;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * UniMIS Main Application Entry Point
 * Faculty of Technology - University of Ruhuna
 * 
 * Production Common Login Interface for all 4 Roles:
 * - Administrators (ADM001 / admin / adm@123)
 * - Lecturers (LEC001 / lecturer1 / lec@123)
 * - Technical Officers (TO001 / to1 / to@123)
 * - Undergraduates (TG/2024/2061 or tg2061 / ug@123)
 */
public class Main extends JFrame {

    // Palette Constants (Ruhuna FOT Theme)
    private static final Color NAVY_PRIMARY = new Color(23, 59, 108);     // #173b6c
    private static final Color NAVY_DARK = new Color(13, 42, 80);        // #0d2a50
    private static final Color GOLD_ACCENT = new Color(245, 166, 35);    // #f5a623
    private static final Color BG_LIGHT = new Color(248, 250, 252);      // #f8fafc
    private static final Color TEXT_DARK = new Color(30, 41, 59);        // #1e293b
    private static final Color TEXT_MUTED = new Color(100, 116, 139);    // #64748b
    private static final Color SUCCESS_GREEN = new Color(25, 135, 84);   // #198754
    private static final Color ERROR_RED = new Color(220, 53, 69);       // #dc3545

    private JTextField txtIdentifier;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    private JButton btnLogin;
    private JLabel lblStatus;

    private final UserDAO userDAO = new UserDAO();

    public Main() {
        setTitle("UniMIS - Faculty Academic Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 560);
        setResizable(false);
        setLocationRelativeTo(null);

        // Main Layout
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(createBrandPanel());
        mainPanel.add(createLoginFormPanel());

        setContentPane(mainPanel);
        testDatabaseConnection();
    }

    /**
     * Left side panel with University of Ruhuna branding and color gradient
     */
    private JPanel createBrandPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, NAVY_PRIMARY, 0, getHeight(), NAVY_DARK);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Decorative Gold Border Line
                g2d.setColor(GOLD_ACCENT);
                g2d.fillRect(getWidth() - 4, 0, 4, getHeight());
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(40, 35, 40, 35));

        JLabel lblEmblem = new JLabel("🎓");
        lblEmblem.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        lblEmblem.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblFaculty = new JLabel("FACULTY OF TECHNOLOGY");
        lblFaculty.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblFaculty.setForeground(GOLD_ACCENT);
        lblFaculty.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUni = new JLabel("UNIVERSITY OF RUHUNA");
        lblUni.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblUni.setForeground(Color.WHITE);
        lblUni.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSystem = new JLabel("UniMIS Academic Portal");
        lblSystem.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblSystem.setForeground(new Color(203, 213, 225));
        lblSystem.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea txtDesc = new JTextArea(
                "Welcome to the Unified Academic Management System.\n\n" +
                "Single login gateway for Undergraduates, Lecturers, Technical Officers, and Administrators.\n\n" +
                "• Index Format: TG/2024/2061 or tg2061\n" +
                "• Staff Code: LEC001 / TO001 / ADM001"
        );
        txtDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtDesc.setForeground(new Color(148, 163, 184));
        txtDesc.setBackground(new Color(0, 0, 0, 0));
        txtDesc.setEditable(false);
        txtDesc.setFocusable(false);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtDesc.setMaximumSize(new Dimension(340, 150));

        panel.add(lblEmblem);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(lblFaculty);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(lblUni);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(lblSystem);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(txtDesc);

        return panel;
    }

    /**
     * Right side panel with user login credentials form
     */
    private JPanel createLoginFormPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_LIGHT);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel lblTitle = new JLabel("System Sign In");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Enter your academic identifier and password to continue.");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSubtitle.setForeground(TEXT_MUTED);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Identifier Input (Username / Index / Staff Code)
        JLabel lblIdentifier = new JLabel("Username / Index No / Staff Code:");
        lblIdentifier.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblIdentifier.setForeground(TEXT_DARK);
        lblIdentifier.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtIdentifier = new JTextField();
        txtIdentifier.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtIdentifier.setMaximumSize(new Dimension(380, 38));
        txtIdentifier.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password Input
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblPassword.setForeground(TEXT_DARK);
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtPassword.setMaximumSize(new Dimension(380, 38));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setFont(new Font("SansSerif", Font.PLAIN, 11));
        chkShowPassword.setForeground(TEXT_MUTED);
        chkShowPassword.setBackground(BG_LIGHT);
        chkShowPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });

        // Login Button
        btnLogin = new JButton("SIGN IN TO UNIMIS");
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(NAVY_PRIMARY);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(380, 42));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> performLogin());

        // Status Feedback Label
        lblStatus = new JLabel("Checking database status...");
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblStatus.setForeground(TEXT_MUTED);
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assemble Form Components
        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(lblSubtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        panel.add(lblIdentifier);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(txtIdentifier);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(lblPassword);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(txtPassword);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(chkShowPassword);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(btnLogin);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(lblStatus);

        return panel;
    }

    /**
     * Test connection to MySQL database via util.DBConnection
     */
    private void testDatabaseConnection() {
        SwingUtilities.invokeLater(() -> {
            try (Connection conn = DBConnection.getConnection()) {
                if (conn != null && !conn.isClosed()) {
                    lblStatus.setText("● Connected to MySQL unimis database");
                    lblStatus.setForeground(SUCCESS_GREEN);
                }
            } catch (SQLException e) {
                lblStatus.setText("✕ DB Connection Error: " + e.getMessage());
                lblStatus.setForeground(ERROR_RED);
            }
        });
    }

    /**
     * Process authentication via UserDAO
     */
    private void performLogin() {
        String identifier = txtIdentifier.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (identifier.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both identifier and password.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnLogin.setEnabled(false);
        lblStatus.setText("Authenticating with database...");
        lblStatus.setForeground(NAVY_PRIMARY);

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.authenticate(identifier, password);
            }

            @Override
            protected void done() {
                btnLogin.setEnabled(true);
                try {
                    User user = get();
                    if (user != null) {
                        lblStatus.setText("● Authentication Successful!");
                        lblStatus.setForeground(SUCCESS_GREEN);

                        // Close Login and Open Role Dashboard
                        dispose();
                        openRoleDashboard(user);
                    } else {
                        lblStatus.setText("✕ Invalid credentials or inactive user account.");
                        lblStatus.setForeground(ERROR_RED);
                        JOptionPane.showMessageDialog(Main.this,
                                "Invalid Username/Index or Password.\nPlease check your credentials and try again.",
                                "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    lblStatus.setText("✕ Database Authentication Error");
                    lblStatus.setForeground(ERROR_RED);
                    JOptionPane.showMessageDialog(Main.this,
                            "Error connecting to database:\n" + ex.getCause().getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    /**
     * Opens Role-Tailored Dashboard Frame after authentication
     */
    private void openRoleDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            RoleDashboardFrame dashboard = new RoleDashboardFrame(user);
            dashboard.setVisible(true);
        });
    }

    /**
     * Main Launch Method
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            Main loginFrame = new Main();
            loginFrame.setVisible(true);
        });
    }
}

/**
 * Role-Tailored Post-Login Dashboard Frame
 */
class RoleDashboardFrame extends JFrame {

    public RoleDashboardFrame(User user) {
        setTitle("UniMIS - " + user.getRole() + " Dashboard | " + user.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(23, 59, 108));
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel lblTitle = new JLabel("UniMIS - " + user.getRole() + " Portal");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblUser = new JLabel("Welcome, " + user.getFullName() + " [" + user.getRole() + "]");
        lblUser.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblUser.setForeground(new Color(245, 166, 35));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblUser, BorderLayout.EAST);

        // Content Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 12));

        // Tab 1: Profile Details
        tabbedPane.addTab("👤 My Profile", createProfilePanel(user));

        // Tab 2: Role Operations
        if ("Undergraduate".equalsIgnoreCase(user.getRole())) {
            tabbedPane.addTab("📚 Enrolled Courses", createCoursesPanel(user));
            tabbedPane.addTab("📊 CA Marks & Eligibility", createEligibilityPanel(user));
        } else if ("Lecturer".equalsIgnoreCase(user.getRole())) {
            tabbedPane.addTab("📖 Assigned Modules", createLecturerCoursesPanel(user));
        } else if ("Technical Officer".equalsIgnoreCase(user.getRole())) {
            tabbedPane.addTab("📝 Attendance & Medicals", createTOMedicalPanel());
        } else if ("Admin".equalsIgnoreCase(user.getRole())) {
            tabbedPane.addTab("👥 System User Accounts", createAdminUserPanel());
        }

        // Tab 3: Announcements
        tabbedPane.addTab("📢 Faculty Notices", createNoticesPanel());

        // Footer Bar
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(241, 245, 249));

        JButton btnLogout = new JButton("Sign Out");
        btnLogout.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLogout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new Main().setVisible(true));
        });
        footerPanel.add(btnLogout);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createProfilePanel(User user) {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        panel.add(new JLabel("Full Name:"));
        panel.add(new JLabel(user.getFullName()));

        panel.add(new JLabel("Username:"));
        panel.add(new JLabel(user.getUsername()));

        panel.add(new JLabel("Email Address:"));
        panel.add(new JLabel(user.getEmail()));

        panel.add(new JLabel("Contact Number:"));
        panel.add(new JLabel(user.getContactNo() != null ? user.getContactNo() : "N/A"));

        if (user instanceof Undergraduate) {
            Undergraduate ug = (Undergraduate) user;
            panel.add(new JLabel("Registration Number:"));
            panel.add(new JLabel(ug.getRegistrationNo()));

            panel.add(new JLabel("Student Type:"));
            panel.add(new JLabel(ug.getStudentType() + " (Batch " + ug.getBatchYear() + ")"));
        } else {
            panel.add(new JLabel("Account Status:"));
            panel.add(new JLabel(user.getUserStatus()));

            panel.add(new JLabel("User ID:"));
            panel.add(new JLabel(String.valueOf(user.getUserId())));
        }

        return panel;
    }

    private JPanel createCoursesPanel(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        String[] cols = {"Course Code", "Course Name", "Total Credits", "CA Weight %", "Semester"};
        JTable table = new JTable(new Object[][]{}, cols);
        JScrollPane sp = new JScrollPane(table);

        try {
            EnrollmentDAO dao = new EnrollmentDAO();
            List<Course> list = dao.getCoursesByStudent(user.getUserId());
            Object[][] data = new Object[list.size()][5];
            for (int i = 0; i < list.size(); i++) {
                Course c = list.get(i);
                data[i][0] = c.getCourseCode();
                data[i][1] = c.getCourseName();
                data[i][2] = c.getTotalCredits();
                data[i][3] = c.getCaWeight() + "%";
                data[i][4] = c.getSemester();
            }
            table.setModel(new javax.swing.table.DefaultTableModel(data, cols));
        } catch (Exception e) {
            panel.add(new JLabel("Error loading enrolled courses: " + e.getMessage()), BorderLayout.NORTH);
        }

        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEligibilityPanel(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel lbl = new JLabel("CA Marks and Exam Eligibility status automatically loaded from DB View 'v_eligibility'");
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(lbl, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createLecturerCoursesPanel(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.add(new JLabel("Lecturer Assigned Course Modules & Marks Entry Interface"), BorderLayout.NORTH);
        return panel;
    }

    private JPanel createTOMedicalPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.add(new JLabel("Technical Officer Attendance Sessions & Medical Approval Queue"), BorderLayout.NORTH);
        return panel;
    }

    private JPanel createAdminUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.add(new JLabel("System Administrator - User Accounts Management (Admins, Lecturers, TOs, Undergraduates)"), BorderLayout.NORTH);
        return panel;
    }

    private JPanel createNoticesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        String[] cols = {"Type", "Notice Title", "Target Role", "Published At"};
        JTable table = new JTable(new Object[][]{}, cols);
        JScrollPane sp = new JScrollPane(table);

        try {
            NoticeDAO dao = new NoticeDAO();
            List<Notice> list = dao.getAllNotices();
            Object[][] data = new Object[list.size()][4];
            for (int i = 0; i < list.size(); i++) {
                Notice n = list.get(i);
                data[i][0] = n.getNoticeType();
                data[i][1] = n.getTitle();
                data[i][2] = n.getTargetRole();
                data[i][3] = n.getPublishedAt();
            }
            table.setModel(new javax.swing.table.DefaultTableModel(data, cols));
        } catch (Exception e) {
            panel.add(new JLabel("Error loading faculty notices: " + e.getMessage()), BorderLayout.NORTH);
        }

        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }
}
