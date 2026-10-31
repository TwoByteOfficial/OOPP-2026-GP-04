package gui.lecturer.LecturerMarksGrades;

import dao.Lecturer.FinalResultsDAO;
import dao.Lecturer.MarksGradesDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinalResultsPanel extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color PALE_GREEN = new Color(232, 245, 233);
    private static final Color BORDER_GREEN = new Color(200, 230, 201);
    private static final Color TEXT_GRAY = new Color(96, 112, 100);

    private final long lecturerId;
    private final FinalResultsDAO resultsDAO = new FinalResultsDAO();
    private final MarksGradesDAO courseDAO = new MarksGradesDAO();

    private final JComboBox<String> courseCombo = new JComboBox<>();
    private final Map<String, Integer> courseMap = new HashMap<>();

    private final DefaultTableModel tableModel;
    private final JTable resultsTable;
    private final JLabel statusLabel = new JLabel("Ready");

    private boolean isUpdatingCombo = false;

    public FinalResultsPanel(long lecturerId) {
        this.lecturerId = lecturerId;

        setLayout(new BorderLayout(0, 15));
        setBackground(PALE_GREEN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Selector
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        selectorPanel.setOpaque(false);

        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        courseLabel.setForeground(DEEP_GREEN);

        courseCombo.setPreferredSize(new Dimension(300, 34));
        courseCombo.addActionListener(e -> {
            if (!isUpdatingCombo) {
                loadFinalResults();
            }
        });

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadFinalResults());

        selectorPanel.add(courseLabel);
        selectorPanel.add(courseCombo);
        selectorPanel.add(refreshBtn);

        // Final Results Table
        String[] columns = {
                "Reg. No.", "Student Name", "Credits", "CA Mark", "Final Exam", "Final Mark",
                "Attendance %", "Eligibility", "Grade", "Grade Point", "SGPA", "CGPA"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultsTable = new JTable(tableModel);
        resultsTable.setRowHeight(34);
        resultsTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        resultsTable.setSelectionBackground(PALE_GREEN);
        resultsTable.setSelectionForeground(DEEP_GREEN);
        resultsTable.setGridColor(BORDER_GREEN);
        resultsTable.setFillsViewportHeight(true);

        resultsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        resultsTable.getTableHeader().setBackground(PALE_GREEN);
        resultsTable.getTableHeader().setForeground(DEEP_GREEN);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(new LineBorder(BORDER_GREEN, 1));

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(statusLabel, BorderLayout.WEST);

        add(selectorPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        loadAssignedCourses();
    }

    private void loadAssignedCourses() {
        isUpdatingCombo = true;
        try {
            courseCombo.removeAllItems();
            courseMap.clear();

            List<Object[]> courses = courseDAO.getAssignedCourses(lecturerId);
            for (Object[] c : courses) {
                int courseId = (int) c[0];
                String courseCode = (String) c[1];
                String courseName = (String) c[2];
                String label = courseCode + " - " + courseName;
                courseCombo.addItem(label);
                courseMap.put(label, courseId);
            }
        } catch (SQLException e) {
            statusLabel.setText("Failed to load courses.");
            System.err.println("Error loading courses: " + e.getMessage());
        } finally {
            isUpdatingCombo = false;
        }

        if (courseCombo.getItemCount() > 0) {
            loadFinalResults();
        }
    }

    private void loadFinalResults() {
        String selectedCourse = (String) courseCombo.getSelectedItem();
        Integer courseId = courseMap.get(selectedCourse);

        tableModel.setRowCount(0);

        if (courseId == null) return;

        statusLabel.setText("Loading final results & grades...");

        try {
            List<Object[]> results = resultsDAO.getFinalResults(courseId);

            for (Object[] row : results) {
                // Bounds-safe array mapping for row indices 0..12
                String regNo = row.length > 0 && row[0] != null ? row[0].toString() : "";
                String name  = row.length > 1 && row[1] != null ? row[1].toString() : "";
                String credits = row.length > 2 && row[2] != null ? row[2].toString() : "2.0";
                String caMark = row.length > 3 && row[3] != null ? row[3].toString() : "0.00";
                String finalExam = row.length > 4 && row[4] != null ? row[4].toString() : "0.00";
                String finalMark = row.length > 5 && row[5] != null ? row[5].toString() : "0.00";
                String attPct = row.length > 6 && row[6] != null ? row[6].toString() + "%" : "0.0%";
                String eligibility = row.length > 7 && row[7] != null ? row[7].toString() : "Not Eligible";
                String grade = row.length > 8 && row[8] != null ? row[8].toString() : "F";
                String gradePoint = row.length > 9 && row[9] != null ? row[9].toString() : "0.00";
                String sgpa = row.length > 10 && row[10] != null ? row[10].toString() : "N/A";
                // Index 11 is semester_credits from DAO
                String cgpa = row.length > 12 && row[12] != null ? row[12].toString() : "N/A";

                tableModel.addRow(new Object[]{
                        regNo,
                        name,
                        credits,
                        caMark,
                        finalExam,
                        finalMark,
                        attPct,
                        eligibility,
                        grade,
                        gradePoint,
                        sgpa,
                        cgpa
                });
            }

            statusLabel.setText("Total Results Loaded: " + results.size());
        } catch (SQLException e) {
            statusLabel.setText("Failed to load final results.");
            JOptionPane.showMessageDialog(this, "Error loading final results: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}