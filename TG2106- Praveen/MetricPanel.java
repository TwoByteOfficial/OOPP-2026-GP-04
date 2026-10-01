import javax.swing.*;
import java.awt.*;

public class MetricPanel extends JPanel {

    private final JTextField txtWeight = new JTextField();
    private final JTextField txtHeight = new JTextField();

    public MetricPanel(MainFrame frame) {
        setLayout(new GridLayout(6, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel head = new JLabel("Input weight and height (Metric)", SwingConstants.CENTER);

        JButton btnClear = new JButton("Clear Values");
        JButton btnCalc = new JButton("Calculate");

        btnClear.addActionListener(e -> {
            txtWeight.setText("");
            txtHeight.setText("");
        });

        btnCalc.addActionListener(e -> {
            try {
                double w = Double.parseDouble(txtWeight.getText().trim());
                double h = Double.parseDouble(txtHeight.getText().trim());

                if (w <= 0 || h <= 0) {
                    JOptionPane.showMessageDialog(this, "Weight/Height must be > 0");
                    return;
                }

                double bmi = frame.getLogic().bmicalm(w, h);
                frame.showResult(bmi);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers");
            }
        });

        add(head); add(new JLabel(""));
        add(new JLabel("Weight in Kg:")); add(txtWeight);
        add(new JLabel("Height in meters:")); add(txtHeight);
        add(btnClear); add(new JLabel(""));
        add(btnCalc); add(new JLabel(""));
    }
}