import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final CardLayout layout = new CardLayout();
    private final JPanel cards = new JPanel(layout);

    private final BmiLogic logic = new BmiLogic();
    private final ResultPanel resultPanel = new ResultPanel(this);

    public MainFrame() {
        setTitle("BMI Calculator");
        setSize(420, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cards.add(new HomePanel(this), "HOME");
        cards.add(new UsPanel(this), "US");
        cards.add(new MetricPanel(this), "METRIC");
        cards.add(resultPanel, "RESULT");

        add(cards);
        showCard("HOME");
    }

    public void showCard(String name) {
        layout.show(cards, name);
    }

    public BmiLogic getLogic() {
        return logic;
    }

    public void showResult(double bmi) {
        resultPanel.setResult(bmi, logic.conditionout(bmi));
        showCard("RESULT");
    }
}