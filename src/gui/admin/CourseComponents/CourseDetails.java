package gui.admin.CourseComponents;

import model.Course;
import javax.swing.*;
import java.awt.*;

public class CourseDetails extends JDialog {

    public CourseDetails(Frame parent, Course course) {
        super(parent, "Course Module Specifications", true);
        setSize(650, 620);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setBackground(new Color(0x1B, 0x5E, 0x20));
        JLabel lblHeader = new JLabel("Module Details: " + course.getCourseCode() + " - " + course.getCourseName());
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        JPanel pnlContent = new JPanel(new GridLayout(13, 2, 12, 10));
        pnlContent.setBackground(Color.WHITE);
        pnlContent.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        addDetailRow(pnlContent, "Course ID:", String.valueOf(course.getCourseId()));
        addDetailRow(pnlContent, "Course Code:", course.getCourseCode());
        addDetailRow(pnlContent, "Course Name:", course.getCourseName());
        addDetailRow(pnlContent, "Theory / Practical Credits:", course.getTheoryCredits() + " / " + course.getPracticalCredits() + " (Total: " + course.getTotalCredits() + ")");

        // CA Breakdown
        addDetailRow(pnlContent, "Total CA Weight:", course.getCaWeight() + "%");
        addDetailRow(pnlContent, "  - Max Quiz 2 Weight:", (course.getQuizWeight() != null ? course.getQuizWeight() : "0.0") + "%");
        addDetailRow(pnlContent, "  - Assignment Weight:", (course.getAssignmentWeight() != null ? course.getAssignmentWeight() : "0.0") + "%");
        addDetailRow(pnlContent, "  - Mid Exam Theory Weight:", (course.getMidTheoryWeight() != null ? course.getMidTheoryWeight() : "0.0") + "%");
        addDetailRow(pnlContent, "  - Mid Exam Practical Weight:", (course.getMidPracticalWeight() != null ? course.getMidPracticalWeight() : "0.0") + "%");

        // End Exam Breakdown
        addDetailRow(pnlContent, "Total Final Exam Weight:", course.getFinalWeight() + "%");
        addDetailRow(pnlContent, "  - End Exam Theory Weight:", (course.getEndTheoryWeight() != null ? course.getEndTheoryWeight() : "0.0") + "%");
        addDetailRow(pnlContent, "  - End Exam Practical Weight:", (course.getEndPracticalWeight() != null ? course.getEndPracticalWeight() : "0.0") + "%");

        addDetailRow(pnlContent, "Academic Year / Semester:", course.getAcademicYear() + " (" + course.getSemester() + ")");
        addDetailRow(pnlContent, "Status:", course.getStatus());

        add(new JScrollPane(pnlContent), BorderLayout.CENTER);

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
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(Color.BLACK);
        panel.add(l);

        JLabel v = new JLabel(value != null ? value : "N/A");
        v.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        v.setForeground(new Color(0x1B, 0x5E, 0x20));
        panel.add(v);
    }
}