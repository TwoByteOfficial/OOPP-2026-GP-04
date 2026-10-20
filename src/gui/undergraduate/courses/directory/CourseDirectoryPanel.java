package gui.undergraduate.courses.directory;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Registered modules list table supporting row selection callbacks.
 */
public class CourseDirectoryPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JTable coursesTable;

    public CourseDirectoryPanel(Consumer<String> onCourseSelected) {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel cardTitle = new JLabel("Registered Module Directory");
        cardTitle.setFont(AdminTheme.font(Font.BOLD, 16));
        cardTitle.setForeground(AdminTheme.FOREST_GREEN);

        JLabel helperText = new JLabel("Click any module to inspect criteria");
        helperText.setFont(AdminTheme.font(Font.PLAIN, 12));
        helperText.setForeground(AdminTheme.MUTED);

        titlePanel.add(cardTitle, BorderLayout.WEST);
        titlePanel.add(helperText, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"Code", "Course Name", "Credits", "Type"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableModel.addRow(new Object[]{"ICT2112", "Object Oriented Programming", "3", "Core"});
        tableModel.addRow(new Object[]{"ICT2123", "Database Management Systems", "3", "Core"});
        tableModel.addRow(new Object[]{"ICT2133", "Software Engineering Essentials", "3", "Core"});
        tableModel.addRow(new Object[]{"ICT2142", "Data Communication & Networks", "2", "Core"});
        tableModel.addRow(new Object[]{"ICT2152", "Operating Systems", "2", "Core"});
        tableModel.addRow(new Object[]{"ENG2113", "Technical Communication", "3", "Elective"});

        coursesTable = new JTable(tableModel);
        coursesTable.setFont(AdminTheme.font(Font.PLAIN, 13));
        coursesTable.setRowHeight(40);
        coursesTable.setShowGrid(false);
        coursesTable.setIntercellSpacing(new Dimension(0, 0));
        coursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        coursesTable.setSelectionBackground(AdminTheme.PASTEL_GREEN);
        coursesTable.setSelectionForeground(AdminTheme.DEEP_GREEN);

        JTableHeader header = coursesTable.getTableHeader();
        header.setFont(AdminTheme.font(Font.BOLD, 13));
        header.setBackground(AdminTheme.MINT);
        header.setForeground(AdminTheme.DEEP_GREEN);
        header.setPreferredSize(new Dimension(0, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD2, 0xD7, 0xE1)));

        coursesTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        coursesTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        coursesTable.getColumnModel().getColumn(2).setPreferredWidth(55);
        coursesTable.getColumnModel().getColumn(3).setPreferredWidth(75);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setBorder(new EmptyBorder(0, 12, 0, 12));
        for (int i = 0; i < coursesTable.getColumnCount(); i++) {
            coursesTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }

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