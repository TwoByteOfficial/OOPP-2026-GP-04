package gui.admin;

import dao.admin.AdminTimetableDAO;
import gui.admin.TimetableComponents.*;
import model.TimetableEntry;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class AdminTimetable extends JPanel {

    private final long userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    private AdminTimetableDAO timetableDAO;
    private List<TimetableEntry> masterList = new ArrayList<>();

    private TimetableTitleBar titleBar;
    private TimetableFilterSet filterSet;
    private TimetableActions actionToolbar;
    private TimetableView timetableView;

    public AdminTimetable(long userId, String username, String fullName, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;

        setLayout(null);
        setBounds(0, 0, 1660, 1080);
        setBackground(new Color(0xE8, 0xF5, 0xE9));

        timetableDAO = new AdminTimetableDAO();

        // Title Bar
        titleBar = new TimetableTitleBar();
        titleBar.setLocation(20, 20);
        add(titleBar);

        // Filter Bar
        filterSet = new TimetableFilterSet(this::applyFilters);
        filterSet.setBounds(20, 120, 1620, 60);
        add(filterSet);

        // Actions Toolbar
        actionToolbar = new TimetableActions(
                this::openAddDialog,
                this::openViewDialog,
                this::openEditDialog,
                this::deleteTimetableEntry,
                this::loadTimetableFromDB
        );
        actionToolbar.setBounds(20, 190, 1620, 60);
        add(actionToolbar);

        // Timetable Table View
        timetableView = new TimetableView();
        timetableView.setBounds(20, 260, 1620, 780);
        add(timetableView);

        loadTimetableFromDB();
    }

    private void loadTimetableFromDB() {
        try {
            masterList = timetableDAO.getAllTimetableEntries();
            titleBar.updateTimetableCount(masterList.size());
            applyFilters();
        } catch (Exception e) {
            System.err.println("[AdminTimetable Error] Could not load timetable entries: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        String dept = filterSet.getSelectedDepartment();
        String batch = filterSet.getSelectedBatch();
        String sem = filterSet.getSelectedSemester();

        List<TimetableEntry> filtered = new ArrayList<>();
        for (TimetableEntry t : masterList) {
            boolean matchesDept = "All Departments".equalsIgnoreCase(dept);
            if (!matchesDept) {
                if (dept.startsWith("1") && t.getDepartmentId() == 1) matchesDept = true;
                else if (dept.startsWith("2") && t.getDepartmentId() == 2) matchesDept = true;
                else if (dept.startsWith("3") && t.getDepartmentId() == 3) matchesDept = true;
            }

            boolean matchesBatch = "All Batches".equalsIgnoreCase(batch)
                    || (String.valueOf(t.getBatchYear()).equalsIgnoreCase(batch));

            boolean matchesSem = "All Semesters".equalsIgnoreCase(sem)
                    || (t.getSemester() != null && t.getSemester().equalsIgnoreCase(sem));

            if (matchesDept && matchesBatch && matchesSem) {
                filtered.add(t);
            }
        }
        timetableView.setTimetables(filtered);
    }

    private void openAddDialog() {
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        TimetableForm form = new TimetableForm(topFrame, timetableDAO, null, this::loadTimetableFromDB);
        form.setVisible(true);
    }

    private void openViewDialog() {
        TimetableEntry selected = timetableView.getSelectedTimetable();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a timetable slot to view.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        TimetableDetails details = new TimetableDetails(topFrame, selected);
        details.setVisible(true);
    }

    private void openEditDialog() {
        TimetableEntry selected = timetableView.getSelectedTimetable();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a timetable slot to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        TimetableForm form = new TimetableForm(topFrame, timetableDAO, selected, this::loadTimetableFromDB);
        form.setVisible(true);
    }

    private void deleteTimetableEntry() {
        TimetableEntry selected = timetableView.getSelectedTimetable();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a timetable slot to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete slot TT-" + selected.getTimetableId() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            try {
                timetableDAO.deleteTimetableEntry(selected.getTimetableId());
                JOptionPane.showMessageDialog(this, "Timetable entry deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadTimetableFromDB();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting timetable entry: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}