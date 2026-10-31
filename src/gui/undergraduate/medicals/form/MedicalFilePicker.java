package gui.undergraduate.medicals.form;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Reusable file selection field supporting native JFileChooser dialogs.
 */
public class MedicalFilePicker extends JPanel {

    private final JTextField filePathField;

    public MedicalFilePicker() {
        setOpaque(false);
        setLayout(new BorderLayout(8, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        filePathField = new JTextField();
        filePathField.setFont(AdminTheme.font(Font.PLAIN, 12));
        filePathField.setEditable(false);
        filePathField.setBackground(new Color(0xF9, 0xFB, 0xFA));

        JButton browseBtn = new JButton("Browse File");
        browseBtn.setFont(AdminTheme.font(Font.BOLD, 12));
        browseBtn.setBackground(AdminTheme.MINT);
        browseBtn.setForeground(AdminTheme.DEEP_GREEN);
        browseBtn.setFocusPainted(false);
        browseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int res = chooser.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                filePathField.setText(file.getAbsolutePath());
            }
        });

        add(filePathField, BorderLayout.CENTER);
        add(browseBtn, BorderLayout.EAST);
    }

    public String getFilePath() {
        return filePathField.getText().trim();
    }

    public void clear() {
        filePathField.setText("");
    }
}