import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Calculator extends JFrame implements ActionListener {
    // UI Components
    private JTextField display;
    private JPanel buttonPanel;
    private JButton historyButton;

    // List to store the last 10 calculations (Part b)
    private List<String> calculationHistory;
    private final int MAX_HISTORY_SIZE = 10;

    // Button labels matching the assignment layout
    private final String[] buttonLabels = {
            "C", "(", ")", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "=", "%"
    };

    public Calculator() {
        // Initialize history list
        calculationHistory = new ArrayList<>();

        // Frame setup
        setTitle("Swing Calculator");
        setSize(350, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Top Panel: Display and History Button
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));

        display = new JTextField("0");
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.PLAIN, 24));
        display.setBackground(new Color(240, 240, 240));
        display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(display, BorderLayout.CENTER);

        // Part (b): History button implementation
        historyButton = new JButton("History (0)");
        historyButton.setFont(new Font("Arial", Font.BOLD, 12));
        historyButton.setFocusable(false);
        historyButton.addActionListener(this);
        topPanel.add(historyButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // 2. Center Panel: Calculator Grid (Part a)
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 8, 8));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.setFocusable(false);
            button.addActionListener(this);
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("C")) {
            display.setText("0");
        } else if (command.equals("=")) {
            String expression = display.getText();
            try {
                double result = evaluate(expression);

                // Format output: remove trailing decimal if it's a whole number
                String resultStr = (result % 1 == 0) ? String.valueOf((int) result) : String.valueOf(result);
                display.setText(resultStr);

                // Part (b): Add to history log
                addHistoryRecord(expression + " = " + resultStr);

            } catch (Exception ex) {
                display.setText("Error");
            }
        } else if (command.equals("History (0)") || command.startsWith("History")) {
            // Part (b): Display history records in a dialog box
            showHistoryDialog();
        } else {
            // Append numbers, operators, or parentheses
            if (display.getText().equals("0") || display.getText().equals("Error")) {
                display.setText(command);
            } else {
                display.setText(display.getText() + command);
            }
        }
    }

    // Part (b): Manage history items (keeps only the last 10 calculations)
    private void addHistoryRecord(String record) {
        if (calculationHistory.size() >= MAX_HISTORY_SIZE) {
            calculationHistory.remove(0); // Remove the oldest item
        }
        calculationHistory.add(record);
        historyButton.setText("History (" + calculationHistory.size() + ")");
    }

    // Part (b): View calculation logs
    private void showHistoryDialog() {
        if (calculationHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No calculations performed yet.", "History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder historyText = new StringBuilder("--- Last 10 Calculations ---\n\n");
        for (int i = calculationHistory.size() - 1; i >= 0; i--) {
            historyText.append(calculationHistory.get(i)).append("\n");
        }

        JOptionPane.showMessageDialog(this, historyText.toString(), "Calculation History", JOptionPane.PLAIN_MESSAGE);
    }

    // A completely self-contained expression parser supporting +, -, *, /, %, and ()
    private double evaluate(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected character: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else if (eat('%')) x %= parseFactor(); // modulo
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses logic
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected token");
                }
                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        // Run UI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new Calculator().setVisible(true);
        });
    }
}
