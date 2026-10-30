package gui.undergraduate.grades.table;

import gui.admin.AdminTheme;
import gui.undergraduate.grades.model.CourseGradeData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Academic transcript directory table displaying module grades, credit weights, and GPV scores.
 */
public class GradesDirectoryPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JTable gradesTable;
    private final DefaultTableModel tableModel;

    public GradesDirectoryPanel(Consumer<String> onCourseSelected) {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel cardTitle = new JLabel("Semester Course Grade Directory");
        cardTitle.setFont(AdminTheme.font(Font.BOLD, 16));
        cardTitle.setForeground(AdminTheme.FOREST_GREEN);

        JLabel helperText = new JLabel("Select a module to inspect GPA impact");
        helperText.setFont(AdminTheme.font(Font.PLAIN, 12));
        helperText.setForeground(AdminTheme.MUTED);

        titlePanel.add(cardTitle, BorderLayout.WEST);
        titlePanel.add(helperText, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"Code", "Course Name", "Credits", "Grade", "GPV", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        gradesTable = new JTable(tableModel);
        gradesTable.setFont(AdminTheme.font(Font.PLAIN, 13));
        gradesTable.setRowHeight(40);
        gradesTable.setShowGrid(false);
        gradesTable.setIntercellSpacing(new Dimension(0, 0));
        gradesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gradesTable.setSelectionBackground(AdminTheme.PASTEL_GREEN);
        gradesTable.setSelectionForeground(AdminTheme.DEEP_GREEN);

        JTableHeader header = gradesTable.getTableHeader();
        header.setFont(AdminTheme.font(Font.BOLD, 13));
        header.setBackground(AdminTheme.MINT);
        header.setForeground(AdminTheme.DEEP_GREEN);
        header.setPreferredSize(new Dimension(0, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD2, 0xD7, 0xE1)));

        // Column widths
        // Column widths balanced for 6 columns
        gradesTable.getColumnModel().getColumn(0).setPreferredWidth(65);  // Code
        gradesTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Course Name
        gradesTable.getColumnModel().getColumn(2).setPreferredWidth(48);  // Credits
        gradesTable.getColumnModel().getColumn(3).setPreferredWidth(44);  // Grade
        gradesTable.getColumnModel().getColumn(4).setPreferredWidth(44);  // GPV
        gradesTable.getColumnModel().getColumn(5).setPreferredWidth(70);  // Status

        // Compact cell padding to prevent text truncation
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setBorder(new EmptyBorder(0, 5, 0, 5));
        for (int i = 0; i < 5; i++) {
            gradesTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }

        // Status badge renderer
        gradesTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(AdminTheme.font(Font.BOLD, 11));
                lbl.setBorder(new EmptyBorder(2, 6, 2, 6));

                String status = String.valueOf(value);
                if ("Completed".equalsIgnoreCase(status)) {
                    lbl.setForeground(AdminTheme.FOREST_GREEN);
                } else if ("Pending".equalsIgnoreCase(status)) {
                    lbl.setForeground(AdminTheme.WARNING);
                } else {
                    lbl.setForeground(AdminTheme.DANGER);
                }
                return lbl;
            }
        });

        gradesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = gradesTable.getSelectedRow();
                if (selectedRow != -1 && onCourseSelected != null) {
                    onCourseSelected.accept((String) gradesTable.getValueAt(selectedRow, 0));
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(gradesTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(0xEAEAEA)));
        tableScroll.getViewport().setBackground(AdminTheme.WHITE);
        tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(tableScroll, BorderLayout.CENTER);
    }

    public void setGradesData(List<CourseGradeData> dataList) {
        tableModel.setRowCount(0);
        for (CourseGradeData d : dataList) {
            tableModel.addRow(new Object[]{
                    d.getCourseCode(),
                    d.getCourseName(),
                    d.getCredits(),
                    d.getGrade(),
                    String.format("%.2f", d.getGpv()),
                    d.getStatus()
            });
        }
    }

    public void selectFirstRow() {
        if (gradesTable.getRowCount() > 0) {
            gradesTable.setRowSelectionInterval(0, 0);
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