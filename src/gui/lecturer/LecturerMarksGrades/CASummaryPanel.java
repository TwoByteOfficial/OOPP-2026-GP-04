package gui.lecturer.LecturerMarksGrades;

import dao.Lecturer.CASummaryDAO;
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

public class CASummaryPanel extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color PALE_GREEN = new Color(232, 245, 233);
    private static final Color BORDER_GREEN = new Color(200, 230, 201);
    private static final Color TEXT_GRAY = new Color(96, 112, 100);

    private final long lecturerId;
    private final CASummaryDAO caDAO = new CASummaryDAO();
    private final MarksGradesDAO courseDAO = new MarksGradesDAO();

    private final JComboBox<String> courseCombo = new JComboBox<>();
    private final Map<String, Integer> courseMap = new HashMap<>();

    private final DefaultTableModel tableModel;
    private final JTable caTable;
    private final JLabel statusLabel = new JLabel("Ready");

    public CASummaryPanel(long lecturerId) {
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
        courseCombo.addActionListener(e -> loadCASummary());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadCASummary());

        selectorPanel.add(courseLabel);
        selectorPanel.add(courseCombo);
        selectorPanel.add(refreshBtn);

        // CA Summary Table
        String[] columns = {"Registration No", "Student Name", "CA Mark", "CA Weight (%)", "CA Percentage", "CA Eligibility Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        caTable = new JTable(tableModel);
        caTable.setRowHeight(34);
        caTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        caTable.setSelectionBackground(PALE_GREEN);
        caTable.setSelectionForeground(DEEP_GREEN);
        caTable.setGridColor(BORDER_GREEN);
        caTable.setFillsViewportHeight(true);

        caTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        caTable.getTableHeader().setBackground(PALE_GREEN);
        caTable.getTableHeader().setForeground(DEEP_GREEN);

        JScrollPane scrollPane = new JScrollPane(caTable);
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
                loadCASummary();
            }
        } catch (SQLException e) {
            statusLabel.setText("Failed to load courses.");
            System.err.println("Error loading courses: " + e.getMessage());
        }
    }

    private void loadCASummary() {
        String selectedCourse = (String) courseCombo.getSelectedItem();
        Integer courseId = courseMap.get(selectedCourse);

        tableModel.setRowCount(0);

        if (courseId == null) return;

        statusLabel.setText("Loading CA summary...");

        try {
            List<Object[]> summaryRows = caDAO.getCASummary(courseId);
            int eligibleCount = 0;

            for (Object[] row : summaryRows) {
                String regNo = (String) row[0];
                String name = (String) row[1];
                BigDecimal caMark = (BigDecimal) row[2];
                BigDecimal caWeight = (BigDecimal) row[3];
                BigDecimal caPercent = (BigDecimal) row[4];
                String eligibility = (String) row[5];

                if ("Eligible".equalsIgnoreCase(eligibility)) {
                    eligibleCount++;
                }

                tableModel.addRow(new Object[]{
                        regNo,
                        name,
                        caMark != null ? caMark.toPlainString() : "0.00",
                        caWeight != null ? caWeight.toPlainString() + "%" : "40%",
                        caPercent != null ? caPercent.toPlainString() + "%" : "0.00%",
                        eligibility
                });
            }

            statusLabel.setText("Total Students: " + summaryRows.size() + " | CA Eligible (>=40%): " + eligibleCount);
        } catch (SQLException e) {
            statusLabel.setText("Failed to load CA summary.");
            JOptionPane.showMessageDialog(this, "Error loading CA summary: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
