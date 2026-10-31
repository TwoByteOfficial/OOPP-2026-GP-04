
package gui.technical.dashboard.bottom;

import dao.TechnicalOfficer.TimetableDAO;
import gui.theme.UniMisTheme;
import model.TimetableEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DepartmentTimetablePanel extends JPanel {

    private final DefaultTableModel tableModel;
    private final JLabel statusLabel;
    private JComboBox<String> dayFilter;

    public DepartmentTimetablePanel(long userId) {

        setLayout(new BorderLayout(0, 14));
        setBackground(UniMisTheme.WHITE);
        setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel heading = new JLabel("Department Timetable");
        heading.setFont(UniMisTheme.FONT_H3);
        heading.setForeground(UniMisTheme.TEXT);




        String[] days = {
                "All Days", "Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday"
        };

        dayFilter = new JComboBox<>(days);

        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setBackground(UniMisTheme.WHITE);
        topPanel.add(heading, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(UniMisTheme.WHITE);
        filterPanel.add(new JLabel("Filter by day:"));
        filterPanel.add(dayFilter);

        topPanel.add(filterPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);





        String[] columns = {
                "Day", "Time", "Course Code", "Course Name",
                "Component", "Lecturer", "Venue", "Batch"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
// Keep the columns wide enough to display their contents.
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.getColumnModel().getColumn(0).setPreferredWidth(95);   // Day
        table.getColumnModel().getColumn(1).setPreferredWidth(150);  // Time
        table.getColumnModel().getColumn(2).setPreferredWidth(100);  // Course Code
        table.getColumnModel().getColumn(3).setPreferredWidth(180);  // Course Name
        table.getColumnModel().getColumn(4).setPreferredWidth(110);  // Component
        table.getColumnModel().getColumn(5).setPreferredWidth(160);  // Lecturer
        table.getColumnModel().getColumn(6).setPreferredWidth(130);  // Venue
        table.getColumnModel().getColumn(7).setPreferredWidth(70);   // Batch


        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(tableModel);

        table.setRowSorter(sorter);





        table.setFont(UniMisTheme.FONT_TINY);
        table.setRowHeight(30);
        table.setForeground(UniMisTheme.TEXT);
        table.setBackground(UniMisTheme.WHITE);
        table.setGridColor(UniMisTheme.BORDER);
        table.setSelectionBackground(UniMisTheme.PASTEL_GREEN);
        table.setSelectionForeground(UniMisTheme.TEXT);

        table.getTableHeader().setFont(UniMisTheme.FONT_BODY_BOLD);
        table.getTableHeader().setBackground(UniMisTheme.MINT);
        table.getTableHeader().setForeground(UniMisTheme.TEXT);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(UniMisTheme.BORDER));

        statusLabel = new JLabel("Loading timetable...");
        statusLabel.setFont(UniMisTheme.FONT_SMALL);
        statusLabel.setForeground(UniMisTheme.TEXT);


        dayFilter.addActionListener(e -> {
            String selectedDay = (String) dayFilter.getSelectedItem();

            if ("All Days".equals(selectedDay)) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(
                        RowFilter.regexFilter("^" + selectedDay + "$", 0)
                );
            }

            statusLabel.setText(
                    "Timetable entries shown: " + table.getRowCount()
            );
        });



        JPanel contentPanel = new JPanel(new BorderLayout(0, 8));
        contentPanel.setBackground(UniMisTheme.WHITE);
        contentPanel.add(statusLabel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        loadTimetable(userId);
    }

    private void loadTimetable(long userId) {

        tableModel.setRowCount(0);

        try {
            TimetableDAO dao = new TimetableDAO();

            List<TimetableEntry> entries =
                    dao.getTimetableByDepartmentUser(userId);

            if (entries.isEmpty()) {
                statusLabel.setText(
                        "No timetable entries found for your department.");
                return;
            }

            for (TimetableEntry entry : entries) {

                String time =
                        entry.getStartTime() + " - " + entry.getEndTime();

                String lecturer = entry.getLecturerName();

                if (lecturer == null || lecturer.trim().isEmpty()) {
                    lecturer = "Not assigned";
                }

                tableModel.addRow(new Object[]{
                        entry.getDayOfWeek(),
                        time,
                        entry.getCourseCode(),
                        entry.getCourseName(),
                        entry.getComponent(),
                        lecturer,
                        entry.getVenue(),
                        entry.getBatchYear()
                });
            }

            statusLabel.setText(
                    "Timetable entries found: " + entries.size());

        } catch (SQLException e) {
            statusLabel.setText("Unable to load the timetable.");

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load the department timetable.\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
