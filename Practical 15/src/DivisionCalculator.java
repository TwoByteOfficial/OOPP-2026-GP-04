import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DivisionCalculator {
    public static void main(String[] args) {
        JFrame  frame = new JFrame("Division Calculator");

        frame.setSize(400, 280);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        //Number 1
        JLabel number1Lable1 = new JLabel("Number 1:");
        number1Lable1.setBounds(50,40,100,30);
        frame.add(number1Lable1);


        JTextField number1TextField = new JTextField();
        number1TextField.setBounds(150,40,100,30);
        frame.add(number1TextField);

        //Number 2
        JLabel number2Labelable1 = new JLabel("Number 2:");
        number2Labelable1.setBounds(50,80,100,30);
        frame.add(number2Labelable1);

        JTextField number2TextField = new JTextField();
        number2TextField.setBounds(150,80,100,30);
        frame.add(number2TextField);

        //Divide button
        JButton divideButton = new JButton("Divide");
        divideButton.setBounds(120,130,100,30);
        frame.add(divideButton);

        //result
        JLabel resultLabel = new JLabel("Result:");
        resultLabel.setBounds(50,170,100,30);
        frame.add(resultLabel);

        //Answer will appear here
        JLabel resultValueLabel = new JLabel("");
        resultValueLabel.setBounds(150,170,100,30);
        frame.add(resultValueLabel);


        frame.setVisible(true);

    }
}
