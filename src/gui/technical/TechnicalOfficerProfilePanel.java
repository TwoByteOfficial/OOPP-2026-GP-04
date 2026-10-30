
package gui.technical;

import dao.TechnicalOfficerProfileDAO;
import gui.theme.UniMisTheme;
import model.TechnicalOfficer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class TechnicalOfficerProfilePanel extends JPanel {

    private final long userId;
    private final TechnicalOfficerProfileDAO profileDAO =
            new TechnicalOfficerProfileDAO();

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

    public TechnicalOfficerProfilePanel(long userId) {

        this.userId = userId;

        setLayout(new BorderLayout());
        setBackground(UniMisTheme.LIGHT_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel heading = new JLabel("My Profile");
        heading.setFont(UniMisTheme.FONT_H2);
        heading.setForeground(UniMisTheme.TEXT);

        JLabel subtitle = new JLabel(
                "View and update your personal and contact information");
        subtitle.setFont(UniMisTheme.FONT_BODY);
        subtitle.setForeground(UniMisTheme.TEXT);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(heading);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        add(header, BorderLayout.NORTH);

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

    private void setReadOnly(JTextField field) {
        field.setEditable(false);
        field.setBackground(new Color(240, 243, 240));
    }

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

    private boolean isValidContactNumber(String contact) {
        return contact.matches("[0-9]{10}");
    }

    private void saveProfile() {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String address = addressArea.getText().trim();
        String labs = labsField.getText().trim();

        if (name.isEmpty() || email.isEmpty()
                || contact.isEmpty() || address.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Name, email, contact number, and address are required."
            );
            return;
        }


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


        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid email address."
            );
            return;
        }

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
