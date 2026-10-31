package gui.undergraduate.medicals.table;

import dao.undergraduate.UndergraduateMedicalDAO.MedicalRecordDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Table panel displaying medical history records submitted by the student.
 * Supports double-clicking to preview the cloud medical document URL.
 */
public class MedicalHistoryPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JTable table;
    private final DefaultTableModel model;
    private final List<MedicalRecordDTO> currentRecords = new ArrayList<>();

    public MedicalHistoryPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(16, 18, 16, 18));

        // Header Row
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel titleLbl = new JLabel("Submitted Applications History");
        titleLbl.setFont(AdminTheme.font(Font.BOLD, 15));
        titleLbl.setForeground(AdminTheme.FOREST_GREEN);

        JLabel subLbl = new JLabel("Double-click row to view document in browser");
        subLbl.setFont(AdminTheme.font(Font.PLAIN, 12));
        subLbl.setForeground(AdminTheme.MUTED);

        topRow.add(titleLbl, BorderLayout.WEST);
        topRow.add(subLbl, BorderLayout.EAST);
        add(topRow, BorderLayout.NORTH);

        String[] headers = {"ID", "Course", "Date", "Reason", "Status"};
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

        // Column width allocations
        table.getColumnModel().getColumn(0).setPreferredWidth(45);
        table.getColumnModel().getColumn(0).setMaxWidth(55);

        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setMaxWidth(90);

        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setMaxWidth(100);

        table.getColumnModel().getColumn(3).setPreferredWidth(170);

        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setMaxWidth(90);

        // Alignments: Center for ID, Course, Date, and Status
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);

        // Alignments: Left for Reason
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setBorder(new EmptyBorder(0, 8, 0, 8));
        table.getColumnModel().getColumn(3).setCellRenderer(left);

        // Status Column Custom Badge Renderer
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, r, c);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(AdminTheme.font(Font.BOLD, 12));
                String text = String.valueOf(val);
                if ("Approved".equalsIgnoreCase(text)) {
                    lbl.setForeground(new Color(0x2E, 0x7D, 0x32)); // Forest Green
                } else if ("Pending".equalsIgnoreCase(text)) {
                    lbl.setForeground(new Color(0xD3, 0x54, 0x00)); // Dark Orange
                } else {
                    lbl.setForeground(new Color(0xC6, 0x28, 0x28)); // Danger Red
                }
                return lbl;
            }
        });

        // Double-click listener to open Google Drive / Cloud URL in default web browser
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1 && row < currentRecords.size()) {
                        String url = currentRecords.get(row).documentUrl;
                        if (url != null && !url.trim().isEmpty()) {
                            try {
                                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                    Desktop.getDesktop().browse(new URI(url.trim()));
                                }
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(MedicalHistoryPanel.this,
                                        "Unable to open URL: " + url, "Invalid URL", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xEAEAEA)));
        scroll.getViewport().setBackground(AdminTheme.WHITE);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scroll, BorderLayout.CENTER);
    }

    public void setRecords(List<MedicalRecordDTO> list) {
        currentRecords.clear();
        model.setRowCount(0);
        if (list != null) {
            currentRecords.addAll(list);
            for (MedicalRecordDTO item : list) {
                model.addRow(new Object[]{
                        item.medicalId,
                        item.courseCode,
                        item.medicalDate,
                        item.reason,
                        item.approvalStatus
                });
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