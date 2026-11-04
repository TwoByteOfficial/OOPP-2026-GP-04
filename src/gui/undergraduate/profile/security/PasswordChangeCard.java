package gui.undergraduate.profile.security;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Security panel allowing undergraduates to safely update their MIS account password.
 * Provides password entry verification, input length and equality validation,
 * inline status messaging, and consistent card styling conforming to the administrative theme.
 */
public class PasswordChangeCard extends JPanel {

    // Card styling constants for 16px corner curves and subtle perimeter border
    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    // Password input fields and dynamic validation status display label
    private final JPasswordField currentPasswordField;
    private final JPasswordField newPasswordField;
    private final JPasswordField confirmPasswordField;
    private final JLabel statusLabel;

    /**
     * Constructs the password change security card and initializes form components.
     */
    public PasswordChangeCard() {
        // Base container layout and margin configuration
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(18, 20, 18, 20));

        // -------------------------------------------------------------
        // Inner Form Container (Vertical Box Layout)
        // -------------------------------------------------------------
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // Section header and instruction labels
        JLabel title = new JLabel("Security & Password Management");
        title.setFont(AdminTheme.font(Font.BOLD, 15));
        title.setForeground(AdminTheme.DEEP_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(title);
        form.add(Box.createVerticalStrut(4));

        JLabel sub = new JLabel("Choose a strong passphrase with at least 6 characters.");
        sub.setFont(AdminTheme.font(Font.PLAIN, 12));
        sub.setForeground(AdminTheme.MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(sub);
        form.add(Box.createVerticalStrut(14));

        // -------------------------------------------------------------
        // Password Input Fields Setup
        // -------------------------------------------------------------
        currentPasswordField = createPasswordField();
        newPasswordField = createPasswordField();
        confirmPasswordField = createPasswordField();

        form.add(createInputBlock("Current Account Password:", currentPasswordField));
        form.add(Box.createVerticalStrut(10));
        form.add(createInputBlock("New Password:", newPasswordField));
        form.add(Box.createVerticalStrut(10));
        form.add(createInputBlock("Confirm New Password:", confirmPasswordField));
        form.add(Box.createVerticalStrut(10));

        // Inline status/error notification label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(AdminTheme.font(Font.PLAIN, 11));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(statusLabel);
        form.add(Box.createVerticalStrut(10));

        // -------------------------------------------------------------
        // Password Update Trigger Button
        // -------------------------------------------------------------
        JButton updateBtn = new JButton("Update Password");
        updateBtn.setFont(AdminTheme.font(Font.BOLD, 13));
        updateBtn.setBackground(AdminTheme.FOREST_GREEN);
        updateBtn.setForeground(AdminTheme.WHITE);
        updateBtn.setFocusPainted(false);
        updateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        updateBtn.addActionListener(e -> handlePasswordUpdate());
        form.add(updateBtn);

        add(form, BorderLayout.NORTH); // NORTH anchoring keeps form compact and prevents vertical stretching
    }

    /**
     * Factory method creating a vertical field group containing a descriptive label and password input field.
     *
     * @param label Field caption string
     * @param field Password field component to nest
     * @return Assembled input block container
     */
    private JPanel createInputBlock(String label, JPasswordField field) {
        JPanel block = new JPanel(new BorderLayout(0, 4));
        block.setOpaque(false);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58)); // Constrain input block height

        JLabel lbl = new JLabel(label);
        lbl.setFont(AdminTheme.font(Font.PLAIN, 11));
        lbl.setForeground(AdminTheme.MUTED);

        block.add(lbl, BorderLayout.NORTH);
        block.add(field, BorderLayout.CENTER);
        return block;
    }

    /**
     * Helper factory creating a styled JPasswordField with rounded borders and uniform dimensions.
     *
     * @return Configured JPasswordField instance
     */
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(0, 34));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        field.setFont(AdminTheme.font(Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD2, 0xD7, 0xE1), 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));
        return field;
    }

    /**
     * Validates input values (empty checks, minimum length, and confirmation matching)
     * and updates the status feedback label accordingly.
     */
    private void handlePasswordUpdate() {
        String curr = new String(currentPasswordField.getPassword());
        String nw = new String(newPasswordField.getPassword());
        String conf = new String(confirmPasswordField.getPassword());

        // Validate presence of all password fields
        if (curr.isEmpty() || nw.isEmpty() || conf.isEmpty()) {
            statusLabel.setText("Please fill all password fields.");
            statusLabel.setForeground(AdminTheme.DANGER);
            return;
        }

        // Validate new password minimum length requirement
        if (nw.length() < 6) {
            statusLabel.setText("New password must be at least 6 characters.");
            statusLabel.setForeground(AdminTheme.DANGER);
            return;
        }

        // Validate equality between new password and confirmation password
        if (!nw.equals(conf)) {
            statusLabel.setText("New passwords do not match.");
            statusLabel.setForeground(AdminTheme.DANGER);
            return;
        }

        // Success state feedback and input field cleanup
        statusLabel.setText("Password successfully updated!");
        statusLabel.setForeground(AdminTheme.FOREST_GREEN);
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    /**
     * Paints an anti-aliased rounded white card background and outer perimeter border.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Draw white card background
        g2.setColor(AdminTheme.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        // Draw outer perimeter border
        g2.setColor(CARD_BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.dispose();
        super.paintComponent(g);
    }
}