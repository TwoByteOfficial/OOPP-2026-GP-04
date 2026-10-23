package gui.admin.SettingsComponents;

import javax.swing.*;
import java.awt.*;

public class AppearanceSettings extends JPanel {

    public AppearanceSettings() {
        setLayout(new GridLayout(3, 2, 14, 12));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                        " Interface Theme & Palette ",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 16),
                        new Color(0x1B, 0x5E, 0x20)
                ),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        addLabel("Active Theme:");
        JLabel lblTheme = new JLabel("UniMis Faculty Mint Green (Default)");
        lblTheme.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTheme.setForeground(new Color(0x38, 0x8E, 0x3C));
        add(lblTheme);

        addLabel("Font Family:");
        JLabel lblFont = new JLabel("Segoe UI / System Default Sans");
        lblFont.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblFont.setForeground(Color.BLACK);
        add(lblFont);

        addLabel("Display Mode:");
        JLabel lblMode = new JLabel("High-Contrast Protection Enabled");
        lblMode.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblMode.setForeground(Color.BLACK);
        add(lblMode);
    }

    private void addLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(Color.BLACK);
        add(l);
    }
}
