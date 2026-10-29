package gui.admin.NoticeComponents;

import dao.admin.AdminNoticeDAO;
import gui.admin.AdminTheme;
import model.Notice;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class EditNoticeComponent extends JButton {

    private final Supplier<Notice> selectedNoticeSupplier;
    private final AdminNoticeDAO noticeDAO;
    private final Runnable onSuccess;

    public EditNoticeComponent(Supplier<Notice> selectedNoticeSupplier, AdminNoticeDAO noticeDAO, Runnable onSuccess) {
        super("Edit Notice");
        this.selectedNoticeSupplier = selectedNoticeSupplier;
        this.noticeDAO = noticeDAO;
        this.onSuccess = onSuccess;

        setFont(AdminTheme.FONT_BODY_BOLD);
        setBackground(AdminTheme.PASTEL_GREEN);
        setForeground(AdminTheme.DEEP_GREEN);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> openEditNoticeWindow());
    }

    private void openEditNoticeWindow() {
        Notice selected = selectedNoticeSupplier.get();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an announcement from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        Frame owner = (parentWindow instanceof Frame) ? (Frame) parentWindow : null;
        EditNoticeModal dialog = new EditNoticeModal(owner, noticeDAO, selected, onSuccess);
        dialog.setVisible(true);
    }

    // Modal Window Class embedded in the same component file
    public static class EditNoticeModal extends JDialog {

        private final AdminNoticeDAO noticeDAO;
        private final Notice editNotice;
        private final Runnable onSuccess;

        private JTextField txtTitle;
        private JTextArea txtContent;
        private JComboBox<String> cmbType;
        private JComboBox<String> cmbTargetRole;
        private JComboBox<String> cmbDepartment;
        private JComboBox<String> cmbStatus;

        public EditNoticeModal(Frame parent, AdminNoticeDAO dao, Notice noticeToEdit, Runnable onSuccess) {
            super(parent, "Edit Notice Announcement", true);
            this.noticeDAO = dao;
            this.editNotice = noticeToEdit;
            this.onSuccess = onSuccess;

            setSize(720, 620);
            setLocationRelativeTo(parent);
            setResizable(false);
            getContentPane().setBackground(AdminTheme.LIGHT_BG);
            setLayout(new BorderLayout());

            initUI();
            populateForm();
        }

        private void initUI() {
            // Header
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 16));
            headerPanel.setBackground(AdminTheme.DEEP_GREEN);

            JLabel lblHeader = new JLabel("Modify Notice Announcement");
            lblHeader.setFont(AdminTheme.FONT_H3);
            lblHeader.setForeground(AdminTheme.WHITE);
            headerPanel.add(lblHeader);
            add(headerPanel, BorderLayout.NORTH);

            // Form
            JPanel pnlForm = new JPanel(new GridBagLayout());
            pnlForm.setBackground(AdminTheme.WHITE);
            pnlForm.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AdminTheme.BORDER, 1),
                    BorderFactory.createEmptyBorder(16, 24, 16, 24)
            ));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Title
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

            // Type
            gbc.gridx = 0; gbc.gridy = 2;
            pnlForm.add(createLabel("Notice Type:"), gbc);
            gbc.gridx = 1;
            cmbType = new JComboBox<>(new String[]{ "General", "Academic", "Exam", "Emergency" });
            styleCombo(cmbType);
            pnlForm.add(cmbType, gbc);

            // Target Role
            gbc.gridx = 0; gbc.gridy = 3;
            pnlForm.add(createLabel("Target Role:"), gbc);
            gbc.gridx = 1;
            cmbTargetRole = new JComboBox<>(new String[]{ "All Roles", "Undergraduate", "Lecturer", "Technical Officer", "Admin" });
            styleCombo(cmbTargetRole);
            pnlForm.add(cmbTargetRole, gbc);

            // Department
            gbc.gridx = 0; gbc.gridy = 4;
            pnlForm.add(createLabel("Target Department:"), gbc);
            gbc.gridx = 1;
            cmbDepartment = new JComboBox<>(new String[]{ "Faculty-Wide (All Depts)", "1 - ICT", "2 - Engineering Technology", "3 - Bio Systems Technology" });
            styleCombo(cmbDepartment);
            pnlForm.add(cmbDepartment, gbc);

            // Status
            gbc.gridx = 0; gbc.gridy = 5;
            pnlForm.add(createLabel("Notice Status:"), gbc);
            gbc.gridx = 1;
            cmbStatus = new JComboBox<>(new String[]{ "Published", "Draft", "Archived" });
            styleCombo(cmbStatus);
            pnlForm.add(cmbStatus, gbc);

            add(pnlForm, BorderLayout.CENTER);

            // Footer
            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
            footerPanel.setBackground(AdminTheme.WHITE);
            footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AdminTheme.BORDER));

            JButton btnSave = new JButton("Update Announcement");
            btnSave.setFont(AdminTheme.FONT_BODY_BOLD);
            btnSave.setBackground(AdminTheme.PRIMARY);
            btnSave.setForeground(AdminTheme.WHITE);
            btnSave.setFocusPainted(false);
            btnSave.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
            btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnSave.addActionListener(e -> updateNotice());

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

        private void populateForm() {
            if (editNotice == null) return;
            txtTitle.setText(editNotice.getTitle());
            txtContent.setText(editNotice.getContent());
            if (editNotice.getNoticeType() != null) cmbType.setSelectedItem(editNotice.getNoticeType());
            if (editNotice.getTargetRole() != null) cmbTargetRole.setSelectedItem(editNotice.getTargetRole());
            cmbDepartment.setSelectedIndex(editNotice.getDepartmentId() != null ? editNotice.getDepartmentId() : 0);
            if (editNotice.getStatus() != null) cmbStatus.setSelectedItem(editNotice.getStatus());
        }

        private void updateNotice() {
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

                editNotice.setTitle(title);
                editNotice.setContent(content);
                editNotice.setNoticeType((String) cmbType.getSelectedItem());
                editNotice.setTargetRole(selectedRole);
                editNotice.setDepartmentId(deptId);
                editNotice.setStatus((String) cmbStatus.getSelectedItem());

                boolean success = noticeDAO.updateNotice(editNotice);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Notice updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    if (onSuccess != null) onSuccess.run();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update notice in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating notice: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
