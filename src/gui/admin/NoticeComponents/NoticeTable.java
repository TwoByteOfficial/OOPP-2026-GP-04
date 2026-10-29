package gui.admin.NoticeComponents;

import gui.admin.AdminTheme;
import model.Notice;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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

        setFont(AdminTheme.FONT_BODY);
        setRowHeight(44);
        setBackground(AdminTheme.WHITE);
        setForeground(new Color(0x21, 0x25, 0x29));
        setSelectionBackground(AdminTheme.PASTEL_GREEN);
        setSelectionForeground(AdminTheme.DEEP_GREEN);
        setShowGrid(true);
        setGridColor(new Color(0xE0, 0xE0, 0xE0));

        JTableHeader header = getTableHeader();
        header.setFont(AdminTheme.FONT_BODY_BOLD);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 46));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setBackground(AdminTheme.MINT);
                l.setForeground(AdminTheme.DEEP_GREEN);
                l.setFont(AdminTheme.FONT_BODY_BOLD);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 1, AdminTheme.BORDER));
                return l;
            }
        });

        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? AdminTheme.WHITE : new Color(0xF9, 0xFB, 0xF9));
                    c.setForeground(new Color(0x21, 0x25, 0x29));
                }
                if (column == 6) { // Status column
                    setFont(AdminTheme.FONT_BODY_BOLD);
                    if ("Published".equalsIgnoreCase(String.valueOf(value))) {
                        setForeground(AdminTheme.FOREST_GREEN);
                    } else if ("Draft".equalsIgnoreCase(String.valueOf(value))) {
                        setForeground(AdminTheme.WARNING);
                    } else {
                        setForeground(AdminTheme.DANGER);
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
