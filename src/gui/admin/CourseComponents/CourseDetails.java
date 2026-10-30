package gui.admin.CourseComponents;

import model.Course;
import javax.swing.*;
import java.awt.*;

public class CourseDetails extends JDialog {

    public CourseDetails(Frame parent, Course course) {
        super(parent, "Course Module Specifications", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setBackground(new Color(0x1B, 0x5E, 0x20));
        JLabel lblHeader = new JLabel("Module Details: " + course.getCourseCode());
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        JPanel pnlContent = new JPanel(new GridLayout(8, 2, 14, 12));
        pnlContent.setBackground(Color.WHITE);
        pnlContent.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        addDetailRow(pnlContent, "Course ID:", String.valueOf(course.getCourseId()));
        addDetailRow(pnlContent, "Course Code:", course.getCourseCode());
        addDetailRow(pnlContent, "Course Name:", course.getCourseName());
        addDetailRow(pnlContent, "Total Credits:", String.valueOf(course.getTotalCredits()));
        addDetailRow(pnlContent, "CA Weight:", course.getCaWeight() + "%");
        addDetailRow(pnlContent, "Final Exam Weight:", course.getFinalWeight() + "%");
        addDetailRow(pnlContent, "Academic Year / Semester:", course.getAcademicYear() + " (" + course.getSemester() + ")");
        addDetailRow(pnlContent, "Status:", course.getStatus());

        add(pnlContent, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        footerPanel.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnClose.setBackground(new Color(0x38, 0x8E, 0x3C));
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> dispose());
        footerPanel.add(btnClose);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(Color.BLACK);
        panel.add(l);

        JLabel v = new JLabel(value != null ? value : "N/A");
        v.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        v.setForeground(new Color(0x1B, 0x5E, 0x20));
        panel.add(v);
    }
}
