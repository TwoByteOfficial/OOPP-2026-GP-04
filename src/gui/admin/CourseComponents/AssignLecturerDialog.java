package gui.admin.CourseComponents;

import dao.admin.AdminCourseDAO;
import model.Course;

import javax.swing.*;
import java.awt.*;

public class AssignLecturerDialog extends JDialog {

    private AdminCourseDAO courseDAO;
    private Course course;
    private JTextField txtLecturerId;
    private JComboBox<String> cmbResponsibility;

    public AssignLecturerDialog(Frame parent, AdminCourseDAO dao, Course course, Runnable onSuccess) {
        super(parent, "Assign Lecturer to " + course.getCourseCode(), true);
        this.courseDAO = dao;
        this.course = course;

        setSize(520, 320);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setBackground(new Color(0x1B, 0x5E, 0x20));
        JLabel lblHeader = new JLabel("Assign Lecturer: " + course.getCourseCode());
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(2, 2, 14, 14));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel l1 = new JLabel("Lecturer User ID:");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l1.setForeground(Color.BLACK);
        body.add(l1);

        txtLecturerId = new JTextField();
        txtLecturerId.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtLecturerId.setBackground(Color.WHITE);
        txtLecturerId.setForeground(Color.BLACK);
        body.add(txtLecturerId);

        JLabel l2 = new JLabel("Responsibility:");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l2.setForeground(Color.BLACK);
        body.add(l2);

        cmbResponsibility = new JComboBox<>(new String[]{ "In-Charge", "Co-Lecturer", "Visiting Lecturer", "Practical Instructor" });
        cmbResponsibility.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cmbResponsibility.setBackground(Color.WHITE);
        cmbResponsibility.setForeground(Color.BLACK);
        body.add(cmbResponsibility);

        add(body, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        footer.setBackground(Color.WHITE);

        JButton btnAssign = new JButton("Assign");
        btnAssign.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnAssign.setBackground(new Color(0x38, 0x8E, 0x3C));
        btnAssign.setForeground(Color.WHITE);
        btnAssign.addActionListener(e -> {
            try {
                long lecId = Long.parseLong(txtLecturerId.getText().trim());
                String resp = (String) cmbResponsibility.getSelectedItem();
                courseDAO.assignLecturerToCourse(course.getCourseId(), lecId, resp);
                JOptionPane.showMessageDialog(this, "Lecturer assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (onSuccess != null) onSuccess.run();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Lecturer ID or DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCancel.addActionListener(e -> dispose());

        footer.add(btnAssign);
        footer.add(btnCancel);
        add(footer, BorderLayout.SOUTH);
    }
}
