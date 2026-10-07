import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    static void main(String[] args) {
        JFrame nf = new JFrame("test gui 03");
        nf.setSize(270, 180);
        nf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nf.setLayout(null);
        nf.setTitle("TEST-GUI-03");

        JLabel lbl1 = new JLabel();
        lbl1.setText("Number 01:");
        lbl1.setBounds(20, 20, 70, 30);
        nf.add(lbl1);

        JTextField tf1 = new JTextField();
        tf1.setBounds(100, 20, 70, 30);
        nf.add(tf1);

        JLabel lbl2 = new JLabel();
        lbl2.setText("Number 02:");
        lbl2.setBounds(20, 60, 70, 30);
        nf.add(lbl2);

        JTextField tf2 = new JTextField();
        tf2.setBounds(100, 60, 70, 30);
        nf.add(tf2);

        JButton btn = new JButton();
        btn.setText("Divide");
        btn.setBounds(50, 100, 100, 30);
        nf.add(btn);

        JLabel lbl3 = new JLabel();
        lbl3.setText("Result");
        lbl3.setBounds(200, 20, 50, 30);
        nf.add(lbl3);

        JLabel lbl4 = new JLabel();
        //lbl4.setText("2.5");
        lbl4.setBounds(200, 60, 50, 30);
        nf.add(lbl4);


        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String strnum1 = tf1.getText().trim();
                String strnum2 = tf2.getText().trim();
                if (strnum1=="0" || strnum2=="0") {
                    error();
                } else {
                    try {
                        int num1 = Integer.parseInt(strnum1);
                        int num2 = Integer.parseInt(strnum2);
                        lbl4.setText((num1 / num2) + "");
                    } catch (NumberFormatException e) {
                        error();
                    }
                }
            }
        });

        nf.setVisible(true);
    }
    public static void error(){
        JFrame erf = new JFrame("test gui 03");
        erf.setSize(270, 180);
        erf.setLayout(null);
        erf.setTitle("Error");

        JLabel lbl5 = new JLabel();
        lbl5.setText("You Cannot Enter 0 for Number...!");
        lbl5.setBounds(30, 50, 250, 30);
        erf.add(lbl5);

        erf.setVisible(true);
    }
}