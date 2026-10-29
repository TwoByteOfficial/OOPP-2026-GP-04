package gui.lecturer.Attendance;

import dao.Lecturer.AttendanceMedicalDAO;
import dao.Lecturer.CourseDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gui.lecturer.SharedComponents.Colors.ATTENDANCE_BACKGROUND;
import static gui.lecturer.SharedComponents.Colors.ATTENDANCE_BORDER;
import static gui.lecturer.SharedComponents.Colors.ATTENDANCE_DEEP_GREEN;
import static gui.lecturer.SharedComponents.Colors.ATTENDANCE_TEXT_GRAY;
import static gui.lecturer.SharedComponents.Colors.ATTENDANCE_WHITE;

public class LecturerAttendance extends JPanel {

    private final long lecturerId;
    private final AttendanceMedicalDAO attDAO = new AttendanceMedicalDAO();
    private final CourseDAO courseDAO = new CourseDAO();

    private final JComboBox<String> courseCombo = new JComboBox<>();
    private final Map<String, Integer> courseMap = new HashMap<>();

    private final JComboBox<String> componentCombo = new JComboBox<>(new String[]{"Combined", "Theory", "Practical"});

    private final DefaultTableModel attTableModel;
    private final JTable attTable;

    private final DefaultTableModel medTableModel;
    private final JTable medTable;

    private final JLabel attStatusLabel = new JLabel("Ready");
    private final JLabel medStatusLabel = new JLabel("Ready");

    public LecturerAttendance(long lecturerId) {
        this.lecturerId = lecturerId;

        setLayout(new BorderLayout(0, 20));
        setBackground(ATTENDANCE_BACKGROUND);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Heading
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);

        JLabel title = new JLabel("Attendance & Medical Records");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(ATTENDANCE_DEEP_GREEN);

        JLabel subtitle = new JLabel("Monitor student attendance summaries and medical certificate submissions.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(ATTENDANCE_TEXT_GRAY);

        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        // Course Selector
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        selectorPanel.setOpaque(false);

        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        courseLabel.setForeground(ATTENDANCE_DEEP_GREEN);

        courseCombo.setPreferredSize(new Dimension(280, 34));
        courseCombo.addActionListener(e -> refreshAllData());

        JLabel componentLabel = new JLabel("Component:");
        componentLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        componentLabel.setForeground(ATTENDANCE_DEEP_GREEN);

        componentCombo.setPreferredSize(new Dimension(140, 34));
        componentCombo.addActionListener(e -> loadAttendanceSummary());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshAllData());

        selectorPanel.add(courseLabel);
        selectorPanel.add(courseCombo);
        selectorPanel.add(componentLabel);
        selectorPanel.add(componentCombo);
        selectorPanel.add(refreshBtn);

        JPanel topContainer = new JPanel(new BorderLayout(0, 15));
        topContainer.setOpaque(false);
        topContainer.add(heading, BorderLayout.NORTH);
        topContainer.add(selectorPanel, BorderLayout.SOUTH);

        // Tabbed View
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(ATTENDANCE_DEEP_GREEN);

        // Tab 1: Attendance Summary Panel
        JPanel attPanel = new JPanel(new BorderLayout(0, 10));
        attPanel.setBackground(ATTENDANCE_BACKGROUND);
        attPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] attColumns = {"Reg. No.", "Full Name", "Total Sessions", "Attended", "Medicals", "Absences", "Raw %", "Adjusted %", "Attendance Status"};
        attTableModel = new DefaultTableModel(attColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        attTable = new JTable(attTableModel);
        attTable.setRowHeight(34);
        attTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        attTable.setSelectionBackground(ATTENDANCE_BACKGROUND);
        attTable.setSelectionForeground(ATTENDANCE_DEEP_GREEN);
        attTable.setGridColor(ATTENDANCE_BORDER);
        attTable.setFillsViewportHeight(true);

        attTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        attTable.getTableHeader().setBackground(ATTENDANCE_BACKGROUND);
        attTable.getTableHeader().setForeground(ATTENDANCE_DEEP_GREEN);

        JScrollPane attScroll = new JScrollPane(attTable);
        attScroll.setBorder(new LineBorder(ATTENDANCE_BORDER, 1));

        attPanel.add(attScroll, BorderLayout.CENTER);
        attPanel.add(attStatusLabel, BorderLayout.SOUTH);

        // Tab 2: Medicals Panel
        JPanel medPanel = new JPanel(new BorderLayout(0, 10));
        medPanel.setBackground(ATTENDANCE_BACKGROUND);
        medPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] medColumns = {"Medical ID", "Reg. No.", "Full Name", "Course Code", "Medical Date", "Reason", "Status", "Review Remarks"};
        medTableModel = new DefaultTableModel(medColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        medTable = new JTable(medTableModel);
        medTable.setRowHeight(34);
        medTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        medTable.setSelectionBackground(ATTENDANCE_BACKGROUND);
        medTable.setSelectionForeground(ATTENDANCE_DEEP_GREEN);
        medTable.setGridColor(ATTENDANCE_BORDER);
        medTable.setFillsViewportHeight(true);

        medTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        medTable.getTableHeader().setBackground(ATTENDANCE_BACKGROUND);
        medTable.getTableHeader().setForeground(ATTENDANCE_DEEP_GREEN);

        JScrollPane medScroll = new JScrollPane(medTable);
        medScroll.setBorder(new LineBorder(ATTENDANCE_BORDER, 1));

        medPanel.add(medScroll, BorderLayout.CENTER);
        medPanel.add(medStatusLabel, BorderLayout.SOUTH);

        tabbedPane.addTab("Attendance Summary", attPanel);
        tabbedPane.addTab("Medical Certificates", medPanel);

        add(topContainer, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        loadAssignedCourses();
    }

    private void loadAssignedCourses() {
        try {
            courseCombo.removeAllItems();
            courseMap.clear();

            List<Object[]> courses = courseDAO.getCoursesByLecturer(lecturerId);
            for (Object[] c : courses) {
                int courseId = (int) c[0];
                String courseCode = (String) c[1];
                String courseName = (String) c[2];
                String label = courseCode + " - " + courseName;
                courseCombo.addItem(label);
                courseMap.put(label, courseId);
            }

            if (courseCombo.getItemCount() > 0) {
                refreshAllData();
            }
        } catch (SQLException e) {
            System.err.println("Error loading courses: " + e.getMessage());
        }
    }

    private void refreshAllData() {
        loadAttendanceSummary();
        loadMedicals();
    }

    private void loadAttendanceSummary() {
        attTableModel.setRowCount(0);

        String selectedCourse = (String) courseCombo.getSelectedItem();
        Integer courseId = courseMap.get(selectedCourse);
        String component = (String) componentCombo.getSelectedItem();

        if (courseId == null) return;

        attStatusLabel.setText("Loading attendance data...");

        try {
            List<Object[]> rows = attDAO.getAttendanceSummary(lecturerId, courseId, component);
            for (Object[] row : rows) {
                attTableModel.addRow(row);
            }
            attStatusLabel.setText("Loaded attendance summary for " + rows.size() + " students.");
        } catch (SQLException e) {
            attStatusLabel.setText("Failed to load attendance summary.");
            System.err.println("Error loading attendance summary: " + e.getMessage());
        }
    }

    private void loadMedicals() {
        medTableModel.setRowCount(0);

        String selectedCourse = (String) courseCombo.getSelectedItem();
        Integer courseId = courseMap.get(selectedCourse);

        if (courseId == null) return;

        medStatusLabel.setText("Loading medical submissions...");

        try {
            List<Object[]> rows = attDAO.getMedicalsByLecturer(lecturerId, courseId);
            for (Object[] row : rows) {
                medTableModel.addRow(row);
            }
            medStatusLabel.setText("Loaded " + rows.size() + " medical records.");
        } catch (SQLException e) {
            medStatusLabel.setText("Failed to load medical records.");
            System.err.println("Error loading medical records: " + e.getMessage());
        }
    }
}
