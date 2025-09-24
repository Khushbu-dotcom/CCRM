package edu.ccrm.model;

import edu.ccrm.model.enums.Grade;
import edu.ccrm.model.enums.Semester;
import edu.ccrm.model.interfaces.Persistable;
import edu.ccrm.model.value.CourseCode;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Enrollment entity representing a student's enrollment in a course.
 * Demonstrates composition and business logic.
 */
public class Enrollment implements Persistable {
    private String id;
    private String studentId;
    private CourseCode courseCode;
    private Semester semester;
    private LocalDateTime enrolledAt;
    private Grade grade;
    private double percentageScore;
    private boolean active;

    public Enrollment(String id, String studentId, CourseCode courseCode, Semester semester) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.studentId = Objects.requireNonNull(studentId, "Student ID cannot be null");
        this.courseCode = Objects.requireNonNull(courseCode, "Course code cannot be null");
        this.semester = Objects.requireNonNull(semester, "Semester cannot be null");
        this.enrolledAt = LocalDateTime.now();
        this.grade = null;
        this.percentageScore = 0.0;
        this.active = true;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public CourseCode getCourseCode() {
        return courseCode;
    }

    public Semester getSemester() {
        return semester;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public Grade getGrade() {
        return grade;
    }

    public double getPercentageScore() {
        return percentageScore;
    }

    public boolean isActive() {
        return active;
    }

    // Setters
    public void setActive(boolean active) {
        this.active = active;
    }

    // Business methods
    public void recordGrade(double percentageScore) {
        if (percentageScore < 0.0 || percentageScore > 100.0) {
            throw new IllegalArgumentException("Percentage score must be between 0.0 and 100.0");
        }
        this.percentageScore = percentageScore;
        this.grade = Grade.fromPercentage(percentageScore);
    }

    public void recordGrade(Grade grade) {
        this.grade = Objects.requireNonNull(grade, "Grade cannot be null");
        this.percentageScore = grade == Grade.INCOMPLETE || grade == Grade.WITHDRAWAL ? 0.0 : 0.0;
    }

    public boolean isGraded() {
        return grade != null;
    }

    public double getGradePoints() {
        return grade != null ? grade.getGradePoints() : 0.0;
    }

    @Override
    public String toCSV() {
        return String.format("%s,%s,%s,%s,%s,%.2f,%s,%s", 
                           id, studentId, courseCode.toString(), semester.toString(), 
                           enrolledAt.toString(), percentageScore, 
                           grade != null ? grade.toString() : "", 
                           active ? "ACTIVE" : "INACTIVE");
    }

    public static Enrollment fromCSV(String csvData) {
        String[] fields = csvData.split(",");
        if (fields.length < 8) {
            throw new IllegalArgumentException("Invalid CSV data for Enrollment");
        }
        
        CourseCode courseCode = CourseCode.parse(fields[2]);
        Semester semester = Semester.valueOf(fields[3].replace(" ", "_"));
        Enrollment enrollment = new Enrollment(fields[0], fields[1], courseCode, semester);
        
        enrollment.percentageScore = Double.parseDouble(fields[5]);
        if (!fields[6].isEmpty()) {
            enrollment.grade = Grade.valueOf(fields[6]);
        }
        enrollment.setActive("ACTIVE".equals(fields[7]));
        
        return enrollment;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Enrollment that = (Enrollment) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Enrollment{id='%s', studentId='%s', courseCode=%s, " +
                           "semester=%s, grade=%s, active=%s}", 
                           id, studentId, courseCode, semester, grade, active);
    }
}
