package gui.undergraduate.profile.info;

import dao.undergraduate.UndergraduateProfileDAO;
import gui.admin.AdminTheme;
import gui.undergraduate.profile.model.UserProfileData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ContactSettingsCard extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);
    private static final Color LABEL_COLOR = new Color(0x2D, 0x37, 0x48);
    private static final Color INFO_BG = new Color(0xF0, 0xFD, 0xF4);
    private static final Color INFO_BORDER = new Color(0xBB, 0xF7, 0xD0);

    private final ProfileAvatarCard avatarCard;
    private final JTextField phoneField;
    private final JTextField addressField;
    private final JTextField pictureUrlField;
    private final JButton previewButton;
    private final JButton saveButton;
    private long currentUserId;

    public ContactSettingsCard(ProfileAvatarCard avatarCard) {
        this.avatarCard = avatarCard;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(22, 24, 22, 24));

        JLabel title = new JLabel("Contact & Profile Settings");
        title.setFont(AdminTheme.font(Font.BOLD, 17));
        title.setForeground(AdminTheme.DEEP_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Manage contact details and customized profile photo.");
        subtitle.setFont(AdminTheme.font(Font.PLAIN, 12));
        subtitle.setForeground(AdminTheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        phoneField = createStyledField("0713000000");
        addressField = createStyledField("City, Sri Lanka");
        pictureUrlField = createStyledField("Paste direct image URL (https://...)");

        // Action for instant live avatar preview
        previewButton = new JButton("Preview");
        previewButton.setFont(AdminTheme.font(Font.BOLD, 12));
        previewButton.setBackground(new Color(0xED, 0xF2, 0xF7));
        previewButton.setFocusPainted(false);
        previewButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        previewButton.addActionListener(e -> {
            String url = pictureUrlField.getText().trim();
            if (avatarCard != null) {
                avatarCard.setLiveImagePreview(url);
            }
        });

        JPanel urlInputPanel = new JPanel(new BorderLayout(8, 0));
        urlInputPanel.setOpaque(false);
        urlInputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        urlInputPanel.setPreferredSize(new Dimension(300, 36));
        urlInputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        urlInputPanel.add(pictureUrlField, BorderLayout.CENTER);
        urlInputPanel.add(previewButton, BorderLayout.EAST);

        JPanel policyBanner = createPolicyBanner();
        policyBanner.setAlignmentX(Component.LEFT_ALIGNMENT);

        saveButton = new JButton("Save Profile Details");
        saveButton.setFont(AdminTheme.font(Font.BOLD, 13));
        saveButton.setForeground(AdminTheme.WHITE);
        saveButton.setBackground(AdminTheme.FOREST_GREEN);
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.addActionListener(e -> saveContactDetails());

        add(title);
        add(Box.createVerticalStrut(4));
        add(subtitle);
        add(Box.createVerticalStrut(18));

        add(createFieldLabel("Personal Contact Number:"));
        add(Box.createVerticalStrut(5));
        add(phoneField);
        add(Box.createVerticalStrut(12));

        add(createFieldLabel("Residential Address:"));
        add(Box.createVerticalStrut(5));
        add(addressField);
        add(Box.createVerticalStrut(12));

        add(createFieldLabel("Custom Profile Picture URL:"));
        add(Box.createVerticalStrut(5));
        add(urlInputPanel);
        add(Box.createVerticalStrut(18));

        add(policyBanner);
        add(Box.createVerticalStrut(18));
        add(saveButton);
        add(Box.createVerticalGlue());
    }

    public void updateDetails(UserProfileData data) {
        if (data == null) return;
        this.currentUserId = data.getUserId();
        phoneField.setText(data.getContactNo() != null ? data.getContactNo() : "");
        addressField.setText(data.getAddress() != null ? data.getAddress() : "");
        pictureUrlField.setText(data.getProfilePicture() != null ? data.getProfilePicture() : "");
        if (avatarCard != null && data.getProfilePicture() != null) {
            avatarCard.setLiveImagePreview(data.getProfilePicture());
        }
    }

    private void saveContactDetails() {
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String picUrl = pictureUrlField.getText().trim();

        boolean ok = UndergraduateProfileDAO.updateContactDetails(currentUserId, phone, address, picUrl);
        if (ok) {
            if (avatarCard != null) {
                avatarCard.setLiveImagePreview(picUrl);
            }
            JOptionPane.showMessageDialog(this,
                    "Student profile and picture URL saved to MySQL database successfully!",
                    "Profile Updated", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Database update error occurred.",
                    "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AdminTheme.font(Font.BOLD, 12));
        lbl.setForeground(LABEL_COLOR);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createStyledField(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        tf.setFont(AdminTheme.font(Font.PLAIN, 13));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tf.setPreferredSize(new Dimension(300, 36));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 12, 6, 12)));
        return tf;
    }

    private JPanel createPolicyBanner() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(INFO_BG);
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INFO_BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JLabel info = new JLabel("<html><b>Faculty Regulation:</b> Undergraduates can customize contact info and profile image URL with database persistence.</html>");
        info.setFont(AdminTheme.font(Font.PLAIN, 11));
        info.setForeground(AdminTheme.DEEP_GREEN);
        banner.add(info, BorderLayout.CENTER);
        return banner;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(AdminTheme.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.setColor(BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.dispose();
        super.paintComponent(g);
    }
}