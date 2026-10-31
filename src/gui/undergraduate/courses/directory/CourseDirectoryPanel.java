package gui.undergraduate.courses.directory;

import dao.undergraduate.UndergraduateCourseDAO.EnrolledCourseDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Registered modules list table connected dynamically to MySQL database.
 * Optimized column widths and cell alignments to avoid text truncation.
 */
public class CourseDirectoryPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JTable coursesTable;
    private final DefaultTableModel tableModel;

    public CourseDirectoryPanel(Consumer<String> onCourseSelected) {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel cardTitle = new JLabel("Registered Module Directory");
        cardTitle.setFont(AdminTheme.font(Font.BOLD, 15));
        cardTitle.setForeground(AdminTheme.FOREST_GREEN);

        JLabel helperText = new JLabel("Click any module to inspect criteria");
        helperText.setFont(AdminTheme.font(Font.PLAIN, 12));
        helperText.setForeground(AdminTheme.MUTED);

        titlePanel.add(cardTitle, BorderLayout.WEST);
        titlePanel.add(helperText, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"Code", "Course Name", "Credits", "Type"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        coursesTable = new JTable(tableModel);
        coursesTable.setFont(AdminTheme.font(Font.PLAIN, 13));
        coursesTable.setRowHeight(36);
        coursesTable.setShowGrid(false);
        coursesTable.setIntercellSpacing(new Dimension(0, 0));
        coursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        coursesTable.setSelectionBackground(AdminTheme.PASTEL_GREEN);
        coursesTable.setSelectionForeground(AdminTheme.DEEP_GREEN);
        coursesTable.setFocusable(false); // Removes the cell focus outline box

        JTableHeader header = coursesTable.getTableHeader();
        header.setFont(AdminTheme.font(Font.BOLD, 12));
        header.setBackground(AdminTheme.MINT);
        header.setForeground(AdminTheme.DEEP_GREEN);
        header.setPreferredSize(new Dimension(0, 34));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD2, 0xD7, 0xE1)));

        // Optimized Column Width Allocation
        coursesTable.getColumnModel().getColumn(0).setPreferredWidth(68);  // Code
        coursesTable.getColumnModel().getColumn(1).setPreferredWidth(240); // Course Name (extra width)
        coursesTable.getColumnModel().getColumn(2).setPreferredWidth(52);  // Credits
        coursesTable.getColumnModel().getColumn(3).setPreferredWidth(65);  // Type

        // Alignments: Left for Code & Name
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setBorder(new EmptyBorder(0, 8, 0, 8));
        coursesTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        coursesTable.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);

        // Center alignment for Credits & Type
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        coursesTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        coursesTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        coursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = coursesTable.getSelectedRow();
                if (selectedRow != -1 && onCourseSelected != null) {
                    onCourseSelected.accept((String) coursesTable.getValueAt(selectedRow, 0));
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(coursesTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(0xEAEAEA)));
        tableScroll.getViewport().setBackground(AdminTheme.WHITE);
        tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(tableScroll, BorderLayout.CENTER);
    }

    public void setCourses(List<EnrolledCourseDTO> courses) {
        tableModel.setRowCount(0);
        for (EnrolledCourseDTO c : courses) {
            tableModel.addRow(new Object[]{
                    c.courseCode,
                    c.courseName,
                    String.valueOf(c.totalCredits),
                    c.enrollmentType
            });
        }
        selectFirstRow();
    }

    public void selectFirstRow() {
        if (coursesTable.getRowCount() > 0) {
            coursesTable.setRowSelectionInterval(0, 0);
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