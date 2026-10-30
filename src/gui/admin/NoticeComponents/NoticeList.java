package gui.admin.NoticeComponents;

import gui.admin.AdminTheme;
import model.Notice;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NoticeList extends JPanel {
    private NoticeTable table;

    public NoticeList(NoticeTable table) {
        this.table = table != null ? table : new NoticeTable();
        setLayout(null);
        setBackground(AdminTheme.WHITE);

        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.setBounds(0, 0, 1620, 680);
        scrollPane.getViewport().setBackground(AdminTheme.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1));
        add(scrollPane);
    }

    public void setNotices(List<Notice> notices) { table.setNotices(notices); }
    public Notice getSelectedNotice() { return table.getSelectedNotice(); }
}
