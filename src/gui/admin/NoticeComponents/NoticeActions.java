package gui.admin.NoticeComponents;

import javax.swing.*;
import java.awt.*;

public class NoticeActions extends JPanel {

    private AddNoticeButton btnAdd;
    private ViewNoticeButton btnView;
    private EditNoticeButton btnEdit;
    private DeleteNoticeButton btnDelete;
    private TogglePublishButton btnToggle;
    private RefreshNoticeButton btnRefresh;

    public NoticeActions(Runnable onAdd, Runnable onView, Runnable onEdit, Runnable onDelete, Runnable onToggle, Runnable onRefresh) {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        setBackground(Color.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));

        btnAdd = new AddNoticeButton(onAdd);
        btnView = new ViewNoticeButton(onView);
        btnEdit = new EditNoticeButton(onEdit);
        btnToggle = new TogglePublishButton(onToggle);
        btnDelete = new DeleteNoticeButton(onDelete);
        btnRefresh = new RefreshNoticeButton(onRefresh);

        add(btnAdd);
        add(btnView);
        add(btnEdit);
        add(btnToggle);
        add(btnDelete);
        add(btnRefresh);
    }
}
