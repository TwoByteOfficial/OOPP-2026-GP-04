package gui.admin.TimetableComponents;

import model.TimetableEntry;
import javax.swing.*;
import java.awt.*;

public class TimetableDetails extends JDialog {

    public TimetableDetails(Frame parent, TimetableEntry entry) {
        super(parent, "Scheduled Timetable Slot Details", true);
        setSize(580, 480);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setBackground(new Color(0x1B, 0x5E, 0x20));
        JLabel lblHeader = new JLabel("Timetable Entry: TT-" + entry.getTimetableId());
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        JPanel pnlContent = new JPanel(new GridLayout(8, 2, 14, 12));
        pnlContent.setBackground(Color.WHITE);
        pnlContent.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        addDetailRow(pnlContent, "Timetable Slot ID:", "TT-" + entry.getTimetableId());
        addDetailRow(pnlContent, "Course ID:", "CID-" + entry.getCourseId());
        addDetailRow(pnlContent, "Day & Time:", entry.getDayOfWeek() + " (" + entry.getStartTime() + " - " + entry.getEndTime() + ")");
        addDetailRow(pnlContent, "Component:", entry.getComponent());
        addDetailRow(pnlContent, "Venue:", entry.getVenue());
        addDetailRow(pnlContent, "Department / Batch:", "Dept " + entry.getDepartmentId() + " (Batch " + entry.getBatchYear() + ")");
        addDetailRow(pnlContent, "Lecturer ID:", entry.getLecturerId() != null ? "UID-" + entry.getLecturerId() : "Unassigned");
        addDetailRow(pnlContent, "Academic Session:", entry.getAcademicYear() + " (" + entry.getSemester() + ")");

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
