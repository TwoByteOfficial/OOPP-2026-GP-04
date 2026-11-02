package gui.admin.CourseComponents;

import model.Course;
import javax.swing.*;
import java.awt.*;

public class CourseDetails extends JDialog {

    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color WHITE        = Color.WHITE;
    private final Color BLACK        = Color.BLACK;

    public CourseDetails(Frame parent, Course course) {
        super(parent, "Course Module Specifications", true);
        setSize(680, 640);
        setLocationRelativeTo(parent);
        setResizable(true);
        getContentPane().setBackground(WHITE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setBackground(DEEP_GREEN);
        JLabel lblHeader = new JLabel("Module Specifications: " + course.getCourseCode());
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(WHITE);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        // Content Panel with GridBagLayout
        JPanel pnlContent = new JPanel(new GridBagLayout());
        pnlContent.setBackground(WHITE);
        pnlContent.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        int row = 0;

        // General Information
        row = addSectionHeader(pnlContent, gbc, row, "General Module Details");
        row = addDetailRow(pnlContent, gbc, row, "Course ID:", String.valueOf(course.getCourseId()), false);
        row = addDetailRow(pnlContent, gbc, row, "Course Code:", course.getCourseCode(), false);
        row = addDetailRow(pnlContent, gbc, row, "Course Name:", course.getCourseName(), false);
        row = addDetailRow(pnlContent, gbc, row, "Credits (Theory / Practical):",
                course.getTheoryCredits() + " / " + course.getPracticalCredits() + " (Total: " + course.getTotalCredits() + ")", false);

        // Continuous Assessment Breakdown
        row = addSectionHeader(pnlContent, gbc, row, "Continuous Assessment (CA) Breakdown");
        row = addDetailRow(pnlContent, gbc, row, "Total CA Weight:", course.getCaWeight() + "%", false);
        row = addDetailRow(pnlContent, gbc, row, "• Max Quiz 2 Weight:", (course.getQuizWeight() != null ? course.getQuizWeight() : "0.0") + "%", true);
        row = addDetailRow(pnlContent, gbc, row, "• Assignment Weight:", (course.getAssignmentWeight() != null ? course.getAssignmentWeight() : "0.0") + "%", true);
        row = addDetailRow(pnlContent, gbc, row, "• Mid Exam Theory Weight:", (course.getMidTheoryWeight() != null ? course.getMidTheoryWeight() : "0.0") + "%", true);
        row = addDetailRow(pnlContent, gbc, row, "• Mid Exam Practical Weight:", (course.getMidPracticalWeight() != null ? course.getMidPracticalWeight() : "0.0") + "%", true);

        // End Semester Examination Breakdown
        row = addSectionHeader(pnlContent, gbc, row, "End Semester Examination Breakdown");
        row = addDetailRow(pnlContent, gbc, row, "Total Final Exam Weight:", course.getFinalWeight() + "%", false);
        row = addDetailRow(pnlContent, gbc, row, "• End Exam Theory Weight:", (course.getEndTheoryWeight() != null ? course.getEndTheoryWeight() : "0.0") + "%", true);
        row = addDetailRow(pnlContent, gbc, row, "• End Exam Practical Weight:", (course.getEndPracticalWeight() != null ? course.getEndPracticalWeight() : "0.0") + "%", true);

        // Academic Status
        row = addSectionHeader(pnlContent, gbc, row, "Academic Term & Status");
        row = addDetailRow(pnlContent, gbc, row, "Academic Year / Semester:", course.getAcademicYear() + " (" + course.getSemester() + ")", false);
        row = addDetailRow(pnlContent, gbc, row, "Status:", course.getStatus(), false);

        // Vertical Glue Filler
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        pnlContent.add(Box.createGlue(), gbc);

        JScrollPane scrollPane = new JScrollPane(pnlContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Footer Button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        footerPanel.setBackground(WHITE);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, PASTEL_GREEN));

        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnClose.setBackground(FOREST_GREEN);
        btnClose.setForeground(WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        btnClose.addActionListener(e -> dispose());
        footerPanel.add(btnClose);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private int addSectionHeader(JPanel panel, GridBagConstraints gbc, int row, String title) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(14, 8, 6, 8);

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(DEEP_GREEN);

        JSeparator line = new JSeparator();
        line.setForeground(PASTEL_GREEN);

        pnlHeader.add(lbl, BorderLayout.NORTH);
        pnlHeader.add(line, BorderLayout.SOUTH);

        panel.add(pnlHeader, gbc);
        return row + 1;
    }

    private int addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value, boolean isIndented) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.40;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = isIndented ? new Insets(4, 24, 4, 8) : new Insets(4, 12, 4, 8);

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", isIndented ? Font.PLAIN : Font.BOLD, 14));
        l.setForeground(BLACK);
        panel.add(l, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.60;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 8, 4, 12);

        JLabel v = new JLabel(value != null ? value : "N/A");
        v.setFont(new Font("Segoe UI", Font.BOLD, 14));
        v.setForeground(DEEP_GREEN);
        panel.add(v, gbc);

        return row + 1;
    }
}