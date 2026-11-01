package gui.admin.EnrollmentComponents;

import model.EnrollmentRequest;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * EnrollmentTable - Custom JTable component for displaying enrollment request records.
 */
public class EnrollmentTable extends JTable {

    private final Color WHITE        = Color.WHITE;
    private final Color SOFT_MINT    = new Color(0xF9, 0xFB, 0xF9);
    private final Color MINT         = new Color(0xE8, 0xF5, 0xE9);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color DARK_TEXT    = new Color(0x21, 0x25, 0x29);
    private final Color DANGER       = new Color(0xDC, 0x35, 0x45);
    private final Color WARNING      = new Color(0xD9, 0x77, 0x06); // Amber / Orange for Pending

    private final DefaultTableModel tableModel;
    private List<EnrollmentRequest> currentDisplayedRequests = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public EnrollmentTable() {
        String[] columnNames = {
                "Request ID", "Reg No", "Student Name", "Course", "Year / Semester", "Type", "Status", "Requested At"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table cells are read-only
            }
        };

        setModel(tableModel);
        setRowHeight(44);
        setFont(new Font("Segoe UI", Font.PLAIN, 15));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));

        // Header Styling
        JTableHeader header = getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(MINT);
        header.setForeground(DEEP_GREEN);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 46));
        header.setReorderingAllowed(false);

        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // Column Widths Allocation
        getColumnModel().getColumn(0).setPreferredWidth(110);
        getColumnModel().getColumn(1).setPreferredWidth(140);
        getColumnModel().getColumn(2).setPreferredWidth(220);
        getColumnModel().getColumn(3).setPreferredWidth(280);
        getColumnModel().getColumn(4).setPreferredWidth(170);
        getColumnModel().getColumn(5).setPreferredWidth(130);
        getColumnModel().getColumn(6).setPreferredWidth(130);
        getColumnModel().getColumn(7).setPreferredWidth(160);

        // Custom Cell Renderer
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                c.setFont(new Font("Segoe UI", Font.PLAIN, 15));

                if (isSelected) {
                    c.setBackground(PASTEL_GREEN);
                    c.setForeground(DEEP_GREEN);
                } else {
                    c.setBackground(row % 2 == 0 ? WHITE : SOFT_MINT);
                    c.setForeground(DARK_TEXT);
                }

                if (column == 6) { // Status Column
                    String status = String.valueOf(value);
                    setFont(new Font("Segoe UI", Font.BOLD, 15));
                    if ("Approved".equalsIgnoreCase(status)) {
                        setForeground(FOREST_GREEN);
                    } else if ("Declined".equalsIgnoreCase(status)) {
                        setForeground(DANGER);
                    } else {
                        setForeground(WARNING);
                    }
                }

                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });
    }

    public void setRequests(List<EnrollmentRequest> requests) {
        this.currentDisplayedRequests = requests;
        tableModel.setRowCount(0);

        for (EnrollmentRequest r : requests) {
            String reqIdStr = "REQ-" + r.getRequestId();
            String courseStr = (r.getCourseCode() != null ? r.getCourseCode() : "") +
                    (r.getCourseName() != null ? " - " + r.getCourseName() : "");
            String yrSemStr = r.getAcademicYear() + " / " + r.getSemester();
            String reqDateStr = r.getRequestedAt() != null ? dateFormat.format(r.getRequestedAt()) : "-";

            Object[] rowData = {
                    reqIdStr,
                    r.getRegistrationNo() != null ? r.getRegistrationNo() : "STU-" + r.getStudentId(),
                    r.getStudentName() != null ? r.getStudentName() : "Student #" + r.getStudentId(),
                    courseStr,
                    yrSemStr,
                    r.getEnrollmentType() != null ? r.getEnrollmentType() : "Normal",
                    r.getStatus() != null ? r.getStatus() : "Pending",
                    reqDateStr
            };

            tableModel.addRow(rowData);
        }
    }

    public EnrollmentRequest getSelectedRequest() {
        int selectedRow = getSelectedRow();
        if (selectedRow >= 0 && selectedRow < currentDisplayedRequests.size()) {
            return currentDisplayedRequests.get(selectedRow);
        }
        return null;
    }
}
