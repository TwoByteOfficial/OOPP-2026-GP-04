
package gui.lecturer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LecturerDashboard extends JPanel {

    private final Color mint = new Color(232, 245, 233);
    private final Color pastel = new Color(200, 230, 201);
    private final Color fresh = new Color(165, 214, 167);
    private final Color forest = new Color(56, 142, 60);
    private final Color deepGreen = new Color(27, 94, 32);

    private final String fullName;

    public LecturerDashboard(String fullName) {
        this.fullName = fullName;

        setLayout(new BorderLayout());
        setBackground(mint);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        add(createDashboardContent(), BorderLayout.CENTER);
    }

    private JPanel createDashboardContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(mint);

        // Welcome section
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(mint);
        welcomePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Welcome back, " + fullName + "!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        welcomeLabel.setForeground(deepGreen);

        JLabel descriptionLabel = new JLabel(
                "Here is an overview of your teaching activities."
        );
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        descriptionLabel.setForeground(forest);

        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalStrut(8));
        welcomePanel.add(descriptionLabel);

        content.add(welcomePanel);
        content.add(Box.createVerticalStrut(25));

        // Statistics cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(mint);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 125)
        );

        statsPanel.add(createStatCard("3", "Assigned Courses"));
        statsPanel.add(createStatCard("20", "Undergraduates"));
        statsPanel.add(createStatCard("4", "Pending Marks"));
        statsPanel.add(createStatCard("3", "New Notices"));

        content.add(statsPanel);
        content.add(Box.createVerticalStrut(25));

        // Lower information cards
        JPanel lowerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        lowerPanel.setBackground(mint);
        lowerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        lowerPanel.add(createInformationCard(
                "My Courses",
                "View your assigned courses and course information."
        ));

        lowerPanel.add(createInformationCard(
                "Recent Notices",
                "Check important academic announcements and updates."
        ));

        content.add(lowerPanel);

        return content;
    }

    private JPanel createStatCard(String value, String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, forest),
                new EmptyBorder(18, 18, 18, 12)
        ));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        valueLabel.setForeground(deepGreen);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        titleLabel.setForeground(forest);

        card.add(valueLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(titleLabel);

        return card;
    }

    private JPanel createInformationCard(String title, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(deepGreen);

        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descriptionArea.setForeground(forest);
        descriptionArea.setBackground(Color.WHITE);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setFocusable(false);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(descriptionArea);

        return card;
    }
}
