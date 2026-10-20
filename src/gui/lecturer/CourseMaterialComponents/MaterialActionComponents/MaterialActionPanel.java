
package gui.lecturer.CourseMaterialComponents.MaterialActionComponents;

import javax.swing.*;
import java.awt.*;

public class MaterialActionPanel extends JPanel {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private static final Color BORDER_GREEN =
            new Color(200, 230, 201);

    private final JButton editButton;
    private final JButton deleteButton;

    public MaterialActionPanel() {
        setLayout(new FlowLayout(
                FlowLayout.CENTER, 8, 5
        ));
        setBackground(Color.WHITE);

        editButton = createButton("Edit");
        deleteButton = createButton("Delete");

        add(editButton);
        add(deleteButton);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);

        button.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        button.setForeground(DEEP_GREEN);
        button.setBackground(PALE_GREEN);
        button.setFocusPainted(false);
        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_GREEN),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                )
        );
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        return button;
    }

    public JButton getEditButton() {
        return editButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }
}
