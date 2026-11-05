package gui.undergraduate.attendance.table;

import dao.undergraduate.UndergraduateAttendanceDAO.AttendanceRecordDTO;
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
 * Attendance directory table displaying course module attendance records.
 * Dynamically sizes viewport to row count to eliminate bottom dead white space.
 */
public class AttendanceDirectoryPanel extends JPanel {

    // Visual card styling constants
    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    // Table, data model, and scroll container components
    private final JTable table;
    private final DefaultTableModel model;
    private final JScrollPane scroll;

    /**
     * Initializes directory table and configures column alignments, widths, and row selection listener.
     */
    public AttendanceDirectoryPanel(Consumer<String> onSelectCourse) {
        // Transparent parent container with 10px vertical gap and padding
        setOpaque(false);
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(14, 16, 14, 16));

        // -------------------------------------------------------------
        // 1. Header Title Row (Panel Title & Subtitle)
        // -------------------------------------------------------------
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel titleLbl = new JLabel("Module Attendance Directory");
        titleLbl.setFont(AdminTheme.font(Font.BOLD, 15));
        titleLbl.setForeground(AdminTheme.DEEP_GREEN);

        JLabel subLbl = new JLabel("Select a module to view breakdown");
        subLbl.setFont(AdminTheme.font(Font.PLAIN, 12));
        subLbl.setForeground(AdminTheme.MUTED);

        topRow.add(titleLbl, BorderLayout.WEST);
        topRow.add(subLbl, BorderLayout.EAST);
        add(topRow, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Table Model & Properties Configuration
        // -------------------------------------------------------------
        String[] headers = {"Code", "Course Name", "Sessions", "Attendance", "Status"};
        model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Table cells are non-editable for read-only directory display
                return false;
            }
        };

        table = new JTable(model);
        table.setFont(AdminTheme.font(Font.PLAIN, 12));
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(AdminTheme.PASTEL_GREEN);
        table.setSelectionForeground(AdminTheme.DEEP_GREEN);
        table.setFocusable(false);

        // Header appearance styling
        JTableHeader th = table.getTableHeader();
        th.setFont(AdminTheme.font(Font.BOLD, 12));
        th.setBackground(AdminTheme.MINT);
        th.setForeground(AdminTheme.DEEP_GREEN);
        th.setPreferredSize(new Dimension(0, 34));
        th.setReorderingAllowed(false);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD2, 0xD7, 0xE1)));

        // -------------------------------------------------------------
        // 3. Column Width Allocations
        // -------------------------------------------------------------
        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(0).setMaxWidth(65);

        table.getColumnModel().getColumn(1).setPreferredWidth(235);

        table.getColumnModel().getColumn(2).setPreferredWidth(55);
        table.getColumnModel().getColumn(2).setMaxWidth(65);

        table.getColumnModel().getColumn(3).setPreferredWidth(82);

        table.getColumnModel().getColumn(4).setPreferredWidth(65);

        // -------------------------------------------------------------
        // 4. Custom Column Cell Renderers (Alignment & Color Coding)
        // -------------------------------------------------------------
        // Left alignment for Course Code & Course Name
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setBorder(new EmptyBorder(0, 8, 0, 8));
        table.getColumnModel().getColumn(0).setCellRenderer(left);
        table.getColumnModel().getColumn(1).setCellRenderer(left);

        // Center alignment for Session count and Attendance percentage
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);

        // Dynamic status badge renderer (Green for Eligible, Red for Not Eligible)
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, r, c);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(AdminTheme.font(Font.BOLD, 12));
                String text = String.valueOf(val);
                if ("Eligible".equalsIgnoreCase(text)) {
                    lbl.setForeground(new Color(0x2E, 0x7D, 0x32));
                } else {
                    lbl.setForeground(new Color(0xC6, 0x28, 0x28));
                }
                return lbl;
            }
        });

        // Row selection listener to notify parent view when a course is clicked
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row != -1 && onSelectCourse != null) {
                    onSelectCourse.accept((String) table.getValueAt(row, 0));
                }
            }
        });

        // -------------------------------------------------------------
        // 5. ScrollPane Setup & Height Adjustment
        // -------------------------------------------------------------
        scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xEAEAEA)));
        scroll.getViewport().setBackground(AdminTheme.WHITE);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        updateTableViewportHeight();
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Recalculates scroll pane viewport height to match the exact row count.
     */
    private void updateTableViewportHeight() {
        int rowCount = Math.max(model.getRowCount(), 1);
        int totalHeight = rowCount * table.getRowHeight();
        table.setPreferredScrollableViewportSize(new Dimension(table.getPreferredScrollableViewportSize().width, totalHeight));
    }

    /**
     * Populates table rows with student attendance records from DAO DTO list.
     */
    public void setAttendanceData(List<AttendanceRecordDTO> list) {
        model.setRowCount(0);
        if (list != null) {
            for (AttendanceRecordDTO c : list) {
                String sessionsStr = c.attendedSessions + "/" + c.totalSessions;
                String attStr = String.format("%.1f%%", c.adjustedPercentage);
                String status = c.isEligible() ? "Eligible" : "Not Eligible";
                model.addRow(new Object[]{c.courseCode, c.courseName, sessionsStr, attStr, status});
            }
        }
        updateTableViewportHeight();
        selectFirstRow();
        revalidate();
        repaint();
    }

    /**
     * Automatically selects the first row by default upon data load.
     */
    public void selectFirstRow() {
        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    /**
     * Computes exact compact height boundary based on header and row counts.
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        int rowCount = Math.max(model.getRowCount(), 1);
        int tableHeight = 34 + (rowCount * table.getRowHeight()); // 34px Header + Rows
        d.height = 14 + 22 + 10 + tableHeight + 14 + 2; // Exact tight boundary height
        return d;
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    /**
     * Paints card container rounded rectangle background and subtle border line.
     */
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