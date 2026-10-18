package gui.login;

import javax.swing.*;
import java.awt.*;

/** Left side: logos + university / faculty name. */
public class LeftPanel extends JPanel {

    public LeftPanel() {
        setBackground(LoginTheme.PASTEL);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        LoginTheme.addAll(this,
                Box.createVerticalGlue(),
                LoginTheme.logoLabel(LoginTheme.UNI_LOGO,     "University Logo", 220, 220),
                LoginTheme.gap(25),
                LoginTheme.logoLabel(LoginTheme.FACULTY_LOGO, "Faculty Logo",    180, 180),
                LoginTheme.gap(30),
                LoginTheme.label("University Of Ruhuna",  LoginTheme.F_UNI, LoginTheme.DEEP,   LoginTheme.CENTER),
                LoginTheme.gap(6),
                LoginTheme.label("Faculty of Technology", LoginTheme.F_FAC, LoginTheme.FOREST, LoginTheme.CENTER),
                Box.createVerticalGlue());
    }
}