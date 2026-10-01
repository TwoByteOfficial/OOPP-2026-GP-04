import javax.swing.*;
import java.awt.*;

public class BMIGui extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private BMI bmiCore;

    // Output components on the Results Card
    private JLabel bmiResultLabel;
    private JLabel categoryResultLabel;

    public BMIGui() {
        bmiCore = new BMI();

        // Window Configuration
        setTitle("BMI Calculator Application");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window on screen

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Initialize and register all 4 cards
        mainContainer.add(createMenuCard(), "MENU");
        mainContainer.add(createUSInputCard(), "US_INPUT");
        mainContainer.add(createMetricInputCard(), "METRIC_INPUT");
        mainContainer.add(createResultsCard(), "RESULTS");

        add(mainContainer);
        cardLayout.show(mainContainer, "MENU"); // Start on the main menu
    }

    // --- CARD 1: MAIN MENU ---
    private JPanel createMenuCard() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel title = new JLabel("Welcome to the BMI Calculator", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton usBtn = new JButton("US Customary Units (Inches / Pounds)");
        JButton metricBtn = new JButton("Metric Units (Meters / Kilograms)");


        usBtn.addActionListener(e -> cardLayout.show(mainContainer, "US_INPUT"));
        metricBtn.addActionListener(e -> cardLayout.show(mainContainer, "METRIC_INPUT"));


        panel.add(title);
        panel.add(usBtn);
        panel.add(metricBtn);
        return panel;
    }

    // --- CARD 2: US CUSTOMARY INPUTS ---
    private JPanel createUSInputCard() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel hLabel = new JLabel("Height (inches):");
        JTextField hField = new JTextField();
        JLabel wLabel = new JLabel("Weight (pounds):");
        JTextField wField = new JTextField();

        JButton backBtn = new JButton("Back");
        JButton calcBtn = new JButton("Calculate");

        backBtn.addActionListener(e -> cardLayout.show(mainContainer, "MENU"));
        calcBtn.addActionListener(e -> {
            try {
                double h = Double.parseDouble(hField.getText());
                double w = Double.parseDouble(wField.getText());
                double score = bmiCore.calculateUS(h, w);

                showResults(score);
                hField.setText(""); wField.setText(""); // clear fields
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(hLabel); panel.add(hField);
        panel.add(wLabel); panel.add(wField);
        panel.add(backBtn); panel.add(calcBtn);
        return panel;
    }

    // --- CARD 3: METRIC INPUTS ---
    private JPanel createMetricInputCard() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel hLabel = new JLabel("Height (meters):");
        JTextField hField = new JTextField();
        JLabel wLabel = new JLabel("Weight (kilograms):");
        JTextField wField = new JTextField();

        JButton backBtn = new JButton("Back");
        JButton calcBtn = new JButton("Calculate");

        backBtn.addActionListener(e -> cardLayout.show(mainContainer, "MENU"));
        calcBtn.addActionListener(e -> {
            try {
                double h = Double.parseDouble(hField.getText());
                double w = Double.parseDouble(wField.getText());
                double score = bmiCore.calculateMetric(h, w);

                showResults(score);
                hField.setText(""); wField.setText(""); // clear fields
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(hLabel); panel.add(hField);
        panel.add(wLabel); panel.add(wField);
        panel.add(backBtn); panel.add(calcBtn);
        return panel;
    }

    // --- CARD 4: RESULTS DISPLAY ---
    private JPanel createResultsCard() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        bmiResultLabel = new JLabel("BMI is: ");
        bmiResultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bmiResultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        categoryResultLabel = new JLabel("BMI Category is: ");
        categoryResultLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        categoryResultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // NIH Reference Box
        JLabel nihLabel = new JLabel(BMI.getNIHGuidelinesText());
        nihLabel.setBorder(BorderFactory.createTitledBorder("Reference Data"));
        nihLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton homeBtn = new JButton("Return to Main Menu");
        homeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        homeBtn.addActionListener(e -> cardLayout.show(mainContainer, "MENU"));

        panel.add(bmiResultLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(categoryResultLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(nihLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(homeBtn);

        return panel;
    }

    // Helper logic to populate data and transit view
    private void showResults(double score) {
        bmiResultLabel.setText(String.format("BMI Score: %.2f", score));
        categoryResultLabel.setText("Health Classification: " + bmiCore.getBMICategory(score));
        cardLayout.show(mainContainer, "RESULTS");
    }
}
