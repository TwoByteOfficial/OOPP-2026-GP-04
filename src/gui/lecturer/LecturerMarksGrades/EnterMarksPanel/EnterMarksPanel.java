package gui.lecturer.LecturerMarksGrades.EnterMarksPanel;

import gui.lecturer.LecturerMarksGrades.EnterMarksPanel.EnterMarksPanelComponents.EnterMarksTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EnterMarksPanel extends JPanel {

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private final JComboBox<String> courseComboBox;
    private final JComboBox<String> assessmentComboBox;

    private final EnterMarksTable marksTable;
    private final JLabel statusLabel;

    public EnterMarksPanel(long lecturerUserId) {

        setLayout(new BorderLayout(0, 18));
        setBackground(PALE_GREEN);
        setBorder(new EmptyBorder(20, 25, 25, 25));

        // Course selector.
        courseComboBox = new JComboBox<>();
        courseComboBox.addItem("Select Course");

        // Assessment selector.
        assessmentComboBox = new JComboBox<>();
        assessmentComboBox.addItem("Select Assessment");

        // Table.
        marksTable = new EnterMarksTable();

        // Status.
        statusLabel = new JLabel(
                "Select a course and assessment to enter marks."
        );

        statusLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );

        // Selection panel.
        JPanel selectionPanel = new JPanel(
                new GridLayout(2, 2, 12, 8)
        );

        selectionPanel.setOpaque(false);

        JLabel courseLabel = new JLabel("Course");
        JLabel assessmentLabel = new JLabel("Assessment");

        courseLabel.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        assessmentLabel.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        selectionPanel.add(courseLabel);
        selectionPanel.add(assessmentLabel);
        selectionPanel.add(courseComboBox);
        selectionPanel.add(assessmentComboBox);

        // Action buttons.
        JButton refreshButton = new JButton("Refresh");
        JButton saveButton = new JButton("Save Marks");

        refreshButton.setFocusPainted(false);
        saveButton.setFocusPainted(false);

        refreshButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        saveButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        // These actions will be connected to the database
        // after the course and assessment loading is implemented.
        refreshButton.addActionListener(event ->
                statusLabel.setText(
                        "Refresh will be available after database integration."
                )
        );

        saveButton.addActionListener(event ->
                statusLabel.setText(
                        "Saving will be available after database integration."
                )
        );

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );

        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        buttonPanel.add(saveButton);

        JPanel footer = new JPanel(
                new BorderLayout(10, 0)
        );

        footer.setOpaque(false);
        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(buttonPanel, BorderLayout.EAST);

        // Main layout.
        add(selectionPanel, BorderLayout.NORTH);
        add(marksTable, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }
}