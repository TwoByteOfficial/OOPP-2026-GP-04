package gui.admin.EnrollmentComponents;

import javax.swing.*;
import java.awt.*;

/**
 * ViewEnrollmentButton - Dedicated button component for viewing full enrollment request details.
 */
public class ViewEnrollmentButton extends JButton {

    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);

    public ViewEnrollmentButton(Runnable onClick) {
        super("🔍 View Details");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
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
