package gui.admin.NoticeComponents;

import dao.admin.AdminNoticeDAO;
import gui.admin.AdminTheme;
import model.Notice;

import javax.swing.*;
import java.awt.*;

public class AddNoticeComponent extends JButton {

    private final AdminNoticeDAO noticeDAO;
    private final long currentAdminUserId;
    private final Runnable onSuccess;

    public AddNoticeComponent(AdminNoticeDAO noticeDAO, long currentAdminUserId, Runnable onSuccess) {
        super("+ Post New Notice");
        this.noticeDAO = noticeDAO;
        this.currentAdminUserId = currentAdminUserId;
        this.onSuccess = onSuccess;

        setFont(AdminTheme.FONT_BODY_BOLD);
        setBackground(AdminTheme.PRIMARY);
        setForeground(AdminTheme.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> openPostNoticeWindow());
    }

    private void openPostNoticeWindow() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        Frame owner = (parentWindow instanceof Frame) ? (Frame) parentWindow : null;
        PostNoticeModal dialog = new PostNoticeModal(owner, noticeDAO, currentAdminUserId, onSuccess);
        dialog.setVisible(true);
    }

    // Modal Window Class embedded in the same component file
    public static class PostNoticeModal extends JDialog {

        private final AdminNoticeDAO noticeDAO;
        private final long adminUserId;
        private final Runnable onSuccess;

        private JTextField txtTitle;
        private JTextArea txtContent;
        private JComboBox<String> cmbType;
        private JComboBox<String> cmbTargetRole;
        private JComboBox<String> cmbDepartment;
        private JComboBox<String> cmbStatus;

        public PostNoticeModal(Frame parent, AdminNoticeDAO dao, long adminUserId, Runnable onSuccess) {
            super(parent, "Post New Notice Announcement", true);
            this.noticeDAO = dao;
            this.adminUserId = adminUserId;
            this.onSuccess = onSuccess;

            setSize(720, 620);
            setLocationRelativeTo(parent);
            setResizable(false);
            getContentPane().setBackground(AdminTheme.LIGHT_BG);
            setLayout(new BorderLayout());

            initUI();
        }

        private void initUI() {
            // Header Panel
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 16));
            headerPanel.setBackground(AdminTheme.DEEP_GREEN);

            JLabel lblHeader = new JLabel("Post New Official Notice");
            lblHeader.setFont(AdminTheme.FONT_H3);
            lblHeader.setForeground(AdminTheme.WHITE);
            headerPanel.add(lblHeader);
            add(headerPanel, BorderLayout.NORTH);

            // Form Panel
            JPanel pnlForm = new JPanel(new GridBagLayout());
            pnlForm.setBackground(AdminTheme.WHITE);
            pnlForm.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AdminTheme.BORDER, 1),
                    BorderFactory.createEmptyBorder(16, 24, 16, 24)
            ));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Notice Title
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
            pnlForm.add(createLabel("Notice Title:"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.8;
            txtTitle = createTextField();
            pnlForm.add(txtTitle, gbc);

            // Content
            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
            pnlForm.add(createLabel("Content / Body:"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.8;
            txtContent = new JTextArea(5, 20);
            txtContent.setFont(AdminTheme.FONT_BODY);
            txtContent.setLineWrap(true);
            txtContent.setWrapStyleWord(true);
            txtContent.setBackground(AdminTheme.WHITE);
            txtContent.setForeground(AdminTheme.BLACK);
            JScrollPane spContent = new JScrollPane(txtContent);
            spContent.setBorder(BorderFactory.createLineBorder(AdminTheme.BORDER, 1));
            pnlForm.add(spContent, gbc);

            // Notice Type
            gbc.gridx = 0; gbc.gridy = 2;
            pnlForm.add(createLabel("Notice Type:"), gbc);
            gbc.gridx = 1;
            cmbType = new JComboBox<>(new String[]{ "General", "Academic", "Exam", "Emergency" });
            styleCombo(cmbType);
            pnlForm.add(cmbType, gbc);

            // Target Role (Includes "All Roles" option)
            gbc.gridx = 0; gbc.gridy = 3;
            pnlForm.add(createLabel("Target Role:"), gbc);
            gbc.gridx = 1;
            cmbTargetRole = new JComboBox<>(new String[]{ "All Roles", "Undergraduate", "Lecturer", "Technical Officer", "Admin" });
            styleCombo(cmbTargetRole);
            pnlForm.add(cmbTargetRole, gbc);

            // Target Department
            gbc.gridx = 0; gbc.gridy = 4;
            pnlForm.add(createLabel("Target Department:"), gbc);
            gbc.gridx = 1;
            cmbDepartment = new JComboBox<>(new String[]{ "Faculty-Wide (All Depts)", "1 - ICT", "2 - Engineering Technology", "3 - Bio Systems Technology" });
            styleCombo(cmbDepartment);
            pnlForm.add(cmbDepartment, gbc);

            // Notice Status
            gbc.gridx = 0; gbc.gridy = 5;
            pnlForm.add(createLabel("Notice Status:"), gbc);
            gbc.gridx = 1;
            cmbStatus = new JComboBox<>(new String[]{ "Published", "Draft", "Archived" });
            styleCombo(cmbStatus);
            pnlForm.add(cmbStatus, gbc);

            add(pnlForm, BorderLayout.CENTER);

            // Footer Panel
            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
            footerPanel.setBackground(AdminTheme.WHITE);
            footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AdminTheme.BORDER));

            JButton btnSave = new JButton("Post Notice");
            btnSave.setFont(AdminTheme.FONT_BODY_BOLD);
            btnSave.setBackground(AdminTheme.PRIMARY);
            btnSave.setForeground(AdminTheme.WHITE);
            btnSave.setFocusPainted(false);
            btnSave.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
            btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnSave.addActionListener(e -> saveNotice());

            JButton btnCancel = new JButton("Cancel");
            btnCancel.setFont(AdminTheme.FONT_BODY_BOLD);
            btnCancel.setBackground(AdminTheme.PASTEL_GREEN);
            btnCancel.setForeground(AdminTheme.DEEP_GREEN);
            btnCancel.setFocusPainted(false);
            btnCancel.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
            btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnCancel.addActionListener(e -> dispose());

            footerPanel.add(btnSave);
            footerPanel.add(btnCancel);
            add(footerPanel, BorderLayout.SOUTH);
        }

        private JLabel createLabel(String text) {
            JLabel l = new JLabel(text);
            l.setFont(AdminTheme.FONT_BODY_BOLD);
            l.setForeground(AdminTheme.BLACK);
            return l;
        }

        private JTextField createTextField() {
            JTextField tf = new JTextField();
            tf.setFont(AdminTheme.FONT_BODY);
            tf.setBackground(AdminTheme.WHITE);
            tf.setForeground(AdminTheme.BLACK);
            tf.setCaretColor(AdminTheme.BLACK);
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AdminTheme.BORDER, 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
            return tf;
        }

        private void styleCombo(JComboBox<?> cb) {
            cb.setFont(AdminTheme.FONT_BODY);
            cb.setBackground(AdminTheme.WHITE);
            cb.setForeground(AdminTheme.BLACK);
        }

        private void saveNotice() {
            try {
                String title = txtTitle.getText().trim();
                String content = txtContent.getText().trim();
                if (title.isEmpty() || content.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Notice Title and Content are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String selectedRole = (String) cmbTargetRole.getSelectedItem();
                if (selectedRole == null || selectedRole.trim().isEmpty()) {
                    selectedRole = "All Roles";
                }

                Integer deptId = cmbDepartment.getSelectedIndex() == 0 ? null : cmbDepartment.getSelectedIndex();

                Notice n = new Notice();
                n.setTitle(title);
                n.setContent(content);
                n.setNoticeType((String) cmbType.getSelectedItem());
                n.setTargetRole(selectedRole);
                n.setDepartmentId(deptId);
                n.setPublishedBy(adminUserId);
                n.setStatus((String) cmbStatus.getSelectedItem());

                boolean success = noticeDAO.createNotice(n);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Notice published successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    if (onSuccess != null) onSuccess.run();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create notice in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving notice: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
