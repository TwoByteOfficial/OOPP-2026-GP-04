package gui.admin;

import dao.admin.AdminCourseDAO;
import dao.admin.AdminTimetableDAO;
import dao.admin.AdminUserDAO;
import gui.admin.TimetableComponents.*;
import model.TimetableEntry;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminTimetable extends JPanel {

    private final AdminTimetableDAO timetableDAO;
    private final AdminCourseDAO courseDAO;
    private final AdminUserDAO userDAO;

    private TimetableTitleBar titleBar;
    private TimetableFilterSet filterSet;
    private TimetableActions actionToolbar;
    private TimetableView tableViewContainer;
    private TimetableTable timetableTable;

    public AdminTimetable(long userId, String username, String fullName, String email, String role) {
        setLayout(new BorderLayout(0, 12));
        setBackground(new Color(0xE8, 0xF5, 0xE9));
        setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

        this.timetableDAO = new AdminTimetableDAO();
        this.courseDAO = new AdminCourseDAO();
        this.userDAO = new AdminUserDAO();

        initUI();
        loadTimetables();
    }

    private void initUI() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        titleBar = new TimetableTitleBar();
        topPanel.add(titleBar);
        topPanel.add(Box.createVerticalStrut(10));

        filterSet = new TimetableFilterSet(this::onFilterChanged);
        topPanel.add(filterSet);
        topPanel.add(Box.createVerticalStrut(10));

        actionToolbar = new TimetableActions(
                this::onAddSlot,
                this::onViewDetails,
                this::onEditSlot,
                this::onDeleteSlot,
                this::loadTimetables
        );
        topPanel.add(actionToolbar);

        add(topPanel, BorderLayout.NORTH);

        timetableTable = new TimetableTable();
        tableViewContainer = new TimetableView(timetableTable);
        add(tableViewContainer, BorderLayout.CENTER);
    }

    private void loadTimetables() {
        try {
            List<TimetableEntry> list = timetableDAO.getAllTimetableEntries();
            timetableTable.setTimetables(list, courseDAO, userDAO);
            titleBar.updateCount(list.size());
        } catch (Exception e) {
            System.err.println("[AdminTimetable] Error loading schedule: " + e.getMessage());
        }
    }

    private void onFilterChanged() {
        int deptId = filterSet.getSelectedDepartmentId();
        int batch = filterSet.getSelectedBatchYear();
        String sem = filterSet.getSelectedSemester();

        try {
            if (deptId == 0 && batch == 0 && "All Semesters".equals(sem)) {
                loadTimetables();
            } else {
                List<TimetableEntry> list = timetableDAO.getAllTimetableEntries();
                List<TimetableEntry> filtered = list.stream().filter(t -> {
                    boolean mDept = (deptId == 0 || t.getDepartmentId() == deptId);
                    boolean mBatch = (batch == 0 || t.getBatchYear() == batch);
                    boolean mSem = ("All Semesters".equals(sem) || sem.equalsIgnoreCase(t.getSemester()));
                    return mDept && mBatch && mSem;
                }).toList();
                timetableTable.setTimetables(filtered, courseDAO, userDAO);
                titleBar.updateCount(filtered.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onAddSlot() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        TimetableForm form = new TimetableForm(owner, timetableDAO, courseDAO, userDAO, null, this::loadTimetables);
        form.setVisible(true);
    }

    private void onViewDetails() {
        TimetableEntry selected = timetableTable.getSelectedTimetable();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a timetable slot from the table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        TimetableDetails details = new TimetableDetails(owner, selected);
        details.setVisible(true);
    }

    private void onEditSlot() {
        TimetableEntry selected = timetableTable.getSelectedTimetable();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a timetable slot from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        TimetableForm form = new TimetableForm(owner, timetableDAO, courseDAO, userDAO, selected, this::loadTimetables);
        form.setVisible(true);
    }

    private void onDeleteSlot() {
        TimetableEntry selected = timetableTable.getSelectedTimetable();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a timetable slot from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Slot TT-" + selected.getTimetableId() + "?",
                "Delete Timetable Slot", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                timetableDAO.deleteTimetableEntry(selected.getTimetableId());
                JOptionPane.showMessageDialog(this, "Slot deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTimetables();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting slot: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
