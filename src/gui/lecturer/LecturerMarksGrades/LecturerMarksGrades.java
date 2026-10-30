package gui.lecturer.LecturerMarksGrades;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LecturerMarksGrades extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color PALE_GREEN = new Color(232, 245, 233);
    private static final Color TEXT_GRAY = new Color(96, 112, 100);

    private final JTabbedPane tabbedPane;

    public LecturerMarksGrades(long lecturerId) {
        setLayout(new BorderLayout(0, 15));
        setBackground(PALE_GREEN);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Heading
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);

        JLabel title = new JLabel("Marks, Grades & Eligibility");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(DEEP_GREEN);

        JLabel subtitle = new JLabel("Enter assessment marks, view CA summaries, eligibility status, and final grade reports.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);

        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(DEEP_GREEN);

        tabbedPane.addTab("Enter Marks", new EnterMarksPanel(lecturerId));
        tabbedPane.addTab("CA Summary", new CASummaryPanel(lecturerId));
        tabbedPane.addTab("Final Exam & Grades", new FinalResultsPanel(lecturerId));
        tabbedPane.addTab("Eligibility Overview", new EligibilityPanel(lecturerId));

        add(heading, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void selectTab(int index) {
        if (index >= 0 && index < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(index);
        }
    }
}
