package edu.ccrm.service;

import edu.ccrm.exceptions.DuplicateEnrollmentException;
import edu.ccrm.exceptions.MaxCreditLimitExceededException;
import edu.ccrm.model.Course;
import edu.ccrm.model.Enrollment;
import edu.ccrm.model.Student;
import edu.ccrm.model.enums.Grade;
import edu.ccrm.model.enums.Semester;
import edu.ccrm.model.value.CourseCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for enrollment and grading operations.
 * Demonstrates business rules, exception handling, and complex operations.
 */
public class EnrollmentService {
    private final List<Enrollment> enrollments;
    private final StudentService studentService;
    private final CourseService courseService;
    private int nextId;
    private static final int MAX_CREDITS_PER_SEMESTER = 18;

    public EnrollmentService(StudentService studentService, CourseService courseService) {
        this.enrollments = new ArrayList<>();
        this.studentService = studentService;
        this.courseService = courseService;
        this.nextId = 1;
    }

    // Enrollment operations
    public Enrollment enrollStudent(String studentId, CourseCode courseCode, Semester semester) 
            throws DuplicateEnrollmentException, MaxCreditLimitExceededException {
        
        // Check if student exists and is active
        Student student = studentService.getStudentById(studentId);
        if (student == null || !student.isActive()) {
            throw new IllegalArgumentException("Student not found or inactive");
        }

        // Check if course exists and is active
        Course course = courseService.getCourseByCode(courseCode);
        if (course == null || !course.isActive()) {
            throw new IllegalArgumentException("Course not found or inactive");
        }

        // Check for duplicate enrollment
        if (isEnrolled(studentId, courseCode, semester)) {
            throw new DuplicateEnrollmentException(studentId, courseCode.toString());
        }

        // Check credit limit
        int currentCredits = getCurrentSemesterCredits(studentId, semester);
        if (currentCredits + course.getCredits() > MAX_CREDITS_PER_SEMESTER) {
            throw new MaxCreditLimitExceededException(studentId, currentCredits, 
                    MAX_CREDITS_PER_SEMESTER, course.getCredits());
        }

        // Create enrollment
        String id = "ENR" + String.format("%04d", nextId++);
        Enrollment enrollment = new Enrollment(id, studentId, courseCode, semester);
        enrollments.add(enrollment);

        // Update student's enrolled courses
        student.enrollInCourse(courseCode);

        return enrollment;
    }

    public boolean unenrollStudent(String studentId, CourseCode courseCode, Semester semester) {
        Enrollment enrollment = findEnrollment(studentId, courseCode, semester);
        if (enrollment != null) {
            enrollment.setActive(false);
            
            // Update student's enrolled courses
            Student student = studentService.getStudentById(studentId);
            if (student != null) {
                student.unenrollFromCourse(courseCode);
            }
            
            return true;
        }
        return false;
    }

    // Grading operations
    public boolean recordGrade(String studentId, CourseCode courseCode, Semester semester, 
                             double percentageScore) {
        Enrollment enrollment = findEnrollment(studentId, courseCode, semester);
        if (enrollment != null && enrollment.isActive()) {
            enrollment.recordGrade(percentageScore);
            updateStudentGPA(studentId);
            return true;
        }
        return false;
    }

    public boolean recordGrade(String studentId, CourseCode courseCode, Semester semester, 
                             Grade grade) {
        Enrollment enrollment = findEnrollment(studentId, courseCode, semester);
        if (enrollment != null && enrollment.isActive()) {
            enrollment.recordGrade(grade);
            updateStudentGPA(studentId);
            return true;
        }
        return false;
    }

    // Query operations
    public boolean isEnrolled(String studentId, CourseCode courseCode, Semester semester) {
        return findEnrollment(studentId, courseCode, semester) != null;
    }

    public Enrollment findEnrollment(String studentId, CourseCode courseCode, Semester semester) {
        return enrollments.stream()
                .filter(e -> e.getStudentId().equals(studentId))
                .filter(e -> e.getCourseCode().equals(courseCode))
                .filter(e -> e.getSemester().equals(semester))
                .filter(Enrollment::isActive)
                .findFirst()
                .orElse(null);
    }

    public List<Enrollment> getStudentEnrollments(String studentId) {
        return enrollments.stream()
                .filter(e -> e.getStudentId().equals(studentId))
                .filter(Enrollment::isActive)
                .collect(Collectors.toList());
    }

    public List<Enrollment> getCourseEnrollments(CourseCode courseCode, Semester semester) {
        return enrollments.stream()
                .filter(e -> e.getCourseCode().equals(courseCode))
                .filter(e -> e.getSemester().equals(semester))
                .filter(Enrollment::isActive)
                .collect(Collectors.toList());
    }

    public List<Enrollment> getSemesterEnrollments(Semester semester) {
        return enrollments.stream()
                .filter(e -> e.getSemester().equals(semester))
                .filter(Enrollment::isActive)
                .collect(Collectors.toList());
    }

    // Statistics and calculations
    public int getCurrentSemesterCredits(String studentId, Semester semester) {
        return enrollments.stream()
                .filter(e -> e.getStudentId().equals(studentId))
                .filter(e -> e.getSemester().equals(semester))
                .filter(Enrollment::isActive)
                .mapToInt(e -> {
                    Course course = courseService.getCourseByCode(e.getCourseCode());
                    return course != null ? course.getCredits() : 0;
                })
                .sum();
    }

    public double calculateStudentGPA(String studentId) {
        List<Enrollment> studentEnrollments = getStudentEnrollments(studentId);
        
        double totalGradePoints = 0.0;
        int totalCredits = 0;

        for (Enrollment enrollment : studentEnrollments) {
            if (enrollment.isGraded()) {
                Course course = courseService.getCourseByCode(enrollment.getCourseCode());
                if (course != null) {
                    double gradePoints = enrollment.getGradePoints() * course.getCredits();
                    totalGradePoints += gradePoints;
                    totalCredits += course.getCredits();
                }
            }
        }

        return totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
    }

    private void updateStudentGPA(String studentId) {
        Student student = studentService.getStudentById(studentId);
        if (student != null) {
            double gpa = calculateStudentGPA(studentId);
            student.setGpa(gpa);
        }
    }

    // Reports
    public List<Student> getTopStudents(int limit) {
        return studentService.getAllStudents().stream()
                .filter(Student::isActive)
                .sorted((s1, s2) -> Double.compare(s2.getGpa(), s1.getGpa()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Enrollment> getGradedEnrollments() {
        return enrollments.stream()
                .filter(Enrollment::isGraded)
                .collect(Collectors.toList());
    }

    public List<Enrollment> getUngradedEnrollments() {
        return enrollments.stream()
                .filter(e -> !e.isGraded() && e.isActive())
                .collect(Collectors.toList());
    }

    // Data management
    public void clearAllEnrollments() {
        enrollments.clear();
        nextId = 1;
    }

    public int getEnrollmentCount() {
        return enrollments.size();
    }
}
