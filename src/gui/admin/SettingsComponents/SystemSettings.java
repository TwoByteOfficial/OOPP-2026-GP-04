package gui.admin.SettingsComponents;

import javax.swing.*;
import java.awt.*;

public class SystemSettings extends JPanel {

    public SystemSettings() {
        setLayout(new GridLayout(3, 2, 14, 12));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                        " System Information & DB Connection ",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 16),
                        new Color(0x1B, 0x5E, 0x20)
                ),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        addLabel("System Version:");
        JLabel v = new JLabel("UniMis Faculty MIS v2.4 (Java Swing)");
        v.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        v.setForeground(Color.BLACK);
        add(v);

        addLabel("Database Engine:");
        JLabel db = new JLabel("MySQL Relational Schema (util.DBConnection)");
        db.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        db.setForeground(new Color(0x1B, 0x5E, 0x20));
        add(db);

        addLabel("Connection Status:");
        JLabel st = new JLabel("● Live Connection Active");
        st.setFont(new Font("Segoe UI", Font.BOLD, 15));
        st.setForeground(new Color(0x38, 0x8E, 0x3C));
        add(st);
    }

    private void addLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(Color.BLACK);
        add(l);
    }
}
