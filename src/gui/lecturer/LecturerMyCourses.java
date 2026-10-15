
package gui.lecturer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LecturerMyCourses extends JPanel {

    private final Color mint = new Color(232, 245, 233);
    private final Color forest = new Color(56, 142, 60);
    private final Color deepGreen = new Color(27, 94, 32);
    private final Color pastel = new Color(200, 230, 201);

    private JTable courseTable;

    public LecturerMyCourses() {
        setLayout(new BorderLayout(0, 20));
        setBackground(mint);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        add(createHeader(), BorderLayout.NORTH);
        add(createCourseTable(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(mint);

        JLabel title = new JLabel("My Courses");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(deepGreen);

        JLabel subtitle = new JLabel(
                "View your assigned courses and course information."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(forest);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);

        return header;
    }

    private JPanel createCourseTable() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel sectionTitle = new JLabel("Assigned Courses");
        sectionTitle.setFont(new Font("SansSerif", Font.BOLD, 19));
        sectionTitle.setForeground(deepGreen);

        String[] columns = {
                "Course Code",
                "Course Name",
                "Theory Credits",
                "Practical Credits",
                "Total Credits",
                "CA Weight",
                "Students",
                "Action"
        };

        Object[][] data = {
                {"ICT2112", "Data Structures and Algorithms", 2, 0, 2, "40%", 19, "View Details"},
                {"ICT2162", "Software Engineering", 2, 0, 2, "30%", 20, "View Details"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };

        courseTable = new JTable(model);
        courseTable.setRowHeight(42);
        courseTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        courseTable.setSelectionBackground(pastel);
        courseTable.setSelectionForeground(deepGreen);
        courseTable.setGridColor(new Color(220, 230, 220));
        courseTable.setShowVerticalLines(false);
        courseTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        int[] columnWidths = {85, 180, 95, 105, 95, 70, 65, 110};

        for (int i = 0; i < columnWidths.length; i++) {
            courseTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(columnWidths[i]);
        }

        courseTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        courseTable.getTableHeader().setBackground(pastel);
        courseTable.getTableHeader().setForeground(deepGreen);
        courseTable.getTableHeader().setPreferredSize(
                new Dimension(0, 42)
        );

        // Display a button in every Action cell.
        courseTable.getColumnModel().getColumn(7)
                .setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
                    JButton button = new JButton("View Details");
                    button.setFont(new Font("SansSerif", Font.BOLD, 12));
                    button.setBackground(forest);
                    button.setForeground(Color.WHITE);
                    button.setFocusPainted(false);
                    button.setBorderPainted(false);
                    return button;
                });

        // Handle clicks on the Action column.
        courseTable.getColumnModel().getColumn(7)
                .setCellEditor(new DefaultCellEditor(new JCheckBox()) {
                    private final JButton button = new JButton("View Details");
                    private int selectedRow;

                    {
                        button.setFont(new Font("SansSerif", Font.BOLD, 12));
                        button.setBackground(forest);
                        button.setForeground(Color.WHITE);
                        button.setFocusPainted(false);
                        button.setBorderPainted(false);

                        button.addActionListener(e -> {
                            fireEditingStopped();

                            String courseCode = courseTable
                                    .getValueAt(selectedRow, 0).toString();

                            showCourseDetails(courseCode);
                        });
                    }

                    @Override
                    public Component getTableCellEditorComponent(
                            JTable table, Object value, boolean isSelected,
                            int row, int column) {
                        selectedRow = row;
                        return button;
                    }

                    @Override
                    public Object getCellEditorValue() {
                        return "View Details";
                    }
                });

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        panel.add(sectionTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void showCourseDetails(String courseCode) {
        String courseName;
        int theoryCredits;
        int practicalCredits;
        String caWeight;
        int students;
        String timetable;

        if (courseCode.equals("ICT2112")) {
            courseName = "Data Structures and Algorithms";
            theoryCredits = 2;
            practicalCredits = 0;
            caWeight = "40%";
            students = 19;
            timetable = "Monday | 10.00 - 12.00 | Theory | Lecture Hall 01";
        } else {
            courseName = "Software Engineering";
            theoryCredits = 2;
            practicalCredits = 0;
            caWeight = "30%";
            students = 20;
            timetable = "Timetable details to be added";
        }

        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                courseCode + " - " + courseName,
                true
        );

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(520, 530);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout(0, 15));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel title = new JLabel(courseCode + " - " + courseName);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(deepGreen);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel details = new JPanel(new GridLayout(0, 2, 12, 16));
        details.setBackground(Color.WHITE);

        addDetail(details, "Course Code", courseCode);
        addDetail(details, "Department", "ICT");
        addDetail(details, "Course Name", courseName);
        addDetail(details, "Theory Credits", String.valueOf(theoryCredits));
        addDetail(details, "Practical Credits", String.valueOf(practicalCredits));
        addDetail(details, "CA Weight", caWeight);
        addDetail(details, "Registered Students", String.valueOf(students));

        JPanel timetablePanel = new JPanel();
        timetablePanel.setLayout(new BoxLayout(timetablePanel, BoxLayout.Y_AXIS));
        timetablePanel.setBackground(Color.WHITE);
        timetablePanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel timetableTitle = new JLabel("Timetable");
        timetableTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        timetableTitle.setForeground(deepGreen);

        JTextArea timetableText = new JTextArea(timetable);
        timetableText.setFont(new Font("SansSerif", Font.PLAIN, 13));
        timetableText.setLineWrap(true);
        timetableText.setWrapStyleWord(true);
        timetableText.setEditable(false);
        timetableText.setFocusable(false);
        timetableText.setBackground(mint);
        timetableText.setBorder(new EmptyBorder(12, 12, 12, 12));

        timetablePanel.add(timetableTitle);
        timetablePanel.add(Box.createVerticalStrut(10));
        timetablePanel.add(timetableText);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(details, BorderLayout.NORTH);
        centerPanel.add(timetablePanel, BorderLayout.CENTER);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        cancelButton.setBackground(forest);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(cancelButton);

        content.add(title, BorderLayout.NORTH);
        content.add(centerPanel, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private void addDetail(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("SansSerif", Font.PLAIN, 13));
        labelComponent.setForeground(forest);

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("SansSerif", Font.BOLD, 13));
        valueComponent.setForeground(deepGreen);

        panel.add(labelComponent);
        panel.add(valueComponent);
    }
}
