package gui.admin.DashboardComponents;

import dao.admin.AdminUserDAO; // Ingest existing user DAO package
import model.User;             // Ingest model definition mapping
import model.Undergraduate;
import model.Lecturer;
import model.TechnicalOfficer;
import model.Admin;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class UserSummary extends JPanel {

    private final Color WHITE = Color.WHITE;
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color DEEP_GREEN = new Color(0x1B, 0x5E, 0x20);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color MUTED = new Color(0x4F, 0x6B, 0x52);

    private AdminUserDAO userDAO; // Added data access node reference

    public UserSummary() {
        setLayout(null);
        setBounds(0, 0, 770, 410);
        setOpaque(false);

        // 1. Initialize DB Operations safely
        userDAO = new AdminUserDAO();
        List<User> recentUsers = new ArrayList<>();
        try {
            List<User> allUsers = userDAO.getAllUsers(); // Fetches users sorted DESC by ID
            // Extract a maximum slice of the top 5 recently created profiles
            for (int i = 0; i < Math.min(5, allUsers.size()); i++) {
                recentUsers.add(allUsers.get(i));
            }
        } catch (Exception e) {
            System.err.println("[User Summary DB Error] Could not load dynamic registration tables.");
            e.printStackTrace();
        }

        JPanel container = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(0x1B, 0x5E, 0x20, 35));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };

        container.setBounds(0, 0, 770, 410);
        container.setOpaque(false);

        JLabel title = new JLabel("Recently Added Users");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(FOREST_GREEN);
        title.setBounds(25, 20, 380, 30);
        container.add(title);

        JPanel tableHeader = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0xE8, 0xF5, 0xE9));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        tableHeader.setBounds(25, 65, 720, 42);
        tableHeader.setOpaque(false);

        String[] columnHeaders = { "User ID", "Name", "Role", "Department", "Status" };
        int[] columnWidths = { 130, 200, 160, 130, 120 };

        int currentX = 15;
        for (int i = 0; i < columnHeaders.length; i++) {
            JLabel header = new JLabel(columnHeaders[i]);
            header.setFont(new Font("Segoe UI", Font.BOLD, 14));
            header.setForeground(FOREST_GREEN);
            header.setBounds(currentX, 0, columnWidths[i], 42);
            tableHeader.add(header);
            currentX += columnWidths[i];
        }
        container.add(tableHeader);

        // 2. Loop and print live rows matching database models dynamically
        for (int row = 0; row < recentUsers.size(); row++) {
            User u = recentUsers.get(row);

            // Deduce role-specific tracking identification numbers dynamically from polymorphs
            String customIdentifier = "UID-" + u.getUserId();
            if (u instanceof Undergraduate) {
                customIdentifier = ((Undergraduate) u).getRegistrationNo(); // e.g. TEC/2024/020
            } else if (u instanceof Lecturer) {
                customIdentifier = ((Lecturer) u).getLecturerCode();
            } else if (u instanceof TechnicalOfficer) {
                customIdentifier = ((TechnicalOfficer) u).getTechnicalOfficerCode();
            } else if (u instanceof Admin) {
                customIdentifier = ((Admin) u).getAdminCode();
            }

            // Format fallback values if code strings are unassigned or blank inside specific datasets
            if (customIdentifier == null || customIdentifier.trim().isEmpty()) {
                customIdentifier = "UID-" + u.getUserId();
            }

            // Convert primitive department IDs into readable shorthand text tokens
            String deptText = (u.getDepartmentId() != null) ? "Dept " + u.getDepartmentId() : "General";
            if (u.getDepartmentId() != null && u.getDepartmentId() == 1) {
                deptText = "ICT"; // Spliced matching your layout logic
            }

            String[] liveRowData = {
                    customIdentifier,
                    u.getFullName(),
                    u.getRole(),
                    deptText,
                    u.getUserStatus() != null ? u.getUserStatus() : "Active"
            };

            JPanel rowPanel = new JPanel(null);
            rowPanel.setOpaque(false);
            rowPanel.setBounds(25, 112 + (row * 54), 720, 54);

            int cellX = 15;
            for (int col = 0; col < liveRowData.length; col++) {
                JLabel cell = new JLabel(liveRowData[col]);
                cell.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                cell.setForeground(col == 0 ? DEEP_GREEN : MUTED);

                if (col == 4) {
                    cell.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    // Highlight Active vs Inactive flags visually
                    if ("Active".equalsIgnoreCase(liveRowData[col])) {
                        cell.setForeground(FOREST_GREEN);
                    } else {
                        cell.setForeground(new Color(0xDC, 0x35, 0x45)); // Muted Red Alert Style
                    }
                }

                cell.setBounds(cellX, 0, columnWidths[col], 54);
                rowPanel.add(cell);
                cellX += columnWidths[col];
            }

            JSeparator separator = new JSeparator();
            separator.setForeground(PASTEL_GREEN);
            separator.setBounds(0, 53, 720, 1);
            rowPanel.add(separator);

            container.add(rowPanel);
        }

        // Show an explicit layout placeholder row if database users index is entirely empty
        if (recentUsers.isEmpty()) {
            JLabel emptyLabel = new JLabel("No records found in users database table register.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(MUTED);
            emptyLabel.setBounds(25, 150, 720, 40);
            container.add(emptyLabel);
        }

        add(container);
    }
}
