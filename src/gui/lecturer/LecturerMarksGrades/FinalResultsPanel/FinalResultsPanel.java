
package gui.lecturer.LecturerMarksGrades.FinalResultsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FinalResultsPanel extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color PALE_GREEN = new Color(232, 245, 233);
    private static final Color BORDER_GREEN = new Color(200, 230, 201);

    public FinalResultsPanel(long lecturerUserId) {
        setLayout(new BorderLayout(0, 18));
        setBorder(new EmptyBorder(20, 5, 5, 5));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Final Results & GPA");
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        title.setForeground(DEEP_GREEN);

        JLabel description = new JLabel(
                "<html><div style='width:650px'>"
                        + "This section will display CA marks, final "
                        + "examination marks, final results, grades and "
                        + "grade points. SGPA and CGPA will be calculated "
                        + "using the applicable grading rules and course "
                        + "credits after the result calculation is connected."
                        + "</div></html>"
        );
        description.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel information = new JPanel(new BorderLayout());
        information.setBackground(PALE_GREEN);
        information.setBorder(new EmptyBorder(18, 18, 18, 18));
        information.add(description, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createLineBorder(BORDER_GREEN));
        content.add(information, BorderLayout.NORTH);

        add(title, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }
}
