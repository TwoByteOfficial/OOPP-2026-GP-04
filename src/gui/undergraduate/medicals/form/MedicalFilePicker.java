package gui.undergraduate.medicals.form;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Reusable file selection field supporting native JFileChooser dialogs.
 * Provides a read-only text field paired with an interactive browse button
 * to allow students to attach supporting medical documentation.
 */
public class MedicalFilePicker extends JPanel {

    // Text field displaying the absolute path of the selected medical document
    private final JTextField filePathField;

    /**
     * Initializes the file picker component with layout, text field, and browse action trigger.
     */
    public MedicalFilePicker() {
        setOpaque(false);
        setLayout(new BorderLayout(8, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        // -------------------------------------------------------------
        // Read-Only Path Display Field
        // -------------------------------------------------------------
        filePathField = new JTextField();
        filePathField.setFont(AdminTheme.font(Font.PLAIN, 12));
        filePathField.setEditable(false);
        filePathField.setBackground(new Color(0xF9, 0xFB, 0xFA));

        // -------------------------------------------------------------
        // Browse File Action Button
        // -------------------------------------------------------------
        JButton browseBtn = new JButton("Browse File");
        browseBtn.setFont(AdminTheme.font(Font.BOLD, 12));
        browseBtn.setBackground(AdminTheme.MINT);
        browseBtn.setForeground(AdminTheme.DEEP_GREEN);
        browseBtn.setFocusPainted(false);
        browseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Handle native file selection dialog on button click
        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int res = chooser.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                filePathField.setText(file.getAbsolutePath());
            }
        });

        // -------------------------------------------------------------
        // Component Assembly
        // -------------------------------------------------------------
        add(filePathField, BorderLayout.CENTER);
        add(browseBtn, BorderLayout.EAST);
    }

    /**
     * Retrieves the absolute file path selected by the user.
     *
     * @return Trimmed file path string, or an empty string if no file is chosen.
     */
    public String getFilePath() {
        return filePathField.getText().trim();
    }

    /**
     * Resets the file path field back to empty state.
     */
    public void clear() {
        filePathField.setText("");
    }
}