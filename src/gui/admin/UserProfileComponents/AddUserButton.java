package gui.admin.UserProfileComponents;

import dao.admin.AdminUserDAO;
import gui.admin.AdminTheme;
import model.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Date;

/**
 * AddUserButton - Single component file containing the Register New User action button
 * AND its on-click registration modal dialog window.
 *
 * All theme colors and typography are retrieved directly from AdminTheme.
 */
public class AddUserButton extends JButton {

    public AddUserButton(Component parentComponent, AdminUserDAO userDAO, Runnable onSuccess) {
        super("➕ Register New User");
        setFont(AdminTheme.font(Font.BOLD, 15));
        setBackground(AdminTheme.FOREST_GREEN);
        setForeground(AdminTheme.WHITE);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        addActionListener(e -> {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(parentComponent != null ? parentComponent : this);
            RegisterUserDialog dialog = new RegisterUserDialog(parentFrame, userDAO, onSuccess);
            dialog.setVisible(true);
        });
    }

    public AddUserButton(Runnable onAdd) {
        this(null, null, onAdd);
    }

    /**
     * Modal Registration Form Dialog
     */
    public static class RegisterUserDialog extends JDialog {

        private final AdminUserDAO userDAO;
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

        public RegisterUserDialog(Frame parent, AdminUserDAO dao, Runnable onSuccess) {
            super(parent, "Register New User Profile", true);
            this.userDAO = dao;
            this.onSuccess = onSuccess;

            setSize(940, 650);
            setLocationRelativeTo(parent);
            setResizable(false);
            getContentPane().setBackground(AdminTheme.MINT);
            setLayout(new BorderLayout());

            initUI();
        }

        private void initUI() {
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
            headerPanel.setBackground(AdminTheme.DEEP_GREEN);

            JLabel lblHeader = new JLabel("Register New Faculty User Account");
            lblHeader.setFont(AdminTheme.font(Font.BOLD, 18));
            lblHeader.setForeground(AdminTheme.WHITE);
            headerPanel.add(lblHeader);

            add(headerPanel, BorderLayout.NORTH);

            JPanel mainContainer = new JPanel();
            mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
            mainContainer.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
            mainContainer.setBackground(AdminTheme.MINT);

            // Section 1: Base Credentials
            JPanel pnlBase = new JPanel(new GridBagLayout());
            pnlBase.setBackground(AdminTheme.WHITE);
            pnlBase.setOpaque(true);
            pnlBase.setBorder(createTitledSectionBorder("Base Credentials & Contact Info"));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 8, 4, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Row 0: Role & Username
            addGridRow(pnlBase, gbc, 0, "System Role *:", cmbRole = new JComboBox<>(new String[]{"Undergraduate", "Lecturer", "Technical Officer", "Admin"}),
                    "Username *:", txtUsername = createStyledTextField());
            cmbRole.setFont(AdminTheme.font(Font.PLAIN, 15));
            cmbRole.setBackground(AdminTheme.WHITE);
            cmbRole.addActionListener(e -> switchRoleCard());

            // Row 1: Password & Full Name
            addGridRow(pnlBase, gbc, 1, "Password *:", txtPassword = new JPasswordField(),
                    "Full Name *:", txtFullName = createStyledTextField());
            txtPassword.setFont(AdminTheme.font(Font.PLAIN, 15));
            txtPassword.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));

            // Row 2: NIC & DOB
            addGridRow(pnlBase, gbc, 2, "NIC Number:", txtNic = createStyledTextField(),
                    "Date of Birth (YYYY-MM-DD):", txtDob = createStyledTextField());

            // Row 3: Gender & Email
            addGridRow(pnlBase, gbc, 3, "Gender:", cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"}),
                    "Email Address:", txtEmail = createStyledTextField());
            cmbGender.setFont(AdminTheme.font(Font.PLAIN, 15));
            cmbGender.setBackground(AdminTheme.WHITE);

            // Row 4: Contact No & Department
            addGridRow(pnlBase, gbc, 4, "Contact Phone:", txtContactNo = createStyledTextField(),
                    "Department:", cmbDepartment = new JComboBox<>(new String[]{"1 - ICT", "2 - Engineering Tech", "3 - Bio Systems Tech"}));
            cmbDepartment.setFont(AdminTheme.font(Font.PLAIN, 15));
            cmbDepartment.setBackground(AdminTheme.WHITE);

            // Row 5: Account Status & Address
            addGridRow(pnlBase, gbc, 5, "Account Status:", cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"}),
                    "Residential Address:", txtAddress = createStyledTextField());
            cmbStatus.setFont(AdminTheme.font(Font.PLAIN, 15));
            cmbStatus.setBackground(AdminTheme.WHITE);

            mainContainer.add(pnlBase);
            mainContainer.add(Box.createVerticalStrut(10));

            // Section 2: Role Specific Details Panel
            roleCardLayout = new CardLayout();
            roleCardPanel = new JPanel(roleCardLayout);
            roleCardPanel.setBackground(AdminTheme.WHITE);
            roleCardPanel.setOpaque(true);
            roleCardPanel.setBorder(createTitledSectionBorder("Role Specific Information"));

            roleCardPanel.add(createUndergraduatePanel(), "Undergraduate");
            roleCardPanel.add(createLecturerPanel(), "Lecturer");
            roleCardPanel.add(createTechnicalOfficerPanel(), "Technical Officer");
            roleCardPanel.add(createAdminPanel(), "Admin");

            mainContainer.add(roleCardPanel);

            JScrollPane scrollPane = new JScrollPane(mainContainer);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);

            add(scrollPane, BorderLayout.CENTER);

            // Bottom Buttons Panel
            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
            footerPanel.setBackground(AdminTheme.WHITE);
            footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AdminTheme.PASTEL_GREEN));

            JButton btnCancel = new JButton("Cancel");
            btnCancel.setFont(AdminTheme.font(Font.BOLD, 15));
            btnCancel.setBackground(AdminTheme.PASTEL_GREEN);
            btnCancel.setForeground(AdminTheme.DEEP_GREEN);
            btnCancel.setFocusPainted(false);
            btnCancel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            btnCancel.addActionListener(e -> dispose());

            JButton btnSave = new JButton("Register User Profile");
            btnSave.setFont(AdminTheme.font(Font.BOLD, 15));
            btnSave.setBackground(AdminTheme.FOREST_GREEN);
            btnSave.setForeground(AdminTheme.WHITE);
            btnSave.setFocusPainted(false);
            btnSave.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            btnSave.addActionListener(e -> saveUser());

            footerPanel.add(btnCancel);
            footerPanel.add(btnSave);

            add(footerPanel, BorderLayout.SOUTH);
        }

        private void switchRoleCard() {
            String selectedRole = (String) cmbRole.getSelectedItem();
            if (selectedRole != null) {
                roleCardLayout.show(roleCardPanel, selectedRole);
            }
        }

        private JPanel createUndergraduatePanel() {
            JPanel p = new JPanel(new GridBagLayout());
            p.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 8, 3, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            addGridRow(p, gbc, 0, "Registration No *:", txtRegNo = createStyledTextField(),
                    "Index No:", txtIndexNo = createStyledTextField());
            addGridRow(p, gbc, 1, "Batch Year:", txtBatchYear = createStyledTextField(),
                    "Academic Year:", txtAcademicYear = createStyledTextField());

            cmbStudentType = new JComboBox<>(new String[]{"Regular", "Repeat", "Direct Entry"});
            cmbStudentType.setFont(AdminTheme.font(Font.PLAIN, 15));
            cmbStudentType.setBackground(AdminTheme.WHITE);

            cmbLevel = new JComboBox<>(new Integer[]{1, 2, 3, 4});
            cmbLevel.setFont(AdminTheme.font(Font.PLAIN, 15));
            cmbLevel.setBackground(AdminTheme.WHITE);

            addGridRow(p, gbc, 2, "Student Type:", cmbStudentType, "Current Level:", cmbLevel);

            cmbSemester = new JComboBox<>(new Integer[]{1, 2});
            cmbSemester.setFont(AdminTheme.font(Font.PLAIN, 15));
            cmbSemester.setBackground(AdminTheme.WHITE);

            addGridRow(p, gbc, 3, "Current Semester:", cmbSemester, "Guardian Name:", txtGuardianName = createStyledTextField());
            addGridRow(p, gbc, 4, "Guardian Contact:", txtGuardianContact = createStyledTextField(), "", new JLabel());

            return p;
        }

        private JPanel createLecturerPanel() {
            JPanel p = new JPanel(new GridBagLayout());
            p.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 8, 3, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            cmbLecturerTitle = new JComboBox<>(new String[]{"Dr.", "Prof.", "Mr.", "Mrs.", "Ms."});
            cmbLecturerTitle.setFont(AdminTheme.font(Font.PLAIN, 15));
            cmbLecturerTitle.setBackground(AdminTheme.WHITE);

            addGridRow(p, gbc, 0, "Lecturer Code *:", txtLecturerCode = createStyledTextField(),
                    "Academic Title:", cmbLecturerTitle);
            addGridRow(p, gbc, 1, "Specialization:", txtSpecialization = createStyledTextField(), "", new JLabel());

            return p;
        }

        private JPanel createTechnicalOfficerPanel() {
            JPanel p = new JPanel(new GridBagLayout());
            p.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 8, 3, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            addGridRow(p, gbc, 0, "TO Code *:", txtToCode = createStyledTextField(),
                    "Designation:", txtToDesignation = createStyledTextField());
            addGridRow(p, gbc, 1, "Assigned Labs:", txtAssignedLabs = createStyledTextField(), "", new JLabel());

            return p;
        }

        private JPanel createAdminPanel() {
            JPanel p = new JPanel(new GridBagLayout());
            p.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 8, 3, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            addGridRow(p, gbc, 0, "Admin Code *:", txtAdminCode = createStyledTextField(),
                    "Designation:", txtAdminDesignation = createStyledTextField());

            return p;
        }

        private void addGridRow(JPanel panel, GridBagConstraints gbc, int row, String lbl1Text, Component comp1, String lbl2Text, Component comp2) {
            gbc.gridy = row;

            gbc.gridx = 0; gbc.weightx = 0.15;
            JLabel l1 = new JLabel(lbl1Text);
            l1.setFont(AdminTheme.font(Font.BOLD, 14));
            l1.setForeground(AdminTheme.DEEP_GREEN);
            panel.add(l1, gbc);

            gbc.gridx = 1; gbc.weightx = 0.35;
            panel.add(comp1, gbc);

            if (!lbl2Text.isEmpty()) {
                gbc.gridx = 2; gbc.weightx = 0.15;
                JLabel l2 = new JLabel(lbl2Text);
                l2.setFont(AdminTheme.font(Font.BOLD, 14));
                l2.setForeground(AdminTheme.DEEP_GREEN);
                panel.add(l2, gbc);

                gbc.gridx = 3; gbc.weightx = 0.35;
                panel.add(comp2, gbc);
            }
        }

        private JTextField createStyledTextField() {
            JTextField tf = new JTextField();
            tf.setFont(AdminTheme.font(Font.PLAIN, 15));
            tf.setBackground(AdminTheme.WHITE);
            tf.setForeground(AdminTheme.BLACK);
            tf.setCaretColor(AdminTheme.BLACK);
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            return tf;
        }

        private Border createTitledSectionBorder(String title) {
            TitledBorder tb = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                    " " + title + " ",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    AdminTheme.font(Font.BOLD, 15),
                    AdminTheme.DEEP_GREEN
            );
            return BorderFactory.createCompoundBorder(tb, BorderFactory.createEmptyBorder(6, 12, 10, 12));
        }

        private void saveUser() {
            try {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();
                String fullName = txtFullName.getText().trim();

                if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in all required fields (Username, Password, Full Name).", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Date dob = null;
                String dobText = txtDob.getText().trim();
                if (!dobText.isEmpty()) {
                    try { dob = Date.valueOf(dobText); }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Invalid Date format. Use YYYY-MM-DD.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                int deptId = cmbDepartment.getSelectedIndex() + 1;
                String role = (String) cmbRole.getSelectedItem();

                if (userDAO != null) {
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
                }

                JOptionPane.showMessageDialog(this, "User profile registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

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
}
