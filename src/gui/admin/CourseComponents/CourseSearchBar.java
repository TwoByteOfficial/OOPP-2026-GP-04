package gui.admin.CourseComponents;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class CourseSearchBar extends JPanel {
    private JTextField txtSearch;

    public CourseSearchBar(Consumer<String> onSearch) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setOpaque(false);

        JLabel lbl = new JLabel("Search Course:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.BLACK);
        add(lbl);

        txtSearch = new JTextField(18);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtSearch.setBackground(Color.WHITE);
        txtSearch.setForeground(Color.BLACK);
        txtSearch.setCaretColor(Color.BLACK);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        txtSearch.addActionListener(e -> onSearch.accept(txtSearch.getText().trim()));
        add(txtSearch);
    }

    public String getSearchQuery() { return txtSearch.getText().trim(); }
    public void reset() { txtSearch.setText(""); }
}
