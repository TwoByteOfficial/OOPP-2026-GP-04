package gui.admin.CourseComponents;

import dao.admin.AdminCourseDAO;
import gui.admin.AdminTheme;
import model.Course;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * DeleteCourseButton - Integrated component file containing the "Delete Course" action button
 * and its on-click deletion confirmation popup window logic.
 */
public class DeleteCourseButton extends JButton {

    public DeleteCourseButton(CourseTable table, Frame parent, AdminCourseDAO courseDAO, Runnable onSuccess) {
        super("🗑️ Delete Course");
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setBackground(AdminTheme.DANGER);
        setForeground(AdminTheme.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> {
            Course selected = table.getSelectedCourse();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a course module from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int choice = JOptionPane.showConfirmDialog(parent,
                    "Are you sure you want to permanently delete course module:\n" +
                            selected.getCourseCode() + " - " + selected.getCourseName() + "?\n\n" +
                            "This operation cannot be undone.",
                    "Confirm Course Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                try {
                    boolean deleted = courseDAO.deleteCourse(selected.getCourseId());
                    if (deleted) {
                        JOptionPane.showMessageDialog(parent, "Course '" + selected.getCourseCode() + "' deleted successfully!", "Course Deleted", JOptionPane.INFORMATION_MESSAGE);
                        if (onSuccess != null) onSuccess.run();
                    } else {
                        JOptionPane.showMessageDialog(parent, "Failed to delete course. Record may no longer exist in database.", "Delete Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    String msg = ex.getMessage();
                    if (msg != null && msg.toLowerCase().contains("foreign key")) {
                        JOptionPane.showMessageDialog(parent,
                                "Cannot delete course '" + selected.getCourseCode() + "' because it is linked to existing student marks, enrollments, or timetable slots. Remove or reassign those dependencies first.",
                                "Cannot Delete Course",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(parent,
                                "Database Error while deleting course: " + msg,
                                "Database Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parent,
                            "An unexpected error occurred while deleting course: " + ex.getMessage(),
                            "System Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
