package gui.undergraduate.profile.info;

import gui.admin.AdminTheme;
import gui.undergraduate.profile.model.UserProfileData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Detailed academic enrolment card listing department, degree, semester, and email records.
 */
public class AcademicDetailsCard extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JLabel degreeVal;
    private final JLabel departmentVal;
    private final JLabel semesterVal;
    private final JLabel emailVal;

    public AcademicDetailsCard() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Academic & Institutional Placement");
        title.setFont(AdminTheme.font(Font.BOLD, 15));
        title.setForeground(AdminTheme.DEEP_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(16));

        degreeVal = new JLabel("Bachelor of Information & Communication Technology (BICT Hons)");
        departmentVal = new JLabel("Department of Information Technology");
        semesterVal = new JLabel("Level II • Semester I (Academic Year 2026/2027)");
        emailVal = new JLabel("tg2069@fot.ruh.ac.lk");

        content.add(createDataRow("Degree Programme:", degreeVal));
        content.add(Box.createVerticalStrut(12));
        content.add(createDataRow("Academic Department:", departmentVal));
        content.add(Box.createVerticalStrut(12));
        content.add(createDataRow("Enrolled Level & Semester:", semesterVal));
        content.add(Box.createVerticalStrut(12));
        content.add(createDataRow("Institutional Email:", emailVal));

        add(content, BorderLayout.NORTH); // NORTH දැමීමෙන් උඩින් පිළිවෙළට තබාගනී
    }

    public void updateDetails(UserProfileData data) {
        if (data == null) return;
        degreeVal.setText(data.getDegreeProgram());
        departmentVal.setText(data.getDepartment());
        semesterVal.setText(data.getCurrentLevel() + " • " + data.getCurrentSemester());
        emailVal.setText(data.getEmail());
    }

    private JPanel createDataRow(String labelText, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout(0, 3));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(AdminTheme.font(Font.PLAIN, 12));
        lbl.setForeground(AdminTheme.MUTED);

        valueLabel.setFont(AdminTheme.font(Font.BOLD, 13));
        valueLabel.setForeground(new Color(0x33, 0x33, 0x33));

        row.add(lbl, BorderLayout.NORTH);
        row.add(valueLabel, BorderLayout.SOUTH);
        return row;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(AdminTheme.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.setColor(CARD_BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.dispose();
        super.paintComponent(g);
    }
}