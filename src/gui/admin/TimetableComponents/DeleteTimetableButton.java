package gui.admin.TimetableComponents;

import javax.swing.*;
import java.awt.*;

public class DeleteTimetableButton extends JButton {
    public DeleteTimetableButton(Runnable onClick) {
        super("Delete Entry");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setBackground(new Color(0xDC, 0x35, 0x45));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addActionListener(e -> onClick.run());
    }
}
