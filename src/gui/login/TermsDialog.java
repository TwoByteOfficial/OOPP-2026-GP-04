package gui.login;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Terms & Conditions window. */
public final class TermsDialog {

    private TermsDialog() {}

    private static final String TERMS_TEXT = String.join("\n\n",
            "UniMis - Terms and Conditions",
            "1. Acceptance of Terms\n"
                    + "By logging in to UniMis you agree to abide by the rules and regulations "
                    + "of the University of Ruhuna and the Faculty of Technology.",
            "2. Authorised Use\n"
                    + "This system is provided only for registered students and staff of the faculty. "
                    + "Credentials are personal and must not be shared.",
            "3. Data Privacy\n"
                    + "All academic and personal data is treated as confidential. Access is logged.",
            "4. Misuse\n"
                    + "Unauthorised access, alteration of records, or disruption of service "
                    + "will result in disciplinary action.",
            "5. Changes\n"
                    + "The faculty reserves the right to modify these terms at any time.");

    public static void show(JFrame owner) {
        JFrame frame = new JFrame("Terms and Conditions");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        JTextArea area = new JTextArea(TERMS_TEXT);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(LoginTheme.F_TERMS);
        area.setForeground(LoginTheme.DEEP);
        area.setBackground(LoginTheme.MINT);
        area.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(LoginTheme.FRESH, 1));

        JButton close = LoginTheme.button("Close", LoginTheme.F_LABEL);
        close.addActionListener(e -> frame.dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        bottom.setBackground(LoginTheme.PASTEL);
        bottom.add(close);

        frame.add(scroll, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);
        frame.setSize(640, 520);
        frame.setLocationRelativeTo(owner);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }
}