package edu.ccrm.model;

import edu.ccrm.model.enums.Grade;
import edu.ccrm.model.enums.Semester;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Transcript entity representing a student's academic record.
 * Demonstrates composition, polymorphism, and business logic.
 */
public class Transcript {
    private String studentId;
    private String studentName;
    private List<TranscriptEntry> entries;
    private double overallGPA;
    private int totalCredits;
    private LocalDateTime generatedAt;

    // Inner class for transcript entries
    public class TranscriptEntry {
        private String courseCode;
        private String courseTitle;
        private int credits;
        private Grade grade;
        private Semester semester;
        private double gradePoints;

        public TranscriptEntry(String courseCode, String courseTitle, int credits, 
                             Grade grade, Semester semester) {
            this.courseCode = Objects.requireNonNull(courseCode, "Course code cannot be null");
            this.courseTitle = Objects.requireNonNull(courseTitle, "Course title cannot be null");
            this.credits = credits;
            this.grade = grade;
            this.semester = Objects.requireNonNull(semester, "Semester cannot be null");
            this.gradePoints = grade != null ? grade.getGradePoints() * credits : 0.0;
        }

        // Getters
        public String getCourseCode() { return courseCode; }
        public String getCourseTitle() { return courseTitle; }
        public int getCredits() { return credits; }
        public Grade getGrade() { return grade; }
        public Semester getSemester() { return semester; }
        public double getGradePoints() { return gradePoints; }

        @Override
        public String toString() {
            return String.format("%-10s %-30s %3d %4s %s", 
                               courseCode, courseTitle, credits, 
                               grade != null ? grade.toString() : "N/A", semester);
        }
    }

    public Transcript(String studentId, String studentName) {
        this.studentId = Objects.requireNonNull(studentId, "Student ID cannot be null");
        this.studentName = Objects.requireNonNull(studentName, "Student name cannot be null");
        this.entries = new ArrayList<>();
        this.overallGPA = 0.0;
        this.totalCredits = 0;
        this.generatedAt = LocalDateTime.now();
    }

    // Getters
    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public List<TranscriptEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public double getOverallGPA() {
        return overallGPA;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    // Business methods
    public void addEntry(String courseCode, String courseTitle, int credits, 
                        Grade grade, Semester semester) {
        TranscriptEntry entry = new TranscriptEntry(courseCode, courseTitle, credits, grade, semester);
        entries.add(entry);
        recalculateGPA();
    }

    public void addEntry(TranscriptEntry entry) {
        entries.add(entry);
        recalculateGPA();
    }

    private void recalculateGPA() {
        double totalGradePoints = 0.0;
        int totalCredits = 0;

        for (TranscriptEntry entry : entries) {
            if (entry.getGrade() != null && entry.getGrade().countsTowardsGPA()) {
                totalGradePoints += entry.getGradePoints();
                totalCredits += entry.getCredits();
            }
        }

        this.totalCredits = totalCredits;
        this.overallGPA = totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
    }

    public List<TranscriptEntry> getEntriesBySemester(Semester semester) {
        return entries.stream()
                .filter(entry -> entry.getSemester().equals(semester))
                .toList();
    }

    public List<TranscriptEntry> getEntriesByGrade(Grade grade) {
        return entries.stream()
                .filter(entry -> entry.getGrade() != null && entry.getGrade().equals(grade))
                .toList();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(80)).append("\n");
        sb.append("TRANSCRIPT FOR: ").append(studentName).append(" (").append(studentId).append(")\n");
        sb.append("Generated: ").append(generatedAt).append("\n");
        sb.append("Overall GPA: ").append(String.format("%.2f", overallGPA)).append("\n");
        sb.append("Total Credits: ").append(totalCredits).append("\n");
        sb.append("=".repeat(80)).append("\n");
        sb.append(String.format("%-10s %-30s %3s %4s %s\n", 
                               "CODE", "TITLE", "CR", "GRADE", "SEMESTER"));
        sb.append("-".repeat(80)).append("\n");
        
        for (TranscriptEntry entry : entries) {
            sb.append(entry.toString()).append("\n");
        }
        
        sb.append("=".repeat(80));
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transcript that = (Transcript) obj;
        return Objects.equals(studentId, that.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }
}
