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
    private TimetableFilterBar filterBar;
    private TimetableActions actionToolbar;
    private TimetableView tableViewContainer;
    private TimetableTable timetableTable;

    public AdminTimetable(long userId, String username, String fullName, String email, String role) {
        setLayout(new BorderLayout(0, 12));
        setBackground(AdminTheme.MINT);
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

        filterBar = new TimetableFilterBar(this::onFilterChanged, this::loadTimetables);
        topPanel.add(filterBar);
        topPanel.add(Box.createVerticalStrut(10));

        timetableTable = new TimetableTable();

        actionToolbar = new TimetableActions(
                this,
                timetableDAO,
                courseDAO,
                userDAO,
                timetableTable::getSelectedTimetable,
                this::loadTimetables
        );
        topPanel.add(actionToolbar);

        add(topPanel, BorderLayout.NORTH);

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
        int deptId = filterBar.getSelectedDepartmentId();
        int batch = filterBar.getSelectedBatchYear();
        String sem = filterBar.getSelectedSemester();

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
}