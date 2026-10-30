package gui.admin.UserProfileComponents;

import javax.swing.*;
import java.awt.*;

/**
 * AddUserButton - Dedicated component for registering new users.
 */
public class AddUserButton extends JButton {

    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color WHITE        = Color.WHITE;

    public AddUserButton(Runnable onClick) {
        super("+ Register New User");
        setFont(new Font("Segoe UI", Font.BOLD, 16));
        setBackground(FOREST_GREEN);
        setForeground(WHITE);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        addActionListener(e -> {
            if (onClick != null) onClick.run();
        });
    }
}
