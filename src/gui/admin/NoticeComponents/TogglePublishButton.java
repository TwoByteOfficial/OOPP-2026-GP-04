package gui.admin.NoticeComponents;

import javax.swing.*;
import java.awt.*;

public class TogglePublishButton extends JButton {
    public TogglePublishButton(Runnable onClick) {
        super("Toggle Status");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setBackground(new Color(0xF0, 0xAD, 0x4E));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addActionListener(e -> onClick.run());
    }
}
