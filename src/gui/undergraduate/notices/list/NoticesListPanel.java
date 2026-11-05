package gui.undergraduate.notices.list;

import gui.undergraduate.notices.model.NoticeData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Clean vertical card feed displaying notices with unified gaps.
 * Anchored to NORTH inside a scrollable viewport to prevent single cards from stretching vertically.
 * Dynamically binds and renders a list of NoticeCardItem components or an empty-state feedback placeholder.
 */
public class NoticesListPanel extends JPanel {

    // Inner vertically stacked container holding individual notice cards
    private final JPanel container;

    // Action callback delegated to notice cards when reading full details
    private final Consumer<NoticeData> onReadMore;

    /**
     * Constructs the scrollable notice list panel and anchors the feed layout to prevent vertical stretching.
     *
     * @param onReadMore Consumer callback handling inspection of a specific notice
     */
    public NoticesListPanel(Consumer<NoticeData> onReadMore) {
        this.onReadMore = onReadMore;
        setOpaque(false);
        setLayout(new BorderLayout());

        // -------------------------------------------------------------
        // Inner Vertical Card Feed Container
        // -------------------------------------------------------------
        container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(4, 2, 8, 6));

        // Wrapping container in a NORTH-anchored panel prevents vertical stretching when rows are few
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(container, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // Scroll Pane Configuration
        // -------------------------------------------------------------
        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Replaces the current card list with the provided collection of notices.
     * Renders a placeholder message if the dataset is empty or null.
     *
     * @param notices Collection of NoticeData instances to populate
     */
    public void setNotices(List<NoticeData> notices) {
        // Clear previous card views from the container
        container.removeAll();

        if (notices == null || notices.isEmpty()) {
            // Render friendly empty state message when no records match filter criteria
            JLabel emptyLbl = new JLabel("No notices found for this category.", SwingConstants.CENTER);
            emptyLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLbl.setBorder(new EmptyBorder(30, 0, 0, 0));
            emptyLbl.setForeground(new Color(0x75, 0x8A, 0x99));
            emptyLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            container.add(emptyLbl);
        } else {
            // Populate feed with NoticeCardItem instances separated by 12px vertical spacing
            for (NoticeData n : notices) {
                container.add(new NoticeCardItem(n, onReadMore));
                container.add(Box.createVerticalStrut(12));
            }
        }

        // Revalidate layout and repaint container surface
        container.revalidate();
        container.repaint();
    }
}