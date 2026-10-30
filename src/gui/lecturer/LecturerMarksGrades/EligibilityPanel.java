package gui.lecturer.LecturerMarksGrades;

import dao.Lecturer.EligibilityDAO;
import dao.Lecturer.MarksGradesDAO;

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

public class EligibilityPanel extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color PALE_GREEN = new Color(232, 245, 233);
    private static final Color BORDER_GREEN = new Color(200, 230, 201);

    private final long lecturerId;
    private final EligibilityDAO eligibilityDAO = new EligibilityDAO();
    private final MarksGradesDAO courseDAO = new MarksGradesDAO();

    private final JComboBox<String> courseCombo = new JComboBox<>();
    private final Map<String, Integer> courseMap = new HashMap<>();

    private final DefaultTableModel tableModel;
    private final JTable eligibilityTable;
    private final JLabel statusLabel = new JLabel("Ready");

    public EligibilityPanel(long lecturerId) {
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
        courseCombo.addActionListener(e -> loadEligibility());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadEligibility());

        selectorPanel.add(courseLabel);
        selectorPanel.add(courseCombo);
        selectorPanel.add(refreshBtn);

        // Eligibility Table
        String[] columns = {
                "Registration No", "Student Name", "Attendance %", "CA Mark",
                "Attendance Req (>=80%)", "CA Req (>=40%)", "Final Exam Eligibility"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        eligibilityTable = new JTable(tableModel);
        eligibilityTable.setRowHeight(34);
        eligibilityTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        eligibilityTable.setSelectionBackground(PALE_GREEN);
        eligibilityTable.setSelectionForeground(DEEP_GREEN);
        eligibilityTable.setGridColor(BORDER_GREEN);
        eligibilityTable.setFillsViewportHeight(true);

        eligibilityTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        eligibilityTable.getTableHeader().setBackground(PALE_GREEN);
        eligibilityTable.getTableHeader().setForeground(DEEP_GREEN);

        JScrollPane scrollPane = new JScrollPane(eligibilityTable);
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

            if (courseCombo.getItemCount() > 0) {
                loadEligibility();
            }
        } catch (SQLException e) {
            statusLabel.setText("Failed to load courses.");
            System.err.println("Error loading courses: " + e.getMessage());
        }
    }

    private void loadEligibility() {
        String selectedCourse = (String) courseCombo.getSelectedItem();
        Integer courseId = courseMap.get(selectedCourse);

        tableModel.setRowCount(0);

        if (courseId == null) return;

        statusLabel.setText("Loading eligibility criteria...");

        try {
            List<Object[]> rows = eligibilityDAO.getEligibilityRows(courseId);
            int eligibleCount = 0;

            for (Object[] row : rows) {
                String regNo = (String) row[0];
                String name = (String) row[1];
                BigDecimal attPct = (BigDecimal) row[2];
                BigDecimal caPct = (BigDecimal) row[3];
                String attReq = (String) row[4];
                String caReq = (String) row[5];
                String finalEligibility = (String) row[6];

                if ("Eligible".equalsIgnoreCase(finalEligibility)) {
                    eligibleCount++;
                }

                tableModel.addRow(new Object[]{
                        regNo,
                        name,
                        attPct != null ? attPct.toPlainString() + "%" : "0.00%",
                        caPct != null ? caPct.toPlainString() + "%" : "0.00%",
                        attReq,
                        caReq,
                        finalEligibility
                });
            }

            statusLabel.setText("Total Enrolled: " + rows.size() + " | Eligible to Sit for Final Exam: " + eligibleCount);
        } catch (SQLException e) {
            statusLabel.setText("Failed to load eligibility.");
            JOptionPane.showMessageDialog(this, "Error loading eligibility: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
