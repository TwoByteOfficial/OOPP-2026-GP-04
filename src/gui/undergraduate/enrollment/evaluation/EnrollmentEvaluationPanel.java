package gui.undergraduate.enrollment.evaluation;

import gui.admin.AdminTheme;
import gui.undergraduate.enrollment.model.EnrollmentCourseData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Course evaluation breakdown panel with slender 4px progress bars,
 * relaxed vertical rhythm, and an integrated bottom action container
 * that tightly concludes the card without empty space.
 * Displays assessment criteria (Quizzes, Assignments, Mid-Exam, End-Exam)
 * and provides contextual enrollment request or status feedback actions.
 */
public class EnrollmentEvaluationPanel extends JPanel {

    // Header labels for module title, credit allocation, and department metadata
    private final JLabel titleLbl = new JLabel("Course Title");
    private final JLabel creditBadge = new JLabel("0 Credits");
    private final JLabel subInfoLbl = new JLabel("Dept. of ICT • Faculty of Technology • Semester I");

    // Evaluation item percentage labels and custom slender progress bar components
    private final JLabel quizVal = new JLabel("20%");
    private final JComponent quizBar = createSlenderProgressBar(20);
    private final JLabel assignVal = new JLabel("30%");
    private final JComponent assignBar = createSlenderProgressBar(30);
    private final JLabel midVal = new JLabel("0%");
    private final JComponent midBar = createSlenderProgressBar(0);
    private final JLabel endVal = new JLabel("50%");
    private final JComponent endBar = createSlenderProgressBar(50);

    // Summary percentage labels for total CA and End-Semester exam weights
    private final JLabel caValLbl = new JLabel("50%");
    private final JLabel finalValLbl = new JLabel("50%");

    // Action container and enrollment request submission button
    private final JPanel actionContainer = new JPanel(new BorderLayout());
    private final JButton requestBtn = new JButton("Submit Enrollment Request");

    // Currently inspected course data and enrollment request action callback
    private EnrollmentCourseData currentCourse;
    private final Consumer<EnrollmentCourseData> onRequestEnrollment;

    public EnrollmentEvaluationPanel(Consumer<EnrollmentCourseData> onRequestEnrollment) {
        this.onRequestEnrollment = onRequestEnrollment;

        setLayout(new BorderLayout());
        setOpaque(false);

        // -------------------------------------------------------------
        // Rounded Card Container with Anti-Aliased Border
        // -------------------------------------------------------------
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw rounded white background card
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Draw subtle card border
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
        // 1. Header: Course Title & Pill-shaped Credit Badge
        // -------------------------------------------------------------
        JPanel headerRow = new JPanel(new BorderLayout(10, 0));
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        titleLbl.setFont(AdminTheme.font(Font.BOLD, 15));
        titleLbl.setForeground(AdminTheme.DEEP_GREEN);

        creditBadge.setFont(AdminTheme.font(Font.BOLD, 12));
        creditBadge.setForeground(AdminTheme.PRIMARY);

        // Custom rounded pill container for credit weight
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

        card.add(Box.createVerticalStrut(4));
        subInfoLbl.setFont(AdminTheme.font(Font.PLAIN, 12));
        subInfoLbl.setForeground(new Color(100, 116, 139));
        subInfoLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        subInfoLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        card.add(subInfoLbl);

        card.add(Box.createVerticalStrut(22));

        // -------------------------------------------------------------
        // 2. Section Title & Assessment Breakdown Progress Rows
        // -------------------------------------------------------------
        JLabel breakdownTitle = new JLabel("Course Assessment & Evaluation Breakdown");
        breakdownTitle.setFont(AdminTheme.font(Font.BOLD, 13));
        breakdownTitle.setForeground(AdminTheme.DEEP_GREEN);
        breakdownTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        breakdownTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        card.add(breakdownTitle);

        card.add(Box.createVerticalStrut(18));

        // Relaxed vertical gaps (22px) between assessment items
        card.add(createProgressRow("Continuous Quizzes (Best 2 of 3)", quizVal, quizBar));
        card.add(Box.createVerticalStrut(22));

        card.add(createProgressRow("Assignments & Lab Practical Tasks", assignVal, assignBar));
        card.add(Box.createVerticalStrut(22));

        card.add(createProgressRow("Mid-Semester Written Examination", midVal, midBar));
        card.add(Box.createVerticalStrut(22));

        card.add(createProgressRow("Final End-Semester Written Examination", endVal, endBar));
        card.add(Box.createVerticalStrut(26));

        // -------------------------------------------------------------
        // 3. Mini Summary Split Boxes (Continuous Assessment & Final Exam)
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

        JLabel ruleLbl = new JLabel("Passing Rule: Minimum CA mark 40% and Final Exam 40% mandatory to pass.");
        ruleLbl.setFont(AdminTheme.font(Font.PLAIN, 11));
        ruleLbl.setForeground(new Color(40, 100, 60));
        ruleBox.add(ruleLbl, BorderLayout.WEST);
        card.add(ruleBox);

        // Gap before action container
        card.add(Box.createVerticalStrut(18));

        // -------------------------------------------------------------
        // 5. Action Status Container (Anchored directly under passing rule)
        // -------------------------------------------------------------
        actionContainer.setOpaque(false);
        actionContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(actionContainer);

        // Anchored to NORTH so the card perfectly hugs the action button with zero empty bottom space
        add(card, BorderLayout.NORTH);

        // Configure submission button styling and click listener
        requestBtn.setFont(AdminTheme.font(Font.BOLD, 13));
        requestBtn.setBackground(AdminTheme.PRIMARY);
        requestBtn.setForeground(Color.WHITE);
        requestBtn.setFocusPainted(false);
        requestBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        requestBtn.setPreferredSize(new Dimension(0, 36));
        requestBtn.addActionListener(e -> {
            if (currentCourse != null && onRequestEnrollment != null) {
                onRequestEnrollment.accept(currentCourse);
            }
        });
    }

    /**
     * Factory method creating a compact bordered box for overall percentage weights.
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
     * Creates an assessment evaluation row containing label, percentage value, and slender progress bar.
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
     * Factory method creating a lightweight custom 4px slender progress bar with smooth anti-aliased fill.
     */
    private static JComponent createSlenderProgressBar(int initialPercent) {
        class SlenderBar extends JComponent {
            private int percent;

            SlenderBar(int p) {
                this.percent = p;
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

                // Track background
                g2.setColor(new Color(225, 240, 232));
                g2.fillRoundRect(0, 0, w, h, h, h);

                // Filled percentage progress
                int fillWidth = (int) Math.round((w * percent) / 100.0);
                if (fillWidth > 0) {
                    g2.setColor(AdminTheme.PRIMARY);
                    g2.fillRoundRect(0, 0, Math.max(fillWidth, h), h, h, h);
                }
                g2.dispose();
            }
        }
        return new SlenderBar(initialPercent);
    }

    /**
     * Helper invoking setPercent on custom SlenderBar component via reflection.
     */
    private void updateProgress(JComponent comp, int percent) {
        try {
            comp.getClass().getDeclaredMethod("setPercent", int.class).invoke(comp, percent);
        } catch (Exception ignored) {}
    }

    /**
     * Binds course details, computes assessment weights, updates progress bars,
     * and toggles the action container between Enrolled badge, Pending badge, and Request button.
     */
    public void displayCourse(EnrollmentCourseData c) {
        this.currentCourse = c;
        if (c == null) return;

        titleLbl.setText(c.getCourseName() + " (" + c.getCourseCode() + ")");
        creditBadge.setText((int) c.getTotalCredits() + " Credits");
        subInfoLbl.setText("Dept. of ICT • Faculty of Technology • " + c.getSemester());

        // Calculate proportional assessment breakdown weights
        int ca = (int) c.getCaWeight();
        int end = (int) c.getEndWeight();
        int quiz = Math.min(ca, 20);
        int assign = Math.max(0, ca - quiz);
        int mid = (c.getTotalCredits() >= 3) ? 10 : 0;
        int finalExam = Math.max(0, end - mid);

        // Update evaluation item labels and progress bars
        quizVal.setText(quiz + "%");
        updateProgress(quizBar, quiz);

        assignVal.setText(assign + "%");
        updateProgress(assignBar, assign);

        midVal.setText(mid + "%");
        updateProgress(midBar, mid);

        endVal.setText(finalExam + "%");
        updateProgress(endBar, finalExam);

        // Update overall CA and Final Exam summary values
        caValLbl.setText(ca + "%");
        finalValLbl.setText(end + "%");

        // Clear previous state from action container
        actionContainer.removeAll();

        // Dynamically render action container according to current enrollment status
        if ("ENROLLED".equals(c.getEnrollmentStatus())) {
            // Already enrolled badge banner with green checkmark icon
            JPanel enrolledCard = new JPanel(new BorderLayout(10, 0)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(235, 248, 240));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(new Color(180, 225, 195));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    g2.dispose();
                }
            };
            enrolledCard.setOpaque(false);
            enrolledCard.setBorder(new EmptyBorder(8, 12, 8, 12));

            JPanel checkIcon = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(AdminTheme.PRIMARY);
                    g2.fillOval(0, 0, 16, 16);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(4, 8, 7, 11);
                    g2.drawLine(7, 11, 12, 5);
                    g2.dispose();
                }
            };
            checkIcon.setPreferredSize(new Dimension(16, 16));
            checkIcon.setOpaque(false);

            JLabel enrolledLbl = new JLabel("Currently Enrolled & Active for Academic Year 2026");
            enrolledLbl.setFont(AdminTheme.font(Font.BOLD, 12));
            enrolledLbl.setForeground(AdminTheme.PRIMARY);

            enrolledCard.add(checkIcon, BorderLayout.WEST);
            enrolledCard.add(enrolledLbl, BorderLayout.CENTER);
            actionContainer.add(enrolledCard, BorderLayout.CENTER);

        } else if ("PENDING".equals(c.getEnrollmentStatus())) {
            // Pending approval badge banner with amber clock icon
            JPanel pendingCard = new JPanel(new BorderLayout(10, 0)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(254, 243, 199));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(new Color(252, 211, 77));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    g2.dispose();
                }
            };
            pendingCard.setOpaque(false);
            pendingCard.setBorder(new EmptyBorder(8, 12, 8, 12));

            JPanel clockIcon = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(217, 119, 6));
                    g2.fillOval(0, 0, 16, 16);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(8, 4, 8, 8);
                    g2.drawLine(8, 8, 11, 8);
                    g2.dispose();
                }
            };
            clockIcon.setPreferredSize(new Dimension(16, 16));
            clockIcon.setOpaque(false);

            JLabel pendingLbl = new JLabel("Enrollment Request Pending Admin Review");
            pendingLbl.setFont(AdminTheme.font(Font.BOLD, 12));
            pendingLbl.setForeground(new Color(180, 83, 9));

            pendingCard.add(clockIcon, BorderLayout.WEST);
            pendingCard.add(pendingLbl, BorderLayout.CENTER);
            actionContainer.add(pendingCard, BorderLayout.CENTER);

        } else {
            // Available for registration: render interactive submission button
            JPanel btnWrapper = new JPanel(new BorderLayout());
            btnWrapper.setOpaque(false);
            btnWrapper.add(requestBtn, BorderLayout.CENTER);
            actionContainer.add(btnWrapper, BorderLayout.CENTER);
        }

        revalidate();
        repaint();
    }
}