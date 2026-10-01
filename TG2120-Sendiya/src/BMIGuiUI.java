import javax.swing.*;
import java.awt.*;

public class BMIGuiUI extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    //Inputs & Outputs fields
    private JTextField usHeight, usWeight, metricHeight, metricWeight;
    private JLabel bmiResult, categoryResult;

    public BMIGuiUI() {
        setTitle("BMI Calculator");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //SCREEN 1: Main Menu
        JPanel s1 = new JPanel(new GridLayout(3, 1, 10, 10));
        s1.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        JLabel title = new JLabel("BMI Calculator", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        JButton goUS = new JButton("US Customary Units (Pounds/Inches)");
        JButton goMetric = new JButton("Metric Units (KG/CM)");
        s1.add(title); s1.add(goUS); s1.add(goMetric);

        //SCREEN 2: US Customary Input
        JPanel s2 = new JPanel(new GridLayout(4, 2, 5, 15));
        s2.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        s2.add(new JLabel("Height (inches):")); usHeight = new JTextField(); s2.add(usHeight);
        s2.add(new JLabel("Weight (pounds):")); usWeight = new JTextField(); s2.add(usWeight);
        JButton back1 = new JButton("Back"); JButton calc1 = new JButton("Calculate");
        s2.add(back1); s2.add(calc1);

        //SCREEN 3: Metric Input
        JPanel s3 = new JPanel(new GridLayout(4, 2, 5, 15));
        s3.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        s3.add(new JLabel("Height (cm):")); metricHeight = new JTextField(); s3.add(metricHeight);
        s3.add(new JLabel("Weight (kg):")); metricWeight = new JTextField(); s3.add(metricWeight);
        JButton back2 = new JButton("Back"); JButton calc2 = new JButton("Calculate");
        s3.add(back2); s3.add(calc2);

        //SCREEN 4: Results
        JPanel s4 = new JPanel(new GridLayout(4, 1, 5, 5));
        s4.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bmiResult = new JLabel("BMI: --", SwingConstants.CENTER);
        bmiResult.setFont(new Font("Arial", Font.BOLD, 16));
        categoryResult = new JLabel("Category: --", SwingConstants.CENTER);
        categoryResult.setFont(new Font("Arial", Font.BOLD, 16));

        JTextArea table = new JTextArea("Underweight: <18.5\nNormal: 18.5-24.9\nOverweight: 25-29.9\nObese: >=30");
        table.setEditable(false); table.setBackground(new Color(240,240,240));
        JButton reset = new JButton("Start Again");
        s4.add(bmiResult); s4.add(categoryResult); s4.add(table); s4.add(reset);


        mainPanel.add(s1, "Screen1"); mainPanel.add(s2, "Screen2");
        mainPanel.add(s3, "Screen3"); mainPanel.add(s4, "Screen4");
        add(mainPanel);


        goUS.addActionListener(e -> cardLayout.show(mainPanel, "Screen2"));
        goMetric.addActionListener(e -> cardLayout.show(mainPanel, "Screen3"));
        back1.addActionListener(e -> cardLayout.show(mainPanel, "Screen1"));
        back2.addActionListener(e -> cardLayout.show(mainPanel, "Screen1"));
        reset.addActionListener(e -> {
            usHeight.setText(""); usWeight.setText(""); metricHeight.setText(""); metricWeight.setText("");
            cardLayout.show(mainPanel, "Screen1");
        });

        calc1.addActionListener(e -> runCalculation(usWeight.getText(), usHeight.getText(), 1));
        calc2.addActionListener(e -> runCalculation(metricWeight.getText(), metricHeight.getText(), 2));
    }

    private void runCalculation(String w, String h, int type) {
        try {
            BMICalculator cal = new BMICalculator(Double.parseDouble(w), Double.parseDouble(h), type);
            double bmi = cal.calculateBMI();
            bmiResult.setText(String.format("BMI: %.2f", bmi));
            categoryResult.setText("Category: " + cal.getCategory(bmi));
            cardLayout.show(mainPanel, "Screen4");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter numbers!");
        }
    }
}
