package edu.ccrm.model;

import edu.ccrm.model.enums.Semester;
import edu.ccrm.model.interfaces.Persistable;
import edu.ccrm.model.value.CourseCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Student entity extending Person.
 * Demonstrates inheritance, encapsulation, and polymorphism.
 */
public class Student extends Person implements Persistable {
    private String regNo;
    private List<CourseCode> enrolledCourses;
    private Semester currentSemester;
    private double gpa;
    private int totalCredits;

    // Static nested class for Student statistics
    public static class Statistics {
        private final int totalStudents;
        private final double averageGPA;
        private final int activeStudents;

        public Statistics(int totalStudents, double averageGPA, int activeStudents) {
            this.totalStudents = totalStudents;
            this.averageGPA = averageGPA;
            this.activeStudents = activeStudents;
        }

        public int getTotalStudents() { return totalStudents; }
        public double getAverageGPA() { return averageGPA; }
        public int getActiveStudents() { return activeStudents; }

        @Override
        public String toString() {
            return String.format("Statistics{total=%d, avgGPA=%.2f, active=%d}", 
                               totalStudents, averageGPA, activeStudents);
        }
    }

    public Student(String id, String regNo, String fullName, String email) {
        super(id, fullName, email);
        this.regNo = Objects.requireNonNull(regNo, "Registration number cannot be null");
        this.enrolledCourses = new ArrayList<>();
        this.currentSemester = Semester.FALL_2025;
        this.gpa = 0.0;
        this.totalCredits = 0;
    }

    // Getters
    public String getRegNo() {
        return regNo;
    }

    public List<CourseCode> getEnrolledCourses() {
        return new ArrayList<>(enrolledCourses);
    }

    public Semester getCurrentSemester() {
        return currentSemester;
    }

    public double getGpa() {
        return gpa;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    // Setters
    public void setRegNo(String regNo) {
        this.regNo = Objects.requireNonNull(regNo, "Registration number cannot be null");
    }

    public void setCurrentSemester(Semester currentSemester) {
        this.currentSemester = Objects.requireNonNull(currentSemester, "Current semester cannot be null");
    }

    public void setGpa(double gpa) {
        if (gpa < 0.0 || gpa > 4.0) {
            throw new IllegalArgumentException("GPA must be between 0.0 and 4.0");
        }
        this.gpa = gpa;
    }

    public void setTotalCredits(int totalCredits) {
        if (totalCredits < 0) {
            throw new IllegalArgumentException("Total credits cannot be negative");
        }
        this.totalCredits = totalCredits;
    }

    // Business methods
    public void enrollInCourse(CourseCode courseCode) {
        if (courseCode == null) {
            throw new IllegalArgumentException("Course code cannot be null");
        }
        if (!enrolledCourses.contains(courseCode)) {
            enrolledCourses.add(courseCode);
        }
    }

    public void unenrollFromCourse(CourseCode courseCode) {
        enrolledCourses.remove(courseCode);
    }

    public boolean isEnrolledIn(CourseCode courseCode) {
        return enrolledCourses.contains(courseCode);
    }

    @Override
    public String getPersonType() {
        return "Student";
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Student: %s (%s) - %s", fullName, regNo, email);
    }

    @Override
    public String toCSV() {
        StringBuilder courses = new StringBuilder();
        for (int i = 0; i < enrolledCourses.size(); i++) {
            if (i > 0) courses.append(";");
            courses.append(enrolledCourses.get(i).toString());
        }
        
        return String.format("%s,%s,%s,%s,%s,%s,%s", 
                           id, regNo, fullName, email, active ? "ACTIVE" : "INACTIVE", 
                           courses.toString(), createdAt.toString());
    }

    public static Student fromCSV(String csvData) {
        String[] fields = csvData.split(",");
        if (fields.length < 6) {
            throw new IllegalArgumentException("Invalid CSV data for Student");
        }
        
        Student student = new Student(fields[0], fields[1], fields[2], fields[3]);
        student.setActive("ACTIVE".equals(fields[4]));
        
        if (fields.length > 5 && !fields[5].isEmpty()) {
            String[] courseCodes = fields[5].split(";");
            for (String courseCode : courseCodes) {
                if (!courseCode.trim().isEmpty()) {
                    student.enrollInCourse(CourseCode.parse(courseCode.trim()));
                }
            }
        }
        
        return student;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Student student = (Student) obj;
        return Objects.equals(regNo, student.regNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), regNo);
    }

    @Override
    public String toString() {
        return String.format("Student{id='%s', regNo='%s', fullName='%s', email='%s', " +
                           "enrolledCourses=%d, gpa=%.2f, active=%s}", 
                           id, regNo, fullName, email, enrolledCourses.size(), gpa, active);
    }
}
