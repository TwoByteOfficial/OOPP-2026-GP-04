package gui.admin;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Central theme constants and helpers for the Administrator UI.
 * Mirrors green palette with Poppins/Segoe UI typography.
 */
public final class AdminTheme {

    private AdminTheme() {}

    public static final Color MINT          = new Color(0xE8, 0xF5, 0xE9);
    public static final Color PASTEL_GREEN  = new Color(0xC8, 0xE6, 0xC9);
    public static final Color FRESH_GREEN   = new Color(0xA5, 0xD6, 0xA7);
    public static final Color MEDIUM_GREEN  = new Color(0x81, 0xC7, 0x84);
    public static final Color FOREST_GREEN  = new Color(0x38, 0x8E, 0x3C);
    public static final Color DEEP_GREEN    = new Color(0x1B, 0x5E, 0x20);

    public static final Color PRIMARY       = FOREST_GREEN;
    public static final Color PRIMARY_DARK  = DEEP_GREEN;
    public static final Color SECONDARY     = FRESH_GREEN;
    public static final Color SUCCESS       = FOREST_GREEN;
    public static final Color DANGER        = new Color(0xDC, 0x35, 0x45);
    public static final Color WARNING       = new Color(0xF0, 0xAD, 0x4E);
    public static final Color INFO          = FOREST_GREEN;

    public static final Color LIGHT_BG      = MINT;
    public static final Color WHITE         = Color.WHITE;
    public static final Color TEXT          = DEEP_GREEN;
    public static final Color DARK_TEXT     = new Color(0x21, 0x25, 0x29);
    public static final Color MUTED         = new Color(0x4F, 0x6B, 0x52);
    public static final Color BORDER        = PASTEL_GREEN;

    /* ---------- Dimensions ---------- */
    public static final int SIDEBAR_WIDTH   = 260;

    /* ---------- Fonts (H1 kept at 28px; other fonts incremented by 2px) ---------- */
    public static final String FONT_FAMILY  = "Segoe UI";

    public static Font font(int style, int size) {
        return new Font(FONT_FAMILY, style, size);
    }

    public static final Font FONT_H1        = font(Font.BOLD,  28); // Kept H1
    public static final Font FONT_H2        = font(Font.BOLD,  26); // 24 -> 26
    public static final Font FONT_H3        = font(Font.BOLD,  22); // 20 -> 22
    public static final Font FONT_BODY      = font(Font.PLAIN, 19); // 17 -> 19
    public static final Font FONT_BODY_BOLD = font(Font.BOLD,  19); // 17 -> 19
    public static final Font FONT_SMALL     = font(Font.PLAIN, 18); // 16 -> 18
    public static final Font FONT_TINY      = font(Font.PLAIN, 17); // 15 -> 17
    public static final Font FONT_MENU      = font(Font.PLAIN, 20); // 18 -> 20
    public static final Font FONT_LOGO      = font(Font.BOLD,  28); // 26 -> 28
    public static final Font FONT_STAT      = font(Font.BOLD,  28); // 26 -> 28

    /* ---------- Borders / helpers ---------- */
    public static Border emptyBorder(int t, int l, int b, int r) {
        return new EmptyBorder(t, l, b, r);
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN),
                new EmptyBorder(20, 22, 20, 22));
    }

    public static JPanel card() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WHITE);
        p.setOpaque(true);
        p.setBorder(cardBorder());
        return p;
    }

    public static JPanel card(LayoutManager lm) {
        JPanel p = new JPanel(lm);
        p.setBackground(WHITE);
        p.setOpaque(true);
        p.setBorder(cardBorder());
        return p;
    }

    /* ---------- Buttons ---------- */
    public static JButton primaryButton(String text) {
        JButton b = baseButton(text);
        b.setBackground(PRIMARY);
        b.setForeground(WHITE);
        return b;
    }

    public static JButton secondaryButton(String text) {
        JButton b = baseButton(text);
        b.setBackground(PASTEL_GREEN);
        b.setForeground(TEXT);
        return b;
    }

    public static JButton dangerButton(String text) {
        JButton b = baseButton(text);
        b.setBackground(DANGER);
        b.setForeground(WHITE);
        return b;
    }

    private static JButton baseButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BODY);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(10, 16, 10, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /* ---------- Labels ---------- */
    public static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_H2);
        l.setForeground(PRIMARY);
        return l;
    }

    public static JLabel subheading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_H3);
        l.setForeground(PRIMARY);
        return l;
    }

    public static JLabel body(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(DARK_TEXT);
        return l;
    }

    public static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SMALL);
        l.setForeground(MUTED);
        return l;
    }
}
