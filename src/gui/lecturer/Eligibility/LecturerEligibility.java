package gui.lecturer.Eligibility;

import gui.lecturer.LecturerMarksGrades.EligibilityPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LecturerEligibility extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color PALE_GREEN = new Color(232, 245, 233);
    private static final Color TEXT_GRAY = new Color(96, 112, 100);

    public LecturerEligibility(long lecturerId) {
        setLayout(new BorderLayout(0, 15));
        setBackground(PALE_GREEN);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Heading
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);

        JLabel title = new JLabel("Final Examination Eligibility");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(DEEP_GREEN);

        JLabel subtitle = new JLabel("Check undergraduate eligibility for final examination (Attendance >= 80% AND CA Mark >= 40%).");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);

        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        EligibilityPanel eligibilityPanel = new EligibilityPanel(lecturerId);

        add(heading, BorderLayout.NORTH);
        add(eligibilityPanel, BorderLayout.CENTER);
    }
}
