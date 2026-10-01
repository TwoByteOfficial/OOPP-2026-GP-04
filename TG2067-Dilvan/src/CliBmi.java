import java.util.Scanner;

public class CliBmi {
    public static void main(String[] args) {
        int select;
        double weight;
        double height;
        do{
            System.out.print("=== BMI Calculator ===\n1.US Unites\n2.Matrix Units\n0.Exit\nEnter Choise:");
            Scanner in = new Scanner(System.in);
            select = in.nextInt();
            switch(select){
                case 1:
                    System.out.print("Please enter a weight in pounds: ");
                    weight = in.nextDouble();
                    System.out.print("Please enter a height in inches: ");
                    height = in.nextDouble();
                    BMI calc = new BMI(height, weight);
                    calc.usUnits();
                    System.out.println(calc.getBmi());
                    break;
                case 2:
                    System.out.print("Please enter a weight in kilograms  : ");
                    weight = in.nextDouble();
                    System.out.print("Please enter a height in centimeters: ");
                    height = in.nextDouble();
                    BMI calc2 = new BMI(height, weight);
                    calc2.metricUnits();
                    System.out.println(calc2.getBmi());
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Wrong choice");
            }
        }while (select!=0);
    }
}