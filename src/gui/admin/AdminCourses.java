package gui.admin;

import dao.admin.AdminCourseDAO;
import dao.admin.AdminUserDAO;
import gui.admin.CourseComponents.*;
import model.Course;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminCourses extends JPanel {

    private final AdminCourseDAO courseDAO;
    private final AdminUserDAO userDAO;

    private CourseTitleBar titleBar;
    private CourseFilterSet filterSet;
    private CourseActions actionToolbar;
    private CourseList tableContainer;
    private CourseTable courseTable;

    public AdminCourses(long userId, String username, String fullName, String email, String role) {
        setLayout(new BorderLayout(0, 12));
        setBackground(new Color(0xE8, 0xF5, 0xE9));
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

        titleBar = new CourseTitleBar();
        topPanel.add(titleBar);
        topPanel.add(Box.createVerticalStrut(10));

        filterSet = new CourseFilterSet(this::onFilterChanged);
        topPanel.add(filterSet);
        topPanel.add(Box.createVerticalStrut(10));

        actionToolbar = new CourseActions(
                this::onAddCourse,
                this::onViewDetails,
                this::onEditCourse,
                this::onDeleteCourse,
                this::onAssignLecturer,
                this::loadCourses
        );
        topPanel.add(actionToolbar);

        add(topPanel, BorderLayout.NORTH);

        courseTable = new CourseTable();
        tableContainer = new CourseList(courseTable);
        add(tableContainer, BorderLayout.CENTER);
    }

    private void loadCourses() {
        try {
            List<Course> list = courseDAO.getAllCourses();
            courseTable.setCourses(list);
            titleBar.updateCount(list.size());
        } catch (Exception e) {
            System.err.println("[AdminCourses] Error loading courses: " + e.getMessage());
        }
    }

    private void onFilterChanged() {
        String query = filterSet.getSearchQuery().toLowerCase();
        int deptId = filterSet.getSelectedDepartmentId();
        String sem = filterSet.getSelectedSemester();

        try {
            List<Course> list = courseDAO.getAllCourses();
            List<Course> filtered = list.stream().filter(c -> {
                boolean mQuery = query.isEmpty() ||
                        c.getCourseCode().toLowerCase().contains(query) ||
                        c.getCourseName().toLowerCase().contains(query);
                boolean mDept = (deptId == 0 || c.getDepartmentId() == deptId);
                boolean mSem = ("All Semesters".equals(sem) || sem.equalsIgnoreCase(c.getSemester()));
                return mQuery && mDept && mSem;
            }).toList();

            courseTable.setCourses(filtered);
            titleBar.updateCount(filtered.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onAddCourse() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        CourseForm form = new CourseForm(owner, courseDAO, null, this::loadCourses);
        form.setVisible(true);
    }

    private void onViewDetails() {
        Course selected = courseTable.getSelectedCourse();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a course module from the table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        CourseDetails details = new CourseDetails(owner, selected);
        details.setVisible(true);
    }

    private void onEditCourse() {
        Course selected = courseTable.getSelectedCourse();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a course module from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        CourseForm form = new CourseForm(owner, courseDAO, selected, this::loadCourses);
        form.setVisible(true);
    }

    private void onDeleteCourse() {
        Course selected = courseTable.getSelectedCourse();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a course module from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete course " + selected.getCourseCode() + " (" + selected.getCourseName() + ")?",
                "Delete Course", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                courseDAO.deleteCourse(selected.getCourseId());
                JOptionPane.showMessageDialog(this, "Course deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCourses();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting course: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onAssignLecturer() {
        Course selected = courseTable.getSelectedCourse();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a course module from the table to assign a lecturer.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        AssignLecturerDialog dialog = new AssignLecturerDialog(owner, courseDAO, userDAO, selected, this::loadCourses);
        dialog.setVisible(true);
    }
}
