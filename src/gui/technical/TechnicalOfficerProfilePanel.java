package gui.technical;

import dao.TechnicalOfficer.TechnicalOfficerProfileDAO;
import gui.theme.UniMisTheme;
import model.TechnicalOfficer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * [OOP - INHERITANCE]: Extends javax.swing.JPanel to build the Technical Officer profile view.
 * 
 * [OOP - ENCAPSULATION]:
 * 1. Encapsulates all user form controls and profile picture state privately.
 * 2. Implements internal input validation rules (e.g. 10-digit phone number, valid email regex)
 *    to protect database integrity before saving.
 * 
 * [OOP - ABSTRACTION]: Profile persistence and image path updates are completely decoupled 
 * using TechnicalOfficerProfileDAO.
 */
public class TechnicalOfficerProfilePanel extends JPanel {

    private final long userId;
    private final Consumer<String> onPictureUpdated;

    // [OOP - ABSTRACTION]: DAO handling profile queries and updates
    private final TechnicalOfficerProfileDAO profileDAO = new TechnicalOfficerProfileDAO();

    // [OOP - ENCAPSULATION]: Profile picture controls and cached path
    private String currentProfilePicture;
    private JLabel pictureLabel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    // [OOP - ENCAPSULATION]: Private form fields
    private final JTextField nameField = new JTextField(25);
    private final JTextField usernameField = new JTextField(25);
    private final JTextField codeField = new JTextField(25);
    private final JTextField designationField = new JTextField(25);
    private final JTextField nicField = new JTextField(25);
    private final JTextField dobField = new JTextField(25);
    private final JTextField genderField = new JTextField(25);
    private final JTextField emailField = new JTextField(25);
    private final JTextField contactField = new JTextField(25);
    private final JTextArea addressArea = new JTextArea(3, 25);
    private final JTextField departmentField = new JTextField(25);
    private final JTextField labsField = new JTextField(25);
    private final JTextField statusField = new JTextField(25);

    /**
     * [OOP - POLYMORPHISM (OVERLOADING)]: Default constructor with userId only.
     *
     * @param userId The database user ID of the Technical Officer
     */
    public TechnicalOfficerProfilePanel(long userId) {
        this(userId, null);
    }

    /**
     * [OOP - POLYMORPHISM (OVERLOADING)]: Parameterized constructor with picture update callback.
     *
     * @param userId           The database user ID of the Technical Officer
     * @param onPictureUpdated [OOP - INTERFACE]: Consumer<String> callback notifying parent frames on PP updates
     */
    public TechnicalOfficerProfilePanel(long userId, Consumer<String> onPictureUpdated) {
        this.userId = userId;
        this.onPictureUpdated = onPictureUpdated;

        setLayout(new BorderLayout());
        setBackground(UniMisTheme.LIGHT_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header section: Title & subtitle on left, Profile picture & 3 buttons on right
        JLabel heading = new JLabel("My Profile");
        heading.setFont(UniMisTheme.FONT_H2);
        heading.setForeground(UniMisTheme.TEXT);

        JLabel subtitle = new JLabel("View and update your personal and contact information");
        subtitle.setFont(UniMisTheme.FONT_BODY);
        subtitle.setForeground(UniMisTheme.TEXT);

        JPanel titleSection = new JPanel();
        titleSection.setOpaque(false);
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.add(heading);
        titleSection.add(Box.createVerticalStrut(6));
        titleSection.add(subtitle);

        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);
        header.add(titleSection, BorderLayout.CENTER);
        header.add(buildPictureSection(), BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Form fields layout
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(22, 24, 22, 24));

        setReadOnly(usernameField);
        setReadOnly(codeField);
        setReadOnly(designationField);
        setReadOnly(nicField);
        setReadOnly(dobField);
        setReadOnly(genderField);
        setReadOnly(departmentField);
        setReadOnly(statusField);

        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setFont(UniMisTheme.FONT_BODY);

        int row = 0;
        row = addField(form, "Full Name", nameField, row);
        row = addField(form, "Username", usernameField, row);
        row = addField(form, "Staff Code", codeField, row);
        row = addField(form, "Designation", designationField, row);
        row = addField(form, "NIC", nicField, row);
        row = addField(form, "Date of Birth", dobField, row);
        row = addField(form, "Gender", genderField, row);
        row = addField(form, "Email", emailField, row);
        row = addField(form, "Contact Number", contactField, row);
        row = addField(form, "Address", new JScrollPane(addressArea), row);
        row = addField(form, "Department ID", departmentField, row);
        row = addField(form, "Assigned Laboratories", labsField, row);
        addField(form, "Account Status", statusField, row);

        JButton saveButton = new JButton("Save Changes");
        saveButton.setBackground(new Color(0x2E, 0x7D, 0x32));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        // [OOP - INTERFACE]: java.awt.event.ActionListener lambda implementation
        saveButton.addActionListener(e -> saveProfile());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(saveButton);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(22, 0, 0, 0));
        content.add(form, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        loadProfile();
    }

    /**
     * [OOP - ENCAPSULATION]: Assembles the profile picture square and Add/Edit/Delete action buttons.
     */
    private JPanel buildPictureSection() {
        pictureLabel = new JLabel("No Picture", SwingConstants.CENTER);
        pictureLabel.setPreferredSize(new Dimension(85, 85));
        pictureLabel.setOpaque(true);
        pictureLabel.setBackground(Color.WHITE);
        pictureLabel.setFont(UniMisTheme.FONT_TINY);
        pictureLabel.setForeground(UniMisTheme.MUTED);
        pictureLabel.setBorder(BorderFactory.createLineBorder(UniMisTheme.BORDER, 2));

        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");

        Dimension btnSize = new Dimension(80, 25);
        for (JButton btn : new JButton[]{addButton, editButton, deleteButton}) {
            btn.setPreferredSize(btnSize);
            btn.setFont(UniMisTheme.FONT_TINY);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        addButton.setBackground(UniMisTheme.MEDIUM_GREEN);
        addButton.setForeground(UniMisTheme.TEXT);

        editButton.setBackground(UniMisTheme.PASTEL_GREEN);
        editButton.setForeground(UniMisTheme.TEXT);

        deleteButton.setBackground(UniMisTheme.DANGER);
        deleteButton.setForeground(Color.WHITE);

        // [OOP - INTERFACE]: ActionListeners on buttons
        addButton.addActionListener(e -> showPictureInputDialog(false));
        editButton.addActionListener(e -> showPictureInputDialog(true));
        deleteButton.addActionListener(e -> deleteProfilePicture());

        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 0, 4));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);

        JPanel pictureSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pictureSection.setOpaque(false);
        pictureSection.add(buttonsPanel);
        pictureSection.add(pictureLabel);

        return pictureSection;
    }

    /**
     * Updates enabled/disabled state of Add, Edit, Delete buttons based on picture presence.
     */
    private void updatePictureButtonStates() {
        boolean hasPic = currentProfilePicture != null && !currentProfilePicture.trim().isEmpty();
        addButton.setEnabled(!hasPic);
        editButton.setEnabled(hasPic);
        deleteButton.setEnabled(hasPic);
    }

    /**
     * Displays image thumbnail in the profile picture square.
     */
    private void displayProfilePicture(String path) {
        ImageIcon icon = loadThumbnail(path, 85, 85);
        if (icon != null) {
            pictureLabel.setIcon(icon);
            pictureLabel.setText("");
            pictureLabel.setToolTipText(path);
        } else {
            pictureLabel.setIcon(null);
            pictureLabel.setText("No Picture");
            pictureLabel.setToolTipText(null);
        }
    }

    /**
     * Shows dialog with image path/link input field, file chooser, and Save button.
     */
    private void showPictureInputDialog(boolean isEdit) {
        boolean hasPic = currentProfilePicture != null && !currentProfilePicture.trim().isEmpty();
        if (!isEdit && hasPic) {
            return;
        }
        if (isEdit && !hasPic) {
            return;
        }

        String title = isEdit ? "Edit Profile Picture" : "Add Profile Picture";

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        JLabel promptLabel = new JLabel("Enter Image File Path or URL Link:");
        promptLabel.setFont(UniMisTheme.FONT_SMALL);

        JPanel inputRow = new JPanel(new BorderLayout(6, 0));
        JTextField pathField = new JTextField(isEdit && currentProfilePicture != null ? currentProfilePicture : "", 24);
        pathField.setFont(UniMisTheme.FONT_SMALL);

        JButton browseButton = new JButton("Browse...");
        browseButton.setFont(UniMisTheme.FONT_SMALL);
        browseButton.setFocusPainted(false);
        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Profile Picture");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                pathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        inputRow.add(pathField, BorderLayout.CENTER);
        inputRow.add(browseButton, BorderLayout.EAST);

        panel.add(promptLabel, BorderLayout.NORTH);
        panel.add(inputRow, BorderLayout.CENTER);

        int option = JOptionPane.showOptionDialog(
                this,
                panel,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{"Save", "Cancel"},
                "Save"
        );

        if (option == 0) { // Save button clicked
            String enteredPath = pathField.getText().trim();
            if (enteredPath.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Image path or link cannot be empty.",
                        "Input Required",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            saveProfilePicture(enteredPath);
        }
    }

    /**
     * Confirms and deletes current profile picture from the database and UI.
     */
    private void deleteProfilePicture() {
        boolean hasPic = currentProfilePicture != null && !currentProfilePicture.trim().isEmpty();
        if (!hasPic) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete your profile picture?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            saveProfilePicture(null);
        }
    }

    /**
     * Persists the profile picture path to the database and notifies parent observers.
     */
    private void saveProfilePicture(String path) {
        try {
            boolean updated = profileDAO.updateProfilePicture(userId, path);
            if (updated) {
                this.currentProfilePicture = path;
                displayProfilePicture(path);
                updatePictureButtonStates();

                if (onPictureUpdated != null) {
                    onPictureUpdated.accept(path);
                }

                JOptionPane.showMessageDialog(
                        this,
                        path == null ? "Profile picture deleted successfully." : "Profile picture saved successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not update profile picture in database.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Reusable helper to load and scale an image thumbnail from a local path or web URL.
     */
    public static ImageIcon loadThumbnail(String path, int width, int height) {
        if (path == null || path.isBlank()) {
            return null;
        }
        try {
            String cleanPath = path.trim().replaceAll("^\"|\"$", "");
            Image img = null;

            if (cleanPath.startsWith("http://") || cleanPath.startsWith("https://")) {
                img = ImageIO.read(new URI(cleanPath).toURL());
            } else {
                File file = new File(cleanPath);
                if (!file.exists()) {
                    file = new File(System.getProperty("user.dir"), cleanPath);
                }
                if (file.exists()) {
                    img = ImageIO.read(file);
                }
            }

            if (img != null) {
                return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * [OOP - ENCAPSULATION]: Private helper for GridBagLayout positioning.
     */
    private int addField(
            JPanel panel, String label, Component field, int row) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 6, 7, 12);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label + ":"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);

        return row + 1;
    }

    /**
     * [OOP - ENCAPSULATION]: Private helper to configure read-only fields.
     */
    private void setReadOnly(JTextField field) {
        field.setEditable(false);
        field.setBackground(new Color(240, 243, 240));
    }

    /**
     * [OOP - ENCAPSULATION & ABSTRACTION]: Loads profile data via TechnicalOfficerProfileDAO.
     */
    private void loadProfile() {

        try {
            TechnicalOfficer officer = profileDAO.getProfile(userId);

            if (officer == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Technical Officer profile was not found.",
                        "Profile",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            nameField.setText(text(officer.getFullName()));
            usernameField.setText(text(officer.getUsername()));
            codeField.setText(text(officer.getTechnicalOfficerCode()));
            designationField.setText(text(officer.getDesignation()));
            nicField.setText(text(officer.getNic()));

            dobField.setText(officer.getDateOfBirth() == null
                    ? "" : officer.getDateOfBirth().toString());

            genderField.setText(text(officer.getGender()));
            emailField.setText(text(officer.getEmail()));
            contactField.setText(text(officer.getContactNo()));
            addressArea.setText(text(officer.getAddress()));

            departmentField.setText(officer.getDepartmentId() == null
                    ? "" : officer.getDepartmentId().toString());

            labsField.setText(text(officer.getAssignedLabs()));
            statusField.setText(text(officer.getUserStatus()));

            // Load and display profile picture
            this.currentProfilePicture = officer.getProfilePicture();
            displayProfilePicture(currentProfilePicture);
            updatePictureButtonStates();

        } catch (SQLException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load your profile.\n\nDatabase error: "
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * [OOP - ENCAPSULATION]: Encapsulated validation rule for contact number (10 digits).
     */
    private boolean isValidContactNumber(String contact) {
        return contact.matches("[0-9]{10}");
    }

    /**
     * [OOP - ENCAPSULATION & ABSTRACTION]: Validates input rules before delegating update to DAO.
     */
    private void saveProfile() {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String address = addressArea.getText().trim();
        String labs = labsField.getText().trim();

        // 1. Check required fields
        if (name.isEmpty() || email.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Name, email, contact number, and address are required."
            );
            return;
        }

        // 2. Validate contact number format (10 digits)
        if (!isValidContactNumber(contact)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Contact number must contain exactly 10 digits (0-9).",
                    "Invalid Contact Number",
                    JOptionPane.WARNING_MESSAGE
            );
            contactField.requestFocus();
            return;
        }

        // 3. Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid email address."
            );
            return;
        }

        // 4. Update database
        try {
            boolean updated = profileDAO.updateProfile(
                    userId, name, email, contact, address, labs
            );

            if (updated) {
                loadProfile();
                JOptionPane.showMessageDialog(
                        this,
                        "Profile updated successfully."
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No profile changes were saved."
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to save your profile.\n\nDatabase error: "
                            + e.getMessage(),
                    "Save Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String text(String value) {
        return value == null ? "" : value;
    }
}
