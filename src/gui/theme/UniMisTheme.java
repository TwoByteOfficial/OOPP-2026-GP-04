
package gui.theme;

import java.awt.Color;
import java.awt.Font;

public final class UniMisTheme {

    private UniMisTheme() {}

    // Green colour palette from AdminTheme
    public static final Color MINT =
            new Color(0xE8, 0xF5, 0xE9);
    public static final Color PASTEL_GREEN =
            new Color(0xC8, 0xE6, 0xC9);
    public static final Color FRESH_GREEN =
            new Color(0xA5, 0xD6, 0xA7);
    public static final Color MEDIUM_GREEN =
            new Color(0x81, 0xC7, 0x84);
    public static final Color FOREST_GREEN =
            new Color(0x38, 0x8E, 0x3C);
    public static final Color DEEP_GREEN =
            new Color(0x1B, 0x5E, 0x20);

    public static final Color PRIMARY = FOREST_GREEN;
    public static final Color PRIMARY_DARK = DEEP_GREEN;
    public static final Color SECONDARY = FRESH_GREEN;
    public static final Color SUCCESS = FOREST_GREEN;
    public static final Color DANGER =
            new Color(0xDC, 0x35, 0x45);
    public static final Color WARNING =
            new Color(0xF0, 0xAD, 0x4E);
    public static final Color INFO = FOREST_GREEN;

    public static final Color LIGHT_BG = MINT;
    public static final Color WHITE = Color.WHITE;
    public static final Color TEXT = DEEP_GREEN;
    public static final Color MUTED =
            new Color(0x4F, 0x6B, 0x52);
    public static final Color BORDER = PASTEL_GREEN;

    public static final int SIDEBAR_WIDTH = 260;
    public static final String FONT_FAMILY = "Segoe UI";

    public static Font font(int style, int size) {
        return new Font(FONT_FAMILY, style, size);
    }

    public static final Font FONT_H1 = font(Font.BOLD, 28);
    public static final Font FONT_H2 = font(Font.BOLD, 24);
    public static final Font FONT_H3 = font(Font.BOLD, 20);
    public static final Font FONT_BODY = font(Font.PLAIN, 17);
    public static final Font FONT_BODY_BOLD = font(Font.BOLD, 17);
    public static final Font FONT_SMALL = font(Font.PLAIN, 16);
    public static final Font FONT_TINY = font(Font.PLAIN, 15);
    public static final Font FONT_MENU = font(Font.PLAIN, 18);
    public static final Font FONT_LOGO = font(Font.BOLD, 26);
}
