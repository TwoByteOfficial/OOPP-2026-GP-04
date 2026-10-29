
package gui.lecturer.Profile;

import dao.Lecturer.LecturerProfileDAO;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.SQLException;

import static gui.lecturer.SharedComponents.Colors.PROFILE_BACKGROUND;
import static gui.lecturer.SharedComponents.Colors.PROFILE_BORDER;
import static gui.lecturer.SharedComponents.Colors.PROFILE_DEEP_GREEN;
import static gui.lecturer.SharedComponents.Colors.PROFILE_TEXT_GRAY;
import static gui.lecturer.SharedComponents.Colors.PROFILE_WHITE;

public class LecturerProfile extends JPanel {

    private final long userId;
    private final LecturerProfileDAO profileDAO =
            new LecturerProfileDAO();

    // Called after the profile is successfully updated.
    private final Runnable onProfileUpdated;

    private final JLabel usernameLabel = new JLabel();
    private final JLabel codeLabel = new JLabel();
    private final JLabel deptLabel = new JLabel();

    private final JComboBox<String> titleCombo =
            new JComboBox<>(
                    new String[]{"Dr.", "Ms.", "Mr.", "Prof."}
            );

    private final JTextField fullNameField = new JTextField();
    private final JTextField nicField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField contactField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextField specializationField = new JTextField();

    private final JLabel statusLabel = new JLabel("Ready");

    // Keep the original constructor for existing code.
    public LecturerProfile(long userId) {
        this(userId, null);
    }

    // Constructor that accepts a dashboard refresh callback.
    public LecturerProfile(
            long userId,
            Runnable onProfileUpdated
    ) {
        this.userId = userId;
        this.onProfileUpdated = onProfileUpdated;

        setLayout(new BorderLayout(0, 18));
        setBackground(PROFILE_BACKGROUND);
        setBorder(new EmptyBorder(24, 28, 20, 28));

        // Heading
        JPanel heading = new JPanel();
        heading.setLayout(
                new BoxLayout(heading, BoxLayout.Y_AXIS)
        );
        heading.setOpaque(false);

        JLabel pageTitle =
                new JLabel("Lecturer Profile & Settings");
        pageTitle.setFont(
                new Font("SansSerif", Font.BOLD, 25)
        );
        pageTitle.setForeground(PROFILE_DEEP_GREEN);

        JLabel subtitle = new JLabel(
                "View and update your personal academic profile details."
        );
        subtitle.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        subtitle.setForeground(PROFILE_TEXT_GRAY);

        heading.add(pageTitle);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);

        // Main card
        JPanel card = new JPanel(new BorderLayout(0, 20));
        card.setBackground(PROFILE_WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(PROFILE_BORDER, 1, true),
                new EmptyBorder(22, 24, 20, 24)
        ));

        // Read-only information
        JPanel readOnlyPanel =
                new JPanel(new GridLayout(1, 3, 12, 0));
        readOnlyPanel.setOpaque(false);
        readOnlyPanel.setBorder(new CompoundBorder(
                new LineBorder(PROFILE_BORDER, 1, true),
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
        addFormField(
                formGrid, gbc, 0, 0,
                "Title", titleCombo
        );
        addFormField(
                formGrid, gbc, 1, 0,
                "Full Name", fullNameField
        );

        // Row 2
        addFormField(
                formGrid, gbc, 0, 1,
                "NIC Number", nicField
        );
        addFormField(
                formGrid, gbc, 1, 1,
                "Email Address", emailField
        );

        // Row 3
        addFormField(
                formGrid, gbc, 0, 2,
                "Contact Number", contactField
        );
        addFormField(
                formGrid, gbc, 1, 2,
                "Home / Office Address", addressField
        );

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

        // Reset button
        JButton resetBtn = new JButton("Reset");
        styleButton(resetBtn, false);
        resetBtn.addActionListener(e -> loadProfile());

        // Save button
        JButton saveBtn =
                new JButton("Save Profile Changes");
        styleButton(saveBtn, true);
        saveBtn.addActionListener(e -> saveProfile());

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        buttonPanel.setOpaque(false);
        buttonPanel.add(resetBtn);
        buttonPanel.add(saveBtn);

        // Card content
        JPanel formContent =
                new JPanel(new BorderLayout(0, 14));
        formContent.setOpaque(false);
        formContent.add(readOnlyPanel, BorderLayout.NORTH);
        formContent.add(formGrid, BorderLayout.CENTER);

        card.add(formContent, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        // Footer status
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        statusLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );
        statusLabel.setForeground(PROFILE_TEXT_GRAY);
        footer.add(statusLabel, BorderLayout.WEST);

        add(heading, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        // Load existing database data
        loadProfile();
    }

    private void styleInfoLabel(JLabel label) {
        label.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        label.setForeground(PROFILE_DEEP_GREEN);
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

        JPanel fieldPanel =
                createFieldPanel(labelText, field);

        parent.add(fieldPanel, gbc);
    }

    private JPanel createFieldPanel(
            String labelText,
            JComponent field
    ) {
        JPanel panel = new JPanel(
                new BorderLayout(0, 6)
        );
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        label.setForeground(PROFILE_DEEP_GREEN);

        field.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        field.setPreferredSize(new Dimension(180, 34));

        if (field instanceof JTextField) {
            JTextField textField = (JTextField) field;
            textField.setMargin(
                    new Insets(5, 9, 5, 9)
            );
            textField.setBorder(new CompoundBorder(
                    new LineBorder(PROFILE_BORDER, 1, true),
                    new EmptyBorder(1, 2, 1, 2)
            ));
        } else if (field instanceof JComboBox<?>) {
            field.setBorder(
                    new LineBorder(PROFILE_BORDER, 1, true)
            );
        }

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private void styleButton(
            JButton button,
            boolean primary
    ) {
        button.setFont(new Font(
                "SansSerif",
                primary ? Font.BOLD : Font.PLAIN,
                13
        ));

        button.setFocusPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );
        button.setBorder(
                new EmptyBorder(9, 16, 9, 16)
        );
        button.setOpaque(true);

        if (primary) {
            button.setBackground(PROFILE_DEEP_GREEN);
            button.setForeground(PROFILE_WHITE);
        } else {
            button.setBackground(PROFILE_WHITE);
            button.setForeground(PROFILE_DEEP_GREEN);
            button.setBorder(new CompoundBorder(
                    new LineBorder(PROFILE_DEEP_GREEN, 1, true),
                    new EmptyBorder(8, 15, 8, 15)
            ));
        }
    }

    private void loadProfile() {
        statusLabel.setText("Loading profile details...");

        try {
            Object[] data =
                    profileDAO.getLecturerProfile(userId);

            if (data != null) {
                usernameLabel.setText(
                        "Username: " + data[1]
                );
                codeLabel.setText(
                        "Lecturer Code: " + data[2]
                );
                deptLabel.setText(
                        "Department: " + data[10]
                );

                titleCombo.setSelectedItem(data[3]);
                fullNameField.setText((String) data[4]);
                nicField.setText((String) data[5]);
                emailField.setText((String) data[6]);
                contactField.setText((String) data[7]);
                addressField.setText((String) data[8]);
                specializationField.setText(
                        (String) data[9]
                );

                statusLabel.setText(
                        "Profile loaded successfully."
                );
            } else {
                statusLabel.setText(
                        "Profile details not found."
                );
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
        String titleStr =
                (String) titleCombo.getSelectedItem();

        String fullNameStr =
                fullNameField.getText().trim();
        String nicStr = nicField.getText().trim();
        String emailStr = emailField.getText().trim();
        String contactStr = contactField.getText().trim();
        String addressStr = addressField.getText().trim();
        String specStr =
                specializationField.getText().trim();

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
            boolean success =
                    profileDAO.updateLecturerProfile(
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
                statusLabel.setText(
                        "Profile updated successfully."
                );

                // Reload the saved information.
                loadProfile();

                JOptionPane.showMessageDialog(
                        this,
                        "Profile details updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Refresh the dashboard greeting.
                if (onProfileUpdated != null) {
                    onProfileUpdated.run();
                }

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
