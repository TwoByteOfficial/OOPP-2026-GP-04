package gui.undergraduate.courses.directory;

import dao.undergraduate.UndergraduateCourseDAO.EnrolledCourseDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Directory table panel for enrolled courses.
 * Dynamically resizes the white card to tightly wrap the exact table row count,
 * eliminating trailing empty white voids.
 */
public class CourseDirectoryPanel extends JPanel {

    // Table and UI layout components
    private final JTable table;
    private final DefaultTableModel model;
    private final JScrollPane scrollPane;
    private final JPanel card;
    private List<EnrolledCourseDTO> courseList = new ArrayList<>();
    private final Consumer<String> onSelectCourse;

    /**
     * Initializes the directory table and its card container with event listeners.
     */
    public CourseDirectoryPanel(Consumer<String> onSelectCourse) {
        this.onSelectCourse = onSelectCourse;
        setLayout(new BorderLayout());
        setOpaque(false);

        // -------------------------------------------------------------
        // 1. Outer Card Container (Custom Rounded White Card)
        // -------------------------------------------------------------
        card = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw rounded white card background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Draw subtle border outline
                g2.setColor(new Color(225, 235, 228));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        // -------------------------------------------------------------
        // 2. Header Caption Row (Section Title & Helper Hint)
        // -------------------------------------------------------------
        JPanel tableTitleRow = new JPanel(new BorderLayout(15, 0));
        tableTitleRow.setOpaque(false);

        JLabel tableTitle = new JLabel("Registered Module Directory");
        tableTitle.setFont(AdminTheme.font(Font.BOLD, 14));
        tableTitle.setForeground(AdminTheme.DEEP_GREEN);

        JLabel tableHint = new JLabel("Click any module to inspect criteria");
        tableHint.setFont(AdminTheme.font(Font.PLAIN, 12));
        tableHint.setForeground(new Color(110, 130, 120));

        tableTitleRow.add(tableTitle, BorderLayout.WEST);
        tableTitleRow.add(tableHint, BorderLayout.EAST);
        card.add(tableTitleRow, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 3. Table Model & Properties Configuration
        // -------------------------------------------------------------
        String[] columns = {"Code", "Course Name", "Credits", "Type"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Read-only directory table; prevent cell editing
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(AdminTheme.font(Font.PLAIN, 13));
        table.setForeground(new Color(30, 41, 59));
        table.setGridColor(new Color(230, 240, 235));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(220, 245, 230));
        table.setSelectionForeground(new Color(27, 67, 50));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Table header visual styling
        table.getTableHeader().setFont(AdminTheme.font(Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(225, 240, 230));
        table.getTableHeader().setForeground(AdminTheme.DEEP_GREEN);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 225, 210)));

        // -------------------------------------------------------------
        // 4. Column Widths & Custom Renderers
        // -------------------------------------------------------------
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);

        // Center align Credits column
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Custom styled green badge renderer for course Type
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, val, isSel, hasFocus, r, c);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setFont(AdminTheme.font(Font.BOLD, 12));
                label.setForeground(new Color(46, 125, 50));
                return label;
            }
        });

        // Row selection listener to notify subscriber of selected course code
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0 && row < courseList.size() && onSelectCourse != null) {
                    onSelectCourse.accept(courseList.get(row).courseCode);
                }
            }
        });

        // ScrollPane wrapper without default thick borders
        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 235, 225), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        card.add(scrollPane, BorderLayout.CENTER);

        // Anchored to NORTH so the card wraps the table height and stops immediately after the last row
        add(card, BorderLayout.NORTH);
    }

    /**
     * Binds enrolled courses data from database DTO list into the table.
     * Dynamically calculates exact container height to eliminate bottom dead space.
     */
    public void setCourses(List<EnrolledCourseDTO> courses) {
        this.courseList = courses;
        model.setRowCount(0);

        // Populate rows with course data
        for (EnrolledCourseDTO c : courses) {
            String typeDisplay = "Normal".equalsIgnoreCase(c.enrollmentType) ? "Core" : c.enrollmentType;
            model.addRow(new Object[]{
                    c.courseCode,
                    c.courseName,
                    c.totalCredits,
                    typeDisplay
            });
        }

        // Dynamically size scrollpane to exact height of rows and header
        int headerHeight = 36;
        int rowHeight = 38;
        int rowCount = Math.max(courses.size(), 1);
        int totalHeight = headerHeight + (rowCount * rowHeight) + 2;

        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, totalHeight));
        scrollPane.setMinimumSize(new Dimension(0, totalHeight));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, totalHeight));

        // Auto-select first course record if data is present
        if (!courses.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
        }

        revalidate();
        repaint();
    }
}