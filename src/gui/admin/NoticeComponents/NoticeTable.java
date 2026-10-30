package gui.admin.NoticeComponents;

import model.Notice;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

public class NoticeTable extends JTable {

    private DefaultTableModel model;
    private List<Notice> noticeList = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public NoticeTable() {
        String[] columns = { "Notice ID", "Title", "Type", "Target Role", "Department", "Published Date", "Status" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        setModel(model);

        setFont(new Font("Segoe UI", Font.PLAIN, 15));
        setRowHeight(44);
        setBackground(Color.WHITE);
        setForeground(new Color(0x21, 0x25, 0x29));
        setSelectionBackground(new Color(0xC8, 0xE6, 0xC9));
        setSelectionForeground(new Color(0x1B, 0x5E, 0x20));
        setShowGrid(true);
        setGridColor(new Color(0xE0, 0xE0, 0xE0));

        JTableHeader header = getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 46));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setBackground(new Color(0xE8, 0xF5, 0xE9));
                l.setForeground(new Color(0x1B, 0x5E, 0x20));
                l.setFont(new Font("Segoe UI", Font.BOLD, 16));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(0xC8, 0xE6, 0xC9)));
                return l;
            }
        });

        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF9, 0xFB, 0xF9));
                    c.setForeground(new Color(0x21, 0x25, 0x29));
                }
                if (column == 6) { // Status column
                    setFont(new Font("Segoe UI", Font.BOLD, 15));
                    if ("Published".equalsIgnoreCase(String.valueOf(value))) {
                        setForeground(new Color(0x38, 0x8E, 0x3C));
                    } else if ("Draft".equalsIgnoreCase(String.valueOf(value))) {
                        setForeground(new Color(0xF0, 0xAD, 0x4E));
                    } else {
                        setForeground(new Color(0xDC, 0x35, 0x45));
                    }
                }
                return c;
            }
        });

        int[] widths = { 90, 360, 130, 150, 120, 170, 110 };
        for (int i = 0; i < widths.length && i < getColumnModel().getColumnCount(); i++) {
            getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    public void setNotices(List<Notice> notices) {
        this.noticeList = notices;
        model.setRowCount(0);
        for (Notice n : notices) {
            String pubDate = n.getPublishedAt() != null ? dateFormat.format(n.getPublishedAt()) : "N/A";
            String deptText = n.getDepartmentId() != null ? "Dept " + n.getDepartmentId() : "Faculty-Wide";

            model.addRow(new Object[]{
                    "NTC-" + n.getNoticeId(),
                    n.getTitle(),
                    n.getNoticeType() != null ? n.getNoticeType() : "General",
                    n.getTargetRole() != null ? n.getTargetRole() : "All Roles",
                    deptText,
                    pubDate,
                    n.getStatus() != null ? n.getStatus() : "Published"
            });
        }
    }

    public Notice getSelectedNotice() {
        int selectedRow = getSelectedRow();
        if (selectedRow >= 0 && selectedRow < noticeList.size()) {
            return noticeList.get(selectedRow);
        }
        return null;
    }
}
