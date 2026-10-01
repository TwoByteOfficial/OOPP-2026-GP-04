public class BMICalculator {
    private double weight;
    private double height;
    private int unitType;

    public BMICalculator(double weight, double height, int unitType) {
        this.weight = weight;
        this.height = height;
        this.unitType = unitType;
    }

    public double calculateBMI() {
        if (unitType == 1) {
            return (weight * 703.0) / (height * height); // US Customary
        } else {
            if (height > 3.0) { height = height / 100.0; } // CM to Meters
            return weight / (height * height); // Metric
        }
    }

    public String getCategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi <= 24.9) return "Normal";
        if (bmi <= 29.9) return "Overweight";
        return "Obese";
    }
}
