package gui.admin;

import dao.admin.AdminCourseDAO;
import gui.admin.CourseComponents.*;
import model.Course;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class AdminCourses extends JPanel {

    private final long userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    private AdminCourseDAO courseDAO;
    private List<Course> masterList = new ArrayList<>();

    private CourseTitleBar titleBar;
    private CourseFilterSet filterSet;
    private CourseActions actionToolbar;
    private CourseList courseListView;

    public AdminCourses(long userId, String username, String fullName, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;

        setLayout(null);
        setBounds(0, 0, 1660, 1080);
        setBackground(new Color(0xE8, 0xF5, 0xE9));

        courseDAO = new AdminCourseDAO();

        // Title Bar
        titleBar = new CourseTitleBar();
        titleBar.setLocation(20, 20);
        add(titleBar);

        // Filter Bar
        filterSet = new CourseFilterSet(this::applyFilters);
        filterSet.setBounds(20, 120, 1620, 60);
        add(filterSet);

        // Actions Toolbar
        actionToolbar = new CourseActions(
                this::openAddDialog,
                this::openViewDialog,
                this::openEditDialog,
                this::deleteCourse,
                this::openAssignLecturerDialog,
                this::loadCoursesFromDB
        );
        actionToolbar.setBounds(20, 190, 1620, 60);
        add(actionToolbar);

        // Course List Table View
        courseListView = new CourseList();
        courseListView.setBounds(20, 260, 1620, 780);
        add(courseListView);

        loadCoursesFromDB();
    }

    private void loadCoursesFromDB() {
        try {
            masterList = courseDAO.getAllCourses();
            titleBar.updateCourseCount(masterList.size());
            applyFilters();
        } catch (Exception e) {
            System.err.println("[AdminCourses Error] Could not load courses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        String query = filterSet.getSearchQuery().toLowerCase();
        String dept = filterSet.getSelectedDepartment();
        String sem = filterSet.getSelectedSemester();

        List<Course> filtered = new ArrayList<>();
        for (Course c : masterList) {
            boolean matchesSearch = query.isEmpty()
                    || (c.getCourseCode() != null && c.getCourseCode().toLowerCase().contains(query))
                    || (c.getCourseName() != null && c.getCourseName().toLowerCase().contains(query));

            boolean matchesDept = "All Departments".equalsIgnoreCase(dept);
            if (!matchesDept) {
                if (dept.startsWith("1") && c.getDepartmentId() == 1) matchesDept = true;
                else if (dept.startsWith("2") && c.getDepartmentId() == 2) matchesDept = true;
                else if (dept.startsWith("3") && c.getDepartmentId() == 3) matchesDept = true;
            }

            boolean matchesSem = "All Semesters".equalsIgnoreCase(sem)
                    || (c.getSemester() != null && c.getSemester().equalsIgnoreCase(sem));

            if (matchesSearch && matchesDept && matchesSem) {
                filtered.add(c);
            }
        }
        courseListView.setCourses(filtered);
    }

    private void openAddDialog() {
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        CourseForm form = new CourseForm(topFrame, courseDAO, null, this::loadCoursesFromDB);
        form.setVisible(true);
    }

    private void openViewDialog() {
        Course selected = courseListView.getSelectedCourse();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to view details.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        CourseDetails details = new CourseDetails(topFrame, selected);
        details.setVisible(true);
    }

    private void openEditDialog() {
        Course selected = courseListView.getSelectedCourse();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        CourseForm form = new CourseForm(topFrame, courseDAO, selected, this::loadCoursesFromDB);
        form.setVisible(true);
    }

    private void openAssignLecturerDialog() {
        Course selected = courseListView.getSelectedCourse();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to assign a lecturer.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        AssignLecturerDialog dialog = new AssignLecturerDialog(topFrame, courseDAO, selected, this::loadCoursesFromDB);
        dialog.setVisible(true);
    }

    private void deleteCourse() {
        Course selected = courseListView.getSelectedCourse();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete course " + selected.getCourseCode() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            try {
                courseDAO.deleteCourse(selected.getCourseId());
                JOptionPane.showMessageDialog(this, "Course deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadCoursesFromDB();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting course: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}