package gui.admin.UserProfileComponents;

import javax.swing.*;
import java.awt.*;

/**
 * DeleteUserButton - Dedicated action button component for deleting user profiles.
 */
public class DeleteUserButton extends JButton {

    private final Color DANGER = new Color(0xDC, 0x35, 0x45);
    private final Color WHITE  = Color.WHITE;

    public DeleteUserButton(Runnable onClick) {
        super("Delete User");
        setFont(new Font("Segoe UI", Font.BOLD, 16));
        setBackground(DANGER);
        setForeground(WHITE);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        addActionListener(e -> {
            if (onClick != null) onClick.run();
        });
    }
}
