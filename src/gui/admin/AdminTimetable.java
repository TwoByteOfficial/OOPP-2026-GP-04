package gui.admin;

import dao.admin.AdminTimetableDAO;
import gui.admin.TimetableComponents.*;
import model.TimetableEntry;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminTimetable extends JPanel {

    private final AdminTimetableDAO timetableDAO;

    private TimetableTitleBar titleBar;
    private TimetableFilterBar filterBar;
    private TimetableActions actionToolbar;
    private TimetableView tableViewContainer;
    private TimetableTable timetableTable;

    public AdminTimetable(long userId, String username, String fullName, String email, String role) {
        this.timetableDAO = new AdminTimetableDAO();

        setLayout(null);
        setBackground(AdminTheme.LIGHT_BG);
        setBounds(0, 0, 1660, 960);

        initUI();
        loadTimetables();
    }

    private void initUI() {
        titleBar = new TimetableTitleBar();
        titleBar.setBounds(20, 20, 1620, 80);
        add(titleBar);

        filterBar = new TimetableFilterBar(this::onFilterChanged, this::loadTimetables);
        filterBar.setBounds(20, 110, 1620, 60);
        add(filterBar);

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);

        timetableTable = new TimetableTable();
        tableViewContainer = new TimetableView(timetableTable);
        tableViewContainer.setBounds(20, 250, 1620, 680);
        add(tableViewContainer);

        actionToolbar = new TimetableActions(owner, timetableDAO, timetableTable::getSelectedTimetable, this::loadTimetables);
        actionToolbar.setBounds(20, 180, 1620, 56);
        add(actionToolbar);
    }

    private void loadTimetables() {
        try {
            List<TimetableEntry> list = timetableDAO.getAllTimetableEntries();
            timetableTable.setTimetables(list);
            titleBar.updateCount(list.size());
        } catch (Exception e) {
            System.err.println("[AdminTimetable] Error loading schedule: " + e.getMessage());
        }
    }

    private void onFilterChanged() {
        int deptId = filterBar.getSelectedDepartmentId();
        int batch = filterBar.getSelectedBatchYear();
        String sem = filterBar.getSelectedSemester();

        try {
            List<TimetableEntry> list = timetableDAO.getAllTimetableEntries();
            List<TimetableEntry> filtered = list.stream().filter(t -> {
                boolean mDept = (deptId == 0 || t.getDepartmentId() == deptId);
                boolean mBatch = (batch == 0 || t.getBatchYear() == batch);
                boolean mSem = ("All Semesters".equalsIgnoreCase(sem) || sem.equalsIgnoreCase(t.getSemester()));
                return mDept && mBatch && mSem;
            }).toList();
            timetableTable.setTimetables(filtered);
            titleBar.updateCount(filtered.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}