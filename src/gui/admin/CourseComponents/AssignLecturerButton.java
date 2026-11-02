package gui.admin.CourseComponents;

import javax.swing.*;
import java.awt.*;

public class AssignLecturerButton extends JButton {
    public AssignLecturerButton(Runnable onClick) {
        super("Assign Lecturer");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setBackground(new Color(0x81, 0xC7, 0x84));
        setForeground(new Color(0x1B, 0x5E, 0x20));
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addActionListener(e -> onClick.run());
    }
}
