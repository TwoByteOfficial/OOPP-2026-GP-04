package gui.admin.UserProfileComponents;

import javax.swing.*;
import java.awt.*;

/**
 * EditUserButton - Dedicated action button component for editing user profiles.
 */
public class EditUserButton extends JButton {

    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);

    public EditUserButton(Runnable onClick) {
        super("Edit User");
        setFont(new Font("Segoe UI", Font.BOLD, 16));
        setBackground(PASTEL_GREEN);
        setForeground(DEEP_GREEN);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        addActionListener(e -> {
            if (onClick != null) onClick.run();
        });
    }
}
