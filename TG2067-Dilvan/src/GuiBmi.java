import javax.swing.*;
import java.awt.*;

public class GuiBmi extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JTextField metricHeightField, metricWeightField;
    private JTextField usHeightField, usWeightField;
    private JLabel resultLabel, categoryLabel;

    public GuiBmi() {
        setTitle("BMI Calculator");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createHomePanel(), "Home");
        mainPanel.add(createUSPanel(), "USUnits");
        mainPanel.add(createMetricPanel(), "MetricUnits");
        mainPanel.add(createResultPanel(), "Result");

        add(mainPanel);
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("BMI Calculator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        Dimension buttonSize = new Dimension(220, 50); // Width: 220px, Height: 50px
        Font buttonFont = new Font("Arial", Font.BOLD, 16);

        JButton usBtn = new JButton("US Customary Units");
        usBtn.setPreferredSize(buttonSize); // Sets the button size
        usBtn.setFont(buttonFont);          // Enlarges the text
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(usBtn, gbc);

        JButton metricBtn = new JButton("Metric Units");
        metricBtn.setPreferredSize(buttonSize); // Sets the button size
        metricBtn.setFont(buttonFont);          // Enlarges the text
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(metricBtn, gbc);

        usBtn.addActionListener(e -> cardLayout.show(mainPanel, "USUnits"));
        metricBtn.addActionListener(e -> cardLayout.show(mainPanel, "MetricUnits"));

        return panel;
    }

    private JPanel createUSPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 5, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(3, 30, 30, 30));

        panel.add(new JLabel("Height (inches):"));
        usHeightField = new JTextField();
        panel.add(usHeightField);

        panel.add(new JLabel("Weight (pounds):"));
        usWeightField = new JTextField();
        panel.add(usWeightField);

        JButton backBtn = new JButton("Back");
        JButton calcBtn = new JButton("Calculate");

        panel.add(calcBtn);
        panel.add(backBtn);

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
        calcBtn.addActionListener(e -> {
            try {
                double h = Double.parseDouble(usHeightField.getText());
                double w = Double.parseDouble(usWeightField.getText());
                BMI bmiObj = new BMI(h, w);
                bmiObj.usUnits();
                displayResult(bmiObj.getBmi());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createMetricPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 5, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        panel.add(new JLabel("Height (cm):"));
        metricHeightField = new JTextField();
        panel.add(metricHeightField);

        panel.add(new JLabel("Weight (kg):"));
        metricWeightField = new JTextField();
        panel.add(metricWeightField);

        JButton backBtn = new JButton("Back");
        JButton calcBtn = new JButton("Calculate");

        panel.add(calcBtn);
        panel.add(backBtn);

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
        calcBtn.addActionListener(e -> {
            try {
                double h = Double.parseDouble(metricHeightField.getText());
                double w = Double.parseDouble(metricWeightField.getText());
                BMI bmiObj = new BMI(h, w);
                bmiObj.metricUnits();
                displayResult(bmiObj.getBmi());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel displayPanel = new JPanel(new GridLayout(2, 1));
        resultLabel = new JLabel("Your BMI: ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));
        categoryLabel = new JLabel("Category: ", SwingConstants.CENTER);
        categoryLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        displayPanel.add(resultLabel);
        displayPanel.add(categoryLabel);
        panel.add(displayPanel, BorderLayout.NORTH);

        String rangesText = "<html><center><b>BMI Standard Categories:</b><br>"
                + "Underweight: Less than 18.5<br>"
                + "Normal weight: Between 18.5 and 24.9<br>"
                + "Overweight: Between 25 and 29.9<br>"
                + "Obese: 30 or greater</center></html>";
        JLabel rangesLabel = new JLabel(rangesText, SwingConstants.CENTER);
        panel.add(rangesLabel, BorderLayout.CENTER);

        JButton homeBtn = new JButton("Calculate Again");
        panel.add(homeBtn, BorderLayout.SOUTH);

        homeBtn.addActionListener(e -> {
            clearFields();
            cardLayout.show(mainPanel, "Home");
        });

        return panel;
    }

    private void displayResult(double bmiValue) {
        resultLabel.setText(String.format("Your BMI: %.2f", bmiValue));

        String category;
        if (bmiValue < 18.5) {
            category = "Underweight";
        } else if (bmiValue < 25.0) {
            category = "Normal weight";
        } else if (bmiValue < 30.0) {
            category = "Overweight";
        } else {
            category = "Obese";
        }

        categoryLabel.setText("Category: " + category);
        cardLayout.show(mainPanel, "Result");
    }

    private void clearFields() {
        metricHeightField.setText("");
        metricWeightField.setText("");
        usHeightField.setText("");
        usWeightField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuiBmi().setVisible(true));
    }
}
