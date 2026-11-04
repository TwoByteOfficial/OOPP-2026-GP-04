package gui.technical.attendance;

import dao.TechnicalOfficer.AttendanceDAO;
import dao.TechnicalOfficer.CourseDAO;
import dao.TechnicalOfficer.EnrollmentDAO;

import model.AttendanceRecord;
import model.AttendanceSession;
import model.Course;
import model.Undergraduate;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main attendance management panel for Technical Officers.
 * Allows officers to:
 * 1. Select a course, session component (Theory/Practical/Tutorial), and session date/number.
 * 2. Load enrolled students and any previously recorded attendance status.
 * 3. Mark individual student attendance (PRESENT / ABSENT) or apply bulk status to all students.
 * 4. Save the batch of attendance records to the database with the officer's auditor user ID.
 * 5. Warns before discarding unsaved attendance edits when switching courses or sessions.
 */
public class AttendancePanel extends JPanel {

    // Dropdown selectors for Course, Component, and Session
    private JComboBox<String> courseComboBox;
    private JComboBox<String> componentComboBox;
    private JComboBox<AttendanceSession> sessionComboBox;

    // Student attendance table
    private JTable studentTable;

    // In-memory lists of loaded courses and currently displayed undergraduates
    private List<Course> courses = new ArrayList<>();
    private List<Undergraduate> loadedStudents = new ArrayList<>();

    // User ID of the logged-in Technical Officer (recorded in attendance_records as recorded_by)
    private final long userId;

    // State flags for change tracking and avoiding cyclic listener events
    private boolean attendanceChanged = false;
    private boolean loadingAttendance = false;
    private boolean changingSelection = false;

    // Tracks previously selected items to rollback if user cancels discarding unsaved changes
    private int previousCourseIndex = 0;
    private String previousComponent = "Select a component";
    private AttendanceSession previousSession = null;

    /**
     * Parameterless constructor defaulting officer user ID to 0.
     */
    public AttendancePanel() {
        this(0);
    }

    /**
     * Primary constructor used by TechnicalOfficerDashboard.
     *
     * @param userId The database user ID of the logged-in Technical Officer
     */
    public AttendancePanel(long userId) {
        this.userId = userId;

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Student Attendance");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);

        // Course selection row
        JPanel coursePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        coursePanel.setBackground(Color.WHITE);
        coursePanel.add(title);
        coursePanel.add(new JLabel("Select Course:"));

        courseComboBox = new JComboBox<>();
        courseComboBox.addItem("Select a course");
        courseComboBox.setPreferredSize(new Dimension(350, 30));
        coursePanel.add(courseComboBox);

        // Component (Theory/Practical) and Session selection row
        JPanel sessionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        sessionPanel.setBackground(Color.WHITE);
        sessionPanel.add(new JLabel("Select Component:"));

        String[] components = {
                "Select a component",
                "Theory",
                "Practical",
                "Tutorial"
        };
        componentComboBox = new JComboBox<>(components);
        sessionPanel.add(componentComboBox);

        sessionPanel.add(new JLabel("Select Session:"));
        sessionComboBox = new JComboBox<>();
        sessionComboBox.setPreferredSize(new Dimension(200, 30));
        sessionPanel.add(sessionComboBox);

        topPanel.add(coursePanel);
        topPanel.add(sessionPanel);
        add(topPanel, BorderLayout.NORTH);

        // Action buttons: Load Students and Save Attendance
        JButton loadButton = new JButton("Load Students");
        JButton saveButton = new JButton("Save Attendance");
        saveButton.setBackground(new Color(0x4C, 0xAF, 0x50));
        saveButton.setForeground(Color.WHITE);
        saveButton.setOpaque(true);
        saveButton.setFocusPainted(false);

        // Bulk status tool to mark all students at once
        JLabel setAllLabel = new JLabel("Set all students attendance status:");
        String[] allStatuses = {
                "Choose status...",
                "PRESENT",
                "ABSENT",
                "Select Status"
        };
        JComboBox<String> setAllStatusComboBox = new JComboBox<>(allStatuses);

        // Bulk status listener: updates column 3 for every row in the table
        setAllStatusComboBox.addActionListener(e -> {
            String selectedStatus = (String) setAllStatusComboBox.getSelectedItem();

            if (selectedStatus == null || selectedStatus.equals("Choose status...")) {
                return;
            }

            DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
            for (int row = 0; row < model.getRowCount(); row++) {
                model.setValueAt(selectedStatus, row, 3);
            }

            // Reset selector back to placeholder
            setAllStatusComboBox.setSelectedIndex(0);
        });

        // Left side: load and save buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loadButton);
        buttonPanel.add(saveButton);

        // Right side: bulk attendance controls
        JPanel setAllPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        setAllPanel.setBackground(Color.WHITE);
        setAllPanel.add(setAllLabel);
        setAllPanel.add(setAllStatusComboBox);

        // Top toolbar above the student table
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setBackground(Color.WHITE);
        actionPanel.add(buttonPanel, BorderLayout.WEST);
        actionPanel.add(setAllPanel, BorderLayout.EAST);

        // Configure table model: only column 3 (Attendance Status) is editable
        studentTable = new JTable(
                new DefaultTableModel(
                        new String[]{
                                "No",
                                "Registration No",
                                "Student Name",
                                "Attendance Status"
                        },
                        0
                ) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return column == 3;
                    }
                }
        );
        studentTable.setFont(new Font("Arial", Font.PLAIN, 16));
        studentTable.setRowHeight(30);

        // Dropdown editor for the Attendance Status column in each table row
        String[] statuses = {
                "Select Status",
                "PRESENT",
                "ABSENT"
        };
        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setBackground(new Color(0xA5, 0xD6, 0xA7));

        studentTable.getColumnModel()
                .getColumn(3)
                .setCellEditor(new DefaultCellEditor(statusComboBox));

        // Listen for user changes in the table to prompt for unsaved changes if navigating away
        studentTable.getModel().addTableModelListener(e -> {
            if (!loadingAttendance
                    && e.getType() == TableModelEvent.UPDATE
                    && e.getColumn() == 3) {
                attendanceChanged = true;
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(actionPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Course combobox selection listener
        courseComboBox.addActionListener(e -> {
            if (changingSelection) {
                return;
            }

            int newIndex = courseComboBox.getSelectedIndex();

            // Prompt user if they have unsaved changes
            if (!confirmDiscardChanges()) {
                changingSelection = true;
                courseComboBox.setSelectedIndex(previousCourseIndex);
                changingSelection = false;
                return;
            }

            previousCourseIndex = newIndex;
            attendanceChanged = false;

            clearStudentTable();
            loadSessions();
        });

        // Component selection listener (Theory vs Practical)
        componentComboBox.addActionListener(e -> {
            if (changingSelection) {
                return;
            }

            String newComponent = (String) componentComboBox.getSelectedItem();

            if (!confirmDiscardChanges()) {
                changingSelection = true;
                componentComboBox.setSelectedItem(previousComponent);
                changingSelection = false;
                return;
            }

            previousComponent = newComponent;
            attendanceChanged = false;

            clearStudentTable();
            loadSessions();
        });

        // Session selection listener
        sessionComboBox.addActionListener(e -> {
            if (changingSelection) {
                return;
            }

            AttendanceSession newSession = (AttendanceSession) sessionComboBox.getSelectedItem();

            if (!confirmDiscardChanges()) {
                changingSelection = true;
                sessionComboBox.setSelectedItem(previousSession);
                changingSelection = false;
                return;
            }

            previousSession = newSession;
            attendanceChanged = false;

            clearStudentTable();
        });

        // "Load Students" button handler
        loadButton.addActionListener(e -> {
            // Stop any active cell editing in the table before reloading
            if (studentTable.isEditing() && !studentTable.getCellEditor().stopCellEditing()) {
                return;
            }

            if (!confirmDiscardChanges()) {
                return;
            }

            int selectedIndex = courseComboBox.getSelectedIndex();
            if (selectedIndex <= 0) {
                JOptionPane.showMessageDialog(this, "Please select a course first.");
                return;
            }

            AttendanceSession selectedSession = (AttendanceSession) sessionComboBox.getSelectedItem();
            if (selectedSession == null) {
                clearStudentTable();
                JOptionPane.showMessageDialog(this, "Please select an attendance session first.");
                return;
            }

            try {
                Course selectedCourse = courses.get(selectedIndex - 1);

                // 1. Fetch enrolled undergraduates for this course
                List<Undergraduate> students =
                        new EnrollmentDAO().getEnrolledStudentsByCourse(selectedCourse.getCourseId());

                // 2. Fetch any already-recorded attendance for this specific session
                List<AttendanceRecord> records =
                        new AttendanceDAO().getAttendanceBySession(selectedSession.getAttendanceSessionId());

                DefaultTableModel model = (DefaultTableModel) studentTable.getModel();

                loadingAttendance = true;
                try {
                    model.setRowCount(0);
                    loadedStudents = students;

                    int number = 1;
                    for (Undergraduate student : students) {
                        String status = "Select Status";

                        // Match student with existing database attendance record if available
                        for (AttendanceRecord record : records) {
                            if (record.getStudentId() == student.getUserId()) {
                                status = record.getAttendanceStatus();
                                break;
                            }
                        }

                        model.addRow(new Object[]{
                                number,
                                student.getRegistrationNo(),
                                student.getFullName(),
                                status
                        });

                        number++;
                    }
                } finally {
                    loadingAttendance = false;
                }

                attendanceChanged = false;

                if (students.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No enrolled students found for this course.");
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

        // "Save Attendance" button handler
        saveButton.addActionListener(e -> {
            // Commit active table editing before collecting values
            if (studentTable.isEditing() && !studentTable.getCellEditor().stopCellEditing()) {
                return;
            }

            if (sessionComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select an attendance session first.");
                return;
            }

            if (loadedStudents.isEmpty()
                    || studentTable.getRowCount() == 0
                    || loadedStudents.size() != studentTable.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Please load students first.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Save attendance for the selected session?",
                    "Confirm Attendance",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                AttendanceSession selectedSession = (AttendanceSession) sessionComboBox.getSelectedItem();
                List<AttendanceRecord> records = new ArrayList<>();

                // Build attendance records from table rows
                for (int i = 0; i < loadedStudents.size(); i++) {
                    Undergraduate student = loadedStudents.get(i);
                    String status = studentTable.getValueAt(i, 3).toString();

                    // Require explicit attendance status selection for all students
                    if (status.equals("Select Status")) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Please select an attendance status for every student."
                        );
                        return;
                    }

                    AttendanceRecord record = new AttendanceRecord();
                    record.setAttendanceSessionId(selectedSession.getAttendanceSessionId());
                    record.setStudentId(student.getUserId());
                    record.setAttendanceStatus(status);

                    // Track which Technical Officer recorded the attendance
                    if (userId > 0) {
                        record.setRecordedBy(userId);
                    }

                    records.add(record);
                }

                // Batch insert or update via DAO transaction
                boolean success = new AttendanceDAO().recordAttendanceBatch(records);

                if (success) {
                    attendanceChanged = false;
                    JOptionPane.showMessageDialog(this, "Attendance saved successfully.");
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Could not save attendance.",
                            "Save Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not save attendance: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Load courses on panel creation
        loadCourses();
    }

    /**
     * Loads active courses from the database and fills the course dropdown.
     */
    private void loadCourses() {
        try {
            courses = new CourseDAO().getAllCourses();

            changingSelection = true;
            courseComboBox.removeAllItems();
            courseComboBox.addItem("Select a course");

            for (Course course : courses) {
                courseComboBox.addItem(
                        course.getCourseCode() + " - " + course.getCourseName()
                );
            }

            courseComboBox.setSelectedIndex(0);
            previousCourseIndex = 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load courses: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            changingSelection = false;
        }
    }

    /**
     * Loads scheduled sessions for the currently selected course and component (Theory/Practical).
     */
    private void loadSessions() {
        int selectedIndex = courseComboBox.getSelectedIndex();
        String component = (String) componentComboBox.getSelectedItem();

        changingSelection = true;
        try {
            sessionComboBox.removeAllItems();
            previousSession = null;

            if (selectedIndex <= 0
                    || component == null
                    || component.equals("Select a component")) {
                return;
            }

            Course selectedCourse = courses.get(selectedIndex - 1);
            List<AttendanceSession> sessions =
                    new AttendanceDAO().getSessionsByCourse(selectedCourse.getCourseId(), component);

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

            // Automatically select first session in dropdown
            previousSession = (AttendanceSession) sessionComboBox.getSelectedItem();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load sessions: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            changingSelection = false;
        }
    }

    /**
     * Clears student table rows and resets internal change tracking.
     */
    private void clearStudentTable() {
        loadingAttendance = true;
        try {
            DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
            model.setRowCount(0);
            loadedStudents.clear();
            attendanceChanged = false;
        } finally {
            loadingAttendance = false;
        }
    }

    /**
     * Prompts the officer to confirm discarding unsaved table edits before switching selection.
     *
     * @return true if there are no unsaved changes or if the user confirmed discard
     */
    private boolean confirmDiscardChanges() {
        if (!attendanceChanged) {
            return true;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "You have unsaved attendance changes.\n" + "Do you want to discard them?",
                "Unsaved Changes",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        return choice == JOptionPane.YES_OPTION;
    }
}
