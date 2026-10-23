package gui.admin.SettingsComponents;

import javax.swing.*;
import java.awt.*;

public class AccountSettings extends JPanel {

    public AccountSettings(String username) {
        setLayout(new GridLayout(4, 2, 14, 12));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                        " Account Credentials & Security ",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 16),
                        new Color(0x1B, 0x5E, 0x20)
                ),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        addLabel("Username (Locked):");
        JTextField txtUser = new JTextField(username);
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUser.setEditable(false);
        txtUser.setBackground(new Color(0xE8, 0xF5, 0xE9));
        txtUser.setForeground(Color.BLACK);
        add(txtUser);

        addLabel("Current Password:");
        JPasswordField txtCurrent = createPasswordField();
        add(txtCurrent);

        addLabel("New Password:");
        JPasswordField txtNew = createPasswordField();
        add(txtNew);

        JButton btnChangePass = new JButton("Update Password");
        btnChangePass.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnChangePass.setBackground(new Color(0x38, 0x8E, 0x3C));
        btnChangePass.setForeground(Color.WHITE);
        btnChangePass.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Password update requested.", "Information", JOptionPane.INFORMATION_MESSAGE);
        });
        add(new JLabel());
        add(btnChangePass);
    }

    private void addLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(Color.BLACK);
        add(l);
    }

    private JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pf.setBackground(Color.WHITE);
        pf.setForeground(Color.BLACK);
        pf.setCaretColor(Color.BLACK);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return pf;
    }
}
