package gui.lecturer.Undergraduate;

import dao.Lecturer.CourseDAO;
import dao.Lecturer.UndergraduateDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gui.lecturer.SharedComponents.Colors.UNDERGRADUATES_BACKGROUND;
import static gui.lecturer.SharedComponents.Colors.UNDERGRADUATES_BORDER;
import static gui.lecturer.SharedComponents.Colors.UNDERGRADUATES_DEEP_GREEN;
import static gui.lecturer.SharedComponents.Colors.UNDERGRADUATES_TEXT_GRAY;
import static gui.lecturer.SharedComponents.Colors.UNDERGRADUATES_WHITE;

public class LecturerUndergraduates extends JPanel {

    private final long lecturerId;
    private final UndergraduateDAO ugDAO = new UndergraduateDAO();
    private final CourseDAO courseDAO = new CourseDAO();

    private final JComboBox<String> courseFilterCombo = new JComboBox<>();
    private final Map<String, Integer> courseMap = new HashMap<>();

    private final JTextField searchField = new JTextField(18);

    private final DefaultTableModel tableModel;
    private final JTable studentTable;
    private final JLabel statusLabel = new JLabel("Ready");

    public LecturerUndergraduates(long lecturerId) {
        this.lecturerId = lecturerId;

        setLayout(new BorderLayout(0, 20));
        setBackground(UNDERGRADUATES_BACKGROUND);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Heading
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);

        JLabel title = new JLabel("Enrolled Undergraduates");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(UNDERGRADUATES_DEEP_GREEN);

        JLabel subtitle = new JLabel("View and search undergraduate students enrolled in your course modules.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(UNDERGRADUATES_TEXT_GRAY);

        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        controlPanel.setOpaque(false);

        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        courseLabel.setForeground(UNDERGRADUATES_DEEP_GREEN);

        courseFilterCombo.setPreferredSize(new Dimension(260, 36));
        courseFilterCombo.addActionListener(e -> loadUndergraduates());

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        searchLabel.setForeground(UNDERGRADUATES_DEEP_GREEN);

        searchField.setPreferredSize(new Dimension(200, 36));

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        searchBtn.setBackground(UNDERGRADUATES_DEEP_GREEN);
        searchBtn.setForeground(UNDERGRADUATES_WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> loadUndergraduates());

        JButton viewDetailsBtn = new JButton("View Student Details");
        viewDetailsBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        viewDetailsBtn.setBackground(UNDERGRADUATES_DEEP_GREEN);
        viewDetailsBtn.setForeground(UNDERGRADUATES_WHITE);
        viewDetailsBtn.setFocusPainted(false);
        viewDetailsBtn.addActionListener(e -> showSelectedStudentDetails());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadUndergraduates());

        controlPanel.add(courseLabel);
        controlPanel.add(courseFilterCombo);
        controlPanel.add(searchLabel);
        controlPanel.add(searchField);
        controlPanel.add(searchBtn);
        controlPanel.add(viewDetailsBtn);
        controlPanel.add(refreshBtn);

        JPanel topContainer = new JPanel(new BorderLayout(0, 15));
        topContainer.setOpaque(false);
        topContainer.add(heading, BorderLayout.NORTH);
        topContainer.add(controlPanel, BorderLayout.SOUTH);

        // Table
        String[] columns = {"User ID", "Reg. No.", "Index No.", "Full Name", "Student Type", "Intake", "Level / Sem", "Email", "Contact No"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(34);
        studentTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        studentTable.setSelectionBackground(UNDERGRADUATES_BACKGROUND);
        studentTable.setSelectionForeground(UNDERGRADUATES_DEEP_GREEN);
        studentTable.setGridColor(UNDERGRADUATES_BORDER);
        studentTable.setFillsViewportHeight(true);

        studentTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        studentTable.getTableHeader().setBackground(UNDERGRADUATES_BACKGROUND);
        studentTable.getTableHeader().setForeground(UNDERGRADUATES_DEEP_GREEN);

        // Hide user_id column
        studentTable.removeColumn(studentTable.getColumnModel().getColumn(0));

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(new LineBorder(UNDERGRADUATES_BORDER, 1));

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(statusLabel, BorderLayout.WEST);

        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        loadAssignedCourses();
        loadUndergraduates();
    }

    private void loadAssignedCourses() {
        try {
            courseFilterCombo.removeAllItems();
            courseMap.clear();

            courseFilterCombo.addItem("All Courses");
            List<Object[]> courses = courseDAO.getCoursesByLecturer(lecturerId);
            for (Object[] c : courses) {
                int courseId = (int) c[0];
                String courseCode = (String) c[1];
                String courseName = (String) c[2];
                String label = courseCode + " - " + courseName;
                courseFilterCombo.addItem(label);
                courseMap.put(label, courseId);
            }
        } catch (SQLException e) {
            System.err.println("Error loading courses: " + e.getMessage());
        }
    }

    private void loadUndergraduates() {
        statusLabel.setText("Loading enrolled undergraduates...");
        tableModel.setRowCount(0);

        String selectedCourse = (String) courseFilterCombo.getSelectedItem();
        Integer courseId = courseMap.get(selectedCourse);
        String query = searchField.getText().trim();

        try {
            List<Object[]> students = ugDAO.getUndergraduatesByLecturer(lecturerId, courseId, query);
            for (Object[] s : students) {
                tableModel.addRow(s);
            }
            statusLabel.setText("Total Enrolled Undergraduates: " + students.size());
        } catch (SQLException e) {
            statusLabel.setText("Error loading undergraduates.");
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSelectedStudentDetails() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an undergraduate from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long studentUserId = (long) tableModel.getValueAt(row, 0);

        try {
            Object[] details = ugDAO.getUndergraduateDetails(studentUserId);
            if (details == null) {
                JOptionPane.showMessageDialog(this, "Student profile details not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Undergraduate Academic Profile", true);
            dialog.setLayout(new BorderLayout(0, 15));
            dialog.getContentPane().setBackground(UNDERGRADUATES_WHITE);

            JPanel header = new JPanel();
            header.setBackground(UNDERGRADUATES_BACKGROUND);
            header.setBorder(new EmptyBorder(15, 20, 15, 20));
            header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

            JLabel nameLabel = new JLabel((String) details[3]);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            nameLabel.setForeground(UNDERGRADUATES_DEEP_GREEN);

            JLabel regLabel = new JLabel("Reg No: " + details[1] + " | Index: " + (details[2] != null ? details[2] : "N/A"));
            regLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
            regLabel.setForeground(UNDERGRADUATES_TEXT_GRAY);

            header.add(nameLabel);
            header.add(Box.createVerticalStrut(4));
            header.add(regLabel);

            JPanel detailsPanel = new JPanel(new GridLayout(8, 2, 10, 10));
            detailsPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
            detailsPanel.setOpaque(false);

            detailsPanel.add(new JLabel("Student Type:"));
            detailsPanel.add(new JLabel((String) details[7]));

            detailsPanel.add(new JLabel("Current Level/Semester:"));
            detailsPanel.add(new JLabel("Level " + details[9] + ", Semester " + details[10]));

            detailsPanel.add(new JLabel("Email Address:"));
            detailsPanel.add(new JLabel((String) details[4]));

            detailsPanel.add(new JLabel("Contact Number:"));
            detailsPanel.add(new JLabel(details[5] != null ? (String) details[5] : "N/A"));

            detailsPanel.add(new JLabel("Guardian Name:"));
            detailsPanel.add(new JLabel(details[11] != null ? (String) details[11] : "N/A"));

            detailsPanel.add(new JLabel("Guardian Contact:"));
            detailsPanel.add(new JLabel(details[12] != null ? (String) details[12] : "N/A"));

            detailsPanel.add(new JLabel("Current SGPA:"));
            BigDecimal sgpa = (BigDecimal) details[13];
            JLabel sgpaLabel = new JLabel(sgpa != null ? sgpa.toPlainString() : "N/A");
            sgpaLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            sgpaLabel.setForeground(UNDERGRADUATES_DEEP_GREEN);
            detailsPanel.add(sgpaLabel);

            detailsPanel.add(new JLabel("Cumulative GPA (CGPA):"));
            BigDecimal cgpa = (BigDecimal) details[14];
            JLabel cgpaLabel = new JLabel(cgpa != null ? cgpa.toPlainString() : "N/A");
            cgpaLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            cgpaLabel.setForeground(UNDERGRADUATES_DEEP_GREEN);
            detailsPanel.add(cgpaLabel);

            JButton closeBtn = new JButton("Close");
            closeBtn.addActionListener(e -> dialog.dispose());

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
            btnPanel.setOpaque(false);
            btnPanel.add(closeBtn);

            dialog.add(header, BorderLayout.NORTH);
            dialog.add(detailsPanel, BorderLayout.CENTER);
            dialog.add(btnPanel, BorderLayout.SOUTH);

            dialog.setSize(480, 480);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching student details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
