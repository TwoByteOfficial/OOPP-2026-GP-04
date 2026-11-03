package gui.admin.EnrollmentComponents;

import javax.swing.*;
import java.awt.*;

/**
 * EnrollmentList - Container scroll pane panel for EnrollmentTable.
 */
public class EnrollmentList extends JPanel {

    private final Color WHITE        = Color.WHITE;
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);

    public EnrollmentList(EnrollmentTable table) {
        setLayout(new BorderLayout());
        setBackground(WHITE);
        setBorder(BorderFactory.createLineBorder(PASTEL_GREEN, 1));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }
}
