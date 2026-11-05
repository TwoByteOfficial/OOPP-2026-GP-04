package gui.undergraduate.grades.model;

/**
 * Data model holding overall cumulative and semester academic metrics.
 * Encapsulates high-level GPA metrics including cumulative CGPA, current term SGPA,
 * completed academic credit hours, and university academic standing (e.g., Dean's List, Good Standing).
 */
public class GpaSummaryData {

    // Grade Point Averages
    private final double cgpa;
    private final double currentSgpa;

    // Academic credit volume and standing status
    private final int completedCredits;
    private final String academicStanding;

    /**
     * Constructs an immutable GpaSummaryData snapshot.
     */
    public GpaSummaryData(double cgpa, double currentSgpa, int completedCredits, String academicStanding) {
        this.cgpa = cgpa;
        this.currentSgpa = currentSgpa;
        this.completedCredits = completedCredits;
        this.academicStanding = academicStanding;
    }

    // -------------------------------------------------------------
    // Getter Methods
    // -------------------------------------------------------------

    public double getCgpa() { return cgpa; }
    public double getCurrentSgpa() { return currentSgpa; }
    public int getCompletedCredits() { return completedCredits; }
    public String getAcademicStanding() { return academicStanding; }
}