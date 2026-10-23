
package gui.technical.attendance;
import dao.EnrollmentDAO;
import model.Undergraduate;
import javax.swing.table.DefaultTableModel;
import dao.AttendanceDAO;
import model.AttendanceSession;
import model.AttendanceRecord;

import dao.CourseDAO;
import model.Course;


import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendancePanel extends JPanel {

    private JComboBox<String> courseComboBox;
    private JTable studentTable;
    private JComboBox<String> componentComboBox;
    private JComboBox<AttendanceSession> sessionComboBox;
    private List<Course> courses = new ArrayList<>();
    private List<Undergraduate> loadedStudents = new ArrayList<>();

    public AttendancePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Student Attendance");
        title.setFont(new Font("Arial", Font.BOLD, 24));


        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);

        JPanel coursePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        coursePanel.setBackground(Color.WHITE);

        coursePanel.add(title);
        coursePanel.add(new JLabel("Select Course:"));

        courseComboBox = new JComboBox<>();
        courseComboBox.addItem("Select a course");

        courseComboBox.setPreferredSize(new Dimension(350, 30));
        coursePanel.add(courseComboBox);


        JPanel sessionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        sessionPanel.setBackground(Color.WHITE);

        JLabel componentLabel = new JLabel("Select Component:");

        String[] components = {
                "Select a component",
                "Theory",
                "Practical",
                "Tutorial"
        };

        componentComboBox = new JComboBox<>(components);

        JLabel sessionLabel = new JLabel("Select Session:");

        sessionComboBox = new JComboBox<>();
        sessionComboBox.setPreferredSize(new Dimension(200, 30));

        sessionPanel.add(componentLabel);
        sessionPanel.add(componentComboBox);
        sessionPanel.add(sessionLabel);
        sessionPanel.add(sessionComboBox);


        topPanel.add(coursePanel);
        topPanel.add(sessionPanel);


        courseComboBox.addActionListener(e -> {
            clearStudentTable();
            loadSessions();
        });

        componentComboBox.addActionListener(e -> {
            clearStudentTable();
            loadSessions();
        });


        add(topPanel, BorderLayout.NORTH);


        JButton loadButton = new JButton("Load Students");

        JButton saveButton = new JButton("Save Attendance");


        loadButton.addActionListener(e -> {

            int selectedIndex = courseComboBox.getSelectedIndex();

            if (selectedIndex <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a course first."
                );
                return;
            }

            if (sessionComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select an attendance session first."
                );
                return;
            }

            if (sessionComboBox.getSelectedItem() == null) {
                clearStudentTable();

                JOptionPane.showMessageDialog(
                        this,
                        "No attendance session is selected or available."
                );
                return;
            }


            try {
                Course selectedCourse = courses.get(selectedIndex - 1);

                List<Undergraduate> students =
                        new EnrollmentDAO().getEnrolledStudentsByCourse(
                                selectedCourse.getCourseId()
                        );
                loadedStudents = students;

                DefaultTableModel model =
                        (DefaultTableModel) studentTable.getModel();

                model.setRowCount(0);


                AttendanceSession selectedSession =
                        (AttendanceSession) sessionComboBox.getSelectedItem();

                List<AttendanceRecord> records =
                        new AttendanceDAO().getAttendanceBySession(
                                selectedSession.getAttendanceSessionId()
                        );

                for (Undergraduate student : students) {
                    String status = "PRESENT";

                    for (AttendanceRecord record : records) {
                        if (record.getStudentId() == student.getUserId()) {
                            status = record.getAttendanceStatus();
                            break;
                        }
                    }

                    model.addRow(new Object[]{
                            student.getRegistrationNo(),
                            student.getFullName(),
                            status
                    });
                }

                if (students.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "No enrolled students found for this course."
                    );
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not load students: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loadButton);
        buttonPanel.add(saveButton);


        studentTable = new JTable(
                new DefaultTableModel(
                        new String[]{"Registration No", "Student Name", "Attendance Status"},
                        0
                ) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return column == 2;
                    }
                }
        );



        String[] statuses = {
                "PRESENT",
                "ABSENT",
                "LATE",
                "MEDICAL"
        };

        JComboBox<String> statusComboBox = new JComboBox<>(statuses);

        studentTable.getColumnModel()
                .getColumn(2)
                .setCellEditor(new DefaultCellEditor(statusComboBox));



        JScrollPane scrollPane = new JScrollPane(studentTable);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        loadCourses();
    }

    private void loadCourses() {
        try {
            courses = new CourseDAO().getAllCourses();

            courseComboBox.removeAllItems();
            courseComboBox.addItem("Select a course");

            for (Course course : courses) {
                courseComboBox.addItem(
                        course.getCourseCode() + " - "
                                + course.getCourseName()
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load courses: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }



    private void loadSessions() {
        int selectedIndex = courseComboBox.getSelectedIndex();
        String component = (String) componentComboBox.getSelectedItem();

        sessionComboBox.removeAllItems();

        if (selectedIndex <= 0 || component == null
                || component.equals("Select a component")) {
            return;
        }

        try {
            Course selectedCourse = courses.get(selectedIndex - 1);

            List<AttendanceSession> sessions =
                    new AttendanceDAO().getSessionsByCourse(
                            selectedCourse.getCourseId(), component);

            if (sessions.isEmpty()) {
                clearStudentTable();

                JOptionPane.showMessageDialog(
                        this,
                        "No sessions are available for this component."
                );
                return;
            }

            for (AttendanceSession session : sessions) {
                sessionComboBox.addItem(session);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load sessions: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearStudentTable() {
        DefaultTableModel model =
                (DefaultTableModel) studentTable.getModel();

        model.setRowCount(0);
    }


}
