package gui.undergraduate.courses;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Module 1.2: My Courses view.
 * Displays enrolled courses list with interactive selection and dynamic evaluation criteria.
 * Optimized with balanced BorderLayout to eliminate unwanted vertical gaps and maximize screen usage.
 */
public class UGCoursesPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private JTable coursesTable;
    private DefaultTableModel tableModel;

    // Dynamic evaluation components
    private JLabel courseTitleLabel;
    private JLabel lecturerLabel;
    private JLabel creditsBadge;
    private CustomProgressBar quizBar;
    private JLabel quizValLabel;
    private CustomProgressBar assignBar;
    private JLabel assignValLabel;
    private CustomProgressBar midBar;
    private JLabel midValLabel;
    private CustomProgressBar endExamBar;
    private JLabel endExamValLabel;

    private final Map<String, CourseEvaluationData> courseDataMap = new HashMap<>();

    public UGCoursesPanel(long userId, String username) {
        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        initSampleData();

        // 1. Top Wrapper: Header + Metric Strip (Fixed tight spacing)
        JPanel topWrapper = new JPanel(new BorderLayout(0, 14));
        topWrapper.setOpaque(false);
        topWrapper.add(buildHeaderSection(), BorderLayout.NORTH);
        topWrapper.add(buildTopMetricStrip(), BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        // 2. Center Content: Balanced Two-Column Split (Fills remaining screen height)
        add(buildSplitContentPanel(), BorderLayout.CENTER);

        // Pre-select first course to populate evaluation panel
        if (coursesTable != null && coursesTable.getRowCount() > 0) {
            coursesTable.setRowSelectionInterval(0, 0);
            updateEvaluationDetails((String) coursesTable.getValueAt(0, 0));
        }
    }

    private JPanel buildHeaderSection() {
        JPanel textGroup = new JPanel();
        textGroup.setOpaque(false);
        textGroup.setLayout(new BoxLayout(textGroup, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("My Enrolled Courses");
        title.setFont(AdminTheme.FONT_H1);
        title.setForeground(AdminTheme.DEEP_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Academic Year 2026/2027 • Semester I Active Enrollments");
        subtitle.setFont(AdminTheme.font(Font.PLAIN, 13));
        subtitle.setForeground(AdminTheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        textGroup.add(title);
        textGroup.add(Box.createVerticalStrut(2));
        textGroup.add(subtitle);

        return textGroup;
    }

    private JPanel buildTopMetricStrip() {
        JPanel strip = new JPanel(new GridLayout(1, 4, 15, 0));
        strip.setOpaque(false);
        strip.setPreferredSize(new Dimension(0, 75));

        strip.add(createMiniStatCard("16 Credits", "Registered Total", AdminTheme.FOREST_GREEN));
        strip.add(createMiniStatCard("5 Modules", "Core Mandatory", AdminTheme.FOREST_GREEN));
        strip.add(createMiniStatCard("1 Module", "Elective Track", AdminTheme.FOREST_GREEN));
        strip.add(createMiniStatCard("100% Valid", "Dean Registered", AdminTheme.FOREST_GREEN));

        return strip;
    }

    private JPanel createMiniStatCard(String mainVal, String subVal, Color badgeColor) {
        JPanel card = createCurvedPanel();
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel badge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(badgeColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(38, 38));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel valLbl = new JLabel(mainVal);
        valLbl.setFont(AdminTheme.font(Font.BOLD, 14));
        valLbl.setForeground(AdminTheme.DEEP_GREEN);

        JLabel subLbl = new JLabel(subVal);
        subLbl.setFont(AdminTheme.font(Font.PLAIN, 12));
        subLbl.setForeground(AdminTheme.MUTED);

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(valLbl);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(subLbl);
        textPanel.add(Box.createVerticalGlue());

        card.add(badge, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildSplitContentPanel() {
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        splitPanel.setOpaque(false);

        splitPanel.add(buildCourseTableCard());
        splitPanel.add(buildEvaluationDetailsCard());

        return splitPanel;
    }

    private JPanel buildCourseTableCard() {
        JPanel card = createCurvedPanel();
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel cardTitle = new JLabel("Registered Module Directory");
        cardTitle.setFont(AdminTheme.font(Font.BOLD, 16));
        cardTitle.setForeground(AdminTheme.FOREST_GREEN);

        JLabel helperText = new JLabel("Click any module to inspect criteria");
        helperText.setFont(AdminTheme.font(Font.PLAIN, 12));
        helperText.setForeground(AdminTheme.MUTED);

        titlePanel.add(cardTitle, BorderLayout.WEST);
        titlePanel.add(helperText, BorderLayout.EAST);
        card.add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"Code", "Course Name", "Credits", "Type"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableModel.addRow(new Object[]{"ICT2112", "Object Oriented Programming", "3", "Core"});
        tableModel.addRow(new Object[]{"ICT2123", "Database Management Systems", "3", "Core"});
        tableModel.addRow(new Object[]{"ICT2133", "Software Engineering Essentials", "3", "Core"});
        tableModel.addRow(new Object[]{"ICT2142", "Data Communication & Networks", "2", "Core"});
        tableModel.addRow(new Object[]{"ICT2152", "Operating Systems", "2", "Core"});
        tableModel.addRow(new Object[]{"ENG2113", "Technical Communication", "3", "Elective"});

        coursesTable = new JTable(tableModel);
        coursesTable.setFont(AdminTheme.font(Font.PLAIN, 13));
        coursesTable.setRowHeight(40); // Increased row height for clean vertical balance
        coursesTable.setShowGrid(false);
        coursesTable.setIntercellSpacing(new Dimension(0, 0));
        coursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        coursesTable.setSelectionBackground(AdminTheme.PASTEL_GREEN);
        coursesTable.setSelectionForeground(AdminTheme.DEEP_GREEN);

        JTableHeader header = coursesTable.getTableHeader();
        header.setFont(AdminTheme.font(Font.BOLD, 13));
        header.setBackground(AdminTheme.MINT);
        header.setForeground(AdminTheme.DEEP_GREEN);
        header.setPreferredSize(new Dimension(0, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD2, 0xD7, 0xE1)));

        coursesTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        coursesTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        coursesTable.getColumnModel().getColumn(2).setPreferredWidth(55);
        coursesTable.getColumnModel().getColumn(3).setPreferredWidth(75);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setBorder(new EmptyBorder(0, 12, 0, 12));
        for (int i = 0; i < coursesTable.getColumnCount(); i++) {
            coursesTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }

        coursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = coursesTable.getSelectedRow();
                if (selectedRow != -1) {
                    String courseCode = (String) coursesTable.getValueAt(selectedRow, 0);
                    updateEvaluationDetails(courseCode);
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(coursesTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(0xEAEAEA)));
        tableScroll.getViewport().setBackground(AdminTheme.WHITE);
        tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        card.add(tableScroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildEvaluationDetailsCard() {
        JPanel card = createCurvedPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        // Header Section
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel titleGroup = new JPanel();
        titleGroup.setOpaque(false);
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));

        courseTitleLabel = new JLabel("Object Oriented Programming (ICT2112)");
        courseTitleLabel.setFont(AdminTheme.font(Font.BOLD, 16));
        courseTitleLabel.setForeground(AdminTheme.FOREST_GREEN);

        lecturerLabel = new JLabel("Lecturer: Dr. Kamal Perera • Department of ICT");
        lecturerLabel.setFont(AdminTheme.font(Font.PLAIN, 13));
        lecturerLabel.setForeground(AdminTheme.MUTED);

        titleGroup.add(courseTitleLabel);
        titleGroup.add(Box.createVerticalStrut(3));
        titleGroup.add(lecturerLabel);

        creditsBadge = new JLabel("3 Credits");
        creditsBadge.setFont(AdminTheme.font(Font.BOLD, 12));
        creditsBadge.setForeground(AdminTheme.FOREST_GREEN);
        creditsBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.FOREST_GREEN, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));

        headerRow.add(titleGroup, BorderLayout.WEST);
        headerRow.add(creditsBadge, BorderLayout.EAST);
        card.add(headerRow);

        card.add(Box.createVerticalStrut(14));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(0xEAEAEA));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sep);
        card.add(Box.createVerticalStrut(14));

        JLabel evalHeading = new JLabel("Course Assessment & Evaluation Breakdown");
        evalHeading.setFont(AdminTheme.font(Font.BOLD, 14));
        evalHeading.setForeground(AdminTheme.DEEP_GREEN);
        evalHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(evalHeading);
        card.add(Box.createVerticalStrut(14));

        // Progress breakdown bars
        quizValLabel = new JLabel("10%");
        quizBar = new CustomProgressBar(10, AdminTheme.FOREST_GREEN);
        card.add(createEvalRow("Continuous Quizzes (Best 2 of 3)", quizValLabel, quizBar));
        card.add(Box.createVerticalStrut(12));

        assignValLabel = new JLabel("15%");
        assignBar = new CustomProgressBar(15, AdminTheme.FOREST_GREEN);
        card.add(createEvalRow("Assignments & Lab Practical Tasks", assignValLabel, assignBar));
        card.add(Box.createVerticalStrut(12));

        midValLabel = new JLabel("15%");
        midBar = new CustomProgressBar(15, AdminTheme.WARNING);
        card.add(createEvalRow("Mid-Semester Written Examination", midValLabel, midBar));
        card.add(Box.createVerticalStrut(12));

        endExamValLabel = new JLabel("60%");
        endExamBar = new CustomProgressBar(60, AdminTheme.FOREST_GREEN);
        card.add(createEvalRow("Final End-Semester Written Examination", endExamValLabel, endExamBar));

        card.add(Box.createVerticalStrut(18));

        // CA & Final Exam weighting pill cards
        JPanel weightRow = new JPanel(new GridLayout(1, 2, 10, 0));
        weightRow.setOpaque(false);
        weightRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        weightRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        weightRow.add(createWeightPill("Continuous Assessment (CA)", "40%"));
        weightRow.add(createWeightPill("Final Written Exam", "60%"));
        card.add(weightRow);

        card.add(Box.createVerticalStrut(12));

        // Policy rule notice
        JPanel noticeCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        noticeCard.setOpaque(true);
        noticeCard.setBackground(AdminTheme.MINT);
        noticeCard.setBorder(BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true));
        noticeCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        noticeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel infoText = new JLabel("Passing Rule: Minimum CA mark 40% and Final Exam 40% mandatory to pass.");
        infoText.setFont(AdminTheme.font(Font.PLAIN, 12));
        infoText.setForeground(AdminTheme.DEEP_GREEN);
        noticeCard.add(infoText);
        card.add(noticeCard);

        return card;
    }

    private JPanel createWeightPill(String title, String val) {
        JPanel pill = new JPanel(new BorderLayout());
        pill.setOpaque(true);
        pill.setBackground(new Color(0xF4, 0xF9, 0xF5));
        pill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JLabel tLbl = new JLabel(title);
        tLbl.setFont(AdminTheme.font(Font.PLAIN, 11));
        tLbl.setForeground(AdminTheme.MUTED);

        JLabel vLbl = new JLabel(val);
        vLbl.setFont(AdminTheme.font(Font.BOLD, 12));
        vLbl.setForeground(AdminTheme.DEEP_GREEN);

        pill.add(tLbl, BorderLayout.WEST);
        pill.add(vLbl, BorderLayout.EAST);
        return pill;
    }

    private JPanel createEvalRow(String name, JLabel valLbl, CustomProgressBar bar) {
        JPanel row = new JPanel(new BorderLayout(0, 5));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel(name);
        title.setFont(AdminTheme.font(Font.PLAIN, 13));
        title.setForeground(new Color(0x33, 0x33, 0x33));

        valLbl.setFont(AdminTheme.font(Font.BOLD, 13));
        valLbl.setForeground(AdminTheme.DEEP_GREEN);

        header.add(title, BorderLayout.WEST);
        header.add(valLbl, BorderLayout.EAST);

        row.add(header, BorderLayout.NORTH);
        row.add(bar, BorderLayout.SOUTH);
        return row;
    }

    private void updateEvaluationDetails(String courseCode) {
        CourseEvaluationData data = courseDataMap.get(courseCode);
        if (data != null) {
            courseTitleLabel.setText(data.courseName + " (" + courseCode + ")");
            lecturerLabel.setText("Lecturer: " + data.lecturer + " • " + data.department);
            creditsBadge.setText(data.credits + " Credits");

            quizValLabel.setText(data.quiz + "%");
            quizBar.setProgress(data.quiz);

            assignValLabel.setText(data.assignment + "%");
            assignBar.setProgress(data.assignment);

            midValLabel.setText(data.midExam + "%");
            midBar.setProgress(data.midExam);

            endExamValLabel.setText(data.endExam + "%");
            endExamBar.setProgress(data.endExam);
        }
    }

    private void initSampleData() {
        courseDataMap.put("ICT2112", new CourseEvaluationData("Object Oriented Programming", "Dr. Kamal Perera", "Department of ICT", 3, 10, 15, 15, 60));
        courseDataMap.put("ICT2123", new CourseEvaluationData("Database Management Systems", "Prof. Nimal Silva", "Department of ICT", 3, 10, 20, 10, 60));
        courseDataMap.put("ICT2133", new CourseEvaluationData("Software Engineering Essentials", "Dr. Sumith Bandara", "Department of ICT", 3, 15, 15, 10, 60));
        courseDataMap.put("ICT2142", new CourseEvaluationData("Data Communication & Networks", "Ms. Ruwani Fernando", "Department of ICT", 2, 10, 10, 20, 60));
        courseDataMap.put("ICT2152", new CourseEvaluationData("Operating Systems", "Mr. Asela Weerasinghe", "Department of ICT", 2, 10, 15, 15, 60));
        courseDataMap.put("ENG2113", new CourseEvaluationData("Technical Communication", "Dr. Priyantha Dissanayake", "Department of Languages", 3, 20, 20, 0, 60));
    }

    private JPanel createCurvedPanel() {
        JPanel panel = new JPanel() {
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
        };
        panel.setOpaque(false);
        return panel;
    }

    /**
     * Custom curved progress bar mimicking modern rounded pill indicators.
     */
    private static class CustomProgressBar extends JComponent {
        private int progress;
        private final Color fillColor;

        CustomProgressBar(int progress, Color fillColor) {
            this.progress = progress;
            this.fillColor = fillColor;
            setPreferredSize(new Dimension(0, 8));
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

    private static class CourseEvaluationData {
        String courseName;
        String lecturer;
        String department;
        int credits;
        int quiz;
        int assignment;
        int midExam;
        int endExam;

        CourseEvaluationData(String courseName, String lecturer, String department, int credits, int quiz, int assignment, int midExam, int endExam) {
            this.courseName = courseName;
            this.lecturer = lecturer;
            this.department = department;
            this.credits = credits;
            this.quiz = quiz;
            this.assignment = assignment;
            this.midExam = midExam;
            this.endExam = endExam;
        }
    }
}