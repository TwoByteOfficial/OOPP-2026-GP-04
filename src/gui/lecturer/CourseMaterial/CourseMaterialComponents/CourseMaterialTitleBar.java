package gui.lecturer.CourseMaterial.CourseMaterialComponents;

import javax.swing.*;
import java.awt.*;
import static gui.lecturer.SharedComponents.Colors.MATERIALS_DEEP_GREEN;
import static gui.lecturer.SharedComponents.Colors.MATERIALS_TEXT_GRAY;


public class CourseMaterialTitleBar extends JPanel {

    private JButton uploadButton;

    public CourseMaterialTitleBar() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        add(createHeading(), BorderLayout.WEST);
        add(createUploadButton(), BorderLayout.EAST);
    }

    private JPanel createHeading() {
        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(new BoxLayout(headingPanel, BoxLayout.Y_AXIS));
        headingPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Course Materials");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(MATERIALS_DEEP_GREEN);

        JLabel description = new JLabel(
                "Upload and manage learning materials for your courses."
        );
        description.setFont(new Font(
                "SansSerif", Font.PLAIN, 13
        ));
        description.setForeground(MATERIALS_TEXT_GRAY);

        headingPanel.add(title);
        headingPanel.add(Box.createVerticalStrut(7));
        headingPanel.add(description);

        return headingPanel;
    }

    private JButton createUploadButton() {

        uploadButton = new JButton("+ Upload Material");

        uploadButton.setFont(new Font(
                "SansSerif", Font.BOLD, 13
        ));
        uploadButton.setForeground(Color.WHITE);
        uploadButton.setBackground(MATERIALS_DEEP_GREEN);
        uploadButton.setFocusPainted(false);
        uploadButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
        uploadButton.setBorder(BorderFactory.createEmptyBorder(
                12, 18, 12, 18
        ));
        uploadButton.setOpaque(true);

        return uploadButton;
    }

    public JButton getUploadButton() {
        return uploadButton;
    }
}
