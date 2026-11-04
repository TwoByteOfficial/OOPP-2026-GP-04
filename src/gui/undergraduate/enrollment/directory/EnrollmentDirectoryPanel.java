package gui.undergraduate.enrollment.directory;

import gui.admin.AdminTheme;
import gui.undergraduate.enrollment.model.EnrollmentCourseData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Left-side curriculum course catalog wrapped in an antialiased 14px rounded card.
 * Dynamically resizes to match the exact row count and provides ample column
 * width for full course title rendering without ellipsis.
 * Triggers inspection callbacks when a course record is selected by the user.
 */
public class EnrollmentDirectoryPanel extends JPanel {

    // Table, model, and scrolling container references
    private final JTable table;
    private final DefaultTableModel model;
    private final JScrollPane scrollPane;
    private final JPanel card;

    // Course dataset and selection event consumer
    private List<EnrollmentCourseData> courses = new ArrayList<>();
    private final Consumer<EnrollmentCourseData> onCourseSelected;

    public EnrollmentDirectoryPanel(Consumer<EnrollmentCourseData> onCourseSelected) {
        this.onCourseSelected = onCourseSelected;
        setLayout(new BorderLayout());
        setOpaque(false);

        // -------------------------------------------------------------
        // 14px Rounded Card Container with Anti-Aliased Border
        // -------------------------------------------------------------
        card = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw rounded white card background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Draw subtle card border line
                g2.setColor(new Color(225, 235, 228));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        // -------------------------------------------------------------
        // Header Caption Row (Catalog Title & Action Hint)
        // -------------------------------------------------------------
        JPanel tableTitleRow = new JPanel(new BorderLayout(15, 0));
        tableTitleRow.setOpaque(false);

        JLabel tableTitle = new JLabel("Curriculum Course Catalog");
        tableTitle.setFont(AdminTheme.font(Font.BOLD, 14));
        tableTitle.setForeground(AdminTheme.DEEP_GREEN);

        JLabel tableHint = new JLabel("Click any module to inspect criteria");
        tableHint.setFont(AdminTheme.font(Font.PLAIN, 12));
        tableHint.setForeground(new Color(110, 130, 120));

        tableTitleRow.add(tableTitle, BorderLayout.WEST);
        tableTitleRow.add(tableHint, BorderLayout.EAST);
        card.add(tableTitleRow, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // Table Model Setup & Non-Editable Cell Override
        // -------------------------------------------------------------
        String[] columns = {"Code", "Course Name", "Credits", "Type", "Status"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        // Configure table typography, row height, and selection styles
        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(AdminTheme.font(Font.PLAIN, 13));
        table.setForeground(new Color(30, 41, 59));
        table.setGridColor(new Color(230, 240, 235));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(220, 245, 230));
        table.setSelectionForeground(new Color(27, 67, 50));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Configure table header appearance and styling
        table.getTableHeader().setFont(AdminTheme.font(Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(225, 240, 230));
        table.getTableHeader().setForeground(AdminTheme.DEEP_GREEN);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 225, 210)));

        // Shared center alignment renderer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Column width optimization (providing generous width to Course Name)
        table.getColumnModel().getColumn(0).setPreferredWidth(75);
        table.getColumnModel().getColumn(1).setPreferredWidth(270);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setPreferredWidth(65);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // Custom status badge renderer with color-coded label text
        table.getColumnModel().getColumn(4).setPreferredWidth(85);
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, val, isSel, hasFocus, r, c);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setFont(AdminTheme.font(Font.BOLD, 12));
                String status = String.valueOf(val);

                if ("Enrolled".equals(status)) {
                    label.setForeground(AdminTheme.PRIMARY);
                } else if ("Requested".equals(status)) {
                    label.setForeground(new Color(217, 119, 6)); // Amber for requested
                } else {
                    label.setForeground(new Color(75, 125, 95));  // Muted green for available
                }
                return label;
            }
        });

        // Row selection listener triggering consumer callback with selected course object
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0 && row < courses.size() && onCourseSelected != null) {
                    onCourseSelected.accept(courses.get(row));
                }
            }
        });

        // Enclosing scroll pane configuration
        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 235, 225), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        card.add(scrollPane, BorderLayout.CENTER);

        // Anchored to NORTH so the card hugs table rows and ends right after the last row
        add(card, BorderLayout.NORTH);
    }

    /**
     * Binds course catalog records to the table model, computes dynamic container height,
     * and selects the first row by default.
     */
    public void setCourses(List<EnrollmentCourseData> list) {
        this.courses = list;
        model.setRowCount(0);
        for (EnrollmentCourseData c : list) {
            String status = "Available";
            if ("ENROLLED".equals(c.getEnrollmentStatus())) {
                status = "Enrolled";
            } else if ("PENDING".equals(c.getEnrollmentStatus())) {
                status = "Requested";
            }

            model.addRow(new Object[]{
                    c.getCourseCode(),
                    c.getCourseName(),
                    (int) c.getTotalCredits(),
                    c.isGpa() ? "Normal" : "NGPA",
                    status
            });
        }

        // Dynamically compute exact viewport height based on row count
        int headerHeight = 36;
        int rowHeight = 38;
        int rowCount = Math.max(list.size(), 1);
        int totalHeight = headerHeight + (rowCount * rowHeight) + 2;

        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, totalHeight));
        scrollPane.setMinimumSize(new Dimension(0, totalHeight));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, totalHeight));

        // Auto-select the first course row if catalog is not empty
        if (!list.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
        }

        revalidate();
        repaint();
    }
}