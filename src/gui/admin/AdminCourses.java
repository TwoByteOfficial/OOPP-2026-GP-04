package gui.admin;

import dao.admin.AdminCourseDAO;
import dao.admin.AdminUserDAO;
import gui.admin.CourseComponents.*;
import model.Course;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminCourses - Main panel view for Course Modules Management.
 */
public class AdminCourses extends JPanel {

    private final AdminCourseDAO courseDAO;
    private final AdminUserDAO userDAO;

    private List<Course> allLoadedCourses = new ArrayList<>();

    private CourseTitleBar titleBar;
    private CourseFilterSet filterSet;
    private CourseActions actionToolbar;
    private CourseTable courseTable;

    public AdminCourses(long userId, String username, String fullName, String email, String role) {
        setLayout(new BorderLayout(0, 12));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

        this.courseDAO = new AdminCourseDAO();
        this.userDAO = new AdminUserDAO();

        initUI();
        loadCourses();
    }

    private void initUI() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        titleBar = new CourseTitleBar(0);
        topPanel.add(titleBar);
        topPanel.add(Box.createVerticalStrut(10));

        filterSet = new CourseFilterSet(this::applyFilters);
        topPanel.add(filterSet);
        topPanel.add(Box.createVerticalStrut(10));

        courseTable = new CourseTable();

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        actionToolbar = new CourseActions(courseTable, parentFrame, courseDAO, userDAO, this::loadCourses);
        topPanel.add(actionToolbar);

        add(topPanel, BorderLayout.NORTH);
        add(courseTable, BorderLayout.CENTER);
    }

    public void loadCourses() {
        try {
            allLoadedCourses = courseDAO.getAllCourses();
            applyFilters();
        } catch (Exception e) {
            System.err.println("[AdminCourses DB Error] Failed to fetch course modules: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to load course modules from database: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilters() {
        if (allLoadedCourses == null) return;

        String query = filterSet.getSearchQuery().toLowerCase();
        int deptId = filterSet.getSelectedDepartmentId();
        String sem = filterSet.getSelectedSemester();

        List<Course> filtered = new ArrayList<>();

        for (Course c : allLoadedCourses) {
            boolean matchDept = (deptId == 0 || c.getDepartmentId() == deptId);

            boolean matchSem = ("All Semesters".equalsIgnoreCase(sem) ||
                    sem.equalsIgnoreCase(c.getSemester()) ||
                    ("Semester 1".equalsIgnoreCase(sem) && "Semester I".equalsIgnoreCase(c.getSemester())) ||
                    ("Semester 2".equalsIgnoreCase(sem) && "Semester II".equalsIgnoreCase(c.getSemester())));

            boolean matchQuery = query.isEmpty() ||
                    (c.getCourseCode() != null && c.getCourseCode().toLowerCase().contains(query)) ||
                    (c.getCourseName() != null && c.getCourseName().toLowerCase().contains(query));

            if (matchDept && matchSem && matchQuery) {
                filtered.add(c);
            }
        }

        courseTable.setCourses(filtered);
        titleBar.updateCount(filtered.size());
    }
}