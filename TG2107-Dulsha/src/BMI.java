public class BMI {
    private double height;
    private double weight;
    private double bmi;

    public double calculateMetric(double height, double weight) {
        this.bmi = weight / (height * height);
        return this.bmi;
    }

    public double calculateUS(double height, double weight) {
        this.bmi = (weight * 703) / (height * height);
        return this.bmi;
    }

    public String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        else if (bmi >= 18.5 && bmi <= 24.9) return "Normal weight";
        else if (bmi >= 25.0 && bmi <= 29.9) return "Overweight";
        else return "Obese";
    }

    public static String getNIHGuidelinesText() {
        return "<html><b>NIH BMI Category Reference Values:</b><br>"
                + "• Underweight: Less than 18.5<br>"
                + "• Normal: Between 18.5 and 24.9<br>"
                + "• Overweight: Between 25 and 29.9<br>"
                + "• Obese: 30 or greater</html>";
    }
}
