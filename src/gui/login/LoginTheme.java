package gui.login;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Shared colours, fonts, assets and small Swing helpers for the login screens. */
public final class LoginTheme {

    private LoginTheme() {}

    /* ---- Colours ---- */
    public static final Color MINT   = new Color(0xE8, 0xF5, 0xE9);
    public static final Color PASTEL = new Color(0xC8, 0xE6, 0xC9);
    public static final Color FRESH  = new Color(0xA5, 0xD6, 0xA7);
    public static final Color FOREST = new Color(0x38, 0x8E, 0x3C);
    public static final Color DEEP   = new Color(0x1B, 0x5E, 0x20);
    public static final Color WHITE  = Color.WHITE;

    /* ---- Assets ---- */

    public static final String ASSETS_DIR = "assets/";

    public static final String UNI_LOGO =
            ASSETS_DIR + "university-logo.png";

    public static final String FACULTY_LOGO =
            ASSETS_DIR + "faculty-logo.png";


    /* ---- Fonts ---- */
    private static final String FONT = "Segoe UI";
    public static final Font F_TITLE  = new Font(FONT, Font.BOLD, 50);
    public static final Font F_TAG    = new Font(FONT, Font.PLAIN, 22);
    public static final Font F_LABEL  = new Font(FONT, Font.BOLD, 18);
    public static final Font F_FIELD  = new Font(FONT, Font.PLAIN, 19);
    public static final Font F_LINK   = new Font(FONT, Font.PLAIN, 16);
    public static final Font F_UNI    = new Font(FONT, Font.BOLD, 32);
    public static final Font F_FAC    = new Font(FONT, Font.PLAIN, 28);
    public static final Font F_BUTTON = new Font(FONT, Font.BOLD, 20);
    public static final Font F_TERMS  = new Font(FONT, Font.PLAIN, 14);

    /* ---- Alignments / cursors ---- */
    public static final float LEFT   = Component.LEFT_ALIGNMENT;
    public static final float CENTER = Component.CENTER_ALIGNMENT;
    public static final Cursor HAND  = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    /* ---- Container helpers ---- */
    public static void addAll(Container parent, Component... children) {
        for (Component c : children) parent.add(c);
    }
    public static Component gap(int height) { return Box.createVerticalStrut(height); }

    /* ---- Labels ---- */
    public static JLabel label(String text, Font font, Color color, float alignX) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(alignX);
        return l;
    }
    public static JLabel formLabel(String text) { return label(text, F_LABEL, DEEP, LEFT); }

    public static JLabel linkLabel(String text, Runnable onClick) {
        JLabel l = label("<html><u>" + text + "</u></html>", F_LINK, FOREST, LEFT);
        l.setCursor(HAND);
        l.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onClick.run(); }
        });
        return l;
    }

    /* ---- Buttons ---- */
    public static JButton button(String text, Font font) {
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

    /* ---- Text fields ---- */
    public static <T extends JTextField> T loginField(T field) {
        return formField(field, 320, 50);
    }
    public static <T extends JTextField> T formField(T field, int width, int height) {
        styleField(field, 8, 10, width, height);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        field.setAlignmentX(LEFT);
        return field;
    }
    public static String readPassword(JPasswordField field) {
        return new String(field.getPassword()).trim();
    }
    private static void styleField(JTextField f, int padV, int padH, int width, int height) {
        f.setFont(F_FIELD);
        f.setForeground(DEEP);
        f.setBackground(WHITE);
        f.setCaretColor(FOREST);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FRESH, 1),
                new EmptyBorder(padV, padH, padV, padH)));
        f.setPreferredSize(new Dimension(width, height));
    }

    /* ---- Logos ---- */
    public static JLabel logoLabel(String path, String fallback, int w, int h) {
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
    private static ImageIcon loadIcon(String path, int maxW, int maxH) {
        ImageIcon original = new ImageIcon(path);
        int w = original.getIconWidth(), h = original.getIconHeight();
        if (w <= 0 || h <= 0) return null;
        double scale = Math.min((double) maxW / w, (double) maxH / h);
        return new ImageIcon(original.getImage().getScaledInstance(
                (int) (w * scale), (int) (h * scale), Image.SCALE_SMOOTH));
    }

    /* ---- Dialogs ---- */
    public static void show(Component parent, String message, String title, int type) {
        JOptionPane.showMessageDialog(parent, message, title, type);
    }
}