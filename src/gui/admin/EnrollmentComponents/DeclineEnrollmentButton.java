package gui.admin.EnrollmentComponents;

import javax.swing.*;
import java.awt.*;

/**
 * DeclineEnrollmentButton - Dedicated button component for declining enrollment requests.
 */
public class DeclineEnrollmentButton extends JButton {

    private final Color DANGER = new Color(0xDC, 0x35, 0x45);
    private final Color WHITE  = Color.WHITE;

    public DeclineEnrollmentButton(Runnable onClick) {
        super("✕ Decline Request");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
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
