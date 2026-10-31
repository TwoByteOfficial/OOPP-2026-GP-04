package gui.admin.NoticeComponents;

import javax.swing.*;
import java.awt.*;

public class AddNoticeButton extends JButton {
    public AddNoticeButton(Runnable onClick) {
        super("+ Post New Notice");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setBackground(new Color(0x38, 0x8E, 0x3C));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addActionListener(e -> onClick.run());
    }
}
