package gui.lecturer.LecturerMarksGrades.MarksGradesComponents;

import javax.swing.*;
import java.awt.*;

public class MarksGradesTitleBar extends JPanel {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color MUTED_GREEN =
            new Color(85, 110, 85);

    public MarksGradesTitleBar() {

        setLayout(new BorderLayout(0, 6));
        setOpaque(false);

        JLabel titleLabel = new JLabel("Marks & Grades");

        titleLabel.setFont(
                new Font("SansSerif", Font.BOLD, 26)
        );

        titleLabel.setForeground(DEEP_GREEN);

        JLabel descriptionLabel = new JLabel(
                "Manage assessments, student marks, results and eligibility."
        );

        descriptionLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        descriptionLabel.setForeground(MUTED_GREEN);

        add(titleLabel, BorderLayout.NORTH);
        add(descriptionLabel, BorderLayout.CENTER);
    }
}