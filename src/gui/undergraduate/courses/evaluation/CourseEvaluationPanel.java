package gui.undergraduate.courses.evaluation;

import dao.undergraduate.UndergraduateCourseDAO.EnrolledCourseDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel displaying dynamic evaluation scheme, rounded progress indicators, and pass rules.
 */
public class CourseEvaluationPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JLabel courseTitleLabel;
    private final JLabel lecturerLabel;
    private final JLabel creditsBadge;
    private final CustomProgressBar quizBar;
    private final JLabel quizValLabel;
    private final CustomProgressBar assignBar;
    private final JLabel assignValLabel;
    private final CustomProgressBar midBar;
    private final JLabel midValLabel;
    private final CustomProgressBar endExamBar;
    private final JLabel endExamValLabel;
    private final JLabel caWeightValLabel;
    private final JLabel finalWeightValLabel;

    public CourseEvaluationPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(16, 18, 16, 18));

        // 1. Header Section - Clean two-line title with badge on top right
        JPanel headerRow = new JPanel(new BorderLayout(10, 0));
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel titleGroup = new JPanel();
        titleGroup.setOpaque(false);
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));

        courseTitleLabel = new JLabel("Module Name");
        courseTitleLabel.setFont(AdminTheme.font(Font.BOLD, 15));
        courseTitleLabel.setForeground(AdminTheme.FOREST_GREEN);

        lecturerLabel = new JLabel("Lecturer Info");
        lecturerLabel.setFont(AdminTheme.font(Font.PLAIN, 12));
        lecturerLabel.setForeground(AdminTheme.MUTED);

        titleGroup.add(courseTitleLabel);
        titleGroup.add(Box.createVerticalStrut(2));
        titleGroup.add(lecturerLabel);

        creditsBadge = new JLabel("0 Credits", SwingConstants.CENTER);
        creditsBadge.setFont(AdminTheme.font(Font.BOLD, 11));
        creditsBadge.setForeground(AdminTheme.FOREST_GREEN);
        creditsBadge.setBackground(AdminTheme.MINT);
        creditsBadge.setOpaque(true);
        creditsBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));

        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(creditsBadge);

        headerRow.add(titleGroup, BorderLayout.CENTER);
        headerRow.add(badgeWrapper, BorderLayout.EAST);
        add(headerRow);

        add(Box.createVerticalStrut(12));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(0xEAEAEA));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(sep);
        add(Box.createVerticalStrut(12));

        JLabel evalHeading = new JLabel("Course Assessment & Evaluation Breakdown");
        evalHeading.setFont(AdminTheme.font(Font.BOLD, 13));
        evalHeading.setForeground(AdminTheme.DEEP_GREEN);
        evalHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(evalHeading);
        add(Box.createVerticalStrut(12));

        quizValLabel = new JLabel("0%");
        quizBar = new CustomProgressBar(0, AdminTheme.FOREST_GREEN);
        add(createEvalRow("Continuous Quizzes (Best 2 of 3)", quizValLabel, quizBar));
        add(Box.createVerticalStrut(10));

        assignValLabel = new JLabel("0%");
        assignBar = new CustomProgressBar(0, AdminTheme.FOREST_GREEN);
        add(createEvalRow("Assignments & Lab Practical Tasks", assignValLabel, assignBar));
        add(Box.createVerticalStrut(10));

        midValLabel = new JLabel("0%");
        midBar = new CustomProgressBar(0, AdminTheme.WARNING);
        add(createEvalRow("Mid-Semester Written Examination", midValLabel, midBar));
        add(Box.createVerticalStrut(10));

        endExamValLabel = new JLabel("0%");
        endExamBar = new CustomProgressBar(0, AdminTheme.FOREST_GREEN);
        add(createEvalRow("Final End-Semester Written Examination", endExamValLabel, endExamBar));

        add(Box.createVerticalStrut(14));

        JPanel weightRow = new JPanel(new GridLayout(1, 2, 10, 0));
        weightRow.setOpaque(false);
        weightRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        weightRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        caWeightValLabel = new JLabel("0%");
        finalWeightValLabel = new JLabel("0%");
        weightRow.add(createWeightPill("Continuous Assessment (CA)", caWeightValLabel));
        weightRow.add(createWeightPill("Final Written Exam", finalWeightValLabel));
        add(weightRow);

        add(Box.createVerticalStrut(12));

        JPanel noticeCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        noticeCard.setOpaque(true);
        noticeCard.setBackground(AdminTheme.MINT);
        noticeCard.setBorder(BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true));
        noticeCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        noticeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JLabel infoText = new JLabel("Passing Rule: Minimum CA mark 40% and Final Exam 40% mandatory to pass.");
        infoText.setFont(AdminTheme.font(Font.PLAIN, 11));
        infoText.setForeground(AdminTheme.DEEP_GREEN);
        noticeCard.add(infoText);
        add(noticeCard);
    }

    public void updateCourse(EnrolledCourseDTO data) {
        if (data == null) return;
        courseTitleLabel.setText(data.courseName + " (" + data.courseCode + ")");

        // Clean clean department title without ugly truncation
        String dept = data.departmentName != null ? data.departmentName : "Department of ICT";
        if (dept.toLowerCase().contains("information and communication technology") || dept.toLowerCase().contains("ict")) {
            dept = "Dept. of ICT";
        }
        lecturerLabel.setText("Coordinator: " + data.coordinatorName + " • " + dept);

        creditsBadge.setText(data.totalCredits + " Credits");

        int q = (int) Math.round(data.quizWeight);
        int a = (int) Math.round(data.assignmentWeight);
        int m = (int) Math.round(data.midTheoryWeight + data.midPracticalWeight);
        int fe = (int) Math.round(data.endTheoryWeight + data.endPracticalWeight);

        quizValLabel.setText(q + "%");
        quizBar.setProgress(q);

        assignValLabel.setText(a + "%");
        assignBar.setProgress(a);

        midValLabel.setText(m + "%");
        midBar.setProgress(m);

        endExamValLabel.setText(fe + "%");
        endExamBar.setProgress(fe);

        caWeightValLabel.setText((int) Math.round(data.caWeight) + "%");
        finalWeightValLabel.setText((int) Math.round(data.finalWeight) + "%");
    }

    private JPanel createWeightPill(String title, JLabel valLbl) {
        JPanel pill = new JPanel(new BorderLayout());
        pill.setOpaque(true);
        pill.setBackground(new Color(0xF4, 0xF9, 0xF5));
        pill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));

        JLabel tLbl = new JLabel(title);
        tLbl.setFont(AdminTheme.font(Font.PLAIN, 11));
        tLbl.setForeground(AdminTheme.MUTED);

        valLbl.setFont(AdminTheme.font(Font.BOLD, 12));
        valLbl.setForeground(AdminTheme.DEEP_GREEN);

        pill.add(tLbl, BorderLayout.WEST);
        pill.add(valLbl, BorderLayout.EAST);
        return pill;
    }

    private JPanel createEvalRow(String name, JLabel valLbl, CustomProgressBar bar) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel(name);
        title.setFont(AdminTheme.font(Font.PLAIN, 12));
        title.setForeground(new Color(0x33, 0x33, 0x33));

        valLbl.setFont(AdminTheme.font(Font.BOLD, 12));
        valLbl.setForeground(AdminTheme.DEEP_GREEN);

        header.add(title, BorderLayout.WEST);
        header.add(valLbl, BorderLayout.EAST);

        row.add(header, BorderLayout.NORTH);
        row.add(bar, BorderLayout.SOUTH);
        return row;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(AdminTheme.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.setColor(CARD_BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.dispose();
        super.paintComponent(g);
    }

    private static class CustomProgressBar extends JComponent {
        private int progress;
        private final Color fillColor;

        CustomProgressBar(int progress, Color fillColor) {
            this.progress = progress;
            this.fillColor = fillColor;
            setPreferredSize(new Dimension(0, 7));
        }

        void setProgress(int progress) {
            this.progress = progress;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            g2.setColor(AdminTheme.MINT);
            g2.fillRoundRect(0, 0, width, height, height, height);

            int fillWidth = (int) (width * (progress / 100.0));
            if (fillWidth > 0) {
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, fillWidth, height, height, height);
            }

            g2.dispose();
        }
    }
}