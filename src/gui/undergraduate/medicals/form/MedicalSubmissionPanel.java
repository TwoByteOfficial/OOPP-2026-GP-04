package gui.undergraduate.medicals.form;

import gui.admin.AdminTheme;
import gui.undergraduate.medicals.model.MedicalRecordData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Student medical certificate submission form using the modular file picker and UniMis courses.
 */
public class MedicalSubmissionPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JComboBox<String> courseComboBox;
    private final JTextField dateField;
    private final JTextField reasonField;
    private final MedicalFilePicker filePicker;

    public MedicalSubmissionPanel(Consumer<MedicalRecordData> onMedicalSubmitted) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel formTitle = new JLabel("Submit Medical Certificate");
        formTitle.setFont(AdminTheme.font(Font.BOLD, 16));
        formTitle.setForeground(AdminTheme.FOREST_GREEN);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel helperText = new JLabel("Upload genuine medical slip certified by university medical officer.");
        helperText.setFont(AdminTheme.font(Font.PLAIN, 12));
        helperText.setForeground(AdminTheme.MUTED);
        helperText.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(formTitle);
        add(Box.createVerticalStrut(2));
        add(helperText);
        add(Box.createVerticalStrut(14));

        // Course Selector using Database Course Codes
        String[] courses = {
                "ICT2112 - Data Structures and Algorithms",
                "ICT2122 - Computer Networks",
                "ICT2132 - Object Oriented Programming Practicum",
                "ICT2142 - Database Management Systems",
                "ICT2152 - Web Application Development",
                "ICT2162 - Software Engineering",
                "ICT2172 - Probability and Statistics",
                "ICT2182 - Technical Communication Skills"
        };
        courseComboBox = new JComboBox<>(courses);
        courseComboBox.setFont(AdminTheme.font(Font.PLAIN, 13));
        courseComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        add(createField("Target Course Module:", courseComboBox));
        add(Box.createVerticalStrut(10));

        dateField = new JTextField("2026-10-14");
        dateField.setFont(AdminTheme.font(Font.PLAIN, 13));
        dateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        add(createField("Absence Date (YYYY-MM-DD):", dateField));
        add(Box.createVerticalStrut(10));

        reasonField = new JTextField();
        reasonField.setFont(AdminTheme.font(Font.PLAIN, 13));
        reasonField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        add(createField("Diagnosis / Absence Reason:", reasonField));
        add(Box.createVerticalStrut(10));

        filePicker = new MedicalFilePicker();
        add(createField("Attach Medical Certificate (PDF / Image):", filePicker));
        add(Box.createVerticalStrut(16));

        JButton submitBtn = new JButton("Submit Medical Application");
        submitBtn.setFont(AdminTheme.font(Font.BOLD, 13));
        submitBtn.setBackground(AdminTheme.FOREST_GREEN);
        submitBtn.setForeground(AdminTheme.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        submitBtn.addActionListener(e -> {
            String selected = (String) courseComboBox.getSelectedItem();
            String code = selected != null ? selected.split(" - ")[0] : "ICT2132";
            String name = selected != null ? selected.split(" - ")[1] : "Course";
            String date = dateField.getText().trim();
            String reason = reasonField.getText().trim();
            String path = filePicker.getFilePath();

            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the reason for absence.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            MedicalRecordData newRecord = new MedicalRecordData(
                    System.currentTimeMillis() % 10000,
                    code, name, date, reason, path.isEmpty() ? "scans/sample.pdf" : path,
                    "Pending", "Awaiting Technical Officer verification"
            );

            if (onMedicalSubmitted != null) {
                onMedicalSubmitted.accept(newRecord);
            }

            reasonField.setText("");
            filePicker.clear();
            JOptionPane.showMessageDialog(this, "Medical application submitted successfully!\nPending verification.", "Submission Received", JOptionPane.INFORMATION_MESSAGE);
        });

        add(submitBtn);
    }

    private JPanel createField(String labelText, JComponent input) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(AdminTheme.font(Font.BOLD, 12));
        lbl.setForeground(new Color(0x44, 0x44, 0x44));

        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        p.add(input);
        return p;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(AdminTheme.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.setColor(CARD_BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.dispose();
        super.paintComponent(g);
    }
}