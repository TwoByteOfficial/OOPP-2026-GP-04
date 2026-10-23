package gui.undergraduate.grades.model;

/**
 * Data model holding overall cumulative and semester academic metrics.
 */
public class GpaSummaryData {
    private final double cgpa;
    private final double currentSgpa;
    private final int completedCredits;
    private final String academicStanding;

    public GpaSummaryData(double cgpa, double currentSgpa, int completedCredits, String academicStanding) {
        this.cgpa = cgpa;
        this.currentSgpa = currentSgpa;
        this.completedCredits = completedCredits;
        this.academicStanding = academicStanding;
    }

    public double getCgpa() { return cgpa; }
    public double getCurrentSgpa() { return currentSgpa; }
    public int getCompletedCredits() { return completedCredits; }
    public String getAcademicStanding() { return academicStanding; }
}