
package gui.lecturer.CourseMaterial.CourseMaterialComponents.UploadMaterialComponents;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MaterialFilePicker extends JPanel {

    private final JTextField filePathField;
    private final JButton browseButton;

    public MaterialFilePicker() {

        setLayout(new BorderLayout(8, 0));
        setBackground(Color.WHITE);

        filePathField = new JTextField();
        filePathField.setEditable(false);
        filePathField.setToolTipText("Selected file path");

        browseButton = new JButton("Browse...");

        browseButton.addActionListener(e -> chooseFile());

        add(filePathField, BorderLayout.CENTER);
        add(browseButton, BorderLayout.EAST);
    }

    private void chooseFile() {

        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle(
                "Select Learning Material"
        );

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {

            File selectedFile = fileChooser.getSelectedFile();

            filePathField.setText(
                    selectedFile.getAbsolutePath()
            );
        }
    }

    public String getSelectedFilePath() {

        String path = filePathField.getText().trim();

        return path.isEmpty() ? null : path;
    }

    // Set an existing file path when editing a material.
    public void setSelectedFilePath(String filePath) {

        filePathField.setText(
                filePath == null ? "" : filePath
        );
    }

    public void clearSelection() {
        filePathField.setText("");
    }
}
