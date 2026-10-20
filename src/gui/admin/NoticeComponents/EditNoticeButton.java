package gui.admin.NoticeComponents;

import javax.swing.*;
import java.awt.*;

public class EditNoticeButton extends JButton {
    public EditNoticeButton(Runnable onClick) {
        super("Edit Notice");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setBackground(new Color(0xC8, 0xE6, 0xC9));
        setForeground(new Color(0x1B, 0x5E, 0x20));
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addActionListener(e -> onClick.run());
    }
}
