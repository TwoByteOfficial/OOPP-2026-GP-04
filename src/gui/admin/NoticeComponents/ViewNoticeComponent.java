package gui.admin.NoticeComponents;

import gui.admin.AdminTheme;
import model.Notice;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class ViewNoticeComponent extends JButton {

    private final Supplier<Notice> selectedNoticeSupplier;

    public ViewNoticeComponent(Supplier<Notice> selectedNoticeSupplier) {
        super("View Notice");
        this.selectedNoticeSupplier = selectedNoticeSupplier;

        setFont(AdminTheme.FONT_BODY_BOLD);
        setBackground(AdminTheme.PASTEL_GREEN);
        setForeground(AdminTheme.DEEP_GREEN);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> openViewNoticeWindow());
    }

    private void openViewNoticeWindow() {
        Notice selected = selectedNoticeSupplier.get();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an announcement from the table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        Frame owner = (parentWindow instanceof Frame) ? (Frame) parentWindow : null;
        ViewNoticeModal dialog = new ViewNoticeModal(owner, selected);
        dialog.setVisible(true);
    }

    // Modal Window Class embedded in the same component file
    public static class ViewNoticeModal extends JDialog {

        public ViewNoticeModal(Frame parent, Notice notice) {
            super(parent, "Official Announcement Details", true);
            setSize(650, 520);
            setLocationRelativeTo(parent);
            setResizable(false);
            getContentPane().setBackground(AdminTheme.WHITE);
            setLayout(new BorderLayout());

            // Header
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
            headerPanel.setBackground(AdminTheme.DEEP_GREEN);
            JLabel lblHeader = new JLabel("Notice: " + notice.getTitle());
            lblHeader.setFont(AdminTheme.FONT_H3);
            lblHeader.setForeground(AdminTheme.WHITE);
            headerPanel.add(lblHeader);
            add(headerPanel, BorderLayout.NORTH);

            // Body
            JPanel pnlContent = new JPanel();
            pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
            pnlContent.setBackground(AdminTheme.WHITE);
            pnlContent.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

            pnlContent.add(createMetaRow("Notice ID:", "NTC-" + notice.getNoticeId()));
            pnlContent.add(createMetaRow("Type:", notice.getNoticeType() != null ? notice.getNoticeType() : "General"));
            pnlContent.add(createMetaRow("Target Role:", notice.getTargetRole() != null ? notice.getTargetRole() : "All Roles"));
            pnlContent.add(createMetaRow("Published Date:", notice.getPublishedAt() != null ? notice.getPublishedAt().toString() : "N/A"));
            pnlContent.add(createMetaRow("Status:", notice.getStatus() != null ? notice.getStatus() : "Published"));
            pnlContent.add(Box.createVerticalStrut(12));

            JLabel lblBodyTitle = new JLabel("Announcement Body:");
            lblBodyTitle.setFont(AdminTheme.FONT_BODY_BOLD);
            lblBodyTitle.setForeground(AdminTheme.BLACK);
            pnlContent.add(lblBodyTitle);
            pnlContent.add(Box.createVerticalStrut(6));

            JTextArea txtBody = new JTextArea(notice.getContent() != null ? notice.getContent() : "");
            txtBody.setFont(AdminTheme.FONT_BODY);
            txtBody.setEditable(false);
            txtBody.setLineWrap(true);
            txtBody.setWrapStyleWord(true);
            txtBody.setBackground(new Color(0xF8, 0xF9, 0xFA));
            txtBody.setForeground(new Color(0x21, 0x25, 0x29));
            JScrollPane sp = new JScrollPane(txtBody);
            sp.setPreferredSize(new Dimension(580, 160));
            sp.setBorder(BorderFactory.createLineBorder(AdminTheme.BORDER, 1));
            pnlContent.add(sp);

            add(pnlContent, BorderLayout.CENTER);

            // Footer
            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
            footerPanel.setBackground(AdminTheme.WHITE);
            JButton btnClose = new JButton("Close");
            btnClose.setFont(AdminTheme.FONT_BODY_BOLD);
            btnClose.setBackground(AdminTheme.PRIMARY);
            btnClose.setForeground(AdminTheme.WHITE);
            btnClose.setFocusPainted(false);
            btnClose.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnClose.addActionListener(e -> dispose());
            footerPanel.add(btnClose);
            add(footerPanel, BorderLayout.SOUTH);
        }

        private JPanel createMetaRow(String label, String value) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
            p.setOpaque(false);
            JLabel l = new JLabel(label);
            l.setFont(AdminTheme.FONT_BODY_BOLD);
            l.setForeground(AdminTheme.BLACK);
            l.setPreferredSize(new Dimension(140, 22));
            p.add(l);

            JLabel v = new JLabel(value);
            v.setFont(AdminTheme.FONT_BODY);
            v.setForeground(AdminTheme.DEEP_GREEN);
            p.add(v);
            return p;
        }
    }
}
