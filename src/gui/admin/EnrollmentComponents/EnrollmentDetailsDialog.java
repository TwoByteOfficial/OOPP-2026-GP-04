package gui.admin.EnrollmentComponents;

import model.EnrollmentRequest;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * EnrollmentDetailsDialog - Modal dialog presenting full detail view of a student enrollment request.
 */
public class EnrollmentDetailsDialog extends JDialog {

    private final Color MINT         = new Color(0xE8, 0xF5, 0xE9);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color WHITE        = Color.WHITE;
    private final Color MUTED        = new Color(0x4F, 0x6B, 0x52);

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public EnrollmentDetailsDialog(Frame owner, EnrollmentRequest request) {
        super(owner, "Enrollment Request Details - REQ-" + request.getRequestId(), true);

        setSize(650, 580);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DEEP_GREEN);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel titleLabel = new JLabel("Enrollment Request Details (REQ-" + request.getRequestId() + ")");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addDetailRow(contentPanel, gbc, row++, "Student Reg No:", request.getRegistrationNo() != null ? request.getRegistrationNo() : "STU-" + request.getStudentId());
        addDetailRow(contentPanel, gbc, row++, "Student Name:", request.getStudentName() != null ? request.getStudentName() : "-");
        addDetailRow(contentPanel, gbc, row++, "Student Type:", request.getStudentType() != null ? request.getStudentType() : "-");
        addDetailRow(contentPanel, gbc, row++, "Course:", (request.getCourseCode() != null ? request.getCourseCode() : "") + " - " + (request.getCourseName() != null ? request.getCourseName() : ""));
        addDetailRow(contentPanel, gbc, row++, "Academic Year / Semester:", request.getAcademicYear() + " / " + request.getSemester());
        addDetailRow(contentPanel, gbc, row++, "Enrollment Type:", request.getEnrollmentType() != null ? request.getEnrollmentType() : "Normal");
        addDetailRow(contentPanel, gbc, row++, "Request Status:", request.getStatus());
        addDetailRow(contentPanel, gbc, row++, "Already Enrolled:", request.isAlreadyEnrolled() ? "Yes (Active Record)" : "No");
        addDetailRow(contentPanel, gbc, row++, "Requested At:", request.getRequestedAt() != null ? dateFormat.format(request.getRequestedAt()) : "-");
        addDetailRow(contentPanel, gbc, row++, "Student Note:", request.getRequestNote() != null ? request.getRequestNote() : "(None)");
        addDetailRow(contentPanel, gbc, row++, "Decided By:", request.getDecidedByName() != null ? request.getDecidedByName() : "-");
        addDetailRow(contentPanel, gbc, row++, "Decided At:", request.getDecidedAt() != null ? dateFormat.format(request.getDecidedAt()) : "-");
        addDetailRow(contentPanel, gbc, row++, "Decision Note:", request.getDecisionNote() != null ? request.getDecisionNote() : "(None)");

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(MINT);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnClose.setBackground(FOREST_GREEN);
        btnClose.setForeground(WHITE);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose());

        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 0.35;
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblLabel.setForeground(DEEP_GREEN);
        panel.add(lblLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblValue.setForeground(Color.BLACK);
        panel.add(lblValue, gbc);
    }
}
