package gui.undergraduate.notices.list;

import gui.undergraduate.notices.model.NoticeData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Clean vertical card feed displaying notices with unified gaps.
 */
public class NoticesListPanel extends JPanel {

    private final JPanel container;
    private final Consumer<NoticeData> onReadMore;

    public NoticesListPanel(Consumer<NoticeData> onReadMore) {
        this.onReadMore = onReadMore;
        setOpaque(false);
        setLayout(new BorderLayout());

        container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(4, 2, 8, 6));

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setNotices(List<NoticeData> notices) {
        container.removeAll();
        if (notices.isEmpty()) {
            JLabel emptyLbl = new JLabel("No notices found for this category.", SwingConstants.CENTER);
            emptyLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLbl.setBorder(new EmptyBorder(30, 0, 0, 0));
            container.add(emptyLbl);
        } else {
            for (NoticeData n : notices) {
                container.add(new NoticeCardItem(n, onReadMore));
                container.add(Box.createVerticalStrut(12));
            }
        }
        container.revalidate();
        container.repaint();
    }
}