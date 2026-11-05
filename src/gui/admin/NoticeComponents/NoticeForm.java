package gui.admin.NoticeComponents;

import dao.admin.AdminNoticeDAO;
import model.Notice;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class NoticeForm extends JDialog {

    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color WHITE        = Color.WHITE;
    private final Color BLACK        = Color.BLACK;

    private AdminNoticeDAO noticeDAO;
    private Notice editNotice;
    private long adminUserId;
    private Runnable onSuccess;

    private JTextField txtTitle;
    private JTextArea txtContent;
    private JComboBox<String> cmbType;
    private JComboBox<String> cmbTargetRole;
    private JComboBox<String> cmbDepartment;
    private JComboBox<String> cmbStatus;

    private JSpinner spnPublishedAt;
    private JSpinner spnExpiresAt;
    private JCheckBox chkEnableExpiry;

    public NoticeForm(Frame parent, AdminNoticeDAO dao, Notice noticeToEdit, Runnable onSuccess) {
        this(parent, dao, noticeToEdit, 1L, onSuccess);
    }

    public NoticeForm(Frame parent, AdminNoticeDAO dao, Notice noticeToEdit, long adminUserId, Runnable onSuccess) {
        super(parent, (noticeToEdit == null ? "Post New Notice Announcement" : "Edit Notice Announcement"), true);
        this.noticeDAO = dao;
        this.editNotice = noticeToEdit;
        this.adminUserId = adminUserId;
        this.onSuccess = onSuccess;

        setSize(740, 740);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(new Color(0xE8, 0xF5, 0xE9));
        setLayout(new BorderLayout());

        initUI();

        if (editNotice != null) {
            populateForm();
        }
    }

    private void initUI() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 16));
        headerPanel.setBackground(DEEP_GREEN);

        JLabel lblHeader = new JLabel(editNotice == null ? "Post New Official Notice" : "Modify Notice Announcement");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(WHITE);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(WHITE);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25;
        pnlForm.add(createLabel("Notice Title:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.75;
        txtTitle = createTextField("");
        pnlForm.add(txtTitle, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.25;
        pnlForm.add(createLabel("Content / Body:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.75;
        txtContent = new JTextArea(4, 20);
        txtContent.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        txtContent.setBackground(WHITE);
        txtContent.setForeground(BLACK);
        JScrollPane spContent = new JScrollPane(txtContent);
        spContent.setBorder(BorderFactory.createLineBorder(PASTEL_GREEN, 1));
        pnlForm.add(spContent, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        pnlForm.add(createLabel("Notice Type:"), gbc);
        gbc.gridx = 1;
        cmbType = new JComboBox<>(new String[]{ "General", "Academic", "Exam", "Attendance", "Event", "Emergency" });
        styleCombo(cmbType);
        pnlForm.add(cmbType, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        pnlForm.add(createLabel("Target Role:"), gbc);
        gbc.gridx = 1;
        cmbTargetRole = new JComboBox<>(new String[]{ "All", "Admin", "Lecturer", "Technical Officer", "Undergraduate" });
        styleCombo(cmbTargetRole);
        pnlForm.add(cmbTargetRole, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        pnlForm.add(createLabel("Target Department:"), gbc);
        gbc.gridx = 1;
        cmbDepartment = new JComboBox<>(new String[]{ "Faculty-Wide (All Depts)", "1 - ICT", "2 - Engineering Technology", "3 - Bio Systems Technology" });
        styleCombo(cmbDepartment);
        pnlForm.add(cmbDepartment, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        pnlForm.add(createLabel("Published Date:"), gbc);
        gbc.gridx = 1;
        spnPublishedAt = createDateSpinner(new Date());
        pnlForm.add(spnPublishedAt, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        pnlForm.add(createLabel("Expire Date:"), gbc);
        gbc.gridx = 1;
        JPanel pnlExpiry = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlExpiry.setBackground(WHITE);
        chkEnableExpiry = new JCheckBox("Set Expiry Date");
        chkEnableExpiry.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkEnableExpiry.setBackground(WHITE);
        chkEnableExpiry.setForeground(BLACK);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        spnExpiresAt = createDateSpinner(cal.getTime());
        spnExpiresAt.setEnabled(false);

        chkEnableExpiry.addActionListener(e -> spnExpiresAt.setEnabled(chkEnableExpiry.isSelected()));

        pnlExpiry.add(chkEnableExpiry);
        pnlExpiry.add(spnExpiresAt);
        pnlForm.add(pnlExpiry, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        pnlForm.add(createLabel("Notice Status:"), gbc);
        gbc.gridx = 1;
        cmbStatus = new JComboBox<>(new String[]{ "Published", "Draft", "Archived" });
        styleCombo(cmbStatus);
        pnlForm.add(cmbStatus, gbc);

        add(pnlForm, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        footerPanel.setBackground(WHITE);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, PASTEL_GREEN));

        JButton btnSave = new JButton(editNotice == null ? "Post Notice" : "Update Announcement");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSave.setBackground(FOREST_GREEN);
        btnSave.setForeground(WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btnSave.addActionListener(e -> saveNotice());

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCancel.setBackground(PASTEL_GREEN);
        btnCancel.setForeground(DEEP_GREEN);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btnCancel.addActionListener(e -> dispose());

        footerPanel.add(btnSave);
        footerPanel.add(btnCancel);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(BLACK);
        return l;
    }

    private JTextField createTextField(String val) {
        JTextField tf = new JTextField(val);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tf.setBackground(WHITE);
        tf.setForeground(BLACK);
        tf.setCaretColor(BLACK);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    private JSpinner createDateSpinner(Date defaultDate) {
        SpinnerDateModel model = new SpinnerDateModel(defaultDate, null, null, Calendar.MINUTE);
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd HH:mm");
        spinner.setEditor(editor);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        spinner.setPreferredSize(new Dimension(220, 34));
        return spinner;
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cb.setBackground(WHITE);
        cb.setForeground(BLACK);
    }

    private void populateForm() {
        txtTitle.setText(editNotice.getTitle());
        txtContent.setText(editNotice.getContent());
        if (editNotice.getNoticeType() != null) cmbType.setSelectedItem(editNotice.getNoticeType());
        if (editNotice.getTargetRole() != null) cmbTargetRole.setSelectedItem(editNotice.getTargetRole());
        cmbDepartment.setSelectedIndex(editNotice.getDepartmentId() != null ? editNotice.getDepartmentId() : 0);
        if (editNotice.getStatus() != null) cmbStatus.setSelectedItem(editNotice.getStatus());

        if (editNotice.getPublishedAt() != null) {
            spnPublishedAt.setValue(new Date(editNotice.getPublishedAt().getTime()));
        }

        if (editNotice.getExpiresAt() != null) {
            chkEnableExpiry.setSelected(true);
            spnExpiresAt.setEnabled(true);
            spnExpiresAt.setValue(new Date(editNotice.getExpiresAt().getTime()));
        } else {
            chkEnableExpiry.setSelected(false);
            spnExpiresAt.setEnabled(false);
        }
    }

    private void saveNotice() {
        try {
            String title = txtTitle.getText().trim();
            String content = txtContent.getText().trim();
            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Notice Title and Content are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Integer deptId = cmbDepartment.getSelectedIndex() == 0 ? null : cmbDepartment.getSelectedIndex();

            Date pubDate = (Date) spnPublishedAt.getValue();
            Timestamp publishedAt = new Timestamp(pubDate.getTime());

            Timestamp expiresAt = null;
            if (chkEnableExpiry.isSelected()) {
                Date expDate = (Date) spnExpiresAt.getValue();
                expiresAt = new Timestamp(expDate.getTime());

                if (expiresAt.before(publishedAt)) {
                    JOptionPane.showMessageDialog(this, "Expire Date cannot be earlier than Published Date!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (editNotice == null) {
                Notice n = new Notice();
                n.setTitle(title);
                n.setContent(content);
                n.setNoticeType((String) cmbType.getSelectedItem());
                n.setTargetRole((String) cmbTargetRole.getSelectedItem());
                n.setDepartmentId(deptId);
                n.setPublishedBy(adminUserId);
                n.setPublishedAt(publishedAt);
                n.setExpiresAt(expiresAt);
                n.setStatus((String) cmbStatus.getSelectedItem());

                noticeDAO.createNotice(n);
                JOptionPane.showMessageDialog(this, "Notice published successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                editNotice.setTitle(title);
                editNotice.setContent(content);
                editNotice.setNoticeType((String) cmbType.getSelectedItem());
                editNotice.setTargetRole((String) cmbTargetRole.getSelectedItem());
                editNotice.setDepartmentId(deptId);
                editNotice.setPublishedAt(publishedAt);
                editNotice.setExpiresAt(expiresAt);
                editNotice.setStatus((String) cmbStatus.getSelectedItem());

                noticeDAO.updateNotice(editNotice);
                JOptionPane.showMessageDialog(this, "Notice updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving notice: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}