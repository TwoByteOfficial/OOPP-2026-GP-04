package gui.undergraduate.medicals.header;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Header panel displaying medical certificate submissions and Dean's approval regulations.
 * Renders the top title and administrative policy guidelines for undergraduate medical excused absence requests.
 */
public class MedicalsHeaderPanel extends JPanel {

    /**
     * Constructs the medicals header panel with vertical layout and academic regulation metadata.
     */
    public MedicalsHeaderPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // -------------------------------------------------------------
        // Main View Title
        // -------------------------------------------------------------
        JLabel title = new JLabel("Medical Certificates & Excuses");
        title.setFont(AdminTheme.FONT_H1);
        title.setForeground(AdminTheme.DEEP_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // -------------------------------------------------------------
        // Subtitle & Submission Policy Note (7-Day Regulation Guideline)
        // -------------------------------------------------------------
        JLabel subtitle = new JLabel("Faculty Academic Regulations • Certificates must be submitted within 7 days of absence");
        subtitle.setFont(AdminTheme.font(Font.PLAIN, 13));
        subtitle.setForeground(AdminTheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // -------------------------------------------------------------
        // Component Assembly
        // -------------------------------------------------------------
        add(title);
        add(Box.createVerticalStrut(2));
        add(subtitle);
    }
}