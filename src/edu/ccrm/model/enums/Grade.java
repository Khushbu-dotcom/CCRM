package edu.ccrm.model.enums;

/**
 * Represents letter grades with their corresponding grade points.
 * Demonstrates enum with values and methods.
 */
public enum Grade {
    A_PLUS("A+", 4.0),
    A("A", 4.0),
    A_MINUS("A-", 3.7),
    B_PLUS("B+", 3.3),
    B("B", 3.0),
    B_MINUS("B-", 2.7),
    C_PLUS("C+", 2.3),
    C("C", 2.0),
    C_MINUS("C-", 1.7),
    D_PLUS("D+", 1.3),
    D("D", 1.0),
    F("F", 0.0),
    INCOMPLETE("I", 0.0),
    WITHDRAWAL("W", 0.0);

    private final String letter;
    private final double gradePoints;

    Grade(String letter, double gradePoints) {
        this.letter = letter;
        this.gradePoints = gradePoints;
    }

    public String getLetter() {
        return letter;
    }

    public double getGradePoints() {
        return gradePoints;
    }

    /**
     * Check if this grade counts towards GPA calculation.
     */
    public boolean countsTowardsGPA() {
        return this != INCOMPLETE && this != WITHDRAWAL;
    }

    /**
     * Get grade from percentage score.
     */
    public static Grade fromPercentage(double percentage) {
        if (percentage >= 97) return A_PLUS;
        if (percentage >= 93) return A;
        if (percentage >= 90) return A_MINUS;
        if (percentage >= 87) return B_PLUS;
        if (percentage >= 83) return B;
        if (percentage >= 80) return B_MINUS;
        if (percentage >= 77) return C_PLUS;
        if (percentage >= 73) return C;
        if (percentage >= 70) return C_MINUS;
        if (percentage >= 67) return D_PLUS;
        if (percentage >= 60) return D;
        return F;
    }

    @Override
    public String toString() {
        return letter;
    }
}
