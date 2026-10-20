package gui.admin.NoticeComponents;

import model.Notice;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NoticeList extends JPanel {

    private NoticeTable table;

    public NoticeList() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        table = new NoticeTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setNotices(List<Notice> notices) { table.setNotices(notices); }
    public Notice getSelectedNotice() { return table.getSelectedNotice(); }
    public NoticeTable getTable() { return table; }
}
