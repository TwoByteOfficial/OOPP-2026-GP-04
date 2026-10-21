package gui.lecturer.LecturerMarksGrades;

import gui.lecturer.LecturerMarksGrades.MarksGradesComponents.MarksGradesTitleBar;
import gui.lecturer.LecturerMarksGrades.EnterMarksPanel.EnterMarksPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LecturerMarksGrades extends JPanel {

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private final long lecturerUserId;

    private final JTabbedPane tabbedPane;

    public LecturerMarksGrades(long lecturerUserId) {

        this.lecturerUserId = lecturerUserId;

        setLayout(new BorderLayout(0, 20));
        setBackground(PALE_GREEN);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Page heading.
        MarksGradesTitleBar titleBar =
                new MarksGradesTitleBar();

        // Navigation tabs.
        tabbedPane = new JTabbedPane();

        tabbedPane.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        tabbedPane.setBackground(PALE_GREEN);
        tabbedPane.setForeground(DEEP_GREEN);

        // Enter Marks page.
        tabbedPane.addTab(
                "Enter Marks",
                new EnterMarksPanel(lecturerUserId)
        );

        // Temporary placeholders for sections
        // that we will implement next.
        tabbedPane.addTab(
                "CA Summary",
                createPlaceholder("Continuous Assessment Summary")
        );

        tabbedPane.addTab(
                "Final Results",
                createPlaceholder("Final Results")
        );

        tabbedPane.addTab(
                "Grades & GPA",
                createPlaceholder("Grades and GPA")
        );

        tabbedPane.addTab(
                "Eligibility",
                createPlaceholder("Examination Eligibility")
        );

        add(titleBar, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void add(MarksGradesTitleBar titleBar, String north) {
    }

    private JPanel createPlaceholder(String message) {

        JPanel panel = new JPanel(new GridBagLayout());

        panel.setBackground(PALE_GREEN);

        JLabel label = new JLabel(message);

        label.setFont(
                new Font("SansSerif", Font.PLAIN, 16)
        );

        label.setForeground(DEEP_GREEN);

        panel.add(label);

        return panel;
    }
}