
package gui.technical.dashboard.overview;
import dao.TODashboardDAO;
import java.sql.SQLException;
import java.util.function.Consumer;

import gui.theme.UniMisTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MedicalStatusPanel extends JPanel {
    private final long userId;
    public MedicalStatusPanel(
            long userId,
            Consumer<String> navigationHandler) {
        this.userId = userId;

        setLayout(new BorderLayout(0, 16));
        setBackground(UniMisTheme.WHITE);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel heading = new JLabel("Medical Status");
        heading.setFont(UniMisTheme.FONT_H3);
        heading.setForeground(UniMisTheme.TEXT);

        add(heading, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));




        JLabel pendingLabel = new JLabel(
                "Pending medical records: —");

        try {
            TODashboardDAO dashboardDAO = new TODashboardDAO();

            int pendingCount =
                    dashboardDAO.getPendingMedicalCount(userId);

            pendingLabel.setText(
                    "Pending medical records: " + pendingCount);

        } catch (SQLException e) {
            e.printStackTrace();
        }


        pendingLabel.setFont(UniMisTheme.FONT_BODY_BOLD);
        pendingLabel.setForeground(UniMisTheme.PRIMARY_DARK);

        JButton viewButton = new JButton("View Medical Records");
        viewButton.setFont(UniMisTheme.FONT_SMALL);
        viewButton.setForeground(UniMisTheme.WHITE);
        viewButton.setBackground(UniMisTheme.PRIMARY);
        viewButton.setFocusPainted(false);
        viewButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        viewButton.setAlignmentX(Component.LEFT_ALIGNMENT);



        viewButton.addActionListener(e ->
                navigationHandler.accept("Medical Records"));


        content.add(pendingLabel);
        content.add(Box.createVerticalStrut(18));
        content.add(viewButton);

        add(content, BorderLayout.CENTER);
    }
}
