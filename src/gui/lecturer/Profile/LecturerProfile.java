package gui.lecturer.Profile;

import dao.Lecturer.LecturerProfileDAO;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.SQLException;

public class LecturerProfile extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color PALE_GREEN = new Color(232, 245, 233);
    private static final Color BORDER_GREEN = new Color(200, 230, 201);
    private static final Color TEXT_GRAY = new Color(96, 112, 100);

    private final long userId;
    private final LecturerProfileDAO profileDAO = new LecturerProfileDAO();

    private final JLabel usernameLabel = new JLabel();
    private final JLabel codeLabel = new JLabel();
    private final JLabel deptLabel = new JLabel();

    private final JComboBox<String> titleCombo = new JComboBox<>(new String[]{"Dr.", "Ms.", "Mr.", "Prof."});
    private final JTextField fullNameField = new JTextField(20);
    private final JTextField nicField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField contactField = new JTextField(20);
    private final JTextField addressField = new JTextField(20);
    private final JTextField specializationField = new JTextField(20);

    private final JLabel statusLabel = new JLabel("Ready");

    public LecturerProfile(long userId) {
        this.userId = userId;

        setLayout(new BorderLayout(0, 20));
        setBackground(PALE_GREEN);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Heading
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);

        JLabel title = new JLabel("Lecturer Profile & Settings");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(DEEP_GREEN);

        JLabel subtitle = new JLabel("View and update your personal academic profile details (Username & credentials managed by Admin).");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);

        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        // Profile Form Card
        JPanel card = new JPanel(new BorderLayout(0, 20));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_GREEN, 1, true),
                new EmptyBorder(25, 30, 25, 30)
        ));

        // Read-only info header
        JPanel readOnlyPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        readOnlyPanel.setOpaque(false);
        readOnlyPanel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_GREEN, 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        usernameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        usernameLabel.setForeground(DEEP_GREEN);

        codeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        codeLabel.setForeground(DEEP_GREEN);

        deptLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        deptLabel.setForeground(DEEP_GREEN);

        readOnlyPanel.add(usernameLabel);
        readOnlyPanel.add(codeLabel);
        readOnlyPanel.add(deptLabel);

        // Form Fields
        JPanel formGrid = new JPanel(new GridLayout(7, 2, 15, 14));
        formGrid.setOpaque(false);

        formGrid.add(createFormLabel("Title:"));
        formGrid.add(titleCombo);

        formGrid.add(createFormLabel("Full Name:"));
        formGrid.add(fullNameField);

        formGrid.add(createFormLabel("NIC Number:"));
        formGrid.add(nicField);

        formGrid.add(createFormLabel("Email Address:"));
        formGrid.add(emailField);

        formGrid.add(createFormLabel("Contact Number:"));
        formGrid.add(contactField);

        formGrid.add(createFormLabel("Home / Office Address:"));
        formGrid.add(addressField);

        formGrid.add(createFormLabel("Specialization / Research:"));
        formGrid.add(specializationField);

        // Buttons
        JButton saveBtn = new JButton("Save Profile Changes");
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveBtn.setBackground(DEEP_GREEN);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> saveProfile());

        JButton resetBtn = new JButton("Reset");
        resetBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        resetBtn.addActionListener(e -> loadProfile());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(resetBtn);
        btnPanel.add(saveBtn);

        card.add(readOnlyPanel, BorderLayout.NORTH);
        card.add(formGrid, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.SOUTH);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(statusLabel, BorderLayout.WEST);

        add(heading, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        loadProfile();
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(DEEP_GREEN);
        return label;
    }

    private void loadProfile() {
        statusLabel.setText("Loading profile details...");
        try {
            Object[] data = profileDAO.getLecturerProfile(userId);
            if (data != null) {
                usernameLabel.setText("Username: " + data[1]);
                codeLabel.setText("Lecturer Code: " + data[2]);
                deptLabel.setText("Department: " + data[10]);

                titleCombo.setSelectedItem(data[3]);
                fullNameField.setText((String) data[4]);
                nicField.setText((String) data[5]);
                emailField.setText((String) data[6]);
                contactField.setText((String) data[7]);
                addressField.setText((String) data[8]);
                specializationField.setText((String) data[9]);

                statusLabel.setText("Profile loaded successfully.");
            } else {
                statusLabel.setText("Profile details not found.");
            }
        } catch (SQLException e) {
            statusLabel.setText("Error loading profile.");
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveProfile() {
        String titleStr = (String) titleCombo.getSelectedItem();
        String fullNameStr = fullNameField.getText().trim();
        String nicStr = nicField.getText().trim();
        String emailStr = emailField.getText().trim();
        String contactStr = contactField.getText().trim();
        String addressStr = addressField.getText().trim();
        String specStr = specializationField.getText().trim();

        if (fullNameStr.isEmpty() || emailStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full Name and Email Address are required fields.", "Input Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = profileDAO.updateLecturerProfile(
                    userId, titleStr, fullNameStr, nicStr, emailStr, contactStr, addressStr, specStr
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Profile details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("Profile updated successfully.");
                loadProfile();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
