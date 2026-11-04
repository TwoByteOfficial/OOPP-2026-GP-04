package gui.admin.EnrollmentComponents;

import javax.swing.*;
import java.awt.*;

/**
 * NewEnrollmentButton - Dedicated button component for creating a new enrollment request.
 */
public class NewEnrollmentButton extends JButton {

    private final Color FRESH_GREEN = new Color(0xA5, 0xD6, 0xA7);
    private final Color DEEP_GREEN  = new Color(0x1B, 0x5E, 0x20);

    public NewEnrollmentButton(Runnable onClick) {
        super("+ New Enrollment");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setBackground(FRESH_GREEN);
        setForeground(DEEP_GREEN);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        addActionListener(e -> {
            if (onClick != null) onClick.run();
        });
    }
}
