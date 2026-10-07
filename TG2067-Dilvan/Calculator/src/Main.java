import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    static void main(String[] args) {
        JFrame nf = new JFrame();
        nf.setSize(400,650);
        nf.setTitle("TEST-GUI-04");
        nf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nf.setLayout(null);

        JLabel lbl = new JLabel();
        lbl.setText("Calculator");
        lbl.setBounds(110, 10, 250, 50);
        lbl.setFont(new Font("Arial", Font.BOLD, 35));
        nf.add(lbl);

        JTextField tf = new JTextField();
        tf.setBounds(30, 80, 340, 60);
        tf.setFont(new Font("Arial", Font.CENTER_BASELINE, 40));
        tf.setHorizontalAlignment(JTextField.RIGHT);
        nf.add(tf);

        //=========================================================

        JButton btn1 = new JButton("<x");
        btn1.setBounds(30, 150, 80, 80);
        btn1.setText("<x");
        btn1.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn1);

        btn1.addActionListener(actionEvent -> {
            String text = tf.getText();
            if (!text.isEmpty()) {
                tf.setText(text.substring(0, text.length() - 1));
            }
        });

        JButton btn2 = new JButton();
        btn2.setBounds(117, 150, 80, 80);
        btn2.setText("AC");
        btn2.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn2);

        btn2.addActionListener(actionEvent -> {
            tf.setText(null);
        });

        JButton btn3 = new JButton("%");
        btn3.setBounds(204, 150, 80, 80);
        btn3.setText("%");
        btn3.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn3);

        btn3.addActionListener(actionEvent -> {
            String btn03 = btn3.getText();
            String set03 = tf.getText();
            tf.setText(set03+""+btn03+"");
        });

        JButton btn4 = new JButton("/");
        btn4.setBounds(290, 150, 80, 80);
        btn4.setText("/");
        btn4.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn4);

        btn4.addActionListener(actionEvent -> {
            String btn04 = btn4.getText();
            String set04 = tf.getText();
            tf.setText(set04+btn04+"");
        });

        //=========================================================

        JButton btn5 = new JButton("7");
        btn5.setBounds(30, 237, 80, 80);
        btn5.setText("7");
        btn5.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn5);

        btn5.addActionListener(actionEvent -> {
            String btn05 = btn5.getText();
            String set05 = tf.getText();
            tf.setText(set05+btn05+"");
        });

        JButton btn6 = new JButton("8");
        btn6.setBounds(117, 237, 80, 80);
        btn6.setText("8");
        btn6.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn6);

        btn6.addActionListener(actionEvent -> {
            String btn06 = btn6.getText();
            String set06 = tf.getText();
            tf.setText(set06+btn06+"");
        });

        JButton btn7 = new JButton("9");
        btn7.setBounds(204, 237, 80, 80);
        btn7.setText("9");
        btn7.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn7);

        btn7.addActionListener(actionEvent -> {
            String btn07 = btn7.getText();
            String set07 = tf.getText();
            tf.setText(set07+btn07+"");
        });

        JButton btn8 = new JButton("*");
        btn8.setBounds(290, 237, 80, 80);
        btn8.setText("*");
        btn8.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn8);

        btn8.addActionListener(actionEvent -> {
            String btn08 = btn8.getText();
            String set08 = tf.getText();
            tf.setText(set08+""+btn08+"");
        });

        //=======================================================

        JButton btn9 = new JButton("4");
        btn9.setBounds(30, 324, 80, 80);
        btn9.setText("4");
        btn9.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn9);

        btn9.addActionListener(actionEvent -> {
            String btn09 = btn9.getText();
            String set09 = tf.getText();
            tf.setText(set09+""+btn09+"");
        });

        JButton btn10 = new JButton("5");
        btn10.setBounds(117, 324, 80, 80);
        btn10.setText("5");
        btn10.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn10);

        btn10.addActionListener(actionEvent -> {
            String btn010 = btn10.getText();
            String set010 = tf.getText();
            tf.setText(set010+""+btn010+"");
        });

        JButton btn11 = new JButton("6");
        btn11.setBounds(204, 324, 80, 80);
        btn11.setText("6");
        btn11.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn11);

        btn11.addActionListener(actionEvent -> {
            String btn011 = btn11.getText();
            String set011 = tf.getText();
            tf.setText(set011+""+btn011+"");
        });

        JButton btn12 = new JButton();
        btn12.setBounds(290, 324, 80, 80);
        btn12.setText("-");
        btn12.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn12);

        btn12.addActionListener(actionEvent -> {
            String btn012 = btn12.getText();
            String set012 = tf.getText();
            tf.setText(set012+""+btn012+"");
        });

        //========================================================

        JButton btn13 = new JButton();
        btn13.setBounds(30, 410, 80, 80);
        btn13.setText("1");
        btn13.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn13);

        btn13.addActionListener(actionEvent -> {
            String btn013 = btn13.getText();
            String set013 = tf.getText();
            tf.setText(set013+""+btn013+"");
        });

        JButton btn14 = new JButton();
        btn14.setBounds(117, 410, 80, 80);
        btn14.setText("2");
        btn14.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn14);

        btn14.addActionListener(actionEvent -> {
            String btn014 = btn14.getText();
            String set014 = tf.getText();
            tf.setText(set014+""+btn014+"");
        });

        JButton btn15 = new JButton();
        btn15.setBounds(204, 410, 80, 80);
        btn15.setText("3");
        btn15.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn15);

        btn15.addActionListener(actionEvent -> {
            String btn015 = btn15.getText();
            String set015 = tf.getText();
            tf.setText(set015+""+btn015+"");
        });

        JButton btn16 = new JButton();
        btn16.setBounds(290, 410, 80, 80);
        btn16.setText("+");
        btn16.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn16);

        btn16.addActionListener(actionEvent -> {
            String btn016 = btn16.getText();
            String set016 = tf.getText();
            tf.setText(set016+""+btn016+"");
        });

        //========================================================

        JButton btn17 = new JButton();
        btn17.setBounds(30, 497, 80, 80);
        btn17.setText("+/-");
        btn17.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn17);

        btn17.addActionListener(actionEvent -> {
            String btn017 = btn17.getText();
            String set017 = tf.getText();
            tf.setText(set017+""+btn017+"");
        });

        JButton btn18 = new JButton();
        btn18.setBounds(117, 497, 80, 80);
        btn18.setText("0");
        btn18.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn18);

        btn18.addActionListener(actionEvent -> {
            String btn018 = btn18.getText();
            String set018 = tf.getText();
            tf.setText(set018+""+btn018+"");
        });

        JButton btn19 = new JButton();
        btn19.setBounds(204, 497, 80, 80);
        btn19.setText(".");
        btn19.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn19);

        btn19.addActionListener(actionEvent -> {
            String btn019 = btn19.getText();
            String set019 = tf.getText();
            tf.setText(set019+""+btn019+"");
        });

        JButton btn20 = new JButton("=");
        btn20.setBounds(290, 497, 80, 80);
        //btn20.setText("=");
        btn20.setFont(new Font("Arial", Font.BOLD, 30));
        nf.add(btn20);

        btn20.addActionListener(actionEvent -> {
            String expression = tf.getText();
            try {
                double result = evaluate(expression);
                tf.setText(String.valueOf(result));
            } catch (Exception e) {
                tf.setText("Error");
            }
        });
        nf.setVisible(true);
    }

    public static double evaluate(String expression) {

        expression = expression.replaceAll("\\s+", "");

        double result = 0;
        double currentNumber = 0;
        char operation = '+';
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                StringBuilder number = new StringBuilder();
                while (i < expression.length()
                        && (Character.isDigit(expression.charAt(i))
                        || expression.charAt(i) == '.')) {
                    number.append(expression.charAt(i));
                    i++;
                }
                currentNumber = Double.parseDouble(number.toString());
                i--;
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                switch (operation) {
                    case '+':
                        result += currentNumber;
                        break;

                    case '-':
                        result -= currentNumber;
                        break;

                    case '*':
                        result *= currentNumber;
                        break;

                    case '/':
                        if (currentNumber == 0) {
                            throw new ArithmeticException("Division by zero");
                        }
                        result /= currentNumber;
                        break;
                }

                operation = c;
                currentNumber = 0;
            }
        }

        // Apply the final number
        switch (operation) {

            case '+':
                result += currentNumber;
                break;

            case '-':
                result -= currentNumber;
                break;

            case '*':
                result *= currentNumber;
                break;

            case '/':
                if (currentNumber == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                result /= currentNumber;
                break;
        }

        return result;
    }
}