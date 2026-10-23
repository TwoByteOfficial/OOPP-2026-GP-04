
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

    private final JComboBox<String> titleCombo =
            new JComboBox<>(new String[]{"Dr.", "Ms.", "Mr.", "Prof."});

    private final JTextField fullNameField = new JTextField();
    private final JTextField nicField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField contactField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextField specializationField = new JTextField();

    private final JLabel statusLabel = new JLabel("Ready");

    public LecturerProfile(long userId) {
        this.userId = userId;

        setLayout(new BorderLayout(0, 18));
        setBackground(PALE_GREEN);
        setBorder(new EmptyBorder(24, 28, 20, 28));

        // Heading
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);

        JLabel pageTitle = new JLabel("Lecturer Profile & Settings");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 25));
        pageTitle.setForeground(DEEP_GREEN);

        JLabel subtitle = new JLabel(
                "View and update your personal academic profile details."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_GRAY);

        heading.add(pageTitle);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);

        // Main card
        JPanel card = new JPanel(new BorderLayout(0, 20));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_GREEN, 1, true),
                new EmptyBorder(22, 24, 20, 24)
        ));

        // Read-only information
        JPanel readOnlyPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        readOnlyPanel.setOpaque(false);
        readOnlyPanel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_GREEN, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        styleInfoLabel(usernameLabel);
        styleInfoLabel(codeLabel);
        styleInfoLabel(deptLabel);

        readOnlyPanel.add(usernameLabel);
        readOnlyPanel.add(codeLabel);
        readOnlyPanel.add(deptLabel);

        // Form: two fields per row
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1
        addFormField(formGrid, gbc, 0, 0, "Title", titleCombo);
        addFormField(formGrid, gbc, 1, 0, "Full Name", fullNameField);

        // Row 2
        addFormField(formGrid, gbc, 0, 1, "NIC Number", nicField);
        addFormField(formGrid, gbc, 1, 1, "Email Address", emailField);

        // Row 3
        addFormField(formGrid, gbc, 0, 2, "Contact Number", contactField);
        addFormField(formGrid, gbc, 1, 2, "Home / Office Address", addressField);

        // Row 4: specialization spans both columns
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;

        JPanel specializationPanel = createFieldPanel(
                "Specialization / Research",
                specializationField
        );

        formGrid.add(specializationPanel, gbc);

        // Buttons
        JButton resetBtn = new JButton("Reset");
        styleButton(resetBtn, false);
        resetBtn.addActionListener(e -> loadProfile());

        JButton saveBtn = new JButton("Save Profile Changes");
        styleButton(saveBtn, true);
        saveBtn.addActionListener(e -> saveProfile());

        JPanel buttonPanel = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 10, 0
        ));
        buttonPanel.setOpaque(false);
        buttonPanel.add(resetBtn);
        buttonPanel.add(saveBtn);

        // Card content
        JPanel formContent = new JPanel(new BorderLayout(0, 14));
        formContent.setOpaque(false);
        formContent.add(readOnlyPanel, BorderLayout.NORTH);
        formContent.add(formGrid, BorderLayout.CENTER);

        card.add(formContent, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        // Footer status
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_GRAY);
        footer.add(statusLabel, BorderLayout.WEST);

        add(heading, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        // Load existing database data
        loadProfile();
    }

    private void styleInfoLabel(JLabel label) {
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(DEEP_GREEN);
    }

    private void addFormField(
            JPanel parent,
            GridBagConstraints gbc,
            int column,
            int row,
            String labelText,
            JComponent field
    ) {
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;

        JPanel fieldPanel = createFieldPanel(labelText, field);
        parent.add(fieldPanel, gbc);
    }

    private JPanel createFieldPanel(
            String labelText,
            JComponent field
    ) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(DEEP_GREEN);

        // Compact input fields
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(180, 34));

        if (field instanceof JTextField) {
            JTextField textField = (JTextField) field;
            textField.setMargin(new Insets(5, 9, 5, 9));
            textField.setBorder(new CompoundBorder(
                    new LineBorder(BORDER_GREEN, 1, true),
                    new EmptyBorder(1, 2, 1, 2)
            ));
        } else if (field instanceof JComboBox<?>) {
            field.setBorder(new LineBorder(BORDER_GREEN, 1, true));
        }

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private void styleButton(JButton button, boolean primary) {
        button.setFont(new Font(
                "SansSerif",
                primary ? Font.BOLD : Font.PLAIN,
                13
        ));

        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(9, 16, 9, 16));
        button.setOpaque(true);

        if (primary) {
            button.setBackground(DEEP_GREEN);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(DEEP_GREEN);
            button.setBorder(new CompoundBorder(
                    new LineBorder(BORDER_GREEN, 1, true),
                    new EmptyBorder(8, 15, 8, 15)
            ));
        }
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

            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
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
            JOptionPane.showMessageDialog(
                    this,
                    "Full Name and Email Address are required fields.",
                    "Input Validation",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            boolean success = profileDAO.updateLecturerProfile(
                    userId,
                    titleStr,
                    fullNameStr,
                    nicStr,
                    emailStr,
                    contactStr,
                    addressStr,
                    specStr
            );

            if (success) {
                JOptionPane.showMessageDialog(
                        this,
                        "Profile details updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                statusLabel.setText("Profile updated successfully.");
                loadProfile();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to update profile.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
