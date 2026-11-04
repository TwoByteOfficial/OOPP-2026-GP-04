package gui.admin.EnrollmentComponents;

import javax.swing.*;
import java.awt.*;

/**
 * RefreshEnrollmentButton - Dedicated button component for refreshing table data.
 */
public class RefreshEnrollmentButton extends JButton {

    private final Color MEDIUM_GREEN = new Color(0x81, 0xC7, 0x84);
    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);

    public RefreshEnrollmentButton(Runnable onClick) {
        super("🔄 Refresh");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setBackground(MEDIUM_GREEN);
        setForeground(DEEP_GREEN);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        addActionListener(e -> {
            if (onClick != null) onClick.run();
        });
    }
}
