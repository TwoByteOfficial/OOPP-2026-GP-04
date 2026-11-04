package gui.admin.EnrollmentComponents;

import javax.swing.*;
import java.awt.*;

/**
 * EnrollmentTitleBar - Title bar component for the Enrollment Requests Management view.
 * Styled using the project's AdminTheme color palette.
 */
public class EnrollmentTitleBar extends JPanel {

    private final Color DEEP_GREEN = new Color(0x1B, 0x5E, 0x20);
    private final Color MUTED      = new Color(0x4F, 0x6B, 0x52);

    private final JLabel lblSubtitle;

    public EnrollmentTitleBar(int totalRequests) {
        setLayout(null);
        setPreferredSize(new Dimension(1620, 80));
        setMinimumSize(new Dimension(1620, 80));
        setOpaque(false);

        JLabel lblTitle = new JLabel("Course Enrollment Requests");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(DEEP_GREEN);
        lblTitle.setBounds(0, 5, 800, 38);
        add(lblTitle);

        lblSubtitle = new JLabel("Manage student course registration requests (" + totalRequests + " total records)");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(MUTED);
        lblSubtitle.setBounds(0, 45, 800, 24);
        add(lblSubtitle);
    }

    public void updateCount(int totalRequests) {
        lblSubtitle.setText("Manage student course registration requests (" + totalRequests + " total records)");
    }
}
