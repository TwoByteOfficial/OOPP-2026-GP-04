
package gui.lecturer;

import javax.swing.*;
import java.awt.*;

public class LecturerCourseMaterials extends JPanel {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    public LecturerCourseMaterials(long lecturerId) {
        setLayout(new BorderLayout(0, 20));
        setBackground(Color.WHITE);

        createPage();
    }

    private void createPage() {
        JLabel title = new JLabel("Course Materials");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(DEEP_GREEN);

        JLabel description = new JLabel(
                "Upload and manage learning materials for your courses."
        );
        description.setFont(new Font("SansSerif", Font.PLAIN, 14));
        description.setForeground(Color.GRAY);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);
        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(description);

        add(header, BorderLayout.NORTH);
    }
}
