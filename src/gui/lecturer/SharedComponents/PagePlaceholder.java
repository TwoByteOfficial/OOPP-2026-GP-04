package gui.lecturer.SharedComponents;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class PagePlaceholder extends JPanel {

    public PagePlaceholder(String message) {

        Color mintBackground = new Color(232, 245, 233);
        Color pastelGreen = new Color(200, 230, 201);
        Color deepGreen = new Color(27, 94, 32);

        setLayout(new BorderLayout());
        setBackground(mintBackground);

        setBorder(
                new EmptyBorder(30, 30, 30, 30)
        );

        JPanel card = new JPanel(new GridBagLayout());

        card.setBackground(Color.WHITE);

        card.setBorder(
                new LineBorder(pastelGreen, 1)
        );

        JLabel messageLabel = new JLabel(message);

        messageLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 17)
        );

        messageLabel.setForeground(deepGreen);

        card.add(messageLabel);

        add(card, BorderLayout.CENTER);
    }
}
