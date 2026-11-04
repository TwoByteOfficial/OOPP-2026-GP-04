
package gui.lecturer.Profile;

import dao.Lecturer.LecturerProfileDAO;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.function.Consumer;

import static gui.lecturer.SharedComponents.Colors.PROFILE_BACKGROUND;
import static gui.lecturer.SharedComponents.Colors.PROFILE_BORDER;
import static gui.lecturer.SharedComponents.Colors.PROFILE_DEEP_GREEN;
import static gui.lecturer.SharedComponents.Colors.PROFILE_TEXT_GRAY;
import static gui.lecturer.SharedComponents.Colors.PROFILE_WHITE;

public class LecturerProfile extends JPanel {

    private final long userId;

    private final LecturerProfileDAO profileDAO =
            new LecturerProfileDAO();

    private final Runnable onProfileUpdated;
    private final Consumer<String> onProfilePictureUpdated;

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

    private final JTextField profilePictureUrlField =
            new JTextField();

    private final JLabel picturePreview = new JLabel(
            "No Profile Picture",
            SwingConstants.CENTER
    );

    private final JLabel statusLabel = new JLabel("Ready");

    private String savedPictureUrl = "";

    public LecturerProfile(long userId) {
        this(userId, null, null);
    }

    public LecturerProfile(
            long userId,
            Runnable onProfileUpdated
    ) {
        this(userId, onProfileUpdated, null);
    }

    public LecturerProfile(
            long userId,
            Runnable onProfileUpdated,
            Consumer<String> onProfilePictureUpdated
    ) {

        this.userId = userId;
        this.onProfileUpdated = onProfileUpdated;
        this.onProfilePictureUpdated = onProfilePictureUpdated;

        setLayout(new BorderLayout(0, 18));
        setBackground(PROFILE_BACKGROUND);
        setBorder(new EmptyBorder(24, 28, 20, 28));

        // Page heading
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
        JPanel card = new JPanel(new BorderLayout(0, 18));
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

        // Profile picture section - ABOVE personal details
        JPanel pictureSection = createPictureSection();

        // Personal details form
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

        // Row 4
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

        // Stack picture section above form
        JPanel formContent = new JPanel();
        formContent.setLayout(
                new BoxLayout(formContent, BoxLayout.Y_AXIS)
        );
        formContent.setOpaque(false);

        readOnlyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pictureSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        formGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        formContent.add(readOnlyPanel);
        formContent.add(Box.createVerticalStrut(18));

        formContent.add(pictureSection);
        formContent.add(Box.createVerticalStrut(18));

        formContent.add(formGrid);

        card.add(formContent, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        // Footer
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

        loadProfile();
    }

    private JPanel createPictureSection() {

        JPanel section = new JPanel(new BorderLayout(20, 0));
        section.setBackground(new Color(248, 252, 248));

        section.setBorder(new CompoundBorder(
                new LineBorder(PROFILE_BORDER, 1, true),
                new EmptyBorder(18, 20, 18, 20)
        ));

        // Preview area
        JPanel previewArea = new JPanel(
                new BorderLayout(0, 8)
        );

        previewArea.setOpaque(false);
        previewArea.setPreferredSize(new Dimension(230, 210));

        JLabel previewTitle = new JLabel("Profile Picture Preview");
        previewTitle.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );
        previewTitle.setForeground(PROFILE_DEEP_GREEN);

        picturePreview.setOpaque(true);
        picturePreview.setBackground(PROFILE_WHITE);
        picturePreview.setForeground(PROFILE_TEXT_GRAY);
        picturePreview.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );

        picturePreview.setBorder(
                new LineBorder(PROFILE_BORDER, 1, true)
        );

        previewArea.add(previewTitle, BorderLayout.NORTH);
        previewArea.add(picturePreview, BorderLayout.CENTER);

        // URL and preview controls
        JPanel controls = new JPanel();
        controls.setLayout(
                new BoxLayout(controls, BoxLayout.Y_AXIS)
        );
        controls.setOpaque(false);

        JLabel urlLabel = new JLabel("Profile Picture URL");
        urlLabel.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        urlLabel.setForeground(PROFILE_DEEP_GREEN);

        profilePictureUrlField.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        profilePictureUrlField.setPreferredSize(
                new Dimension(400, 38)
        );

        profilePictureUrlField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 38)
        );

        profilePictureUrlField.setBorder(new CompoundBorder(
                new LineBorder(PROFILE_BORDER, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JButton previewButton = new JButton("Preview Picture");
        styleButton(previewButton, true);
        previewButton.addActionListener(e -> previewProfilePicture());

        JButton clearButton = new JButton("Remove Picture");
        styleButton(clearButton, false);

        clearButton.addActionListener(e -> {
            profilePictureUrlField.setText("");
            picturePreview.setIcon(null);
            picturePreview.setText("No Profile Picture");
            savedPictureUrl = "";
        });

        JPanel buttonRow = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0)
        );
        buttonRow.setOpaque(false);
        buttonRow.add(previewButton);
        buttonRow.add(clearButton);

        JLabel helpText = new JLabel(
                "Paste a direct HTTP/HTTPS image URL."
        );

        helpText.setFont(
                new Font("SansSerif", Font.PLAIN, 11)
        );
        helpText.setForeground(PROFILE_TEXT_GRAY);

        controls.add(Box.createVerticalStrut(25));
        controls.add(urlLabel);
        controls.add(Box.createVerticalStrut(8));
        controls.add(profilePictureUrlField);
        controls.add(Box.createVerticalStrut(12));
        controls.add(buttonRow);
        controls.add(Box.createVerticalStrut(8));
        controls.add(helpText);

        section.add(previewArea, BorderLayout.WEST);
        section.add(controls, BorderLayout.CENTER);

        return section;
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
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
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

                fullNameField.setText(
                        data[4] == null ? "" : data[4].toString()
                );

                nicField.setText(
                        data[5] == null ? "" : data[5].toString()
                );

                emailField.setText(
                        data[6] == null ? "" : data[6].toString()
                );

                contactField.setText(
                        data[7] == null ? "" : data[7].toString()
                );

                addressField.setText(
                        data[8] == null ? "" : data[8].toString()
                );

                specializationField.setText(
                        data[9] == null ? "" : data[9].toString()
                );

                savedPictureUrl = data.length > 11 && data[11] != null
                        ? data[11].toString()
                        : "";

                profilePictureUrlField.setText(savedPictureUrl);

                if (!savedPictureUrl.isBlank()) {
                    previewProfilePicture();
                } else {
                    picturePreview.setIcon(null);
                    picturePreview.setText("No Profile Picture");
                }

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

    private boolean isValidImageUrl(String value) {

        if (value == null || value.isBlank()) {
            return true;
        }

        if (value.length() > 500) {
            return false;
        }

        return value.matches("(?i)^https?://.+");
    }

    private void previewProfilePicture() {

        String urlText =
                profilePictureUrlField.getText().trim();

        if (urlText.isEmpty()) {
            picturePreview.setIcon(null);
            picturePreview.setText("No Profile Picture");
            return;
        }

        if (!isValidImageUrl(urlText)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid HTTP or HTTPS image URL.",
                    "Invalid URL",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        picturePreview.setText("Loading image...");
        picturePreview.setIcon(null);

        new SwingWorker<BufferedImage, Void>() {

            @Override
            protected BufferedImage doInBackground()
                    throws Exception {

                URL url = new URL(urlText);
                URLConnection connection = url.openConnection();

                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty(
                        "User-Agent", "UniMIS"
                );

                try (java.io.InputStream input =
                             connection.getInputStream()) {

                    return ImageIO.read(input);
                }
            }

            @Override
            protected void done() {

                try {

                    BufferedImage image = get();

                    if (image == null) {
                        throw new Exception(
                                "The URL does not contain a supported image."
                        );
                    }

                    int width = picturePreview.getWidth();
                    int height = picturePreview.getHeight();

                    if (width <= 0) width = 230;
                    if (height <= 0) height = 160;

                    double scale = Math.min(
                            (double) width / image.getWidth(),
                            (double) height / image.getHeight()
                    );

                    int newWidth = Math.max(
                            1, (int) (image.getWidth() * scale)
                    );

                    int newHeight = Math.max(
                            1, (int) (image.getHeight() * scale)
                    );

                    Image scaled = image.getScaledInstance(
                            newWidth,
                            newHeight,
                            Image.SCALE_SMOOTH
                    );

                    picturePreview.setText("");
                    picturePreview.setIcon(new ImageIcon(scaled));

                    statusLabel.setText(
                            "Picture preview loaded."
                    );

                } catch (Exception e) {

                    picturePreview.setIcon(null);
                    picturePreview.setText(
                            "Unable to load picture"
                    );

                    statusLabel.setText(
                            "Invalid or unavailable image URL."
                    );
                }
            }

        }.execute();
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

        String pictureUrl =
                profilePictureUrlField.getText().trim();

        if (fullNameStr.isEmpty() || emailStr.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Full Name and Email Address are required fields.",
                    "Input Validation",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (!isValidImageUrl(pictureUrl)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid HTTP or HTTPS image URL. Maximum 500 characters.",
                    "Invalid Profile Picture URL",
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
                            specStr,
                            pictureUrl
                    );

            if (success) {

                savedPictureUrl = pictureUrl;

                // Update topbar immediately.
                if (onProfilePictureUpdated != null) {
                    onProfilePictureUpdated.accept(pictureUrl);
                }

                // Refresh dashboard greeting.
                if (onProfileUpdated != null) {
                    onProfileUpdated.run();
                }

                statusLabel.setText(
                        "Profile updated successfully."
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Profile details and picture updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

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
