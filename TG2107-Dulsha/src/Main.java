import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Run the GUI thread safely
        SwingUtilities.invokeLater(() -> {
            BMIGui app = new BMIGui();
            app.setVisible(true);
        });
    }
}
