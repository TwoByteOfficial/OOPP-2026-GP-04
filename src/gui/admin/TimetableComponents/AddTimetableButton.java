package gui.admin.TimetableComponents;

import javax.swing.*;
import java.awt.*;

public class AddTimetableButton extends JButton {
    public AddTimetableButton(Runnable onClick) {
        super("+ Schedule Slot");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setBackground(new Color(0x38, 0x8E, 0x3C));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addActionListener(e -> onClick.run());
    }
}
