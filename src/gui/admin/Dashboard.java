package gui.admin;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;

public class Dashboard extends JFrame {
    public Dashboard() {
        setTitle("Admin Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup();

        SwingUtilities.invokeLater(() -> {
            new Dashboard().setVisible(true);
        });
    }
}