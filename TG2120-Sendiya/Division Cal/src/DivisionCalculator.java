import javax.swing.*;
import java.awt.*;

public class DivisionCalculator extends JFrame {

    private JTextField txtNumber1;
    private JTextField txtNumber2;
    private JLabel lblResult;

    public DivisionCalculator() {

        setTitle("Division Calculator");
        setSize(400, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        JLabel lblNumber1 = new JLabel("Number 1 :");
        JLabel lblNumber2 = new JLabel("Number 2 :");
        JLabel lblResultTitle = new JLabel("Result");

        txtNumber1 = new JTextField(10);
        txtNumber2 = new JTextField(10);

        lblResult = new JLabel(" ");

        JButton btnDivide = new JButton("Divide");

        // Create layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Number 1 label
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblNumber1, gbc);

        // Number 1 text field
        gbc.gridx = 1;
        add(txtNumber1, gbc);

        // Result title
        gbc.gridx = 2;
        add(lblResultTitle, gbc);

        // Number 2 label
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lblNumber2, gbc);

        // Number 2 text field
        gbc.gridx = 1;
        add(txtNumber2, gbc);

        // Result label
        gbc.gridx = 2;
        add(lblResult, gbc);

        // Divide button
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnDivide, gbc);

        // Button action
        btnDivide.addActionListener(e -> divideNumbers());

        setVisible(true);
    }

    private void divideNumbers() {

        // Clear previous result
        lblResult.setText(" ");

        try {
            double number1 =
                    Double.parseDouble(txtNumber1.getText().trim());

            double number2 =
                    Double.parseDouble(txtNumber2.getText().trim());

            // Check division by zero
            if (number2 == 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "You can't enter 0 for second number",
                        "Message",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }

            // Calculate division
            double result = number1 / number2;

            // Display result
            lblResult.setText(
                    String.valueOf(result)
            );

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numbers.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new DivisionCalculator()
        );
    }
}
