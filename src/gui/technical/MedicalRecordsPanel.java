
package gui.technical;

import dao.TechnicalOfficer.MedicalDAO;
import model.Medical;
import gui.theme.UniMisTheme;
import dao.TechnicalOfficer.AttendanceDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MedicalRecordsPanel extends JPanel {


    private final DefaultTableModel tableModel;
    private final JTable medicalTable;
    private final JComboBox<String> statusFilter;
    private final JComboBox<String> courseFilter;
    private final JTextField searchField;
    private final long userId;



    public MedicalRecordsPanel(long userId) {
        this.userId = userId;

        setLayout(new BorderLayout(15, 15));
        setBackground(UniMisTheme.LIGHT_BG);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Medical Records");
        title.setFont(UniMisTheme.FONT_H2);
        title.setForeground(UniMisTheme.TEXT);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(UniMisTheme.FONT_BODY);
        refreshButton.setBackground(UniMisTheme.MEDIUM_GREEN);
        refreshButton.setForeground(UniMisTheme.TEXT);
        refreshButton.addActionListener(e -> loadMedicalRecords());


        statusFilter = new JComboBox<>(new String[]{
                "All", "Pending", "Approved", "Rejected"
        });
        statusFilter.setFont(UniMisTheme.FONT_SMALL);

        courseFilter = new JComboBox<>();
        courseFilter.setFont(UniMisTheme.FONT_SMALL);

        searchField = new JTextField(15);
        searchField.setFont(UniMisTheme.FONT_SMALL);
        searchField.setToolTipText(
                "Search registration number, student name, or course code"
        );

        statusFilter.addActionListener(e -> loadMedicalRecords());
        courseFilter.addActionListener(e -> loadMedicalRecords());
        searchField.addActionListener(e -> loadMedicalRecords());

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel filterPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 5)
        );
        filterPanel.setOpaque(false);
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(new JLabel("Course:"));
        filterPanel.add(courseFilter);
        filterPanel.add(refreshButton);

        header.add(title);
        header.add(Box.createVerticalStrut(12));
        header.add(filterPanel);
        add(header, BorderLayout.NORTH);










        courseFilter.setFont(UniMisTheme.FONT_SMALL);


        searchField.setFont(UniMisTheme.FONT_SMALL);
        searchField.setToolTipText(
                "Search registration number, student name, or course code"
        );

//        statusFilter.addActionListener(e -> loadMedicalRecords());
//        courseFilter.addActionListener(e -> loadMedicalRecords());
//
//        searchField.addActionListener(e -> loadMedicalRecords());





        String[] columns = {
                "Medical ID",
                "Registration No",
                "Student Name",
                "Course Code",
                "Course Name",
                "Medical Date",
                "Reason",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        medicalTable = new JTable(tableModel);
        medicalTable.setFont(UniMisTheme.FONT_SMALL);
        medicalTable.setRowHeight(30);
        medicalTable.getTableHeader().setFont(
                UniMisTheme.FONT_BODY_BOLD);
        medicalTable.getTableHeader().setBackground(
                UniMisTheme.PASTEL_GREEN);
        medicalTable.setSelectionBackground(
                UniMisTheme.FRESH_GREEN);

        medicalTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = medicalTable.getSelectedRow();

                    if (row != -1) {
                        showMedicalDetails(medicalTable.convertRowIndexToModel(row));
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(medicalTable);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(UniMisTheme.BORDER));

        add(scrollPane, BorderLayout.CENTER);
        JButton approveButton = new JButton("Approve Selected");
        approveButton.setFont(UniMisTheme.FONT_BODY);
        approveButton.setBackground(UniMisTheme.MEDIUM_GREEN);
        approveButton.setForeground(UniMisTheme.TEXT);

        approveButton.addActionListener(e -> approveSelectedMedical());

        JButton rejectButton = new JButton("Reject Selected");
        rejectButton.setFont(UniMisTheme.FONT_BODY);
        rejectButton.setBackground(UniMisTheme.DANGER);
        rejectButton.setForeground(Color.WHITE);

        rejectButton.addActionListener(e -> rejectSelectedMedical());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(rejectButton);
        bottomPanel.add(approveButton);

        add(bottomPanel, BorderLayout.SOUTH);

        loadCourses();
        loadMedicalRecords();
    }


    private void loadMedicalRecords() {
        tableModel.setRowCount(0);

        try {
            String selectedStatus =
                    statusFilter.getSelectedItem().toString();

            String selectedCourse =
                    courseFilter.getSelectedItem() == null
                            ? "All Courses"
                            : courseFilter.getSelectedItem().toString();

            int courseId = 0;

            if (!selectedCourse.equals("All Courses")) {
                java.util.Map<String, Integer> courses =
                        new MedicalDAO().getAllCourses();

                courseId = courses.get(selectedCourse);
            }

            String searchText = searchField.getText();

            List<Medical> records =
                    new MedicalDAO().searchMedicalRecords(
                            selectedStatus,
                            courseId,
                            searchText
                    );

            for (Medical medical : records) {
                tableModel.addRow(new Object[]{
                        medical.getMedicalId(),
                        medical.getRegistrationNo(),
                        medical.getStudentName(),
                        medical.getCourseCode(),
                        medical.getCourseName(),
                        medical.getMedicalDate(),
                        medical.getReason(),
                        medical.getApprovalStatus()
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load medical records:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }



    private void approveSelectedMedical() {

        int selectedRow = medicalTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medical record first.",
                    "No Record Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = medicalTable.convertRowIndexToModel(selectedRow);
        String status = tableModel.getValueAt(modelRow, 7).toString();

        if (!status.equals("Pending")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Only pending medical records can be approved.",
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String comment = JOptionPane.showInputDialog(
                this,
                "Enter a review comment:"
        );

        if (comment == null) {
            return;
        }

        try {
            long medicalId = Long.parseLong(
                    tableModel.getValueAt(modelRow, 0).toString()
            );

            boolean success = new AttendanceDAO().approveMedical(
                    medicalId,
                    userId,
                    comment
            );

            if (success) {
                JOptionPane.showMessageDialog(
                        this,
                        "Medical record approved successfully."
                );
                loadMedicalRecords();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not approve medical record:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    private void rejectSelectedMedical() {

        int selectedRow = medicalTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medical record first.",
                    "No Record Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = medicalTable.convertRowIndexToModel(selectedRow);
        String status = tableModel.getValueAt(modelRow, 7).toString();

        if (!status.equals("Pending")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Only pending medical records can be rejected.",
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String comment = JOptionPane.showInputDialog(
                this,
                "Enter the reason for rejection:"
        );

        if (comment == null) {
            return;
        }

        if (comment.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a rejection reason.",
                    "Reason Required",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            long medicalId = Long.parseLong(
                    tableModel.getValueAt(modelRow, 0).toString()
            );

            boolean success = new AttendanceDAO().rejectMedical(
                    medicalId,
                    userId,
                    comment.trim()
            );

            if (success) {
                JOptionPane.showMessageDialog(
                        this,
                        "Medical record rejected successfully."
                );
                loadMedicalRecords();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "This record is no longer pending. Refresh and try again.",
                        "Record Not Updated",
                        JOptionPane.WARNING_MESSAGE
                );
                loadMedicalRecords();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not reject medical record:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }





    private void showMedicalDetails(int row) {

        String details =
                "Medical ID: " + tableModel.getValueAt(row, 0) + "\n\n" +
                        "Registration No: " + tableModel.getValueAt(row, 1) + "\n\n" +
                        "Student Name: " + tableModel.getValueAt(row, 2) + "\n\n" +
                        "Course Code: " + tableModel.getValueAt(row, 3) + "\n\n" +
                        "Course Name: " + tableModel.getValueAt(row, 4) + "\n\n" +
                        "Medical Date: " + tableModel.getValueAt(row, 5) + "\n\n" +
                        "Reason: " + tableModel.getValueAt(row, 6) + "\n\n" +
                        "Status: " + tableModel.getValueAt(row, 7);

        JTextArea textArea = new JTextArea(details);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(UniMisTheme.FONT_BODY);
        textArea.setBackground(UniMisTheme.WHITE);
        textArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 350));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Medical Record Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    private void loadCourses() {
        try {
            courseFilter.removeAllItems();
            courseFilter.addItem("All Courses");

            java.util.Map<String, Integer> courses =
                    new MedicalDAO().getAllCourses();

            for (String courseCode : courses.keySet()) {
                courseFilter.addItem(courseCode);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load courses:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }



}
