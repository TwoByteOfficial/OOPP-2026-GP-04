package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class Login {

    /* ---------- Color Palette (Light Green) ---------- */
    static final Color MINT   = new Color(0xE8, 0xF5, 0xE9); // background
    static final Color PASTEL = new Color(0xC8, 0xE6, 0xC9); // left panel / card
    static final Color FRESH  = new Color(0xA5, 0xD6, 0xA7); // borders
    static final Color MEDIUM = new Color(0x81, 0xC7, 0x84); // hover
    static final Color FOREST = new Color(0x38, 0x8E, 0x3C); // buttons / headings
    static final Color DEEP   = new Color(0x1B, 0x5E, 0x20); // text
    static final Color WHITE  = Color.WHITE;

    /* ---------- Logo paths ---------- */
    private static final String UNI_LOGO     = "/home/dasindu-dilvan/Documents/8.OOPP Mini Project/OOPP-2026-GP-04/assets/university-logo.png";
    private static final String FACULTY_LOGO = "/home/dasindu-dilvan/Documents/8.OOPP Mini Project/OOPP-2026-GP-04/assets/faculty-logo.png";

    /* ---------- Fonts ---------- */
    private static final Font F_TITLE   = new Font("Segoe UI", Font.BOLD, 50);
    private static final Font F_TAG     = new Font("Segoe UI", Font.PLAIN, 22);
    private static final Font F_LABEL   = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font F_FIELD   = new Font("Segoe UI", Font.PLAIN, 19);
    private static final Font F_SMALL   = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font F_LINK    = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font F_UNI     = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font F_FAC     = new Font("Segoe UI", Font.PLAIN, 28);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::start);
    }

    private static void start() {
        JFrame logframe = new JFrame("UniMis - Faculty Academic Management Information System");
        logframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        logframe.setResizable(false);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(buildLeftPanel());
        root.add(buildRightPanel(logframe));
        logframe.setContentPane(root);

        logframe.setLocationRelativeTo(null);
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        device.setFullScreenWindow(logframe);
    }

    /* =========================================================
       LEFT PANEL : Logos + University / Faculty name
       ========================================================= */
    private static JPanel buildLeftPanel() {
        JPanel left = new JPanel();
        left.setBackground(PASTEL);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel uniLogo     = logoLabel(UNI_LOGO,     "University Logo", 220, 220);
        JLabel facultyLogo = logoLabel(FACULTY_LOGO, "Faculty Logo",    180, 180);

        JLabel uniName = new JLabel("University Of Ruhuna");
        uniName.setFont(F_UNI);
        uniName.setForeground(DEEP);
        uniName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel facName = new JLabel("Faculty of Technology");
        facName.setFont(F_FAC);
        facName.setForeground(FOREST);
        facName.setAlignmentX(Component.CENTER_ALIGNMENT);

        left.add(Box.createVerticalGlue());
        left.add(uniLogo);
        left.add(Box.createVerticalStrut(25));
        left.add(facultyLogo);
        left.add(Box.createVerticalStrut(30));
        left.add(uniName);
        left.add(Box.createVerticalStrut(6));
        left.add(facName);
        left.add(Box.createVerticalGlue());

        return left;
    }

    /** Loads an image scaled to fit, or falls back to a labeled box. */
    private static JLabel logoLabel(String path, String fallback, int w, int h) {
        ImageIcon icon = loadIcon(path, w, h);
        JLabel label = icon != null ? new JLabel(icon)
                : new JLabel(fallback, SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (icon == null) {
            label.setOpaque(true);
            label.setBackground(WHITE);
            label.setForeground(DEEP);
            label.setFont(F_LABEL);
            label.setPreferredSize(new Dimension(w, h));
            label.setMaximumSize(new Dimension(w, h));
        }
        return label;
    }

    private static ImageIcon loadIcon(String path, int maxW, int maxH) {
        File file = new File(path);
        if (!file.exists()) return null;
        ImageIcon original = new ImageIcon(file.getAbsolutePath());
        int w = original.getIconWidth(), h = original.getIconHeight();
        if (w <= 0 || h <= 0) return null;
        double scale = Math.min((double) maxW / w, (double) maxH / h);
        Image img = original.getImage().getScaledInstance(
                (int) (w * scale), (int) (h * scale), Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    /* =========================================================
       RIGHT PANEL : Login form
       ========================================================= */
    private static JPanel buildRightPanel(JFrame owner) {
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(MINT);

        JPanel form = new JPanel();
        form.setBackground(MINT);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(0, 60, 0, 60));

        /* --- Title + tagline --- */
        JLabel title = new JLabel("Login");
        title.setFont(F_TITLE);
        title.setForeground(FOREST);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("UniMis - Faculty Academic Management Information System");
        tagline.setFont(F_TAG);
        tagline.setForeground(DEEP);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        /* --- Username --- */
        JLabel userLabel = formLabel("Username");
        JTextField userField = new JTextField();
        styleField(userField);

        /* --- Password --- */
        JLabel passLabel = formLabel("Password");
        JPasswordField passField = new JPasswordField();
        styleField(passField);

        /* --- Forgot password --- */
        JLabel forgot = linkLabel("Forgot Password?");
        forgot.setAlignmentX(Component.LEFT_ALIGNMENT);
        forgot.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(owner,
                        "Please contact the Faculty IT Division to reset your password.\n" +
                                "Email: it.support@ruh.ac.lk",
                        "Forgot Password", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        /* --- Login button --- */
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loginBtn.setForeground(WHITE);
        loginBtn.setBackground(FOREST);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.setPreferredSize(new Dimension(320, 44));
        loginBtn.addActionListener(e -> doLogin(owner, userField, passField));

        /* --- Terms link --- */
        JLabel terms = linkLabel("Terms and Conditions");
        terms.setAlignmentX(Component.LEFT_ALIGNMENT);
        terms.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                showTerms(owner);
            }
        });

        /* --- Assemble form --- */
        form.add(title);
        form.add(Box.createVerticalStrut(6));
        form.add(tagline);
        form.add(Box.createVerticalStrut(35));

        form.add(userLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(userField);
        form.add(Box.createVerticalStrut(18));

        form.add(passLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(passField);
        form.add(Box.createVerticalStrut(8));

        form.add(forgot);
        form.add(Box.createVerticalStrut(24));
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(30));

        form.add(terms);

        right.add(form);
        return right;
    }

    private static JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_LABEL);
        l.setForeground(DEEP);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private static void styleField(JTextField field) {
        field.setFont(F_FIELD);
        field.setForeground(DEEP);
        field.setBackground(WHITE);
        field.setCaretColor(FOREST);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FRESH, 1),
                new EmptyBorder(8, 10, 8, 10)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        field.setPreferredSize(new Dimension(320, 50));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private static JLabel linkLabel(String text) {
        JLabel l = new JLabel("<html><u>" + text + "</u></html>");
        l.setFont(F_LINK);
        l.setForeground(FOREST);
        l.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return l;
    }

    /* =========================================================
       Login action (replace with real authentication)
       ========================================================= */
    private static void doLogin(JFrame owner, JTextField user, JPasswordField pass) {
        String u = user.getText().trim();
        String p = new String(pass.getPassword()).trim();
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(owner,
                    "Please enter both username and password.",
                    "Login", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(owner,
                "Welcome, " + u + "!",
                "Login Successful", JOptionPane.INFORMATION_MESSAGE);
    }



    /* =========================================================
       Terms & Conditions frame
       ========================================================= */
    private static void showTerms(JFrame owner) {
        JFrame frame = new JFrame("Terms and Conditions");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);

        JTextArea area = new JTextArea(
                "UniMis - Terms and Conditions\n\n" +
                        "1. Acceptance of Terms\n" +
                        "By logging in to UniMis you agree to abide by the rules and regulations " +
                        "of the University of Ruhuna and the Faculty of Technology.\n\n" +
                        "2. Authorised Use\n" +
                        "This system is provided only for registered students and staff of the faculty. " +
                        "Credentials are personal and must not be shared.\n\n" +
                        "3. Data Privacy\n" +
                        "All academic and personal data is treated as confidential. Access is logged.\n\n" +
                        "4. Misuse\n" +
                        "Unauthorised access, alteration of records, or disruption of service " +
                        "will result in disciplinary action.\n\n" +
                        "5. Changes\n" +
                        "The faculty reserves the right to modify these terms at any time."
        );
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setForeground(DEEP);
        area.setBackground(MINT);
        area.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(FRESH, 1));

        JButton close = new JButton("Close");
        close.setFont(F_LABEL);
        close.setForeground(WHITE);
        close.setBackground(FOREST);
        close.setFocusPainted(false);
        close.setBorderPainted(false);
        close.setOpaque(true);
        close.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
}