package gui.admin.NoticeComponents;

import gui.admin.AdminTheme;
import model.Notice;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NoticeList extends JPanel {

    private final NoticeTable table;

    public NoticeList() {
        this(new NoticeTable());
    }

    public NoticeList(NoticeTable table) {
        this.table = table != null ? table : new NoticeTable();
        setLayout(new BorderLayout());
        setBackground(AdminTheme.WHITE);

        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.getViewport().setBackground(AdminTheme.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(AdminTheme.BORDER, 1));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setNotices(List<Notice> notices) {
        table.setNotices(notices);
    }

    public Notice getSelectedNotice() {
        return table.getSelectedNotice();
    }

    public NoticeTable getTable() {
        return table;
    }
}
