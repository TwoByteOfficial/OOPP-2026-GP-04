package gui.undergraduate.grades.table;

import dao.undergraduate.UndergraduateGradesDAO.StudentCourseGradeDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;

/**
 * Grade records directory table displaying course units and semester grades.
 * Column widths are optimized to eliminate module title truncation.
 */
public class GradesDirectoryPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JTable table;
    private final DefaultTableModel model;

    public GradesDirectoryPanel(Consumer<String> onSelectCourse) {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(16, 18, 16, 18));

        // Header Title Row
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel titleLbl = new JLabel("Semester Course Grade Directory");
        titleLbl.setFont(AdminTheme.font(Font.BOLD, 15));
        titleLbl.setForeground(AdminTheme.FOREST_GREEN);

        JLabel subLbl = new JLabel("Select a module to inspect GPA impact");
        subLbl.setFont(AdminTheme.font(Font.PLAIN, 12));
        subLbl.setForeground(AdminTheme.MUTED);

        topRow.add(titleLbl, BorderLayout.WEST);
        topRow.add(subLbl, BorderLayout.EAST);
        add(topRow, BorderLayout.NORTH);

        String[] headers = {"Code", "Course Name", "Credits", "Grade", "GPV", "Status"};
        model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
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

        JTableHeader th = table.getTableHeader();
        th.setFont(AdminTheme.font(Font.BOLD, 12));
        th.setBackground(AdminTheme.MINT);
        th.setForeground(AdminTheme.DEEP_GREEN);
        th.setPreferredSize(new Dimension(0, 34));
        th.setReorderingAllowed(false);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD2, 0xD7, 0xE1)));

        // Column width allocations to prevent text clipping
        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(0).setMaxWidth(65);

        // Course Name expanded to 235px to display full title without ellipsis
        table.getColumnModel().getColumn(1).setPreferredWidth(235);

        table.getColumnModel().getColumn(2).setPreferredWidth(45);
        table.getColumnModel().getColumn(2).setMaxWidth(55);

        table.getColumnModel().getColumn(3).setPreferredWidth(45);
        table.getColumnModel().getColumn(3).setMaxWidth(55);

        table.getColumnModel().getColumn(4).setPreferredWidth(50);
        table.getColumnModel().getColumn(4).setMaxWidth(60);

        table.getColumnModel().getColumn(5).setPreferredWidth(65);

        // Alignments: Left for Code & Course Name
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setBorder(new EmptyBorder(0, 8, 0, 8));
        table.getColumnModel().getColumn(0).setCellRenderer(left);
        table.getColumnModel().getColumn(1).setCellRenderer(left);

        // Alignments: Center for Credits, Grade, GPV, and Status
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);

        // Custom status badge renderer
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, r, c);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(AdminTheme.font(Font.BOLD, 12));
                String text = String.valueOf(val);
                if ("Completed".equalsIgnoreCase(text)) {
                    lbl.setForeground(new Color(0x2E, 0x7D, 0x32));
                } else {
                    lbl.setForeground(new Color(0xC6, 0xC6, 0x28));
                }
                return lbl;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row != -1 && onSelectCourse != null) {
                    onSelectCourse.accept((String) table.getValueAt(row, 0));
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xEAEAEA)));
        scroll.getViewport().setBackground(AdminTheme.WHITE);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scroll, BorderLayout.CENTER);
    }

    public void updateData(List<?> list) {
        model.setRowCount(0);
        if (list != null) {
            for (Object item : list) {
                String code = extractString(item, "getCourseCode", "courseCode");
                String name = extractString(item, "getCourseName", "courseName");
                String cred = extractString(item, "getCredits", "credits");
                String grade = extractString(item, "getGrade", "grade");
                String rawGpv = extractString(item, "getGpv", "gpv");
                String status = extractString(item, "getStatus", "status");

                String formattedGpv = formatToTwoDecimals(rawGpv);
                if (status == null || status.isEmpty()) status = "Completed";

                model.addRow(new Object[]{code, name, cred, grade, formattedGpv, status});
            }
        }
        selectFirstRow();
    }

    public void setGradesData(List<StudentCourseGradeDTO> list) {
        updateData(list);
    }

    public void setData(List<StudentCourseGradeDTO> list) {
        updateData(list);
    }

    public void selectFirstRow() {
        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    private String formatToTwoDecimals(String val) {
        try {
            double d = Double.parseDouble(val);
            return String.format("%.2f", d);
        } catch (Exception e) {
            return val != null ? val : "0.00";
        }
    }

    private String extractString(Object obj, String getterName, String fieldName) {
        try {
            Method m = obj.getClass().getMethod(getterName);
            Object res = m.invoke(obj);
            return res != null ? String.valueOf(res) : "";
        } catch (Exception e) {
            try {
                Object res = obj.getClass().getField(fieldName).get(obj);
                return res != null ? String.valueOf(res) : "";
            } catch (Exception ex) {
                return "";
            }
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