package gui.technical.dashboard.overview;

import dao.TechnicalOfficer.TODashboardDAO;
import java.sql.SQLException;
import java.util.function.Consumer;

import gui.theme.UniMisTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Medical status widget displayed on the Technical Officer Dashboard.
 * Shows the current number of student medical certificates awaiting officer review
 * and provides a direct shortcut button to open the full Medical Records management screen.
 */
public class MedicalStatusPanel extends JPanel {

    // Technical Officer user ID
    private final long userId;

    /**
     * Constructs the Medical Status panel.
     *
     * @param userId            Database user ID of the Technical Officer
     * @param navigationHandler Callback to navigate to "Medical Records"
     */
    public MedicalStatusPanel(
            long userId,
            Consumer<String> navigationHandler) {

        this.userId = userId;

        setLayout(new BorderLayout(0, 16));
        setBackground(UniMisTheme.WHITE);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        // Header label
        JLabel heading = new JLabel("Medical Status");
        heading.setFont(UniMisTheme.FONT_H3);
        heading.setForeground(UniMisTheme.TEXT);
        add(heading, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Pending count label
        JLabel pendingLabel = new JLabel("Pending medical records: —");

        // Query pending count from database
        try {
            TODashboardDAO dashboardDAO = new TODashboardDAO();
            int pendingCount = dashboardDAO.getPendingMedicalCount(userId);
            pendingLabel.setText("Pending medical records: " + pendingCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        pendingLabel.setFont(UniMisTheme.FONT_BODY_BOLD);
        pendingLabel.setForeground(UniMisTheme.PRIMARY_DARK);

        // Shortcut button to open the Medical Records management panel
        JButton viewButton = new JButton("View Medical Records");
        viewButton.setFont(UniMisTheme.FONT_SMALL);
        viewButton.setForeground(UniMisTheme.WHITE);
        viewButton.setBackground(UniMisTheme.PRIMARY);
        viewButton.setFocusPainted(false);
        viewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // On click: navigate directly to "Medical Records" page
        viewButton.addActionListener(e -> navigationHandler.accept("Medical Records"));

        content.add(pendingLabel);
        content.add(Box.createVerticalStrut(18));
        content.add(viewButton);

        add(content, BorderLayout.CENTER);
    }
}
