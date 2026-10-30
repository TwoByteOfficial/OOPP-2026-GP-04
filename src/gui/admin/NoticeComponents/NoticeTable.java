package gui.admin.NoticeComponents;

import gui.admin.AdminTheme;
import model.Notice;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
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

        setFont(AdminTheme.FONT_SMALL);
        setRowHeight(44);
        setBackground(AdminTheme.WHITE);
        setSelectionBackground(AdminTheme.PASTEL_GREEN);
        setSelectionForeground(AdminTheme.DEEP_GREEN);
        setShowGrid(true);
        setGridColor(new Color(0xE0, 0xE0, 0xE0));
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
        int row = getSelectedRow();
        if (row >= 0 && row < noticeList.size()) return noticeList.get(row);
        return null;
    }
}
