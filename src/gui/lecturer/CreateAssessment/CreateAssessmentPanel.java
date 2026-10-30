package gui.lecturer.CreateAssessment;

import dao.Lecturer.CreateAssessmentDAO;
import dao.Lecturer.CreateAssessmentDAO.AssessmentOption;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateAssessmentPanel extends JPanel {

    private static final Color GREEN = new Color(27, 94, 32);
    private static final Color PALE_GREEN = new Color(232, 245, 233);
    private static final Color BORDER_GREEN = new Color(200, 230, 201);
    private static final Color TEXT_GRAY = new Color(96, 112, 100);

    private final long lecturerId;
    private final CreateAssessmentDAO dao = new CreateAssessmentDAO();

    private final JComboBox<String> courseCombo = new JComboBox<>();
    private final JComboBox<AssessmentOption> typeCombo =
            new JComboBox<>();

    private final Map<String, Integer> courseMap = new HashMap<>();

    private final JTextField nameField = new JTextField(22);
    private final JTextField dateField = new JTextField(12);

    private final JLabel categoryLabel = new JLabel("-");
    private final JLabel componentLabel = new JLabel("-");
    private final JLabel weightLabel = new JLabel("-");
    private final JLabel maxMarkLabel = new JLabel("100");
    private final JLabel statusLabel = new JLabel("Ready");

    private final JButton saveButton =
            new JButton("Create Assessment");

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new String[]{
                            "Name",
                            "Category",
                            "Type",
                            "Component",
                            "Weight",
                            "Maximum",
                            "Date"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private boolean loadingCourses = false;

    public CreateAssessmentPanel(long lecturerId) {

        this.lecturerId = lecturerId;

        setLayout(new BorderLayout(0, 15));
        setBackground(PALE_GREEN);
        setBorder(new EmptyBorder(22, 22, 22, 22));

        add(createHeading(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        courseCombo.addActionListener(e -> {
            if (!loadingCourses) {
                refreshCourseData();
            }
        });

        typeCombo.addActionListener(e ->
                updateAssessmentInformation(true)
        );

        loadCourses();
    }

    // -----------------------------------------------------
    // HEADING
    // -----------------------------------------------------

    private JPanel createHeading() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = new JLabel("Create Assessment");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(GREEN);

        JLabel subtitle = new JLabel(
                "Create assessment definitions for your assigned courses."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);

        return panel;
    }

    // -----------------------------------------------------
    // MAIN CONTENT
    // -----------------------------------------------------

    private JPanel createMainContent() {

        JPanel content = new JPanel(new BorderLayout(0, 15));
        content.setOpaque(false);

        content.add(createFormPanel(), BorderLayout.NORTH);
        content.add(createAssessmentTable(), BorderLayout.CENTER);

        return content;
    }

    // -----------------------------------------------------
    // FORM
    // -----------------------------------------------------

    private JPanel createFormPanel() {

        JPanel panel = new JPanel(new GridBagLayout());

        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GREEN, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(panel, gbc, 0, 0, "Course", courseCombo);
        addField(panel, gbc, 1, 0, "Available Assessment Type", typeCombo);

        addField(panel, gbc, 0, 1, "Assessment Name", nameField);
        addField(panel, gbc, 1, 1,
                "Assessment Date (YYYY-MM-DD)", dateField);

        addField(panel, gbc, 0, 2, "Category", categoryLabel);
        addField(panel, gbc, 1, 2, "Component", componentLabel);

        addField(panel, gbc, 0, 3, "Configured Weight", weightLabel);
        addField(panel, gbc, 1, 3, "Maximum Mark", maxMarkLabel);

        saveButton.setBackground(GREEN);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        saveButton.setFocusPainted(false);

        saveButton.addActionListener(e -> createAssessment());

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearForm());

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        buttons.setOpaque(false);
        buttons.add(clearButton);
        buttons.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1;

        panel.add(buttons, gbc);

        return panel;
    }

    private void addField(
            JPanel panel,
            GridBagConstraints gbc,
            int column,
            int row,
            String labelText,
            JComponent input
    ) {

        gbc.gridx = column;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 1;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(GREEN);

        JPanel field = new JPanel(new BorderLayout(0, 5));
        field.setOpaque(false);

        field.add(label, BorderLayout.NORTH);
        field.add(input, BorderLayout.CENTER);

        if (input instanceof JComboBox<?>) {
            input.setPreferredSize(new Dimension(250, 32));
        } else if (input instanceof JTextField) {
            input.setPreferredSize(new Dimension(250, 32));
        } else {
            input.setFont(new Font("SansSerif", Font.PLAIN, 13));
            input.setForeground(TEXT_GRAY);
        }

        panel.add(field, gbc);
    }

    // -----------------------------------------------------
    // EXISTING ASSESSMENTS TABLE
    // -----------------------------------------------------

    private JPanel createAssessmentTable() {

        JPanel panel = new JPanel(new BorderLayout(0, 8));

        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GREEN, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel heading =
                new JLabel("Existing Course Assessments");

        heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        heading.setForeground(GREEN);

        JTable table = new JTable(tableModel);

        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setGridColor(BORDER_GREEN);
        table.setFillsViewportHeight(true);

        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        table.getTableHeader().setBackground(PALE_GREEN);
        table.getTableHeader().setForeground(GREEN);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(BORDER_GREEN));

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        statusLabel.setForeground(TEXT_GRAY);
        footer.add(statusLabel, BorderLayout.WEST);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    // -----------------------------------------------------
    // LOAD ASSIGNED COURSES
    // -----------------------------------------------------

    private void loadCourses() {

        loadingCourses = true;

        try {
            courseCombo.removeAllItems();
            courseMap.clear();

            List<Object[]> courses =
                    dao.getAssignedCourses(lecturerId);

            for (Object[] course : courses) {

                int courseId = (Integer) course[0];

                String courseCode = (String) course[1];
                String courseName = (String) course[2];

                String label = courseCode + " - " + courseName;

                courseCombo.addItem(label);
                courseMap.put(label, courseId);
            }

        } catch (SQLException e) {

            showError(
                    "Unable to load assigned courses: " + e.getMessage()
            );

        } finally {
            loadingCourses = false;
        }

        if (courseCombo.getItemCount() == 0) {

            typeCombo.removeAllItems();
            tableModel.setRowCount(0);

            saveButton.setEnabled(false);

            statusLabel.setText(
                    "No active assigned courses found."
            );

            return;
        }

        refreshCourseData();
    }

    // -----------------------------------------------------
    // REFRESH SELECTED COURSE
    // -----------------------------------------------------

    private void refreshCourseData() {

        // Always load existing assessments first.
        loadExistingAssessments();

        // Then load assessment types that are not yet created.
        loadAvailableAssessmentTypes();

        updateAssessmentInformation(false);
    }

    // -----------------------------------------------------
    // LOAD ONLY MISSING ASSESSMENT TYPES
    // -----------------------------------------------------

    private void loadAvailableAssessmentTypes() {

        String selectedCourse =
                (String) courseCombo.getSelectedItem();

        Integer courseId = courseMap.get(selectedCourse);

        typeCombo.removeAllItems();

        if (courseId == null) {
            saveButton.setEnabled(false);
            return;
        }

        try {

            List<AssessmentOption> options =
                    dao.getMissingAssessmentTypes(
                            lecturerId,
                            courseId
                    );

            for (AssessmentOption option : options) {
                typeCombo.addItem(option);
            }

            if (options.isEmpty()) {

                saveButton.setEnabled(false);

                categoryLabel.setText("-");
                componentLabel.setText("-");
                weightLabel.setText("-");
                maxMarkLabel.setText("100");

                nameField.setText("");

                statusLabel.setText(
                        "All configured assessment types have already been created."
                );

            } else {

                saveButton.setEnabled(true);

                typeCombo.setSelectedIndex(0);

                updateAssessmentInformation(true);

                statusLabel.setText(
                        options.size() + " assessment type(s) available."
                );
            }

        } catch (SQLException e) {

            saveButton.setEnabled(false);

            statusLabel.setText(
                    "Unable to load available assessment types."
            );

            showError(
                    "Unable to load available assessment types: "
                            + e.getMessage()
            );
        }
    }

    // -----------------------------------------------------
    // UPDATE ASSESSMENT INFORMATION
    // -----------------------------------------------------

    private void updateAssessmentInformation(
            boolean updateDefaultName
    ) {

        AssessmentOption option =
                (AssessmentOption) typeCombo.getSelectedItem();

        if (option == null) {

            categoryLabel.setText("-");
            componentLabel.setText("-");
            weightLabel.setText("-");
            maxMarkLabel.setText("100");

            return;
        }

        categoryLabel.setText(option.getCategory());
        componentLabel.setText(option.getComponent());
        maxMarkLabel.setText("100");

        BigDecimal weight = option.getWeight();

        if (weight == null) {
            weightLabel.setText("-");
        } else if (option.getType().startsWith("Quiz")) {

            weightLabel.setText(
                    weight.toPlainString()
                            + "% group weight (best 2 of 3)"
            );

        } else {

            weightLabel.setText(weight.toPlainString() + "%");
        }

        if (updateDefaultName) {
            nameField.setText(getDefaultName(option.getType()));
        }
    }

    // -----------------------------------------------------
    // DEFAULT ASSESSMENT NAMES
    // -----------------------------------------------------

    private String getDefaultName(String type) {

        switch (type) {

            case "Quiz1":
                return "Quiz 01";

            case "Quiz2":
                return "Quiz 02";

            case "Quiz3":
                return "Quiz 03";

            case "Assignment":
                return "Assignment";

            case "Mid Exam Theory":
                return "Mid Examination - Theory";

            case "Mid Exam Practical":
                return "Mid Examination - Practical";

            case "End Exam Theory":
                return "End Examination - Theory";

            case "End Exam Practical":
                return "End Examination - Practical";

            default:
                return type;
        }
    }

    // -----------------------------------------------------
    // CREATE ASSESSMENT
    // -----------------------------------------------------

    private void createAssessment() {

        String selectedCourse =
                (String) courseCombo.getSelectedItem();

        Integer courseId = courseMap.get(selectedCourse);

        if (courseId == null) {
            showWarning("Please select a course.");
            return;
        }

        AssessmentOption option =
                (AssessmentOption) typeCombo.getSelectedItem();

        if (option == null) {
            showWarning(
                    "There are no available assessment types for this course."
            );
            return;
        }

        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            showWarning("Please enter an assessment name.");
            nameField.requestFocus();
            return;
        }

        Date assessmentDate = null;

        String dateText = dateField.getText().trim();

        if (!dateText.isEmpty()) {

            try {
                assessmentDate = Date.valueOf(dateText);

            } catch (IllegalArgumentException e) {

                showWarning(
                        "Date must use YYYY-MM-DD format."
                );

                dateField.requestFocus();
                return;
            }
        }

        String type = option.getType();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Create this assessment?\n\n"
                        + "Course: " + selectedCourse
                        + "\nName: " + name
                        + "\nType: " + option
                        + "\nCategory: " + option.getCategory()
                        + "\nComponent: " + option.getComponent()
                        + "\nConfigured Weight: "
                        + option.getWeight() + "%"
                        + "\nMaximum Mark: 100",
                "Confirm Assessment",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        saveButton.setEnabled(false);

        try {

            dao.createAssessment(
                    lecturerId,
                    courseId,
                    name,
                    type,
                    assessmentDate
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Assessment created successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();

            // Reload the table and remove the newly created type
            // from the available assessment dropdown.
            refreshCourseData();

        } catch (Exception e) {

            showError(
                    e.getMessage() == null
                            ? "Unable to create assessment."
                            : e.getMessage()
            );

            // Refresh in case the assessment was created elsewhere.
            refreshCourseData();

        } finally {

            if (typeCombo.getItemCount() > 0) {
                saveButton.setEnabled(true);
            }
        }
    }

    // -----------------------------------------------------
    // LOAD EXISTING ASSESSMENTS
    // -----------------------------------------------------

    private void loadExistingAssessments() {

        tableModel.setRowCount(0);

        String selectedCourse =
                (String) courseCombo.getSelectedItem();

        Integer courseId = courseMap.get(selectedCourse);

        if (courseId == null) {
            return;
        }

        try {

            List<Object[]> assessments =
                    dao.getAssessments(lecturerId, courseId);

            for (Object[] assessment : assessments) {

                tableModel.addRow(new Object[]{
                        assessment[1],
                        assessment[2],
                        assessment[3],
                        assessment[4],
                        assessment[5],
                        assessment[6],
                        assessment[7] == null
                                ? "-"
                                : assessment[7].toString()
                });
            }

            if (assessments.isEmpty()) {
                statusLabel.setText("No assessments created for this course yet.");
            }

        } catch (SQLException e) {

            showError(
                    "Unable to load assessments: " + e.getMessage()
            );
        }
    }

    // -----------------------------------------------------
    // CLEAR FORM
    // -----------------------------------------------------

    private void clearForm() {

        nameField.setText("");
        dateField.setText("");

        nameField.requestFocus();
    }

    // -----------------------------------------------------
    // MESSAGES
    // -----------------------------------------------------

    private void showWarning(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Validation",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}