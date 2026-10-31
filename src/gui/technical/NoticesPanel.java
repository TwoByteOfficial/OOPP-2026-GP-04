
package gui.technical;

import dao.TechnicalOfficer.NoticeDAO;
import gui.theme.UniMisTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoticesPanel extends JPanel {

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final List<Object[]> notices;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JComboBox<String> roleFilter;
    private final JComboBox<String> typeFilter;

    public NoticesPanel(long userId) {

        setLayout(new BorderLayout(0, 14));
        setBackground(UniMisTheme.LIGHT_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Heading
        JLabel heading = new JLabel("Notices");
        heading.setFont(UniMisTheme.FONT_H2);
        heading.setForeground(UniMisTheme.TEXT);

        // Filters
        roleFilter = new JComboBox<>(new String[]{
                "Any Target Role", "All", "Technical Officer"
        });


        typeFilter = new JComboBox<>(new String[]{
                "All Types", "General", "Academic", "Exam",
                "Attendance", "Event", "Emergency"
        });

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        filterPanel.setOpaque(false);

        filterPanel.add(new JLabel("Target Role:"));
        filterPanel.add(roleFilter);
        filterPanel.add(new JLabel("Notice Type:"));
        filterPanel.add(typeFilter);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.add(heading);
        topPanel.add(Box.createVerticalStrut(12));
        topPanel.add(filterPanel);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {
                "Title", "Target Role", "Notice Type", "Published At"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(UniMisTheme.FONT_BODY);
        table.setRowHeight(34);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setForeground(UniMisTheme.TEXT);
        table.setBackground(UniMisTheme.WHITE);
        table.setGridColor(UniMisTheme.BORDER);
        table.setSelectionBackground(UniMisTheme.PASTEL_GREEN);
        table.setSelectionForeground(UniMisTheme.TEXT);

        table.getTableHeader().setFont(UniMisTheme.FONT_BODY_BOLD);
        table.getTableHeader().setBackground(UniMisTheme.MINT);
        table.getTableHeader().setForeground(UniMisTheme.TEXT);
        table.getTableHeader().setReorderingAllowed(false);

        // Make the Title column wider
//        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.getColumnModel().getColumn(0).setPreferredWidth(380);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(UniMisTheme.BORDER));

        JButton viewButton = new JButton("View Notice");
        viewButton.addActionListener(e -> viewSelectedNotice());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(viewButton);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Load notices
        List<Object[]> loadedNotices;

        try {
            NoticeDAO dao = new NoticeDAO();
            loadedNotices = dao.getNoticesForTechnicalOfficer(userId);

            for (Object[] notice : loadedNotices) {
                tableModel.addRow(new Object[]{
                        notice[1],  // Title
                        notice[4],  // Target Role
                        notice[3],  // Notice Type
                        notice[5]   // Published At
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load notices.\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            loadedNotices = new ArrayList<>();
        }

        notices = loadedNotices;

        // Keep newest notices first
        sorter.setSortKeys(java.util.Collections.singletonList(
                new RowSorter.SortKey(3, SortOrder.DESCENDING)
        ));

        // Filter by target role
        roleFilter.addActionListener(e -> applyFilters());

        // Filter by notice type
        typeFilter.addActionListener(e -> applyFilters());

        // Double-click a notice to view its details
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2
                        && SwingUtilities.isLeftMouseButton(e)) {
                    viewSelectedNotice();
                }
            }
        });
    }

    private void applyFilters() {

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        String selectedRole =
                String.valueOf(roleFilter.getSelectedItem());

        String selectedType =
                String.valueOf(typeFilter.getSelectedItem());

        if (!selectedRole.equals("Any Target Role")) {
            filters.add(RowFilter.regexFilter(
                    "^" + java.util.regex.Pattern.quote(selectedRole) + "$",
                    1
            ));
        }

        if (!selectedType.equals("All Types")) {
            filters.add(RowFilter.regexFilter(
                    "^" + java.util.regex.Pattern.quote(selectedType) + "$",
                    2
            ));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void viewSelectedNotice() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a notice first.",
                    "No Notice Selected",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        Object[] notice = notices.get(modelRow);

        // Notice details window
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Notice Details",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setLayout(new BorderLayout(12, 12));

        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBorder(new EmptyBorder(20, 22, 20, 22));
        contentPanel.setBackground(UniMisTheme.WHITE);

        JLabel titleLabel = new JLabel(
                "<html><div style='width:480px;'>"
                        + escapeHtml(String.valueOf(notice[1]))
                        + "</div></html>"
        );
        titleLabel.setFont(UniMisTheme.FONT_H2);
        titleLabel.setForeground(UniMisTheme.TEXT);

        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // Notice information
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 12, 10));
        infoPanel.setOpaque(false);

        addDetail(infoPanel, "Target Role", notice[4]);
        addDetail(infoPanel, "Notice Type", notice[3]);
        addDetail(infoPanel, "Published At", notice[5]);
        addDetail(infoPanel, "Expires At",
                notice[6] == null ? "No expiry date" : notice[6]);
        addDetail(infoPanel, "Status", notice[7]);

        // Notice content
        JTextArea contentArea = new JTextArea(
                String.valueOf(notice[2]));
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(UniMisTheme.FONT_BODY);
        contentArea.setForeground(UniMisTheme.TEXT);
        contentArea.setBackground(UniMisTheme.WHITE);
        contentArea.setCaretPosition(0);
        contentArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBorder(
                BorderFactory.createTitledBorder("Notice Content"));

        JPanel detailsCenter = new JPanel(new BorderLayout(0, 16));
        detailsCenter.setOpaque(false);
        detailsCenter.add(infoPanel, BorderLayout.NORTH);
        detailsCenter.add(contentScrollPane, BorderLayout.CENTER);

        contentPanel.add(detailsCenter, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setSize(650, 550);
        dialog.setMinimumSize(new Dimension(550, 450));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addDetail(JPanel panel, String label, Object value) {

        JLabel nameLabel = new JLabel(label + ":");
        nameLabel.setFont(UniMisTheme.FONT_BODY_BOLD);
        nameLabel.setForeground(UniMisTheme.TEXT);

        JLabel valueLabel = new JLabel(
                value == null ? "—" : String.valueOf(value));
        valueLabel.setFont(UniMisTheme.FONT_BODY);
        valueLabel.setForeground(UniMisTheme.TEXT);

        panel.add(nameLabel);
        panel.add(valueLabel);
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
