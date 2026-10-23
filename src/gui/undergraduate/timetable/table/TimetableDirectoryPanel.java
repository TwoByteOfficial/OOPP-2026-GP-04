package gui.undergraduate.timetable.table;

import gui.admin.AdminTheme;
import gui.undergraduate.timetable.model.TimetableEntryData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Weekly lecture schedule table with interactive row inspection and column alignments.
 */
public class TimetableDirectoryPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JTable scheduleTable;
    private final DefaultTableModel tableModel;

    public TimetableDirectoryPanel(Consumer<Integer> onRowSelected) {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel cardTitle = new JLabel("Class Schedule Directory");
        cardTitle.setFont(AdminTheme.font(Font.BOLD, 16));
        cardTitle.setForeground(AdminTheme.FOREST_GREEN);

        JLabel helperText = new JLabel("Select a session to view hall & instructor details");
        helperText.setFont(AdminTheme.font(Font.PLAIN, 12));
        helperText.setForeground(AdminTheme.MUTED);

        titlePanel.add(cardTitle, BorderLayout.WEST);
        titlePanel.add(helperText, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"Day", "Time", "Code", "Course Name", "Type", "Venue"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scheduleTable = new JTable(tableModel);
        scheduleTable.setFont(AdminTheme.font(Font.PLAIN, 12));
        scheduleTable.setRowHeight(32); // Rows 10 without scrollbar
        scheduleTable.setShowGrid(false);
        scheduleTable.setIntercellSpacing(new Dimension(0, 0));
        scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleTable.setSelectionBackground(AdminTheme.PASTEL_GREEN);
        scheduleTable.setSelectionForeground(AdminTheme.DEEP_GREEN);

        JTableHeader header = scheduleTable.getTableHeader();
        header.setFont(AdminTheme.font(Font.BOLD, 12));
        header.setBackground(AdminTheme.MINT);
        header.setForeground(AdminTheme.DEEP_GREEN);
        header.setPreferredSize(new Dimension(0, 32));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD2, 0xD7, 0xE1)));

        // Column widths for the expanded table area
        scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(85);  // Day
        scheduleTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Time
        scheduleTable.getColumnModel().getColumn(2).setPreferredWidth(65);  // Code
        scheduleTable.getColumnModel().getColumn(3).setPreferredWidth(210); // Course Name
        scheduleTable.getColumnModel().getColumn(4).setPreferredWidth(65);  // Type
        scheduleTable.getColumnModel().getColumn(5).setPreferredWidth(125); // Venue

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setBorder(new EmptyBorder(0, 6, 0, 6));
        for (int i = 0; i < 6; i++) {
            if (i != 4) {
                scheduleTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            }
        }

        // Dedicated badge renderer for Type column
        scheduleTable.getColumnModel().getColumn(4).setCellRenderer(new TimetableBadgeRenderer());

        scheduleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = scheduleTable.getSelectedRow();
                if (row != -1 && onRowSelected != null) {
                    onRowSelected.accept(row);
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(scheduleTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(0xEAEAEA)));
        tableScroll.getViewport().setBackground(AdminTheme.WHITE);
        tableScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(tableScroll, BorderLayout.CENTER);
    }

    public void setEntries(List<TimetableEntryData> entries) {
        tableModel.setRowCount(0);
        for (TimetableEntryData d : entries) {
            tableModel.addRow(new Object[]{
                    d.getDayOfWeek(),
                    d.getTimeSlot(),
                    d.getCourseCode(),
                    d.getCourseName(),
                    d.getComponent(),
                    d.getVenue()
            });
        }
    }

    public void selectFirstRow() {
        if (scheduleTable.getRowCount() > 0) {
            scheduleTable.setRowSelectionInterval(0, 0);
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