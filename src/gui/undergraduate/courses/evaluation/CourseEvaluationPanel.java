package gui.undergraduate.courses.evaluation;

import dao.undergraduate.UndergraduateCourseDAO.EnrolledCourseDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Course assessment breakdown panel with optimized vertical spacing
 * and auto-formatted department naming to avoid text truncation.
 * Visualizes continuous assessments (CA) and final exam weight percentages.
 */
public class CourseEvaluationPanel extends JPanel {

    // Header labels and badge components
    private final JLabel titleLbl = new JLabel("Course Title");
    private final JLabel creditBadge = new JLabel("0 Credits");
    private final JLabel subInfoLbl = new JLabel("Coordinator: Staff • Dept. of ICT");

    // Assessment component weight labels and slender progress bar components
    private final JLabel quizVal = new JLabel("10%");
    private final JComponent quizBar = createSlenderProgressBar(10, AdminTheme.PRIMARY);

    private final JLabel assignVal = new JLabel("10%");
    private final JComponent assignBar = createSlenderProgressBar(10, AdminTheme.PRIMARY);

    private final JLabel midVal = new JLabel("20%");
    private final JComponent midBar = createSlenderProgressBar(20, new Color(230, 126, 34));

    private final JLabel endVal = new JLabel("60%");
    private final JComponent endBar = createSlenderProgressBar(60, AdminTheme.PRIMARY);

    // Summary percentage labels for CA vs Final exam aggregate splits
    private final JLabel caValLbl = new JLabel("40%");
    private final JLabel finalValLbl = new JLabel("60%");

    public CourseEvaluationPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // -------------------------------------------------------------
        // Outer Card Container (Custom Rounded White Card)
        // -------------------------------------------------------------
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw rounded white background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Draw card border outline
                g2.setColor(new Color(225, 235, 228));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        // -------------------------------------------------------------
        // 1. Header: Course Title & Credit Badge
        // -------------------------------------------------------------
        JPanel headerRow = new JPanel(new BorderLayout(10, 0));
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        titleLbl.setFont(AdminTheme.font(Font.BOLD, 15));
        titleLbl.setForeground(AdminTheme.DEEP_GREEN);

        creditBadge.setFont(AdminTheme.font(Font.BOLD, 12));
        creditBadge.setForeground(AdminTheme.PRIMARY);

        // Rounded pill badge wrapper for credit value display
        JPanel pillBadge = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 248, 243));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(new Color(185, 220, 198));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
            }
        };
        pillBadge.setOpaque(false);
        pillBadge.setBorder(new EmptyBorder(3, 8, 3, 8));
        pillBadge.add(creditBadge, BorderLayout.CENTER);

        headerRow.add(titleLbl, BorderLayout.WEST);
        headerRow.add(pillBadge, BorderLayout.EAST);
        card.add(headerRow);

        // Course coordinator and department metadata sub-label
        card.add(Box.createVerticalStrut(4));
        subInfoLbl.setFont(AdminTheme.font(Font.PLAIN, 12));
        subInfoLbl.setForeground(new Color(100, 116, 139));
        subInfoLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        subInfoLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        card.add(subInfoLbl);

        card.add(Box.createVerticalStrut(22));

        // -------------------------------------------------------------
        // 2. Section Title & Assessment Component Rows
        // -------------------------------------------------------------
        JLabel breakdownTitle = new JLabel("Course Assessment & Evaluation Breakdown");
        breakdownTitle.setFont(AdminTheme.font(Font.BOLD, 13));
        breakdownTitle.setForeground(AdminTheme.DEEP_GREEN);
        breakdownTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        breakdownTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        card.add(breakdownTitle);

        card.add(Box.createVerticalStrut(18));

        // Quizzes weight row
        card.add(createProgressRow("Continuous Quizzes (Best 2 of 3)", quizVal, quizBar));
        card.add(Box.createVerticalStrut(22));

        // Assignments and lab practicals weight row
        card.add(createProgressRow("Assignments & Lab Practical Tasks", assignVal, assignBar));
        card.add(Box.createVerticalStrut(22));

        // Mid-semester exam weight row
        card.add(createProgressRow("Mid-Semester Written Examination", midVal, midBar));
        card.add(Box.createVerticalStrut(22));

        // Final end-semester written exam weight row
        card.add(createProgressRow("Final End-Semester Written Examination", endVal, endBar));
        card.add(Box.createVerticalStrut(26));

        // -------------------------------------------------------------
        // 3. Side-by-side Metric Split Boxes (CA vs Final Exam)
        // -------------------------------------------------------------
        JPanel summaryRow = new JPanel(new GridLayout(1, 2, 10, 0));
        summaryRow.setOpaque(false);
        summaryRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        summaryRow.add(createSummaryMiniBox("Continuous Assessment (CA)", caValLbl));
        summaryRow.add(createSummaryMiniBox("Final Written Exam", finalValLbl));
        card.add(summaryRow);

        card.add(Box.createVerticalStrut(14));

        // -------------------------------------------------------------
        // 4. Passing Rule Notice Box
        // -------------------------------------------------------------
        JPanel ruleBox = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 248, 242));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(new Color(200, 230, 210));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
            }
        };
        ruleBox.setOpaque(false);
        ruleBox.setBorder(new EmptyBorder(6, 12, 6, 12));
        ruleBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        ruleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Regulation: 40% minimum CA and 40% minimum final exam threshold to pass
        JLabel ruleLbl = new JLabel("Passing Rule: Minimum CA mark 40% and Final Exam 40% mandatory to pass.");
        ruleLbl.setFont(AdminTheme.font(Font.PLAIN, 11));
        ruleLbl.setForeground(new Color(40, 100, 60));
        ruleBox.add(ruleLbl, BorderLayout.WEST);
        card.add(ruleBox);

        // Anchor card container to NORTH to eliminate bottom trailing dead space
        add(card, BorderLayout.NORTH);
    }

    /**
     * Constructs a compact summary mini box for overall weight distributions.
     */
    private JPanel createSummaryMiniBox(String title, JLabel valueLbl) {
        JPanel box = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(new Color(210, 230, 218));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
            }
        };
        box.setOpaque(false);
        box.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(AdminTheme.font(Font.PLAIN, 12));
        titleLbl.setForeground(AdminTheme.DEEP_GREEN);

        valueLbl.setFont(AdminTheme.font(Font.BOLD, 12));
        valueLbl.setForeground(AdminTheme.DEEP_GREEN);
        valueLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        box.add(titleLbl, BorderLayout.WEST);
        box.add(valueLbl, BorderLayout.EAST);
        return box;
    }

    /**
     * Creates an assessment component row with a title, percentage text, and slender progress bar.
     */
    private JPanel createProgressRow(String label, JLabel valLabel, JComponent bar) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel textRow = new JPanel(new BorderLayout());
        textRow.setOpaque(false);
        textRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        textRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        JLabel nameLbl = new JLabel(label);
        nameLbl.setFont(AdminTheme.font(Font.PLAIN, 12));
        nameLbl.setForeground(new Color(50, 65, 55));

        valLabel.setFont(AdminTheme.font(Font.BOLD, 12));
        valLabel.setForeground(AdminTheme.DEEP_GREEN);

        textRow.add(nameLbl, BorderLayout.WEST);
        textRow.add(valLabel, BorderLayout.EAST);

        container.add(textRow);
        container.add(Box.createVerticalStrut(5));
        container.add(bar);
        return container;
    }

    /**
     * Factory method creating a lightweight custom progress bar component.
     */
    private static JComponent createSlenderProgressBar(int initialPercent, Color fillColor) {
        class SlenderBar extends JComponent {
            private int percent;
            private final Color fill;

            SlenderBar(int p, Color f) {
                this.percent = p;
                this.fill = f;
                setPreferredSize(new Dimension(0, 4));
                setMinimumSize(new Dimension(0, 4));
                setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
                setAlignmentX(Component.LEFT_ALIGNMENT);
            }

            void setPercent(int p) {
                this.percent = p;
                repaint();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Background track
                g2.setColor(new Color(225, 240, 232));
                g2.fillRoundRect(0, 0, w, h, h, h);

                // Foreground active fill
                int fillWidth = (int) Math.round((w * percent) / 100.0);
                if (fillWidth > 0) {
                    g2.setColor(fill);
                    g2.fillRoundRect(0, 0, Math.max(fillWidth, h), h, h, h);
                }
                g2.dispose();
            }
        }
        return new SlenderBar(initialPercent, fillColor);
    }

    /**
     * Reflectively invokes the internal setPercent method on custom progress bar instances.
     */
    private void updateProgress(JComponent comp, int percent) {
        try {
            comp.getClass().getDeclaredMethod("setPercent", int.class).invoke(comp, percent);
        } catch (Exception ignored) {}
    }

    /**
     * Binds enrolled course evaluation criteria and assessment weights from DTO to UI components.
     */
    public void updateCourse(EnrolledCourseDTO c) {
        if (c == null) return;

        titleLbl.setText(c.courseName + " (" + c.courseCode + ")");
        creditBadge.setText(c.totalCredits + " Credits");

        // Format department name cleanly to prevent ellipsis truncation
        String dept = c.departmentName;
        if (dept != null && (dept.contains("Information and Communication") || dept.length() > 20)) {
            dept = "Dept. of ICT";
        }
        subInfoLbl.setText("Coordinator: " + c.coordinatorName + " • " + dept);

        // Extract component percentages from DTO
        int quiz = (int) c.quizWeight;
        int assign = (int) c.assignmentWeight;
        int mid = (int) (c.midTheoryWeight + c.midPracticalWeight);
        int finalExam = (int) c.finalWeight;

        // Update labels and progress bar components
        quizVal.setText(quiz + "%");
        updateProgress(quizBar, quiz);

        assignVal.setText(assign + "%");
        updateProgress(assignBar, assign);

        midVal.setText(mid + "%");
        updateProgress(midBar, mid);

        endVal.setText(finalExam + "%");
        updateProgress(endBar, finalExam);

        // Update summary boxes for overall CA and Final Exam weights
        caValLbl.setText((int) c.caWeight + "%");
        finalValLbl.setText(finalExam + "%");

        revalidate();
        repaint();
    }
}