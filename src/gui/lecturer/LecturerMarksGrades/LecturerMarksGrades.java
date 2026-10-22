
package gui.lecturer.LecturerMarksGrades;

import gui.lecturer.LecturerMarksGrades.EnterMarksPanel.EnterMarksPanel;
import gui.lecturer.LecturerMarksGrades.CASummaryPanel.CASummaryPanel;
import gui.lecturer.LecturerMarksGrades.EligibilityPanel.EligibilityPanel;
import gui.lecturer.LecturerMarksGrades.FinalResultsPanel.FinalResultsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LecturerMarksGrades extends JPanel {

    private final long lecturerUserId;

    public LecturerMarksGrades(long lecturerUserId) {
        this.lecturerUserId = lecturerUserId;

        setLayout(new BorderLayout(0, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Marks & Grades");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab(
                "Enter Marks",
                new EnterMarksPanel(lecturerUserId)
        );

        tabs.addTab(
                "CA Summary",
                new CASummaryPanel(lecturerUserId)
        );

        tabs.addTab(
                "Eligibility",
                new EligibilityPanel(lecturerUserId)
        );

        tabs.addTab(
                "Final Results & GPA",
                new FinalResultsPanel(lecturerUserId)
        );

        add(title, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }
}
