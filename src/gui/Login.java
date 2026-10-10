package gui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class Login {

    /* ================= Light Green Palette ================= */
    private static final Color MINT   = new Color(0xE8, 0xF5, 0xE9); // Mint Background
    private static final Color PASTEL = new Color(0xC8, 0xE6, 0xC9); // Pastel Green
    private static final Color FRESH  = new Color(0xA5, 0xD6, 0xA7); // Fresh Green
    private static final Color MEDIUM = new Color(0x81, 0xC7, 0x84); // Medium Green
    private static final Color FOREST = new Color(0x38, 0x8E, 0x3C); // Forest Green
    private static final Color DEEP   = new Color(0x1B, 0x5E, 0x20); // Deep Green
    private static final Color WHITE  = Color.WHITE;

    private static final String UNI_LOGO     = "/home/dasindu-dilvan/Documents/8.OOPP Mini Project/OOPP-2026-GP-04/assets/university-logo.png";
    private static final String FACULTY_LOGO = "/home/dasindu-dilvan/Documents/8.OOPP Mini Project/OOPP-2026-GP-04/assets/faculty-logo.png";

    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 44);
    private static final Font FONT_TAGLINE = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_FIELD   = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 13);

    /* ================= Entry Point ================= */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::start);
    }

    private static void start() {
        JFrame logframe = new JFrame("UniMis - Faculty Academic Management Information System");
        logframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        logframe.setResizable(false);

        JPanel root = new JPanel(new GridLayout(1, 2, 0, 0));
        root.setBackground(MINT);
        root.add(createLeftPanel());
        root.add(createRightPanel(logframe));

        logframe.setContentPane(root);

        logframe.setLocationRelativeTo(null);
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        device.setFullScreenWindow(logframe);
    }

    /* ================= LEFT HALF : Logos ================= */
    private static JPanel createLeftPanel() {
        JPanel left = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, PASTEL, getWidth(), getHeight(), FRESH));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        left.setOpaque(true);
        left.setBorder(new EmptyBorder(50, 50, 50, 50));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(0, 0, 45, 0);
        left.add(createLogoLabel(UNI_LOGO, "University Logo", 260, 260), gc);

        gc.gridy = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        left.add(createLogoLabel(FACULTY_LOGO, "Faculty Logo", 240, 240), gc);

        return left;
    }

    private static JLabel createLogoLabel(String path, String fallbackText, int maxW, int maxH) {
        ImageIcon icon = loadScaledIcon(path, maxW, maxH);
        JLabel label;

        if (icon != null) {
            label = new JLabel(icon);
            label.setOpaque(false);
        } else {
            label = new JLabel(fallbackText, SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            label.setForeground(DEEP);
            label.setOpaque(true);
            label.setBackground(WHITE);
            label.setPreferredSize(new Dimension(maxW, maxH));
            label.setBorder(new LineBorder(FRESH, 2, true));
        }
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private static ImageIcon loadScaledIcon(String path, int maxW, int maxH) {
        File file = new File(path);
        if (!file.exists()) {
            file = new File(new File(path).getName()); // fallback: look next to the app
        }
        if (!file.exists()) return null;

        ImageIcon original = new ImageIcon(file.getAbsolutePath());
        int w = original.getIconWidth();
        int h = original.getIconHeight();
        if (w <= 0 || h <= 0) return null;

        double scale = Math.min((double) maxW / w, (double) maxH / h);
        int nw = (int) Math.round(w * scale);
        int nh = (int) Math.round(h * scale);

        Image scaled = original.getImage().getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /* ================= RIGHT HALF ================= */
    private static JPanel createRightPanel(JFrame owner) {
        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(MINT);
        right.add(createHeader(), BorderLayout.NORTH);
        right.add(createFormArea(owner), BorderLayout.CENTER);
        right.add(createFooter(owner), BorderLayout.SOUTH);
        return right;
    }

    /* ---------- Top : Title + Tag-line ---------- */
    private static JPanel createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(70, 70, 10, 70));

        JLabel title = new JLabel("Login");
        title.setFont(FONT_TITLE);
        title.setForeground(FOREST);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("UniMis - Faculty Academic Management Information System");
        tagline.setFont(FONT_TAGLINE);
        tagline.setForeground(DEEP);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel underline = new JPanel();
        underline.setBackground(MEDIUM);
        underline.setPreferredSize(new Dimension(90, 4));
        underline.setMaximumSize(new Dimension(90, 4));
        underline.setMinimumSize(new Dimension(90, 4));
        underline.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(tagline);
        header.add(Box.createVerticalStrut(18));
        header.add(underline);

        return header;
    }

    /* ---------- Middle : Form Card ---------- */
    private static JPanel createFormArea(JFrame owner) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(10, 70, 10, 70));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(PASTEL);
        card.setBorder(new CompoundBorder(
                new LineBorder(FRESH, 1, true),
                new EmptyBorder(32, 36, 32, 36)));

        JTextField userField = new JTextField();
        styleField(userField);

        JPasswordField passField = new JPasswordField();
        styleField(passField);

        JLabel forgot = createLinkLabel("Forgot Password?");
        forgot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(owner,
                        "Please contact the Faculty IT Division to reset your password.\n" +
                                "Email: it.support@university.edu",
                        "Forgot Password",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotPanel.setOpaque(false);
        forgotPanel.add(forgot);

        FlatButton loginButton = new FlatButton("Login", FOREST);
        loginButton.setPreferredSize(new Dimension(340, 46));
        loginButton.addActionListener(e -> handleLogin(owner, userField, passField));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        gc.gridy = 0;
        gc.insets = new Insets(0, 0, 6, 0);
        card.add(fieldLabel("Username"), gc);

        gc.gridy = 1;
        gc.insets = new Insets(0, 0, 18, 0);
        card.add(userField, gc);

        gc.gridy = 2;
        gc.insets = new Insets(0, 0, 6, 0);
        card.add(fieldLabel("Password"), gc);

        gc.gridy = 3;
        gc.insets = new Insets(0, 0, 4, 0);
        card.add(passField, gc);

        gc.gridy = 4;
        gc.insets = new Insets(0, 0, 18, 0);
        card.add(forgotPanel, gc);

        gc.gridy = 5;
        gc.insets = new Insets(0, 0, 0, 0);
        card.add(loginButton, gc);

        wrapper.add(card);
        return wrapper;
    }

    private static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(DEEP);
        return label;
    }

    private static void styleField(JTextField field) {
        field.setFont(FONT_FIELD);
        field.setForeground(DEEP);
        field.setBackground(WHITE);
        field.setCaretColor(FOREST);
        field.setPreferredSize(new Dimension(340, 40));
        field.setBorder(new CompoundBorder(
                new LineBorder(MEDIUM, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
    }

    /* ---------- Bottom : Instructions + Terms ---------- */
    private static JPanel createFooter(JFrame owner) {
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(new EmptyBorder(10, 70, 60, 70));

        JLabel help = new JLabel("<html>"
                + "1. Use the username issued by your faculty office.<br>"
                + "2. Enter your password and click <b>Login</b>.<br>"
                + "3. Forgot your password? Use the link above or contact the IT Division."
                + "</html>");
        help.setFont(FONT_SMALL);
        help.setForeground(DEEP);
        help.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel terms = createLinkLabel("Terms and Conditions");
        terms.setAlignmentX(Component.LEFT_ALIGNMENT);
        terms.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showTermsAndConditions(owner);
            }
        });

        footer.add(help);
        footer.add(Box.createVerticalStrut(14));
        footer.add(terms);

        return footer;
    }

    private static JLabel createLinkLabel(String text) {
        JLabel link = new JLabel("<html><u>" + text + "</u></html>");
        link.setFont(FONT_SMALL);
        link.setForeground(FOREST);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return link;
    }

    /* ================= Terms & Conditions Frame ================= */
    private static void showTermsAndConditions(JFrame owner) {
        JFrame termsFrame = new JFrame("Terms and Conditions");
        termsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        termsFrame.setLayout(new BorderLayout());

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setForeground(DEEP);
        area.setBackground(MINT);
        area.setBorder(new EmptyBorder(20, 20, 20, 20));
        area.setText(
                "UniMis - Faculty Academic Management Information System\n" +
                        "Terms and Conditions\n\n" +
                        "1. Acceptance of Terms\n" +
                        "By logging in to UniMis you agree to abide by the rules and regulations " +
                        "of the university and of your faculty.\n\n" +
                        "2. Authorised Use\n" +
                        "This system is provided only for registered students and staff of the faculty. " +
                        "Credentials are personal and must not be shared with anyone.\n\n" +
                        "3. Data Privacy\n" +
                        "All academic and personal data handled by this system is treated as confidential. " +
                        "Access is logged and monitored.\n\n" +
                        "4. Misuse\n" +
                        "Any attempt to gain unauthorised access, alter records, or disrupt the service " +
                        "will result in disciplinary action.\n\n" +
                        "5. Changes\n" +
                        "The faculty reserves the right to modify these terms at any time. " +
                        "Continued use of the system constitutes acceptance of the updated terms."
        );

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(new LineBorder(FRESH, 1));

        FlatButton closeButton = new FlatButton("Close", FOREST);
        closeButton.setPreferredSize(new Dimension(120, 38));
        closeButton.addActionListener(e -> termsFrame.dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        bottom.setBackground(PASTEL);
        bottom.add(closeButton);

        termsFrame.add(scroll, BorderLayout.CENTER);
        termsFrame.add(bottom, BorderLayout.SOUTH);
        termsFrame.setSize(640, 520);
        termsFrame.setLocationRelativeTo(owner);
        termsFrame.setAlwaysOnTop(true);
        termsFrame.setVisible(true);
    }

    /* ================= Login Action ================= */
    private static void handleLogin(JFrame owner, JTextField userField, JPasswordField passField) {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(owner,
                    "Please enter both username and password.",
                    "Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // TODO : replace with your real authentication logic
        JOptionPane.showMessageDialog(owner,
                "Welcome, " + username + "!",
                "Login Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    /* ================= Custom Flat Button ================= */
    static class FlatButton extends JButton {
        private final Color base;

        FlatButton(String text, Color base) {
            super(text);
            this.base = base;
            setForeground(WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 20, 10, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill = base;
            if (getModel().isPressed()) {
                fill = DEEP;
            } else if (getModel().isRollover()) {
                fill = MEDIUM;
            }
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();

            super.paintComponent(g);
        }
    }
}