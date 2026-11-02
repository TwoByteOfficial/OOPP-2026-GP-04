package gui.lecturer.Eligibility;

import gui.lecturer.LecturerMarksGrades.EligibilityPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static gui.lecturer.SharedComponents.Colors.ELIGIBILITY_BACKGROUND;
import static gui.lecturer.SharedComponents.Colors.ELIGIBILITY_DEEP_GREEN;
import static gui.lecturer.SharedComponents.Colors.ELIGIBILITY_TEXT_GRAY;

public class LecturerEligibility extends JPanel {

    public LecturerEligibility(long lecturerId) {
        setLayout(new BorderLayout(0, 15));
        setBackground(ELIGIBILITY_BACKGROUND);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Heading
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);

        JLabel title = new JLabel("Final Examination Eligibility");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(ELIGIBILITY_DEEP_GREEN);

        JLabel subtitle = new JLabel("Check undergraduate eligibility for final examination (Attendance >= 80% AND CA Mark >= 40%).");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(ELIGIBILITY_TEXT_GRAY);

        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        EligibilityPanel eligibilityPanel = new EligibilityPanel(lecturerId);

        add(heading, BorderLayout.NORTH);
        add(eligibilityPanel, BorderLayout.CENTER);
    }
}
