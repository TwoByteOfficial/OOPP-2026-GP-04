package gui.admin.UserProfileComponents;

import dao.admin.AdminUserDAO;
import model.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Date;

/**
 * UserProfileForm - Compact & Neatly Aligned Modal Dialog.
 * Uses GridBagLayout to align labels and text fields precisely in 2 columns.
 * Size: 780px x 600px.
 */
public class UserProfileForm extends JDialog {

    private final Color MINT          = new Color(0xE8, 0xF5, 0xE9);
    private final Color PASTEL_GREEN  = new Color(0xC8, 0xE6, 0xC9);
    private final Color FOREST_GREEN  = new Color(0x38, 0x8E, 0x3C);
    private final Color DEEP_GREEN    = new Color(0x1B, 0x5E, 0x20);
    private final Color WHITE         = Color.WHITE;
    private final Color BLACK         = Color.BLACK;

    private final AdminUserDAO userDAO;
    private final User editUser;
    private final Runnable onSuccess;

    private JComboBox<String> cmbRole;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtFullName;
    private JTextField txtNic;
    private JTextField txtDob;
    private JComboBox<String> cmbGender;
    private JTextField txtEmail;
    private JTextField txtContactNo;
    private JTextField txtAddress;
    private JComboBox<String> cmbDepartment;
    private JComboBox<String> cmbStatus;

    private CardLayout roleCardLayout;
    private JPanel roleCardPanel;

    // Admin
    private JTextField txtAdminCode;
    private JTextField txtAdminDesignation;

    // Lecturer
    private JTextField txtLecturerCode;
    private JComboBox<String> cmbLecturerTitle;
    private JTextField txtSpecialization;

    // Technical Officer
    private JTextField txtToCode;
    private JTextField txtToDesignation;
    private JTextField txtAssignedLabs;

    // Undergraduate
    private JTextField txtRegNo;
    private JTextField txtIndexNo;
    private JTextField txtBatchYear;
    private JTextField txtAcademicYear;
    private JComboBox<String> cmbStudentType;
    private JComboBox<Integer> cmbLevel;
    private JComboBox<Integer> cmbSemester;
    private JTextField txtGuardianName;
    private JTextField txtGuardianContact;

    public UserProfileForm(Frame parent, AdminUserDAO dao, User userToEdit, Runnable onSuccess) {
        super(parent, (userToEdit == null ? "Register New User Profile" : "Edit User Profile"), true);
        this.userDAO = dao;
        this.editUser = userToEdit;
        this.onSuccess = onSuccess;

        setSize(940, 650);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(MINT);
        setLayout(new BorderLayout());

        initUI();

        if (editUser != null) {
            populateFormForEdit();
        }
    }

    private void initUI() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        headerPanel.setBackground(DEEP_GREEN);

        JLabel lblHeader = new JLabel(editUser == null ? "Register New Faculty User Account" : "Modify User Account Details");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(WHITE);
        headerPanel.add(lblHeader);

        add(headerPanel, BorderLayout.NORTH);

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        mainContainer.setBackground(MINT);

        // Section 1: Base Credentials
        JPanel pnlBase = new JPanel(new GridBagLayout());
        pnlBase.setBackground(WHITE);
        pnlBase.setOpaque(true);
        pnlBase.setBorder(createTitledSectionBorder("Base Credentials & Contact Info"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Role & Username
        addGridRow(pnlBase, gbc, 0, "Role:", cmbRole = createComboBox(new String[]{ "Undergraduate", "Lecturer", "Technical Officer", "Admin" }), "Username:", txtUsername = createTextField(""));
        if (editUser != null) cmbRole.setEnabled(false);
        cmbRole.addActionListener(e -> switchRoleCard((String) cmbRole.getSelectedItem()));
        if (editUser != null) { txtUsername.setEditable(false); txtUsername.setBackground(MINT); }

        // Row 1: Password & Full Name
        addGridRow(pnlBase, gbc, 1, "Password:", txtPassword = createPasswordField(), "Full Name:", txtFullName = createTextField(""));

        // Row 2: NIC & DOB
        addGridRow(pnlBase, gbc, 2, "NIC No:", txtNic = createTextField(""), "DOB (YYYY-MM-DD):", txtDob = createTextField("2000-01-01"));

        // Row 3: Gender & Email
        addGridRow(pnlBase, gbc, 3, "Gender:", cmbGender = createComboBox(new String[]{ "Male", "Female", "Other" }), "Email:", txtEmail = createTextField(""));

        // Row 4: Contact No & Address
        addGridRow(pnlBase, gbc, 4, "Contact No:", txtContactNo = createTextField(""), "Address:", txtAddress = createTextField(""));

        // Row 5: Department & Status
        addGridRow(pnlBase, gbc, 5, "Department:", cmbDepartment = createComboBox(new String[]{ "1 - ICT", "2 - Engineering Technology", "3 - Bio Systems Technology" }), "Account Status:", cmbStatus = createComboBox(new String[]{ "Active", "Inactive" }));

        mainContainer.add(pnlBase);
        mainContainer.add(Box.createVerticalStrut(10));

        // Section 2: Role Cards
        roleCardLayout = new CardLayout();
        roleCardPanel = new JPanel(roleCardLayout);
        roleCardPanel.setBackground(WHITE);
        roleCardPanel.setOpaque(true);
        roleCardPanel.setBorder(createTitledSectionBorder("Role Specific Information"));

        // Admin Card
        JPanel pnlAdmin = new JPanel(new GridBagLayout());
        pnlAdmin.setBackground(WHITE);
        addGridRow(pnlAdmin, gbc, 0, "Admin Code:", txtAdminCode = createTextField("ADM-001"), "Designation:", txtAdminDesignation = createTextField("System Administrator"));
        roleCardPanel.add(pnlAdmin, "Admin");

        // Lecturer Card
        JPanel pnlLecturer = new JPanel(new GridBagLayout());
        pnlLecturer.setBackground(WHITE);
        addGridRow(pnlLecturer, gbc, 0, "Lecturer Code:", txtLecturerCode = createTextField("LEC-001"), "Title:", cmbLecturerTitle = createComboBox(new String[]{ "Dr.", "Prof.", "Mr.", "Ms." }));
        addSingleGridRow(pnlLecturer, gbc, 1, "Specialization:", txtSpecialization = createTextField("Software Engineering"));
        roleCardPanel.add(pnlLecturer, "Lecturer");

        // Technical Officer Card
        JPanel pnlTO = new JPanel(new GridBagLayout());
        pnlTO.setBackground(WHITE);
        addGridRow(pnlTO, gbc, 0, "TO Code:", txtToCode = createTextField("TO-001"), "Designation:", txtToDesignation = createTextField("Senior Technical Officer"));
        addSingleGridRow(pnlTO, gbc, 1, "Assigned Labs:", txtAssignedLabs = createTextField("Software Lab 1, Network Lab"));
        roleCardPanel.add(pnlTO, "Technical Officer");

        // Undergraduate Card
        JPanel pnlUG = new JPanel(new GridBagLayout());
        pnlUG.setBackground(WHITE);
        addGridRow(pnlUG, gbc, 0, "Reg No:", txtRegNo = createTextField("TG/2024/001"), "Index No:", txtIndexNo = createTextField("TG001"));
        addGridRow(pnlUG, gbc, 1, "Batch Year:", txtBatchYear = createTextField("2024"), "Academic Year:", txtAcademicYear = createTextField("2024/2025"));
        addGridRow(pnlUG, gbc, 2, "Student Type:", cmbStudentType = createComboBox(new String[]{ "Regular", "Repeat", "Direct Intake" }), "Lvl & Sem:", createLvlSemPanel());
        addGridRow(pnlUG, gbc, 3, "Guardian Name:", txtGuardianName = createTextField(""), "Guardian Contact:", txtGuardianContact = createTextField(""));
        roleCardPanel.add(pnlUG, "Undergraduate");

        mainContainer.add(roleCardPanel);

        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.getViewport().setBackground(MINT);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        footerPanel.setBackground(WHITE);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, PASTEL_GREEN));

        JButton btnSave = new JButton(editUser == null ? "Save Account" : "Update Profile");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(FOREST_GREEN);
        btnSave.setForeground(WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> saveUserAccount());

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setBackground(PASTEL_GREEN);
        btnCancel.setForeground(DEEP_GREEN);
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> dispose());

        footerPanel.add(btnSave);
        footerPanel.add(btnCancel);
        add(footerPanel, BorderLayout.SOUTH);

        switchRoleCard("Undergraduate");
    }

    private JPanel createLvlSemPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setBackground(WHITE);
        p.add(new JLabel("Lvl:"));
        cmbLevel = createComboBox(new Integer[]{ 1, 2, 3, 4 });
        p.add(cmbLevel);
        p.add(new JLabel("Sem:"));
        cmbSemester = createComboBox(new Integer[]{ 1, 2 });
        p.add(cmbSemester);
        return p;
    }

    private void addGridRow(JPanel panel, GridBagConstraints gbc, int row, String lbl1, JComponent comp1, String lbl2, JComponent comp2) {
        gbc.gridy = row;

        gbc.gridx = 0; gbc.weightx = 0.15;
        panel.add(createLabel(lbl1), gbc);

        gbc.gridx = 1; gbc.weightx = 0.35;
        panel.add(comp1, gbc);

        gbc.gridx = 2; gbc.weightx = 0.15;
        panel.add(createLabel(lbl2), gbc);

        gbc.gridx = 3; gbc.weightx = 0.35;
        panel.add(comp2, gbc);
    }

    private void addSingleGridRow(JPanel panel, GridBagConstraints gbc, int row, String lbl, JComponent comp) {
        gbc.gridy = row;

        gbc.gridx = 0; gbc.weightx = 0.15;
        panel.add(createLabel(lbl), gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 0.85;
        panel.add(comp, gbc);

        gbc.gridwidth = 1; // reset
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(BLACK);
        return lbl;
    }

    private JTextField createTextField(String val) {
        JTextField tf = new JTextField(val);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(WHITE);
        tf.setForeground(BLACK);
        tf.setCaretColor(BLACK);
        tf.setPreferredSize(new Dimension(160, 30));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }

    private JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setBackground(WHITE);
        pf.setForeground(BLACK);
        pf.setCaretColor(BLACK);
        pf.setPreferredSize(new Dimension(160, 30));
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return pf;
    }

    private <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(WHITE);
        combo.setForeground(BLACK);
        combo.setPreferredSize(new Dimension(160, 30));
        combo.setFocusable(false);
        return combo;
    }

    private Border createTitledSectionBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                " " + title + " ",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                DEEP_GREEN
        );
        return BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(8, 12, 10, 12));
    }

    private void switchRoleCard(String roleName) {
        if (roleCardLayout != null && roleCardPanel != null) {
            roleCardLayout.show(roleCardPanel, roleName);
        }
    }

    private void populateFormForEdit() {
        txtUsername.setText(editUser.getUsername());
        txtFullName.setText(editUser.getFullName());
        txtNic.setText(editUser.getNic());
        if (editUser.getDateOfBirth() != null) txtDob.setText(editUser.getDateOfBirth().toString());
        cmbGender.setSelectedItem(editUser.getGender());
        txtEmail.setText(editUser.getEmail());
        txtContactNo.setText(editUser.getContactNo());
        txtAddress.setText(editUser.getAddress());
        cmbStatus.setSelectedItem(editUser.getUserStatus());

        cmbRole.setSelectedItem(editUser.getRole());
        switchRoleCard(editUser.getRole());

        if (editUser instanceof Admin) {
            Admin a = (Admin) editUser;
            txtAdminCode.setText(a.getAdminCode());
            txtAdminDesignation.setText(a.getDesignation());
        } else if (editUser instanceof Lecturer) {
            Lecturer l = (Lecturer) editUser;
            txtLecturerCode.setText(l.getLecturerCode());
            cmbLecturerTitle.setSelectedItem(l.getTitle());
            txtSpecialization.setText(l.getSpecialization());
        } else if (editUser instanceof TechnicalOfficer) {
            TechnicalOfficer t = (TechnicalOfficer) editUser;
            txtToCode.setText(t.getTechnicalOfficerCode());
            txtToDesignation.setText(t.getDesignation());
            txtAssignedLabs.setText(t.getAssignedLabs());
        } else if (editUser instanceof Undergraduate) {
            Undergraduate ug = (Undergraduate) editUser;
            txtRegNo.setText(ug.getRegistrationNo());
            txtIndexNo.setText(ug.getIndexNo());
            txtBatchYear.setText(String.valueOf(ug.getBatchYear()));
            txtAcademicYear.setText(ug.getAcademicYear());
            cmbStudentType.setSelectedItem(ug.getStudentType());
            cmbLevel.setSelectedItem(ug.getCurrentLevel());
            cmbSemester.setSelectedItem(ug.getCurrentSemester());
            txtGuardianName.setText(ug.getGuardianName());
            txtGuardianContact.setText(ug.getGuardianContact());
        }
    }

    private void saveUserAccount() {
        try {
            String role = (String) cmbRole.getSelectedItem();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String fullName = txtFullName.getText().trim();

            if (username.isEmpty() || fullName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and Full Name are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date dob;
            try { dob = Date.valueOf(txtDob.getText().trim()); } catch (Exception parseEx) { dob = Date.valueOf("2000-01-01"); }

            int deptId = cmbDepartment.getSelectedIndex() + 1;

            if (editUser == null) {
                if (password.isEmpty()) password = "Password123";

                if ("Admin".equalsIgnoreCase(role)) {
                    Admin a = new Admin();
                    bindBaseFields(a, username, password, fullName, dob, deptId);
                    a.setAdminCode(txtAdminCode.getText().trim());
                    a.setDesignation(txtAdminDesignation.getText().trim());
                    userDAO.createAdmin(a);

                } else if ("Lecturer".equalsIgnoreCase(role)) {
                    Lecturer l = new Lecturer();
                    bindBaseFields(l, username, password, fullName, dob, deptId);
                    l.setLecturerCode(txtLecturerCode.getText().trim());
                    l.setTitle((String) cmbLecturerTitle.getSelectedItem());
                    l.setSpecialization(txtSpecialization.getText().trim());
                    userDAO.createLecturer(l);

                } else if ("Technical Officer".equalsIgnoreCase(role)) {
                    TechnicalOfficer t = new TechnicalOfficer();
                    bindBaseFields(t, username, password, fullName, dob, deptId);
                    t.setTechnicalOfficerCode(txtToCode.getText().trim());
                    t.setDesignation(txtToDesignation.getText().trim());
                    t.setAssignedLabs(txtAssignedLabs.getText().trim());
                    userDAO.createTechnicalOfficer(t);

                } else {
                    Undergraduate ug = new Undergraduate();
                    bindBaseFields(ug, username, password, fullName, dob, deptId);
                    ug.setRegistrationNo(txtRegNo.getText().trim());
                    ug.setIndexNo(txtIndexNo.getText().trim());
                    try { ug.setBatchYear(Integer.parseInt(txtBatchYear.getText().trim())); } catch (Exception ignored) { ug.setBatchYear(2024); }
                    ug.setAcademicYear(txtAcademicYear.getText().trim());
                    ug.setStudentType((String) cmbStudentType.getSelectedItem());
                    ug.setCurrentLevel((Integer) cmbLevel.getSelectedItem());
                    ug.setCurrentSemester((Integer) cmbSemester.getSelectedItem());
                    ug.setGuardianName(txtGuardianName.getText().trim());
                    ug.setGuardianContact(txtGuardianContact.getText().trim());
                    userDAO.createUndergraduate(ug);
                }

                JOptionPane.showMessageDialog(this, "User profile registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            } else {
                editUser.setFullName(fullName);
                editUser.setNic(txtNic.getText().trim());
                editUser.setDateOfBirth(dob);
                editUser.setGender((String) cmbGender.getSelectedItem());
                editUser.setEmail(txtEmail.getText().trim());
                editUser.setContactNo(txtContactNo.getText().trim());
                editUser.setAddress(txtAddress.getText().trim());
                editUser.setUserStatus((String) cmbStatus.getSelectedItem());
                editUser.setDepartmentId(deptId);

                userDAO.updateUser(editUser);
                JOptionPane.showMessageDialog(this, "User profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            if (onSuccess != null) onSuccess.run();
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving user profile: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void bindBaseFields(User u, String username, String password, String fullName, Date dob, int deptId) {
        u.setUsername(username);
        u.setPasswordHash(password);
        u.setFullName(fullName);
        u.setNic(txtNic.getText().trim());
        u.setDateOfBirth(dob);
        u.setGender((String) cmbGender.getSelectedItem());
        u.setEmail(txtEmail.getText().trim());
        u.setContactNo(txtContactNo.getText().trim());
        u.setAddress(txtAddress.getText().trim());
        u.setDepartmentId(deptId);
        u.setUserStatus((String) cmbStatus.getSelectedItem());
    }
}
