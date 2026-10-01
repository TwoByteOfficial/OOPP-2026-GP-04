public class BMI {
    private double height;
    private double weight;
    private double bmi;

    public BMI(double height, double weight) {
        this.height = height;
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void metricUnits() {
        double heightInMeters = this.height / 100.0;
        this.bmi = this.weight / (heightInMeters * heightInMeters);
    }

    public void usUnits() {
        this.bmi = (this.weight * 703.0) / (this.height * this.height);
    }

    public double getBmi() {
        return bmi;
    }
}
