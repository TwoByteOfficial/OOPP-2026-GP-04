package gui.undergraduate.attendance.table;

import gui.admin.AdminTheme;
import gui.undergraduate.attendance.model.CourseAttendanceData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Module-wise attendance directory table with status badges and interactive row selection.
 */
public class AttendanceDirectoryPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JTable attendanceTable;
    private final DefaultTableModel tableModel;

    public AttendanceDirectoryPanel(Consumer<String> onCourseSelected) {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel cardTitle = new JLabel("Module Attendance Directory");
        cardTitle.setFont(AdminTheme.font(Font.BOLD, 16));
        cardTitle.setForeground(AdminTheme.FOREST_GREEN);

        JLabel helperText = new JLabel("Select a module to view breakdown");
        helperText.setFont(AdminTheme.font(Font.PLAIN, 12));
        helperText.setForeground(AdminTheme.MUTED);

        titlePanel.add(cardTitle, BorderLayout.WEST);
        titlePanel.add(helperText, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"Code", "Course Name", "Sessions", "Attendance", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setFont(AdminTheme.font(Font.PLAIN, 13));
        attendanceTable.setRowHeight(40);
        attendanceTable.setShowGrid(false);
        attendanceTable.setIntercellSpacing(new Dimension(0, 0));
        attendanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        attendanceTable.setSelectionBackground(AdminTheme.PASTEL_GREEN);
        attendanceTable.setSelectionForeground(AdminTheme.DEEP_GREEN);

        JTableHeader header = attendanceTable.getTableHeader();
        header.setFont(AdminTheme.font(Font.BOLD, 13));
        header.setBackground(AdminTheme.MINT);
        header.setForeground(AdminTheme.DEEP_GREEN);
        header.setPreferredSize(new Dimension(0, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD2, 0xD7, 0xE1)));

        // Column widths
        attendanceTable.getColumnModel().getColumn(0).setPreferredWidth(65);  // Code
        attendanceTable.getColumnModel().getColumn(1).setPreferredWidth(205); // Course Name
        attendanceTable.getColumnModel().getColumn(2).setPreferredWidth(65);  // Sessions
        attendanceTable.getColumnModel().getColumn(3).setPreferredWidth(85);  // Attendance
        attendanceTable.getColumnModel().getColumn(4).setPreferredWidth(70);  // Status

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setBorder(new EmptyBorder(0, 12, 0, 12));
        for (int i = 0; i < 4; i++) {
            attendanceTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }

        // Custom renderer for Status column (Eligible / At Risk badge)
        attendanceTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(AdminTheme.font(Font.BOLD, 11));
                lbl.setBorder(new EmptyBorder(2, 6, 2, 6));

                String status = String.valueOf(value);
                if ("Eligible".equalsIgnoreCase(status)) {
                    lbl.setForeground(AdminTheme.FOREST_GREEN);
                } else {
                    lbl.setForeground(AdminTheme.DANGER);
                }
                return lbl;
            }
        });

        attendanceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = attendanceTable.getSelectedRow();
                if (selectedRow != -1 && onCourseSelected != null) {
                    onCourseSelected.accept((String) attendanceTable.getValueAt(selectedRow, 0));
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(attendanceTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(0xEAEAEA)));
        tableScroll.getViewport().setBackground(AdminTheme.WHITE);
        tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(tableScroll, BorderLayout.CENTER);
    }

    public void setCourseData(List<CourseAttendanceData> courseList) {
        tableModel.setRowCount(0);
        for (CourseAttendanceData data : courseList) {
            int total = data.getTotalLectures() + data.getTotalPracticals();
            int attended = data.getAttendedLectures() + data.getAttendedPracticals() + data.getApprovedMedicals();
            String sessions = attended + "/" + total;
            String percentage = String.format("%.1f%%", data.getOverallPercentage());
            String status = data.isEligible() ? "Eligible" : "At Risk";

            tableModel.addRow(new Object[]{
                    data.getCourseCode(),
                    data.getCourseName(),
                    sessions,
                    percentage,
                    status
            });
        }
    }

    public void selectFirstRow() {
        if (attendanceTable.getRowCount() > 0) {
            attendanceTable.setRowSelectionInterval(0, 0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(AdminTheme.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.setColor(CARD_BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.dispose();
        super.paintComponent(g);
    }
}