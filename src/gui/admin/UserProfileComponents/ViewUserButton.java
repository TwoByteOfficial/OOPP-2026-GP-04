package gui.admin.UserProfileComponents;

import gui.admin.AdminTheme;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

/**
 * ViewUserButton - Single component file containing the View User Details action button
 * AND its on-click view details modal dialog window.
 *
 * All theme colors and typography are retrieved directly from AdminTheme.
 */
public class ViewUserButton extends JButton {

    public ViewUserButton(Component parentComponent, Supplier<User> userSupplier) {
        super("👁️ View Details");
        setFont(AdminTheme.font(Font.BOLD, 15));
        setBackground(AdminTheme.PASTEL_GREEN);
        setForeground(AdminTheme.DEEP_GREEN);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        addActionListener(e -> {
            User selectedUser = userSupplier != null ? userSupplier.get() : null;
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(this, "Please select a user profile row from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(parentComponent != null ? parentComponent : this);
            ViewUserDetailsDialog dialog = new ViewUserDetailsDialog(parentFrame, selectedUser);
            dialog.setVisible(true);
        });
    }

    public ViewUserButton(Runnable onView) {
        super("👁️ View Details");
        setFont(AdminTheme.font(Font.BOLD, 15));
        setBackground(AdminTheme.PASTEL_GREEN);
        setForeground(AdminTheme.DEEP_GREEN);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        addActionListener(e -> {
            if (onView != null) onView.run();
        });
    }

    /**
     * Modal View Details Dialog Window
     */
    public static class ViewUserDetailsDialog extends JDialog {

        public ViewUserDetailsDialog(Frame parent, User user) {
            super(parent, "Faculty User Profile Details", true);
            setSize(680, 580);
            setLocationRelativeTo(parent);
            setResizable(false);
            getContentPane().setBackground(AdminTheme.MINT);
            setLayout(new BorderLayout());

            initUI(user);
        }

        private void initUI(User u) {
            if (u == null) return;

            JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
            header.setBackground(AdminTheme.DEEP_GREEN);

            JLabel lblTitle = new JLabel("Profile Details - " + u.getFullName() + " (" + u.getRole() + ")");
            lblTitle.setFont(AdminTheme.font(Font.BOLD, 18));
            lblTitle.setForeground(AdminTheme.WHITE);
            header.add(lblTitle);

            add(header, BorderLayout.NORTH);

            JPanel content = new JPanel(new GridLayout(0, 2, 14, 12));
            content.setBackground(AdminTheme.WHITE);
            content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

            addDetailRow(content, "Full Name:", u.getFullName());
            addDetailRow(content, "System Role:", u.getRole());
            addDetailRow(content, "Username:", u.getUsername());
            addDetailRow(content, "Email Address:", u.getEmail());
            addDetailRow(content, "Contact Phone:", u.getContactNo());
            addDetailRow(content, "NIC Number:", u.getNic());
            addDetailRow(content, "Gender:", u.getGender());
            addDetailRow(content, "Date of Birth:", u.getDateOfBirth() != null ? u.getDateOfBirth().toString() : "N/A");

            String deptText = (u.getDepartmentId() != null) ? "Dept " + u.getDepartmentId() : "General";
            if (u.getDepartmentId() != null && u.getDepartmentId() == 1) deptText = "ICT";
            else if (u.getDepartmentId() != null && u.getDepartmentId() == 2) deptText = "Engineering Tech";
            else if (u.getDepartmentId() != null && u.getDepartmentId() == 3) deptText = "Bio Systems Tech";

            addDetailRow(content, "Department:", deptText);
            addDetailRow(content, "Account Status:", u.getUserStatus());
            addDetailRow(content, "Residential Address:", u.getAddress());

            // Role specific rows
            if (u instanceof Admin) {
                Admin a = (Admin) u;
                addDetailRow(content, "Admin Code:", a.getAdminCode());
                addDetailRow(content, "Designation:", a.getDesignation());
            } else if (u instanceof Lecturer) {
                Lecturer l = (Lecturer) u;
                addDetailRow(content, "Lecturer Code:", l.getLecturerCode());
                addDetailRow(content, "Academic Title:", l.getTitle());
                addDetailRow(content, "Specialization:", l.getSpecialization());
            } else if (u instanceof TechnicalOfficer) {
                TechnicalOfficer t = (TechnicalOfficer) u;
                addDetailRow(content, "TO Code:", t.getTechnicalOfficerCode());
                addDetailRow(content, "Designation:", t.getDesignation());
                addDetailRow(content, "Assigned Labs:", t.getAssignedLabs());
            } else if (u instanceof Undergraduate) {
                Undergraduate ug = (Undergraduate) u;
                addDetailRow(content, "Registration No:", ug.getRegistrationNo());
                addDetailRow(content, "Index No:", ug.getIndexNo());
                addDetailRow(content, "Batch Year:", String.valueOf(ug.getBatchYear()));
                addDetailRow(content, "Student Level & Semester:", "L" + ug.getCurrentLevel() + " S" + ug.getCurrentSemester());
                addDetailRow(content, "Guardian Info:", ug.getGuardianName() + " (" + ug.getGuardianContact() + ")");
            }

            JScrollPane scrollPane = new JScrollPane(content);
            scrollPane.getViewport().setBackground(AdminTheme.WHITE);
            scrollPane.setBorder(null);
            add(scrollPane, BorderLayout.CENTER);

            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 14));
            footer.setBackground(AdminTheme.WHITE);
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AdminTheme.PASTEL_GREEN));

            JButton btnClose = new JButton("Close");
            btnClose.setFont(AdminTheme.font(Font.BOLD, 15));
            btnClose.setBackground(AdminTheme.FOREST_GREEN);
            btnClose.setForeground(AdminTheme.WHITE);
            btnClose.setFocusPainted(false);
            btnClose.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));
            btnClose.addActionListener(e -> dispose());
            footer.add(btnClose);

            add(footer, BorderLayout.SOUTH);
        }

        private void addDetailRow(JPanel pnl, String label, String value) {
            JLabel lblKey = new JLabel(label);
            lblKey.setFont(AdminTheme.font(Font.BOLD, 15));
            lblKey.setForeground(AdminTheme.DEEP_GREEN);

            JLabel lblVal = new JLabel(value != null && !value.isEmpty() ? value : "N/A");
            lblVal.setFont(AdminTheme.font(Font.PLAIN, 15));
            lblVal.setForeground(AdminTheme.BLACK);

            pnl.add(lblKey);
            pnl.add(lblVal);
        }
    }
}
