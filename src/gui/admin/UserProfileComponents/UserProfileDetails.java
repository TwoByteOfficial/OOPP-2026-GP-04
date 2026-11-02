package gui.admin.UserProfileComponents;

import model.*;

import javax.swing.*;
import java.awt.*;

/**
 * UserProfileDetails - Modal popup dialog for viewing comprehensive user profile details.
 */
public class UserProfileDetails extends JDialog {

    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color MINT         = new Color(0xE8, 0xF5, 0xE9);
    private final Color WHITE        = Color.WHITE;
    private final Color BLACK        = Color.BLACK;

    public UserProfileDetails(Frame parent, User user) {
        super(parent, "User Profile Metadata Details", true);

        setSize(680, 620);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(MINT);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 16));
        header.setBackground(DEEP_GREEN);
        JLabel lblHeader = new JLabel("Faculty Profile: " + user.getFullName());
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(WHITE);
        header.add(lblHeader);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(0, 2, 14, 12));
        content.setBackground(WHITE);
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        addDetailRow(content, "User ID:", "UID-" + user.getUserId());
        addDetailRow(content, "Username:", user.getUsername());
        addDetailRow(content, "System Role:", user.getRole());
        addDetailRow(content, "Full Name:", user.getFullName());
        addDetailRow(content, "NIC Number:", user.getNic());
        addDetailRow(content, "Date of Birth:", user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : "N/A");
        addDetailRow(content, "Gender:", user.getGender());
        addDetailRow(content, "Email Address:", user.getEmail());
        addDetailRow(content, "Contact No:", user.getContactNo());
        addDetailRow(content, "Address:", user.getAddress());
        addDetailRow(content, "Department ID:", user.getDepartmentId() != null ? String.valueOf(user.getDepartmentId()) : "General");
        addDetailRow(content, "Account Status:", user.getUserStatus());

        if (user instanceof Admin) {
            Admin a = (Admin) user;
            addDetailRow(content, "Admin Code:", a.getAdminCode());
            addDetailRow(content, "Designation:", a.getDesignation());
        } else if (user instanceof Lecturer) {
            Lecturer l = (Lecturer) user;
            addDetailRow(content, "Lecturer Code:", l.getLecturerCode());
            addDetailRow(content, "Title & Specialization:", l.getTitle() + " (" + l.getSpecialization() + ")");
        } else if (user instanceof TechnicalOfficer) {
            TechnicalOfficer t = (TechnicalOfficer) user;
            addDetailRow(content, "TO Code:", t.getTechnicalOfficerCode());
            addDetailRow(content, "Assigned Labs:", t.getAssignedLabs());
        } else if (user instanceof Undergraduate) {
            Undergraduate ug = (Undergraduate) user;
            addDetailRow(content, "Registration No:", ug.getRegistrationNo());
            addDetailRow(content, "Index No:", ug.getIndexNo());
            addDetailRow(content, "Batch / Level / Sem:", ug.getBatchYear() + " / L" + ug.getCurrentLevel() + "S" + ug.getCurrentSemester());
            addDetailRow(content, "Guardian Info:", ug.getGuardianName() + " (" + ug.getGuardianContact() + ")");
        }

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.getViewport().setBackground(WHITE);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 14));
        footer.setBackground(WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, PASTEL_GREEN));

        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnClose.setBackground(FOREST_GREEN);
        btnClose.setForeground(WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));
        btnClose.addActionListener(e -> dispose());
        footer.add(btnClose);

        add(footer, BorderLayout.SOUTH);
    }

    private void addDetailRow(JPanel pnl, String label, String value) {
        JLabel lblKey = new JLabel(label);
        lblKey.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblKey.setForeground(DEEP_GREEN);

        JLabel lblVal = new JLabel(value != null && !value.isEmpty() ? value : "N/A");
        lblVal.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblVal.setForeground(BLACK);

        pnl.add(lblKey);
        pnl.add(lblVal);
    }
}
