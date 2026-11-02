package gui.admin;

import dao.admin.AdminCourseDAO;
import dao.admin.AdminUserDAO;
import gui.admin.CourseComponents.*;
import model.Course;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
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
            JOptionPane.showMessageDialog(this,
                    "Error loading course modules from database: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
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
                boolean mSem = ("All Semesters".equalsIgnoreCase(sem) ||
                        sem.equalsIgnoreCase(c.getSemester()) ||
                        ("Semester 1".equalsIgnoreCase(sem) && "Semester I".equalsIgnoreCase(c.getSemester())) ||
                        ("Semester 2".equalsIgnoreCase(sem) && "Semester II".equalsIgnoreCase(c.getSemester())));
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
            JOptionPane.showMessageDialog(this, "Please select a course module from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
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
                "Are you sure you want to permanently delete course " + selected.getCourseCode() + " (" + selected.getCourseName() + ")?",
                "Confirm Course Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = courseDAO.deleteCourse(selected.getCourseId());
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Course '" + selected.getCourseCode() + "' deleted successfully!", "Course Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete course. Record may no longer exist in database.", "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                String msg = e.getMessage();
                if (msg != null && msg.toLowerCase().contains("foreign key")) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot delete course '" + selected.getCourseCode() + "' because it is linked to existing student marks, enrollments, or timetable slots. Remove or reassign those dependencies first.",
                            "Cannot Delete Course",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Database Error while deleting course: " + msg,
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "An unexpected error occurred while deleting course: " + e.getMessage(),
                        "System Error",
                        JOptionPane.ERROR_MESSAGE);
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