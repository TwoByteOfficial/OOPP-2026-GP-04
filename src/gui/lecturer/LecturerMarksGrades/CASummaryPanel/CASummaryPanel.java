
package gui.lecturer.LecturerMarksGrades.CASummaryPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CASummaryPanel extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color PALE_GREEN = new Color(232, 245, 233);
    private static final Color BORDER_GREEN = new Color(200, 230, 201);

    public CASummaryPanel(long lecturerUserId) {
        setLayout(new BorderLayout(0, 18));
        setBorder(new EmptyBorder(20, 5, 5, 5));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Continuous Assessment Summary");
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        title.setForeground(DEEP_GREEN);

        JLabel description = new JLabel(
                "<html><div style='width:650px'>"
                        + "This section will show each enrolled student's "
                        + "CA mark for the selected course. The calculation "
                        + "will use the configured assessment weights and "
                        + "the highest two quiz marks out of three, where "
                        + "three quizzes are configured."
                        + "</div></html>"
        );
        description.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel information = new JPanel();
        information.setLayout(new BoxLayout(information, BoxLayout.Y_AXIS));
        information.setBackground(PALE_GREEN);
        information.setBorder(new EmptyBorder(18, 18, 18, 18));
        information.add(description);

        JPanel placeholder = new JPanel(new BorderLayout());
        placeholder.setBackground(Color.WHITE);
        placeholder.setBorder(BorderFactory.createLineBorder(BORDER_GREEN));
        placeholder.add(information, BorderLayout.NORTH);

        add(title, BorderLayout.NORTH);
        add(placeholder, BorderLayout.CENTER);
    }
}
