package edu.ccrm.exceptions;

/**
 * Custom exception for when a student exceeds maximum credit limit.
 * Demonstrates custom exception creation with additional context.
 */
public class MaxCreditLimitExceededException extends Exception {
    private final String studentId;
    private final int currentCredits;
    private final int maxCredits;
    private final int attemptedCredits;

    public MaxCreditLimitExceededException(String studentId, int currentCredits, 
                                         int maxCredits, int attemptedCredits) {
        super(String.format("Student %s cannot enroll in %d credits. Current: %d, Max: %d", 
                           studentId, attemptedCredits, currentCredits, maxCredits));
        this.studentId = studentId;
        this.currentCredits = currentCredits;
        this.maxCredits = maxCredits;
        this.attemptedCredits = attemptedCredits;
    }

    public MaxCreditLimitExceededException(String studentId, int currentCredits, 
                                         int maxCredits, int attemptedCredits, String message) {
        super(message);
        this.studentId = studentId;
        this.currentCredits = currentCredits;
        this.maxCredits = maxCredits;
        this.attemptedCredits = attemptedCredits;
    }

    public MaxCreditLimitExceededException(String studentId, int currentCredits, 
                                         int maxCredits, int attemptedCredits, 
                                         String message, Throwable cause) {
        super(message, cause);
        this.studentId = studentId;
        this.currentCredits = currentCredits;
        this.maxCredits = maxCredits;
        this.attemptedCredits = attemptedCredits;
    }

    public String getStudentId() {
        return studentId;
    }

    public int getCurrentCredits() {
        return currentCredits;
    }

    public int getMaxCredits() {
        return maxCredits;
    }

    public int getAttemptedCredits() {
        return attemptedCredits;
    }
}
