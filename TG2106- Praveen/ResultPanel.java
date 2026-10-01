import javax.swing.*;
import java.awt.*;

public class ResultPanel extends JPanel {

    private final JLabel lblBmi = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblCond = new JLabel("", SwingConstants.CENTER);

    public ResultPanel(MainFrame frame) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new GridLayout(2, 1, 5, 5));
        lblBmi.setFont(new Font("Arial", Font.BOLD, 16));
        lblCond.setFont(new Font("Arial", Font.BOLD, 16));
        top.add(lblBmi);
        top.add(lblCond);

        JTextArea info = new JTextArea(
                "BMI VALUES\n" +
                        "Underweight: less than 18.5\n" +
                        "Normal: between 18.5 and 24.9\n" +
                        "Overweight: between 25 and 29.9\n" +
                        "Obese: 30 or greater\n"
        );
        info.setEditable(false);
        info.setBackground(getBackground());

        JButton btnHome = new JButton("Home");
        btnHome.addActionListener(e -> frame.showCard("HOME"));

        add(top, BorderLayout.NORTH);
        add(info, BorderLayout.CENTER);
        add(btnHome, BorderLayout.SOUTH);
    }

    public void setResult(double bmi, String condition) {
        lblBmi.setText(String.format("Your BMI is %.2f", bmi));
        lblCond.setText("You are " + condition + ".");

        // optional simple colors
        switch (condition) {
            case "Underweight" -> lblCond.setForeground(Color.BLUE);
            case "Normal" -> lblCond.setForeground(new Color(0, 140, 0));
            case "Overweight" -> lblCond.setForeground(Color.ORANGE);
            default -> lblCond.setForeground(Color.RED);
        }
    }
}