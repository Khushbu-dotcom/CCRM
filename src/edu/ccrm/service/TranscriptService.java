package edu.ccrm.service;

import edu.ccrm.model.Course;
import edu.ccrm.model.Student;
import edu.ccrm.model.Transcript;
import edu.ccrm.model.enums.Semester;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for transcript generation and management.
 * Demonstrates complex business logic and data aggregation.
 */
public class TranscriptService {
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    public TranscriptService(StudentService studentService, CourseService courseService, 
                           EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
    }

    /**
     * Generate a complete transcript for a student.
     */
    public Transcript generateTranscript(String studentId) {
        Student student = studentService.getStudentById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }

        Transcript transcript = new Transcript(studentId, student.getFullName());
        
        // Get all enrollments for the student
        List<Transcript.TranscriptEntry> entries = enrollmentService.getStudentEnrollments(studentId)
                .stream()
                .map(enrollment -> {
                    Course course = courseService.getCourseByCode(enrollment.getCourseCode());
                    if (course != null) {
                        return transcript.new TranscriptEntry(
                            enrollment.getCourseCode().toString(),
                            course.getTitle(),
                            course.getCredits(),
                            enrollment.getGrade(),
                            enrollment.getSemester()
                        );
                    }
                    return null;
                })
                .filter(entry -> entry != null)
                .collect(Collectors.toList());

        // Add entries to transcript
        for (Transcript.TranscriptEntry entry : entries) {
            transcript.addEntry(entry);
        }

        return transcript;
    }

    /**
     * Generate a transcript for a specific semester.
     */
    public Transcript generateSemesterTranscript(String studentId, Semester semester) {
        Student student = studentService.getStudentById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }

        Transcript transcript = new Transcript(studentId, student.getFullName());
        
        // Get enrollments for the specific semester
        List<Transcript.TranscriptEntry> entries = enrollmentService.getStudentEnrollments(studentId)
                .stream()
                .filter(enrollment -> enrollment.getSemester().equals(semester))
                .map(enrollment -> {
                    Course course = courseService.getCourseByCode(enrollment.getCourseCode());
                    if (course != null) {
                        return transcript.new TranscriptEntry(
                            enrollment.getCourseCode().toString(),
                            course.getTitle(),
                            course.getCredits(),
                            enrollment.getGrade(),
                            enrollment.getSemester()
                        );
                    }
                    return null;
                })
                .filter(entry -> entry != null)
                .collect(Collectors.toList());

        // Add entries to transcript
        for (Transcript.TranscriptEntry entry : entries) {
            transcript.addEntry(entry);
        }

        return transcript;
    }

    /**
     * Get GPA distribution statistics.
     */
    public String getGPADistribution() {
        List<Student> activeStudents = studentService.getActiveStudents();
        
        long excellent = activeStudents.stream()
                .filter(s -> s.getGpa() >= 3.7)
                .count();
        
        long good = activeStudents.stream()
                .filter(s -> s.getGpa() >= 3.0 && s.getGpa() < 3.7)
                .count();
        
        long satisfactory = activeStudents.stream()
                .filter(s -> s.getGpa() >= 2.0 && s.getGpa() < 3.0)
                .count();
        
        long needsImprovement = activeStudents.stream()
                .filter(s -> s.getGpa() < 2.0)
                .count();

        return String.format("GPA Distribution:\n" +
                           "Excellent (3.7+): %d students\n" +
                           "Good (3.0-3.7): %d students\n" +
                           "Satisfactory (2.0-3.0): %d students\n" +
                           "Needs Improvement (<2.0): %d students",
                           excellent, good, satisfactory, needsImprovement);
    }

    /**
     * Get semester-wise enrollment statistics.
     */
    public String getSemesterStatistics() {
        StringBuilder stats = new StringBuilder("Semester-wise Statistics:\n");
        
        for (Semester semester : Semester.values()) {
            long enrollmentCount = enrollmentService.getSemesterEnrollments(semester).size();
            stats.append(String.format("%s: %d enrollments\n", semester, enrollmentCount));
        }
        
        return stats.toString();
    }

    /**
     * Get course popularity statistics.
     */
    public String getCoursePopularityStats() {
        return courseService.getAllCourses().stream()
                .filter(Course::isActive)
                .map(course -> {
                    long enrollmentCount = enrollmentService.getCourseEnrollments(
                            course.getCourseCode(), course.getSemester()).size();
                    return String.format("%s (%s): %d students", 
                                       course.getCourseCode(), course.getTitle(), enrollmentCount);
                })
                .sorted((s1, s2) -> {
                    // Sort by enrollment count (descending)
                    int count1 = Integer.parseInt(s1.split(": ")[1].split(" ")[0]);
                    int count2 = Integer.parseInt(s2.split(": ")[1].split(" ")[0]);
                    return Integer.compare(count2, count1);
                })
                .collect(Collectors.joining("\n", "Course Popularity:\n", ""));
    }
}
