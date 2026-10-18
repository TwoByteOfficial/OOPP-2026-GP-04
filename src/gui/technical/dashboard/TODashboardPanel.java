
package gui.technical.dashboard;

import gui.technical.dashboard.header.DashboardHeaderPanel;
import gui.technical.dashboard.summary.DashboardSummaryPanel;
import gui.technical.dashboard.actions.DashboardQuickActionsPanel;
import gui.technical.dashboard.overview.DashboardOverviewPanel;
import gui.technical.dashboard.bottom.DashboardBottomPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TODashboardPanel extends JPanel {

    private final String fullName;
    private final String role;

    private final Color NAVY = new Color(24, 58, 99);
    private final Color BLUE = new Color(43, 99, 235);
    private final Color GREEN = new Color(25, 135, 84);
    private final Color ORANGE = new Color(230, 126, 20);
    private final Color TEAL = new Color(15, 118, 110);
    private final Color TEXT = new Color(40, 55, 75);
    private final Color BACKGROUND = new Color(245, 247, 251);



    public TODashboardPanel(String fullName, String role) {
        this.fullName = fullName;
        this.role = role;

        setLayout(new BorderLayout(0, 18));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        add(new DashboardHeaderPanel(fullName, role), BorderLayout.NORTH);
        add(buildDashboardContent(), BorderLayout.CENTER);
    }



    private JPanel buildDashboardContent() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(buildSummaryCards());
        content.add(Box.createVerticalStrut(18));
        content.add(new DashboardQuickActionsPanel());
        content.add(Box.createVerticalStrut(18));
        content.add(buildOverview());
        content.add(Box.createVerticalStrut(18));
        content.add(new DashboardBottomPanel());

        return content;
    }


    private JPanel buildSummaryCards() {
        return new DashboardSummaryPanel();
    }


    private JPanel createSummaryCard(
            String title, String value, Color accent) {

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(18, 14, 18, 10)));

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel number = new JLabel(value);
        number.setFont(new Font("Segoe UI", Font.BOLD, 25));
        number.setForeground(TEXT);

        JLabel caption = new JLabel(title);
        caption.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        caption.setForeground(new Color(100, 112, 128));

        card.add(number);
        card.add(Box.createVerticalStrut(7));
        card.add(caption);

        return card;
    }

    private JPanel buildQuickActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        actions.setBackground(Color.WHITE);
        actions.setBorder(new EmptyBorder(5, 5, 5, 5));

        actions.add(createActionButton("Record Attendance", BLUE));
        actions.add(createActionButton("Add Medical", ORANGE));
        actions.add(createActionButton("View Timetable", NAVY));
        actions.add(createActionButton("View Notices", TEAL));

        return actions;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(10, 14, 10, 14));

        return button;
    }

    private JPanel buildOverview() {
        return new DashboardOverviewPanel();
    }

    private JPanel createInformationPanel(
            String titleText, String description) {

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 230, 238)),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(NAVY);

        JLabel detail = new JLabel(
                "<html><body style='width:200px'>"
                        + description + "</body></html>");
        detail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detail.setForeground(new Color(105, 118, 135));

        panel.add(title, BorderLayout.NORTH);
        panel.add(detail, BorderLayout.CENTER);

        return panel;
    }
}
