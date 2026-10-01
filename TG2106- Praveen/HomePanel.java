import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    public HomePanel(MainFrame frame) {
        setLayout(new GridLayout(5, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("BMI Calculator", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel sub = new JLabel("Select your Measure Types", SwingConstants.CENTER);

        JButton btnUS = new JButton("US Customary");
        JButton btnMetric = new JButton("Metric");

        btnUS.addActionListener(e -> frame.showCard("US"));
        btnMetric.addActionListener(e -> frame.showCard("METRIC"));

        add(title);
        add(sub);
        add(new JLabel("")); // blank space
        add(btnUS);
        add(btnMetric);
    }
}