package gui.admin.CourseComponents;

import javax.swing.*;
import java.awt.*;

public class DeleteCourseButton extends JButton {
    public DeleteCourseButton(Runnable onClick) {
        super("Delete Course");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setBackground(new Color(0xDC, 0x35, 0x45));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addActionListener(e -> onClick.run());
    }
}
