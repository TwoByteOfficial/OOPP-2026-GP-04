package gui.admin.EnrollmentComponents;

import javax.swing.*;
import java.awt.*;

/**
 * EnrollmentDecisionDialog - Modal dialog for admin to enter decision notes when approving or declining an enrollment request.
 */
public class EnrollmentDecisionDialog extends JDialog {

    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color DANGER       = new Color(0xDC, 0x35, 0x45);
    private final Color WHITE        = Color.WHITE;
    private final Color MINT         = new Color(0xE8, 0xF5, 0xE9);

    private final JTextArea txtDecisionNote;
    private boolean confirmed = false;

    public EnrollmentDecisionDialog(Frame owner, String actionType, String studentName, String courseCode) {
        super(owner, actionType + " Enrollment Request", true);

        setSize(520, 320);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        boolean isApprove = "Approve".equalsIgnoreCase(actionType);
        Color headerColor = isApprove ? FOREST_GREEN : DANGER;

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(headerColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel titleLabel = new JLabel(actionType + " Request for " + studentName + " (" + courseCode + ")");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout(0, 10));
        bodyPanel.setBackground(WHITE);
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel lblPrompt = new JLabel("Enter decision note / reason for " + actionType.toLowerCase() + "ing:");
        lblPrompt.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPrompt.setForeground(DEEP_GREEN);
        bodyPanel.add(lblPrompt, BorderLayout.NORTH);

        txtDecisionNote = new JTextArea(4, 30);
        txtDecisionNote.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtDecisionNote.setLineWrap(true);
        txtDecisionNote.setWrapStyleWord(true);
        txtDecisionNote.setText(isApprove ? "Enrollment request approved by administrator." : "Enrollment request declined by administrator.");

        JScrollPane scrollNote = new JScrollPane(txtDecisionNote);
        scrollNote.setBorder(BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1));
        bodyPanel.add(scrollNote, BorderLayout.CENTER);

        add(bodyPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        footerPanel.setBackground(MINT);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        JButton btnSubmit = new JButton(isApprove ? "Confirm Approval" : "Confirm Decline");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubmit.setBackground(headerColor);
        btnSubmit.setForeground(WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        footerPanel.add(btnCancel);
        footerPanel.add(btnSubmit);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getDecisionNote() {
        return txtDecisionNote.getText().trim();
    }
}
