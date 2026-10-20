package gui.admin.UserProfileComponents;

import model.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Modal dialog for displaying complete profile details of a selected user account.
 * Explicitly styled to prevent OS Dark Theme contrast artifacts.
 */
public class UserProfileDetails extends JDialog {

    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color MINT         = new Color(0xE8, 0xF5, 0xE9);
    private final Color WHITE        = Color.WHITE;
    private final Color BLACK        = Color.BLACK;
    private final Color MUTED        = new Color(0x4F, 0x6B, 0x52);

    private final User user;

    public UserProfileDetails(Frame parent, User user) {
        super(parent, "User Profile Metadata Details", true);
        this.user = user;

        setSize(780, 620);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(MINT);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 16));
        headerPanel.setBackground(DEEP_GREEN);

        JLabel lblHeader = new JLabel("User Profile Details  —  " + (user != null ? user.getFullName() : "Unknown"));
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22)); // 20 -> 22
        lblHeader.setForeground(WHITE);
        headerPanel.add(lblHeader);

        add(headerPanel, BorderLayout.NORTH);

        // Content Scroll Container
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        mainContainer.setBackground(MINT);

        // Section 1: Core Base Information
        JPanel pnlBase = new JPanel(new GridLayout(7, 2, 14, 10));
        pnlBase.setBackground(WHITE);
        pnlBase.setOpaque(true);
        pnlBase.setBorder(createTitledBorder("Core Account Credentials"));

        String customId = "UID-" + user.getUserId();
        if (user instanceof Undergraduate) customId = ((Undergraduate) user).getRegistrationNo();
        else if (user instanceof Lecturer) customId = ((Lecturer) user).getLecturerCode();
        else if (user instanceof TechnicalOfficer) customId = ((TechnicalOfficer) user).getTechnicalOfficerCode();
        else if (user instanceof Admin) customId = ((Admin) user).getAdminCode();

        addFieldRow(pnlBase, "System Identifier / Code:", customId);
        addFieldRow(pnlBase, "Username:", user.getUsername());
        addFieldRow(pnlBase, "Role / Category:", user.getRole());
        addFieldRow(pnlBase, "Full Name:", user.getFullName());
        addFieldRow(pnlBase, "NIC Number:", user.getNic() != null ? user.getNic() : "N/A");
        addFieldRow(pnlBase, "Gender:", user.getGender() != null ? user.getGender() : "N/A");
        addFieldRow(pnlBase, "Account Status:", user.getUserStatus() != null ? user.getUserStatus() : "Active");

        mainContainer.add(pnlBase);
        mainContainer.add(Box.createVerticalStrut(16));

        // Section 2: Contact & Department Information
        JPanel pnlContact = new JPanel(new GridLayout(4, 2, 14, 10));
        pnlContact.setBackground(WHITE);
        pnlContact.setOpaque(true);
        pnlContact.setBorder(createTitledBorder("Contact & Faculty Information"));

        String deptText = (user.getDepartmentId() != null) ? "Department " + user.getDepartmentId() : "General / Administrative";
        if (user.getDepartmentId() != null && user.getDepartmentId() == 1) deptText = "ICT";
        else if (user.getDepartmentId() != null && user.getDepartmentId() == 2) deptText = "Engineering Technology";
        else if (user.getDepartmentId() != null && user.getDepartmentId() == 3) deptText = "Bio Systems Technology";

        addFieldRow(pnlContact, "Email Address:", user.getEmail() != null ? user.getEmail() : "N/A");
        addFieldRow(pnlContact, "Contact Phone:", user.getContactNo() != null ? user.getContactNo() : "N/A");
        addFieldRow(pnlContact, "Residential Address:", user.getAddress() != null ? user.getAddress() : "N/A");
        addFieldRow(pnlContact, "Faculty Department:", deptText);

        mainContainer.add(pnlContact);
        mainContainer.add(Box.createVerticalStrut(16));

        // Section 3: Role Specific Extra Information
        JPanel pnlRole = new JPanel(new GridLayout(4, 2, 14, 10));
        pnlRole.setBackground(WHITE);
        pnlRole.setOpaque(true);
        pnlRole.setBorder(createTitledBorder("Role Specific Attributes"));

        if (user instanceof Admin) {
            Admin a = (Admin) user;
            addFieldRow(pnlRole, "Admin Code:", a.getAdminCode());
            addFieldRow(pnlRole, "Official Designation:", a.getDesignation());
        } else if (user instanceof Lecturer) {
            Lecturer l = (Lecturer) user;
            addFieldRow(pnlRole, "Lecturer Code:", l.getLecturerCode());
            addFieldRow(pnlRole, "Academic Title:", l.getTitle());
            addFieldRow(pnlRole, "Specialization:", l.getSpecialization());
        } else if (user instanceof TechnicalOfficer) {
            TechnicalOfficer t = (TechnicalOfficer) user;
            addFieldRow(pnlRole, "TO Code:", t.getTechnicalOfficerCode());
            addFieldRow(pnlRole, "Designation:", t.getDesignation());
            addFieldRow(pnlRole, "Assigned Labs:", t.getAssignedLabs());
        } else if (user instanceof Undergraduate) {
            Undergraduate ug = (Undergraduate) user;
            addFieldRow(pnlRole, "Registration No:", ug.getRegistrationNo());
            addFieldRow(pnlRole, "Index No:", ug.getIndexNo());
            addFieldRow(pnlRole, "Academic Level & Sem:", "Level " + ug.getCurrentLevel() + ", Semester " + ug.getCurrentSemester());
            addFieldRow(pnlRole, "Guardian Details:", (ug.getGuardianName() != null ? ug.getGuardianName() : "N/A") + " (" + (ug.getGuardianContact() != null ? ug.getGuardianContact() : "N/A") + ")");
        }

        mainContainer.add(pnlRole);

        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.getViewport().setBackground(MINT);
        scrollPane.setBackground(MINT);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 14));
        footerPanel.setBackground(WHITE);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, PASTEL_GREEN));

        JButton btnClose = new JButton("Close Dialog");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16)); // 14 -> 16
        btnClose.setBackground(FOREST_GREEN);
        btnClose.setForeground(WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        footerPanel.add(btnClose);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void addFieldRow(JPanel panel, String labelText, String valueText) {
        JLabel lblName = new JLabel(labelText);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15)); // 13 -> 15
        lblName.setForeground(MUTED);

        JLabel lblVal = new JLabel(valueText != null && !valueText.trim().isEmpty() ? valueText : "N/A");
        lblVal.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // 14 -> 16
        lblVal.setForeground(BLACK);

        panel.add(lblName);
        panel.add(lblVal);
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                " " + title + " ",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), // 14 -> 16
                DEEP_GREEN
        );
    }
}
