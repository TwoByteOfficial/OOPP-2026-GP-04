package gui.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/** Palette + Swing helpers matching the HTML/CSS theme. */
public final class AdminTheme {

    private AdminTheme() {}

    /* ---- Colours (from styles.css :root) ---- */
    public static final Color PRIMARY      = new Color(0x17, 0x3B, 0x6C);
    public static final Color PRIMARY_DARK = new Color(0x0D, 0x2A, 0x50);
    public static final Color SECONDARY    = new Color(0xF5, 0xA6, 0x23);
    public static final Color SUCCESS      = new Color(0x19, 0x87, 0x54);
    public static final Color DANGER       = new Color(0xDC, 0x35, 0x45);
    public static final Color WARNING      = new Color(0xF0, 0xAD, 0x4E);
    public static final Color INFO         = new Color(0x0D, 0x6E, 0xFD);
    public static final Color LIGHT        = new Color(0xF5, 0xF7, 0xFB);
    public static final Color WHITE        = Color.WHITE;
    public static final Color TEXT         = new Color(0x26, 0x32, 0x38);
    public static final Color MUTED        = new Color(0x6C, 0x75, 0x7D);
    public static final Color BORDER       = new Color(0xE4, 0xE8, 0xEF);

    /* ---- Fonts ---- */
    public static final String FONT = "Segoe UI";
    public static final Font F_H1        = new Font(FONT, Font.BOLD, 24);
    public static final Font F_H2        = new Font(FONT, Font.BOLD, 20);
    public static final Font F_H3        = new Font(FONT, Font.BOLD, 16);
    public static final Font F_BODY      = new Font(FONT, Font.PLAIN, 13);
    public static final Font F_SMALL     = new Font(FONT, Font.PLAIN, 11);
    public static final Font F_LABEL     = new Font(FONT, Font.BOLD, 12);
    public static final Font F_MENU      = new Font(FONT, Font.PLAIN, 14);
    public static final Font F_BTN       = new Font(FONT, Font.BOLD, 13);

    public static final Cursor HAND = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    /* ---- Helpers ---- */
    public static void addAll(Container p, Component... cs) {
        for (Component c : cs) p.add(c);
    }

    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    /** Flat styled button matching .btn-primary / .btn-danger / .btn-secondary */
    public static JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(F_BTN);
        b.setBackground(bg);
        b.setForeground(bg.equals(new Color(0xE9, 0xED, 0xF3)) ? TEXT : WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(HAND);
        b.setBorder(new EmptyBorder(9, 15, 9, 15));
        return b;
    }

    /** Rounded card container matching .panel */
    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(20, 22, 20, 22)));
        return p;
    }

    /** White table with PRIMARY header, matching .data-table */
    public static JScrollPane table(Object[][] rows, String[] headers) {
        JTable t = new JTable(rows, headers) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        styleTable(t);
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        sp.getViewport().setBackground(WHITE);
        return sp;
    }

    public static void styleTable(JTable t) {
        t.setFont(F_BODY);
        t.setForeground(TEXT);
        t.setBackground(WHITE);
        t.setRowHeight(36);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(0xDB, 0xEA, 0xFE));
        t.setSelectionForeground(TEXT);

        JTableHeader h = t.getTableHeader();
        h.setFont(F_LABEL);
        h.setForeground(PRIMARY);
        h.setBackground(new Color(0xF4, 0xF7, 0xFB));
        h.setPreferredSize(new Dimension(h.getPreferredSize().width, 38));

        DefaultTableCellRenderer cell = new DefaultTableCellRenderer();
        cell.setBorder(new EmptyBorder(0, 12, 0, 12));
        t.setDefaultRenderer(Object.class, cell);
    }

    public static JTextField field(int cols) {
        JTextField f = new JTextField(cols);
        styleInput(f);
        return f;
    }

    public static JPasswordField passwordField(int cols) {
        JPasswordField f = new JPasswordField(cols);
        styleInput(f);
        return f;
    }

    public static void styleInput(JTextField f) {
        f.setFont(F_BODY);
        f.setForeground(TEXT);
        f.setBackground(WHITE);
        f.setCaretColor(PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 10, 8, 10)));
    }

    public static JComboBox<String> combo(String... items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(F_BODY);
        c.setBackground(WHITE);
        c.setForeground(TEXT);
        c.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return c;
    }

    public static void showInfo(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}