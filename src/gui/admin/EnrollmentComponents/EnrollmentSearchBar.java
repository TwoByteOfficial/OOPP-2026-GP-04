package gui.admin.EnrollmentComponents;

import javax.swing.*;
import java.awt.*;

/**
 * EnrollmentSearchBar - Standalone component for searching enrollment requests.
 */
public class EnrollmentSearchBar extends JPanel {

    private final Color WHITE        = Color.WHITE;
    private final Color BLACK        = Color.BLACK;
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);

    private final JTextField txtSearch;

    public EnrollmentSearchBar(Runnable onSearch) {
        setLayout(new BorderLayout(8, 0));
        setOpaque(false);

        JLabel lblIcon = new JLabel("🔍");
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtSearch.setBackground(WHITE);
        txtSearch.setForeground(BLACK);
        txtSearch.setCaretColor(BLACK);
        txtSearch.setPreferredSize(new Dimension(280, 38));
        txtSearch.setToolTipText("Search student name, reg no, or course code...");
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        txtSearch.addActionListener(e -> {
            if (onSearch != null) onSearch.run();
        });

        add(lblIcon, BorderLayout.WEST);
        add(txtSearch, BorderLayout.CENTER);
    }

    public String getSearchQuery() {
        return txtSearch.getText().trim();
    }

    public void clear() {
        txtSearch.setText("");
    }
}
