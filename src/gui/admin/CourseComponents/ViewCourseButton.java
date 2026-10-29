package gui.admin.CourseComponents;

import dao.admin.AdminCourseDAO;
import gui.admin.AdminTheme;
import model.Course;
import model.CourseLecturer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ViewCourseButton - Integrated component file containing the "View Details" button
 * and its on-click modal dialog window for viewing complete course module details and CA/END assessment weights.
 */
public class ViewCourseButton extends JButton {

    public ViewCourseButton(CourseTable table, AdminCourseDAO courseDAO) {
        super("👁️ View Details");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setBackground(AdminTheme.PASTEL_GREEN);
        setForeground(AdminTheme.DEEP_GREEN);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> {
            Course selected = table.getSelectedCourse();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a course module from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            ViewCourseDialog dialog = new ViewCourseDialog(owner, selected, courseDAO);
            dialog.setVisible(true);
        });
    }

    /**
     * ViewCourseDialog - On-click popup modal displaying comprehensive course details and assessment structure.
     */
    public static class ViewCourseDialog extends JDialog {

        public ViewCourseDialog(Frame parent, Course course, AdminCourseDAO courseDAO) {
            super(parent, "Course Module Specifications: " + course.getCourseCode(), true);
            setSize(840, 720);
            setLocationRelativeTo(parent);
            setResizable(false);
            getContentPane().setBackground(AdminTheme.LIGHT_BG);
            setLayout(new BorderLayout());

            initUI(course, courseDAO);
        }

        private void initUI(Course course, AdminCourseDAO courseDAO) {
            // Header
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 16));
            headerPanel.setBackground(AdminTheme.DEEP_GREEN);

            JLabel lblHeader = new JLabel("Course Specifications: " + course.getCourseCode() + " - " + course.getCourseName());
            lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblHeader.setForeground(AdminTheme.WHITE);
            headerPanel.add(lblHeader);
            add(headerPanel, BorderLayout.NORTH);

            // Main Content Scroll Panel
            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBackground(AdminTheme.LIGHT_BG);
            content.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

            // Section 1: Basic Specifications
            JPanel pnlSpecs = new JPanel(new GridLayout(6, 2, 14, 10));
            pnlSpecs.setBackground(AdminTheme.WHITE);
            pnlSpecs.setBorder(createTitledBorder("1. Academic Specifications"));

            addDetailPair(pnlSpecs, "Course Code:", course.getCourseCode());
            addDetailPair(pnlSpecs, "Course Name:", course.getCourseName());
            addDetailPair(pnlSpecs, "Theory Credits:", course.getTheoryCredits() + " Credits");
            addDetailPair(pnlSpecs, "Practical Credits:", course.getPracticalCredits() + " Credits");
            addDetailPair(pnlSpecs, "Total Credits:", course.getTotalCredits() + " Credits");

            String deptText = "Department " + course.getDepartmentId();
            if (course.getDepartmentId() == 1) deptText = "ICT";
            else if (course.getDepartmentId() == 2) deptText = "Engineering Technology";
            else if (course.getDepartmentId() == 3) deptText = "Bio Systems Technology";

            addDetailPair(pnlSpecs, "Department:", deptText);
            addDetailPair(pnlSpecs, "Academic Year:", course.getAcademicYear());
            addDetailPair(pnlSpecs, "Semester:", course.getSemester());
            addDetailPair(pnlSpecs, "Status:", course.getStatus());
            addDetailPair(pnlSpecs, "Created At:", String.valueOf(course.getCreatedAt()));

            content.add(pnlSpecs);
            content.add(Box.createVerticalStrut(14));

            // Section 2: Assessment Weight Breakdown (CA Weight & END Weight)
            Map<String, BigDecimal> weights = (courseDAO != null) ? courseDAO.getCourseAssessmentWeights(course.getCourseId()) : null;

            BigDecimal quizW = getWeightVal(weights, "quiz");
            BigDecimal assignW = getWeightVal(weights, "assignment");
            BigDecimal midTheoryW = getWeightVal(weights, "midTheory");
            BigDecimal midPracticalW = getWeightVal(weights, "midPractical");
            BigDecimal endTheoryW = getWeightVal(weights, "endTheory");
            BigDecimal endPracticalW = getWeightVal(weights, "endPractical");

            BigDecimal calcCaWeight = quizW.add(assignW).add(midTheoryW).add(midPracticalW);
            BigDecimal calcEndWeight = endTheoryW.add(endPracticalW);

            JPanel pnlAssessments = new JPanel(new GridLayout(5, 2, 14, 10));
            pnlAssessments.setBackground(AdminTheme.WHITE);
            pnlAssessments.setBorder(createTitledBorder("2. Course Assessment Structure & Weight Breakdown (%)"));

            // CA Weight Components
            addDetailPair(pnlAssessments, "[CA] Max 2 Quiz Weight:", quizW + "%");
            addDetailPair(pnlAssessments, "[CA] Assignment Weight:", assignW + "%");
            addDetailPair(pnlAssessments, "[CA] Mid Exam Theory Weight:", midTheoryW + "%");
            addDetailPair(pnlAssessments, "[CA] Mid Exam Practical Weight:", midPracticalW + "%");

            // END Weight Components
            addDetailPair(pnlAssessments, "[END] End Exam Theory Weight:", endTheoryW + "%");
            addDetailPair(pnlAssessments, "[END] End Exam Practical Weight:", endPracticalW + "%");

            // Total Summaries
            addDetailPair(pnlAssessments, "Total CA Weight (Sum of CA):", calcCaWeight + "%");
            addDetailPair(pnlAssessments, "Total END Weight (Sum of END):", calcEndWeight + "%");

            content.add(pnlAssessments);
            content.add(Box.createVerticalStrut(14));

            // Section 3: Assigned Lecturers
            JPanel pnlLecturers = new JPanel(new BorderLayout(10, 10));
            pnlLecturers.setBackground(AdminTheme.WHITE);
            pnlLecturers.setBorder(createTitledBorder("3. Assigned Lecturers & Responsibilities"));

            DefaultListModel<String> listModel = new DefaultListModel<>();
            if (courseDAO != null) {
                try {
                    List<CourseLecturer> lecturers = courseDAO.getCourseLecturers(course.getCourseId());
                    if (lecturers == null || lecturers.isEmpty()) {
                        listModel.addElement("No lecturers assigned to this course yet.");
                    } else {
                        for (CourseLecturer cl : lecturers) {
                            listModel.addElement("Lecturer ID: " + cl.getLecturerId() + "  |  Responsibility: " + cl.getResponsibility());
                        }
                    }
                } catch (Exception e) {
                    listModel.addElement("Could not fetch assigned lecturers.");
                }
            } else {
                listModel.addElement("N/A");
            }

            JList<String> lstLecturers = new JList<>(listModel);
            lstLecturers.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lstLecturers.setVisibleRowCount(3);
            pnlLecturers.add(new JScrollPane(lstLecturers), BorderLayout.CENTER);

            content.add(pnlLecturers);

            JScrollPane scrollPane = new JScrollPane(content);
            scrollPane.setBorder(null);
            add(scrollPane, BorderLayout.CENTER);

            // Footer
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 14));
            footer.setBackground(AdminTheme.WHITE);
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AdminTheme.PASTEL_GREEN));

            JButton btnClose = new JButton("Close");
            btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btnClose.setBackground(AdminTheme.FOREST_GREEN);
            btnClose.setForeground(AdminTheme.WHITE);
            btnClose.setFocusPainted(false);
            btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnClose.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
            btnClose.addActionListener(e -> dispose());
            footer.add(btnClose);

            add(footer, BorderLayout.SOUTH);
        }

        private BigDecimal getWeightVal(Map<String, BigDecimal> map, String key) {
            if (map != null && map.containsKey(key)) {
                return map.get(key);
            }
            return BigDecimal.ZERO;
        }

        private void addDetailPair(JPanel pnl, String label, String value) {
            JPanel pair = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
            pair.setOpaque(false);

            JLabel lblKey = new JLabel(label);
            lblKey.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblKey.setForeground(AdminTheme.DEEP_GREEN);

            JLabel lblVal = new JLabel(value != null && !value.isEmpty() ? value : "N/A");
            lblVal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lblVal.setForeground(AdminTheme.BLACK);

            pair.add(lblKey);
            pair.add(lblVal);
            pnl.add(pair);
        }

        private TitledBorder createTitledBorder(String title) {
            TitledBorder tb = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1), title);
            tb.setTitleFont(new Font("Segoe UI", Font.BOLD, 15));
            tb.setTitleColor(AdminTheme.DEEP_GREEN);
            return tb;
        }
    }
}
