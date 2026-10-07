import javax.swing.*;
import java.awt.*;

public class Cal {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Calculator");

        frame.setSize(550, 350);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        Font font = new Font("Arial", Font.BOLD, 20);

        // Number 1
        JLabel number1 = new JLabel("Number 1:");
        number1.setFont(font);
        number1.setBounds(50, 60, 120, 40);

        JTextField number1Field = new JTextField();
        number1Field.setFont(font);
        number1Field.setBounds(200, 60, 200, 40);

        // Number 2
        JLabel number2 = new JLabel("Number 2:");
        number2.setFont(font);
        number2.setBounds(50, 120, 120, 40);

        JTextField number2Field = new JTextField();
        number2Field.setFont(font);
        number2Field.setBounds(200, 120, 200, 40);

        JLabel result = new JLabel("Result");
        result.setFont(font);
        result.setBounds(450, 60, 200, 40);

        JLabel showresult = new JLabel("");
        showresult.setFont(font);
        showresult.setBounds(450, 120, 200, 40);

        JButton button = new JButton("Divide");
        button.setFont(font);
        button.setBounds(150, 200, 200, 40);

        panel.add(number1);
        panel.add(number1Field);

        panel.add(number2);
        panel.add(number2Field);

        panel.add(result);
        panel.add(showresult);

        panel.add(button);

        frame.add(panel);

        button.addActionListener(e->{
            double num1 = Integer.parseInt(number1Field.getText());
            double num2 = Integer.parseInt(number2Field.getText());

            if (num2 == 0) {
                showresult.setText("Cannot divide by zero");
            } else {
                double r = num1 / num2;
                showresult.setText("" + r);
            }
        });

        frame.setVisible(true);
    }
}