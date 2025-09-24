package edu.ccrm.exceptions;

/**
 * Custom exception for duplicate enrollment attempts.
 * Demonstrates custom exception creation.
 */
public class DuplicateEnrollmentException extends Exception {
    private final String studentId;
    private final String courseCode;

    public DuplicateEnrollmentException(String studentId, String courseCode) {
        super(String.format("Student %s is already enrolled in course %s", studentId, courseCode));
        this.studentId = studentId;
        this.courseCode = courseCode;
    }

    public DuplicateEnrollmentException(String studentId, String courseCode, String message) {
        super(message);
        this.studentId = studentId;
        this.courseCode = courseCode;
    }

    public DuplicateEnrollmentException(String studentId, String courseCode, String message, Throwable cause) {
        super(message, cause);
        this.studentId = studentId;
        this.courseCode = courseCode;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }
}
