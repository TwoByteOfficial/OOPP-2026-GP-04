package gui.admin.EnrollmentComponents;

import javax.swing.*;
import java.awt.*;

/**
 * ApproveEnrollmentButton - Dedicated button component for approving enrollment requests.
 */
public class ApproveEnrollmentButton extends JButton {

    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color WHITE        = Color.WHITE;

    public ApproveEnrollmentButton(Runnable onClick) {
        super("✓ Approve Request");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setBackground(FOREST_GREEN);
        setForeground(WHITE);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        addActionListener(e -> {
            if (onClick != null) onClick.run();
        });
    }
}
