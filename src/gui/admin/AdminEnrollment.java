package gui.admin;

import dao.admin.AdminCourseDAO;
import dao.admin.AdminEnrollDAO;
import gui.admin.EnrollmentComponents.*;
import model.EnrollmentRequest;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * AdminEnrollment - Main Management Panel for Student Course Enrollment Requests.
 * Integrates EnrollmentComponents, AdminEnrollDAO, and AdminTheme styling.
 */
public class AdminEnrollment extends JPanel {

    private final long adminUserId;
    private final String adminUsername;
    private final String adminFullName;

    private final AdminEnrollDAO enrollDAO;
    private final AdminCourseDAO courseDAO;

    private EnrollmentTitleBar titleBar;
    private EnrollmentFilterSet filterSet;
    private EnrollmentActions actionToolbar;
    private EnrollmentList tableContainer;
    private EnrollmentTable enrollmentTable;

    public AdminEnrollment(long userId, String username, String fullName, String email, String role) {
        this.adminUserId = userId;
        this.adminUsername = username;
        this.adminFullName = fullName;

        setLayout(new BorderLayout(0, 12));
        setBackground(new Color(0xE8, 0xF5, 0xE9));
        setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

        this.enrollDAO = new AdminEnrollDAO();
        this.courseDAO = new AdminCourseDAO();

        initUI();
        loadEnrollmentRequests();
    }

    private void initUI() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        titleBar = new EnrollmentTitleBar(0);
        topPanel.add(titleBar);
        topPanel.add(Box.createVerticalStrut(10));

        filterSet = new EnrollmentFilterSet(this::onFilterChanged);
        topPanel.add(filterSet);
        topPanel.add(Box.createVerticalStrut(10));

        actionToolbar = new EnrollmentActions(
                this::onApproveRequest,
                this::onDeclineRequest,
                this::onViewDetails,
                this::onNewEnrollment,
                this::loadEnrollmentRequests
        );
        topPanel.add(actionToolbar);

        add(topPanel, BorderLayout.NORTH);

        enrollmentTable = new EnrollmentTable();
        tableContainer = new EnrollmentList(enrollmentTable);
        add(tableContainer, BorderLayout.CENTER);
    }

    private void loadEnrollmentRequests() {
        try {
            List<EnrollmentRequest> list = enrollDAO.getAllEnrollmentRequests();
            enrollmentTable.setRequests(list);
            titleBar.updateCount(list.size());
        } catch (Exception e) {
            System.err.println("[AdminEnrollment] Error loading enrollment requests: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error loading enrollment requests from database: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onFilterChanged() {
        String query = filterSet.getSearchQuery().toLowerCase();
        String statusFilter = filterSet.getSelectedStatus();
        String semFilter = filterSet.getSelectedSemester();

        try {
            List<EnrollmentRequest> list = enrollDAO.getAllEnrollmentRequests();
            List<EnrollmentRequest> filtered = list.stream().filter(r -> {
                boolean matchQuery = query.isEmpty() ||
                        (r.getRegistrationNo() != null && r.getRegistrationNo().toLowerCase().contains(query)) ||
                        (r.getStudentName() != null && r.getStudentName().toLowerCase().contains(query)) ||
                        (r.getCourseCode() != null && r.getCourseCode().toLowerCase().contains(query)) ||
                        (r.getCourseName() != null && r.getCourseName().toLowerCase().contains(query));

                boolean matchStatus = "All Statuses".equalsIgnoreCase(statusFilter) ||
                        statusFilter.equalsIgnoreCase(r.getStatus());

                boolean matchSem = "All Semesters".equalsIgnoreCase(semFilter) ||
                        semFilter.equalsIgnoreCase(r.getSemester());

                return matchQuery && matchStatus && matchSem;
            }).toList();

            enrollmentTable.setRequests(filtered);
            titleBar.updateCount(filtered.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onApproveRequest() {
        EnrollmentRequest selected = enrollmentTable.getSelectedRequest();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an enrollment request from the table to approve.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!"Pending".equalsIgnoreCase(selected.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only 'Pending' requests can be approved. Current status: " + selected.getStatus(), "Action Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        EnrollmentDecisionDialog dialog = new EnrollmentDecisionDialog(owner, "Approve", selected.getStudentName(), selected.getCourseCode());
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                boolean approved = enrollDAO.approveEnrollmentRequest(selected.getRequestId(), adminUserId, dialog.getDecisionNote());
                if (approved) {
                    JOptionPane.showMessageDialog(this, "Enrollment request REQ-" + selected.getRequestId() + " approved successfully!", "Request Approved", JOptionPane.INFORMATION_MESSAGE);
                    loadEnrollmentRequests();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to approve enrollment request.", "Approval Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Database Error during approval: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDeclineRequest() {
        EnrollmentRequest selected = enrollmentTable.getSelectedRequest();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an enrollment request from the table to decline.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!"Pending".equalsIgnoreCase(selected.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only 'Pending' requests can be declined. Current status: " + selected.getStatus(), "Action Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        EnrollmentDecisionDialog dialog = new EnrollmentDecisionDialog(owner, "Decline", selected.getStudentName(), selected.getCourseCode());
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                boolean declined = enrollDAO.declineEnrollmentRequest(selected.getRequestId(), adminUserId, dialog.getDecisionNote());
                if (declined) {
                    JOptionPane.showMessageDialog(this, "Enrollment request REQ-" + selected.getRequestId() + " declined.", "Request Declined", JOptionPane.INFORMATION_MESSAGE);
                    loadEnrollmentRequests();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to decline enrollment request.", "Decline Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Database Error during decline: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onViewDetails() {
        EnrollmentRequest selected = enrollmentTable.getSelectedRequest();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an enrollment request from the table to view details.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        EnrollmentDetailsDialog dialog = new EnrollmentDetailsDialog(owner, selected);
        dialog.setVisible(true);
    }

    private void onNewEnrollment() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        EnrollmentForm form = new EnrollmentForm(owner, enrollDAO, courseDAO, this::loadEnrollmentRequests);
        form.setVisible(true);
    }
}
