package gui.lecturer.LecturerMarksGrades;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static gui.lecturer.SharedComponents.Colors.MARKS_BACKGROUND;
import static gui.lecturer.SharedComponents.Colors.MARKS_DEEP_GREEN;
import static gui.lecturer.SharedComponents.Colors.MARKS_TEXT_GRAY;
import static gui.lecturer.SharedComponents.Colors.MARKS_WHITE;

public class LecturerMarksGrades extends JPanel {

    private final JTabbedPane tabbedPane;

    public LecturerMarksGrades(long lecturerId) {
        setLayout(new BorderLayout(0, 15));
        setBackground(MARKS_BACKGROUND);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Heading
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);

        JLabel title = new JLabel("Marks, Grades & Eligibility");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(MARKS_DEEP_GREEN);

        JLabel subtitle = new JLabel("Enter assessment marks, view CA summaries, eligibility status, and final grade reports.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(MARKS_TEXT_GRAY);

        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.setBackground(MARKS_WHITE);
        tabbedPane.setForeground(MARKS_DEEP_GREEN);

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
