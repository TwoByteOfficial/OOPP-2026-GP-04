public class BmiLogic {

    // US / English
    public double bmicalu(double weight, double height) {
        return (weight * 703.0) / (height * height);
    }

    // Metric
    public double bmicalm(double weight, double height) {
        return weight / (height * height);
    }

    // BMI condition
    public String conditionout(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi >= 18.5 && bmi <= 24.9) {
            return "Normal";
        } else if (bmi >= 25 && bmi <= 29.9) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }
}