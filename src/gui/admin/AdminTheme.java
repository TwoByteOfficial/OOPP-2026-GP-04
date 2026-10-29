package gui.admin;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class AdminTheme {

    private AdminTheme() {}

    // Color Palette
    public static final Color MINT          = new Color(232, 245, 233);
    public static final Color PASTEL_GREEN  = new Color(200, 230, 201);
    public static final Color FRESH_GREEN   = new Color(165, 214, 167);
    public static final Color MEDIUM_GREEN  = new Color(129, 199, 132);
    public static final Color FOREST_GREEN  = new Color(56, 142, 60);
    public static final Color DEEP_GREEN    = new Color(27, 94, 32);

    // Contextual / Functional Colors
    public static final Color PRIMARY       = FOREST_GREEN;
    public static final Color PRIMARY_DARK  = DEEP_GREEN;
    public static final Color SECONDARY     = FRESH_GREEN;
    public static final Color SUCCESS       = FOREST_GREEN;
    public static final Color DANGER        = new Color(220, 53, 69);
    public static final Color WARNING       = new Color(240, 173, 78);
    public static final Color INFO          = FOREST_GREEN;

    public static final Color LIGHT_BG      = MINT;
    public static final Color WHITE         = Color.WHITE;
    public static final Color BLACK         = Color.BLACK;
    public static final Color TEXT          = DEEP_GREEN;
    public static final Color MUTED         = new Color(79, 107, 82);
    public static final Color BORDER        = PASTEL_GREEN;

    // Translucent Card Border / Shadow Overlays
    public static final Color CARD_SHADOW    = new Color(27, 94, 32, 35);
    public static final Color CARD_SHADOW_30 = new Color(27, 94, 32, 30);
    public static final Color CARD_SHADOW_35 = new Color(27, 94, 32, 35);

    // Layout Constants
    public static final int SIDEBAR_WIDTH   = 260;

    // Typography
    public static final String FONT_FAMILY  = "Segoe UI";

    public static Font font(int style, int size) {
        return new Font(FONT_FAMILY, style, size);
    }

    public static final Font FONT_H1        = font(Font.BOLD,  28);
    public static final Font FONT_H2        = font(Font.BOLD,  26);
    public static final Font FONT_H3        = font(Font.BOLD,  22);
    public static final Font FONT_BODY      = font(Font.PLAIN, 18);
    public static final Font FONT_BODY_BOLD = font(Font.BOLD,  18);
    public static final Font FONT_SMALL     = font(Font.PLAIN, 16);
    public static final Font FONT_TINY      = font(Font.PLAIN, 15);
    public static final Font FONT_MENU      = font(Font.BOLD,  20);
    public static final Font FONT_LOGO      = font(Font.BOLD,  28);
    public static final Font FONT_STAT      = font(Font.BOLD,  26);

    public static Border emptyBorder(int t, int l, int b, int r) {
        return new EmptyBorder(t, l, b, r);
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225)),
                new EmptyBorder(20, 22, 20, 22));
    }

    public static JPanel card() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WHITE);
        p.setBorder(cardBorder());
        return p;
    }

    public static JPanel card(LayoutManager lm) {
        JPanel p = new JPanel(lm);
        p.setBackground(WHITE);
        p.setBorder(cardBorder());
        return p;
    }

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
        b.setFont(FONT_BODY_BOLD);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 16, 10, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

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
        l.setForeground(TEXT);
        return l;
    }

    public static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SMALL);
        l.setForeground(MUTED);
        return l;
    }
}