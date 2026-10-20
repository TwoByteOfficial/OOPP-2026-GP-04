package gui.admin.UserProfileComponents;

import dao.admin.AdminUserDAO;
import model.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;

/**
 * UserProfileForm - Re-organized & Theme-Protected Modal Dialog.
 * Enforces explicit White/Mint background colors and crisp Black foreground text colors
 * across all input components, drop-downs, labels, and panels to prevent OS Dark Theme
 * text contrast artifacts and gray backgrounds.
 */
public class UserProfileForm extends JDialog {

    /* ---- Color Theme Constants ---- */
    private final Color MINT          = new Color(0xE8, 0xF5, 0xE9);
    private final Color PASTEL_GREEN  = new Color(0xC8, 0xE6, 0xC9);
    private final Color FOREST_GREEN  = new Color(0x38, 0x8E, 0x3C);
    private final Color DEEP_GREEN    = new Color(0x1B, 0x5E, 0x20);
    private final Color WHITE         = MINT;
    private final Color BLACK         = new Color(0, 0, 0);

    private final AdminUserDAO userDAO;
    private final User editUser; // null if registering new user
    private final Runnable onSuccess;

    /* ---- Form Base Components ---- */
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

    /* ---- Dynamic Role Panel Components ---- */
    private CardLayout roleCardLayout;
    private JPanel roleCardPanel;

    // Admin Specific Fields
    private JTextField txtAdminCode;
    private JTextField txtAdminDesignation;

    // Lecturer Specific Fields
    private JTextField txtLecturerCode;
    private JComboBox<String> cmbLecturerTitle;
    private JTextField txtSpecialization;

    // Technical Officer Specific Fields
    private JTextField txtToCode;
    private JTextField txtToDesignation;
    private JTextField txtAssignedLabs;

    // Undergraduate Specific Fields
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

        setSize(880, 750);
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
        /* ========================================================================== */
        /*  HEADER PANEL                                                              */
        /* ========================================================================== */
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 16));
        headerPanel.setBackground(DEEP_GREEN);

        JLabel lblHeader = new JLabel(editUser == null ? "Register New Faculty User Account" : "Modify User Account Details");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22)); // 20 -> 22
        lblHeader.setForeground(WHITE);
        headerPanel.add(lblHeader);

        add(headerPanel, BorderLayout.NORTH);

        /* ========================================================================== */
        /*  SCROLLABLE CONTENT CONTAINER                                              */
        /* ========================================================================== */
        JPanel mainScrollContainer = new JPanel();
        mainScrollContainer.setLayout(new BoxLayout(mainScrollContainer, BoxLayout.Y_AXIS));
        mainScrollContainer.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        mainScrollContainer.setBackground(MINT);

        /* -------------------------------------------------------------------------- */
        /*  SECTION 1: BASE CREDENTIALS & CONTACT INFORMATION                         */
        /* -------------------------------------------------------------------------- */
        JPanel pnlBase = new JPanel(new GridLayout(6, 4, 14, 12));
        pnlBase.setBackground(WHITE);
        pnlBase.setOpaque(true);
        pnlBase.setBorder(createTitledSectionBorder("Base Credentials & Contact Info"));

        // Row 1: Role & Username
        pnlBase.add(createLabel("Role:"));
        cmbRole = createComboBox(new String[]{ "Undergraduate", "Lecturer", "Technical Officer", "Admin" });
        if (editUser != null) {
            cmbRole.setEnabled(false); // Locked during edit mode
        }
        cmbRole.addActionListener(e -> switchRoleCard((String) cmbRole.getSelectedItem()));
        pnlBase.add(cmbRole);

        pnlBase.add(createLabel("Username:"));
        txtUsername = createTextField("");
        if (editUser != null) {
            txtUsername.setEditable(false);
            txtUsername.setBackground(MINT);
        }
        pnlBase.add(txtUsername);

        // Row 2: Password & Full Name
        pnlBase.add(createLabel("Password:"));
        txtPassword = createPasswordField();
        if (editUser != null) {
            txtPassword.setToolTipText("Leave blank to retain current password");
        }
        pnlBase.add(txtPassword);

        pnlBase.add(createLabel("Full Name:"));
        txtFullName = createTextField("");
        pnlBase.add(txtFullName);

        // Row 3: NIC & DOB
        pnlBase.add(createLabel("NIC No:"));
        txtNic = createTextField("");
        pnlBase.add(txtNic);

        pnlBase.add(createLabel("Date of Birth (YYYY-MM-DD):"));
        txtDob = createTextField("2000-01-01");
        pnlBase.add(txtDob);

        // Row 4: Gender & Email
        pnlBase.add(createLabel("Gender:"));
        cmbGender = createComboBox(new String[]{ "Male", "Female", "Other" });
        pnlBase.add(cmbGender);

        pnlBase.add(createLabel("Email:"));
        txtEmail = createTextField("");
        pnlBase.add(txtEmail);

        // Row 5: Contact No & Address
        pnlBase.add(createLabel("Contact No:"));
        txtContactNo = createTextField("");
        pnlBase.add(txtContactNo);

        pnlBase.add(createLabel("Address:"));
        txtAddress = createTextField("");
        pnlBase.add(txtAddress);

        // Row 6: Department & Status
        pnlBase.add(createLabel("Department:"));
        cmbDepartment = createComboBox(new String[]{ "1 - ICT", "2 - Engineering Technology", "3 - Bio Systems Technology" });
        pnlBase.add(cmbDepartment);

        pnlBase.add(createLabel("Account Status:"));
        cmbStatus = createComboBox(new String[]{ "Active", "Inactive" });
        pnlBase.add(cmbStatus);

        mainScrollContainer.add(pnlBase);
        mainScrollContainer.add(Box.createVerticalStrut(16));

        /* -------------------------------------------------------------------------- */
        /*  SECTION 2: DYNAMIC ROLE-SPECIFIC INFORMATION CARDS                        */
        /* -------------------------------------------------------------------------- */
        roleCardLayout = new CardLayout();
        roleCardPanel = new JPanel(roleCardLayout);
        roleCardPanel.setBackground(WHITE);
        roleCardPanel.setOpaque(true);
        roleCardPanel.setBorder(createTitledSectionBorder("Role Specific Information"));

        // --- Admin Card ---
        JPanel pnlAdmin = new JPanel(new GridLayout(2, 2, 14, 12));
        pnlAdmin.setBackground(WHITE);
        pnlAdmin.setOpaque(true);
        pnlAdmin.add(createLabel("Admin Code:"));
        txtAdminCode = createTextField("ADM-001");
        pnlAdmin.add(txtAdminCode);

        pnlAdmin.add(createLabel("Designation:"));
        txtAdminDesignation = createTextField("System Administrator");
        pnlAdmin.add(txtAdminDesignation);
        roleCardPanel.add(pnlAdmin, "Admin");

        // --- Lecturer Card ---
        JPanel pnlLecturer = new JPanel(new GridLayout(3, 2, 14, 12));
        pnlLecturer.setBackground(WHITE);
        pnlLecturer.setOpaque(true);
        pnlLecturer.add(createLabel("Lecturer Code:"));
        txtLecturerCode = createTextField("LEC-001");
        pnlLecturer.add(txtLecturerCode);

        pnlLecturer.add(createLabel("Title:"));
        cmbLecturerTitle = createComboBox(new String[]{ "Dr.", "Prof.", "Mr.", "Ms." });
        pnlLecturer.add(cmbLecturerTitle);

        pnlLecturer.add(createLabel("Specialization:"));
        txtSpecialization = createTextField("Software Engineering");
        pnlLecturer.add(txtSpecialization);
        roleCardPanel.add(pnlLecturer, "Lecturer");

        // --- Technical Officer Card ---
        JPanel pnlTO = new JPanel(new GridLayout(3, 2, 14, 12));
        pnlTO.setBackground(WHITE);
        pnlTO.setOpaque(true);
        pnlTO.add(createLabel("TO Code:"));
        txtToCode = createTextField("TO-001");
        pnlTO.add(txtToCode);

        pnlTO.add(createLabel("Designation:"));
        txtToDesignation = createTextField("Senior Technical Officer");
        pnlTO.add(txtToDesignation);

        pnlTO.add(createLabel("Assigned Labs:"));
        txtAssignedLabs = createTextField("Software Lab 1, Network Lab");
        pnlTO.add(txtAssignedLabs);
        roleCardPanel.add(pnlTO, "Technical Officer");

        // --- Undergraduate Card ---
        JPanel pnlUG = new JPanel(new GridLayout(6, 2, 14, 10));
        pnlUG.setBackground(WHITE);
        pnlUG.setOpaque(true);
        pnlUG.add(createLabel("Registration No:"));
        txtRegNo = createTextField("TG/2024/001");
        pnlUG.add(txtRegNo);

        pnlUG.add(createLabel("Index No:"));
        txtIndexNo = createTextField("TG001");
        pnlUG.add(txtIndexNo);

        pnlUG.add(createLabel("Batch Year:"));
        txtBatchYear = createTextField("2024");
        pnlUG.add(txtBatchYear);

        pnlUG.add(createLabel("Academic Year:"));
        txtAcademicYear = createTextField("2024/2025");
        pnlUG.add(txtAcademicYear);

        pnlUG.add(createLabel("Student Type / Intake:"));
        cmbStudentType = createComboBox(new String[]{ "Regular", "Repeat", "Direct Intake" });
        pnlUG.add(cmbStudentType);

        pnlUG.add(createLabel("Current Level & Semester:"));
        JPanel pnlLvlSem = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlLvlSem.setBackground(WHITE);
        pnlLvlSem.setOpaque(true);
        pnlLvlSem.add(createLabel("Lvl:"));
        cmbLevel = createComboBox(new Integer[]{ 1, 2, 3, 4 });
        pnlLvlSem.add(cmbLevel);
        pnlLvlSem.add(createLabel("Sem:"));
        cmbSemester = createComboBox(new Integer[]{ 1, 2 });
        pnlLvlSem.add(cmbSemester);
        pnlUG.add(pnlLvlSem);

        pnlUG.add(createLabel("Guardian Name:"));
        txtGuardianName = createTextField("");
        pnlUG.add(txtGuardianName);

        pnlUG.add(createLabel("Guardian Contact:"));
        txtGuardianContact = createTextField("");
        pnlUG.add(txtGuardianContact);
        roleCardPanel.add(pnlUG, "Undergraduate");

        mainScrollContainer.add(roleCardPanel);

        // Wrap Container in Scroll Pane
        JScrollPane scrollPane = new JScrollPane(mainScrollContainer);
        scrollPane.getViewport().setBackground(MINT);
        scrollPane.setBackground(MINT);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        /* ========================================================================== */
        /*  FOOTER PANEL WITH ACTION BUTTONS                                          */
        /* ========================================================================== */
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        footerPanel.setBackground(WHITE);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, PASTEL_GREEN));

        JButton btnSave = new JButton(editUser == null ? "Save Account" : "Update Profile");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 16)); // 14 -> 16
        btnSave.setBackground(FOREST_GREEN);
        btnSave.setForeground(WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> saveUserAccount());

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // 14 -> 16
        btnCancel.setBackground(PASTEL_GREEN);
        btnCancel.setForeground(DEEP_GREEN);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());

        footerPanel.add(btnSave);
        footerPanel.add(btnCancel);
        add(footerPanel, BorderLayout.SOUTH);

        switchRoleCard("Undergraduate");
    }

    /* ============================================================================== */
    /*  THEME STYLING HELPER FACTORIES                                                */
    /* ============================================================================== */

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15)); // 13 -> 15
        lbl.setForeground(BLACK);
        return lbl;
    }

    private JTextField createTextField(String initialValue) {
        JTextField tf = new JTextField(initialValue);
        styleInputComponent(tf);
        return tf;
    }

    private JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        styleInputComponent(pf);
        return pf;
    }

    private void styleInputComponent(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // 14 -> 16
        tf.setBackground(WHITE);
        tf.setForeground(BLACK);
        tf.setCaretColor(BLACK);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    private <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // 14 -> 16
        combo.setBackground(WHITE);
        combo.setForeground(BLACK);
        combo.setFocusable(false);

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    l.setBackground(PASTEL_GREEN);
                    l.setForeground(DEEP_GREEN);
                } else {
                    l.setBackground(WHITE);
                    l.setForeground(BLACK);
                }
                l.setOpaque(true);
                l.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // 14 -> 16
                l.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return l;
            }
        });

        return combo;
    }

    private Border createTitledSectionBorder(String title) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                " " + title + " ",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), // 14 -> 16
                DEEP_GREEN
        );
        return BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(12, 16, 16, 16)
        );
    }

    /* ============================================================================== */
    /*  BUSINESS LOGIC & EVENT HANDLERS                                              */
    /* ============================================================================== */

    private void switchRoleCard(String roleName) {
        if (roleCardLayout != null && roleCardPanel != null) {
            roleCardLayout.show(roleCardPanel, roleName);
        }
    }

    private void populateFormForEdit() {
        txtUsername.setText(editUser.getUsername());
        txtFullName.setText(editUser.getFullName());
        txtNic.setText(editUser.getNic());
        if (editUser.getDateOfBirth() != null) {
            txtDob.setText(editUser.getDateOfBirth().toString());
        }
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
            try {
                dob = Date.valueOf(txtDob.getText().trim());
            } catch (Exception parseEx) {
                dob = Date.valueOf("2000-01-01");
            }

            int deptId = cmbDepartment.getSelectedIndex() + 1;

            if (editUser == null) {
                if (password.isEmpty()) {
                    password = "Password123";
                }

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
                    try {
                        ug.setBatchYear(Integer.parseInt(txtBatchYear.getText().trim()));
                    } catch (Exception ignored) {
                        ug.setBatchYear(2024);
                    }
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

            if (onSuccess != null) {
                onSuccess.run();
            }
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
