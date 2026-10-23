package gui.undergraduate.medicals.table;

import gui.admin.AdminTheme;
import gui.undergraduate.medicals.model.MedicalRecordData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * Medical history directory table utilizing the modular status badge renderer.
 */
public class MedicalHistoryPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final DefaultTableModel tableModel;

    public MedicalHistoryPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel cardTitle = new JLabel("Submitted Applications History");
        cardTitle.setFont(AdminTheme.font(Font.BOLD, 16));
        cardTitle.setForeground(AdminTheme.FOREST_GREEN);

        JLabel helperText = new JLabel("Live status from TO verification");
        helperText.setFont(AdminTheme.font(Font.PLAIN, 12));
        helperText.setForeground(AdminTheme.MUTED);

        titlePanel.add(cardTitle, BorderLayout.WEST);
        titlePanel.add(helperText, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Course", "Date", "Reason", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable historyTable = new JTable(tableModel);
        historyTable.setFont(AdminTheme.font(Font.PLAIN, 13));
        historyTable.setRowHeight(40);
        historyTable.setShowGrid(false);
        historyTable.setIntercellSpacing(new Dimension(0, 0));
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setSelectionBackground(AdminTheme.PASTEL_GREEN);
        historyTable.setSelectionForeground(AdminTheme.DEEP_GREEN);

        JTableHeader header = historyTable.getTableHeader();
        header.setFont(AdminTheme.font(Font.BOLD, 13));
        header.setBackground(AdminTheme.MINT);
        header.setForeground(AdminTheme.DEEP_GREEN);
        header.setPreferredSize(new Dimension(0, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD2, 0xD7, 0xE1)));

        // Column widths
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(75);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(85);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(185);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(75);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setBorder(new EmptyBorder(0, 8, 0, 8));
        for (int i = 0; i < 4; i++) {
            historyTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }

        // Use modular status badge renderer
        historyTable.getColumnModel().getColumn(4).setCellRenderer(new MedicalStatusBadgeRenderer());

        JScrollPane tableScroll = new JScrollPane(historyTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(0xEAEAEA)));
        tableScroll.getViewport().setBackground(AdminTheme.WHITE);
        tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(tableScroll, BorderLayout.CENTER);
    }

    public void setRecords(List<MedicalRecordData> records) {
        tableModel.setRowCount(0);
        for (MedicalRecordData r : records) {
            tableModel.addRow(new Object[]{
                    "#" + r.getMedicalId(),
                    r.getCourseCode(),
                    r.getMedicalDate(),
                    r.getReason(),
                    r.getApprovalStatus()
            });
        }
    }

    public void addRecord(MedicalRecordData r) {
        tableModel.insertRow(0, new Object[]{
                "#" + r.getMedicalId(),
                r.getCourseCode(),
                r.getMedicalDate(),
                r.getReason(),
                r.getApprovalStatus()
        });
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