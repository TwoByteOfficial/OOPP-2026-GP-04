package gui.undergraduate.dashboard.overview;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 1.1.4.2 - Undergraduate Academic Performance & Medical Summary Card
 * CA Performance, Exam Status , Medicals
 */
public class AcademicOverviewPanel extends JPanel {

    public AcademicOverviewPanel(int totalEnrolled, int caCompleted, int approvedMedicals, int pendingMedicals) {
        setLayout(new BorderLayout(0, 14));
        setBackground(AdminTheme.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD2, 0xD7, 0xE1), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        // Header Title
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Academic & Medical Summary");
        title.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 17));
        title.setForeground(AdminTheme.DEEP_GREEN);

        JLabel sub = new JLabel("Current Semester Progress");
        sub.setFont(new Font(AdminTheme.FONT_FAMILY, Font.PLAIN, 13));
        sub.setForeground(AdminTheme.MUTED);

        header.add(title, BorderLayout.WEST);
        header.add(sub, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Content Rows
        JPanel items = new JPanel(new GridLayout(3, 1, 0, 10));
        items.setOpaque(false);

        items.add(createSummaryRow("Continuous Assessment (CA)",
                caCompleted + " of " + totalEnrolled + " Modules Released", AdminTheme.FOREST_GREEN));
        items.add(createSummaryRow("Medical Records Approved",
                approvedMedicals + " Submissions Validated", AdminTheme.DEEP_GREEN));
        items.add(createSummaryRow("Medical Submissions Pending",
                pendingMedicals > 0 ? pendingMedicals + " Under Review" : "No Pending Medicals",
                pendingMedicals > 0 ? AdminTheme.WARNING : AdminTheme.MUTED));

        add(items, BorderLayout.CENTER);
    }

    private JPanel createSummaryRow(String titleText, String detailText, Color statusColor) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 14));
        title.setForeground(AdminTheme.DEEP_GREEN);

        JLabel detail = new JLabel(detailText);
        detail.setFont(new Font(AdminTheme.FONT_FAMILY, Font.PLAIN, 13));
        detail.setForeground(statusColor);

        p.add(title, BorderLayout.WEST);
        p.add(detail, BorderLayout.EAST);
        return p;
    }
}