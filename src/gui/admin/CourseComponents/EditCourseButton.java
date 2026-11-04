package gui.admin.CourseComponents;

import javax.swing.*;
import java.awt.*;

public class EditCourseButton extends JButton {
    public EditCourseButton(Runnable onClick) {
        super("Edit Course");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setBackground(new Color(0xC8, 0xE6, 0xC9));
        setForeground(new Color(0x1B, 0x5E, 0x20));
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addActionListener(e -> onClick.run());
    }
}
