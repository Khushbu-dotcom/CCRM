package edu.ccrm.cli;

import edu.ccrm.config.AppConfig;
import edu.ccrm.exceptions.DuplicateEnrollmentException;
import edu.ccrm.exceptions.MaxCreditLimitExceededException;
import edu.ccrm.model.*;
import edu.ccrm.model.enums.Grade;
import edu.ccrm.model.enums.Semester;
import edu.ccrm.model.value.CourseCode;
import edu.ccrm.service.*;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.Comparator;

/**
 * Entry point for the Campus Course & Records Manager (CCRM).
 * Demonstrates comprehensive Java features including OOP, Streams, NIO.2, and more.
 */
public class CCRM {
    private static StudentService studentService;
    private static CourseService courseService;
    private static EnrollmentService enrollmentService;
    private static TranscriptService transcriptService;
    private static FileService fileService;
    private static Scanner scanner;

    public static void main(String[] args) {
        // Enable assertions for debugging - helps catch bugs early
        assert true : "Assertions are enabled";
        
        // Get the singleton configuration instance
        AppConfig appConfig = AppConfig.getInstance();
        appConfig.initialize();
        
        // Set up all our service dependencies
        initializeServices(appConfig);
        
        // Show some platform info before starting
        printPlatformNote();
        
        // Start the main application loop
        runMainMenu();
    }

    private static void initializeServices(AppConfig appConfig) {
        // Initialize all our service classes with proper dependencies
        studentService = new StudentService();
        courseService = new CourseService();
        
        // Enrollment service needs both student and course services
        enrollmentService = new EnrollmentService(studentService, courseService);
        
        // Transcript service needs all three for generating reports
        transcriptService = new TranscriptService(studentService, courseService, enrollmentService);
        
        // File service handles all I/O operations
        fileService = new FileService(appConfig.getDataRootDirectory());
        
        // Scanner for user input
        scanner = new Scanner(System.in);
    }

    private static void printPlatformNote() {
        // Display some basic platform information
        System.out.println("Java Platforms: ME (embedded), SE (standard), EE (enterprise). Running SE.");
        System.out.println("JDK Version: " + System.getProperty("java.version"));
        System.out.println("JVM: " + System.getProperty("java.vm.name"));
    }

    private static void runMainMenu() {
        boolean running = true;
        
        // Main application loop - keeps running until user chooses to exit
        mainMenuLoop: while (running) {
            System.out.println();
            System.out.println("=== CCRM Main Menu ===");
            System.out.println("1. Manage Students");
            System.out.println("2. Manage Courses");
            System.out.println("3. Enrollment");
            System.out.println("4. Grades & Transcript");
            System.out.println("5. Import/Export Data");
            System.out.println("6. Backup & Show Backup Size");
            System.out.println("7. Reports");
            System.out.println("8. Advanced Java Concepts Demo");
            System.out.println("9. AppConfig Info");
            System.out.println("10. Exit");
            System.out.print("Choose an option (1-10): ");

            String choice = scanner.nextLine();
            
            // Handle user selection
            switch (choice) {
                case "1":
                    manageStudents();
                    break;
                case "2":
                    manageCourses();
                    break;
                case "3":
                    manageEnrollments();
                    break;
                case "4":
                    manageGradesAndTranscripts();
                    break;
                case "5":
                    manageImportExport();
                    break;
                case "6":
                    manageBackup();
                    break;
                case "7":
                    generateReports();
                    break;
                case "8":
                    demonstrateAdvancedConcepts();
                    break;
                case "9":
                    System.out.println(AppConfig.getInstance());
                    break;
                case "10":
                    System.out.println("Exiting CCRM. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        // Clean up resources
        scanner.close();
    }

    // Student Management - handles all student-related operations
    private static void manageStudents() {
        boolean inStudentMenu = true;
        
        // Keep showing student menu until user goes back to main menu
        while (inStudentMenu) {
            System.out.println();
            System.out.println("=== Student Management ===");
            System.out.println("1. Add Student");
            System.out.println("2. List All Students");
            System.out.println("3. Search Students");
            System.out.println("4. Update Student");
            System.out.println("5. Deactivate Student");
            System.out.println("6. View Student Profile");
            System.out.println("7. Back to Main Menu");
            System.out.print("Choose an option (1-7): ");

            String choice = scanner.nextLine();
            
            // Process the user's choice
            switch (choice) {
                case "1":
                    addStudent();
                    break;
                case "2":
                    listAllStudents();
                    break;
                case "3":
                    searchStudents();
                    break;
                case "4":
                    updateStudent();
                    break;
                case "5":
                    deactivateStudent();
                    break;
                case "6":
                    viewStudentProfile();
                    break;
                case "7":
                    inStudentMenu = false; // Exit this submenu
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addStudent() {
        try {
            // Get student information from user
            System.out.print("Enter registration number: ");
            String regNo = scanner.nextLine();
            System.out.print("Enter full name: ");
            String fullName = scanner.nextLine();
            System.out.print("Enter email: ");
            String email = scanner.nextLine();

            // Create the student using our service
            Student student = studentService.addStudent(regNo, fullName, email);
            System.out.println("Student added successfully: " + student);
        } catch (Exception e) {
            // Handle any errors that might occur
            System.err.println("Error adding student: " + e.getMessage());
        }
    }

    private static void listAllStudents() {
        List<Student> students = studentService.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            System.out.println("All Students:");
            students.forEach(System.out::println);
        }
    }

    private static void searchStudents() {
        System.out.println("Search by:");
        System.out.println("1. Name");
        System.out.println("2. Email");
        System.out.println("3. Registration Number");
        System.out.print("Choose option (1-3): ");
        
        String choice = scanner.nextLine();
        List<Student> results = new ArrayList<>();
        
        switch (choice) {
            case "1":
                System.out.print("Enter name to search: ");
                String name = scanner.nextLine();
                results = studentService.searchByName(name);
                break;
            case "2":
                System.out.print("Enter email to search: ");
                String email = scanner.nextLine();
                results = studentService.searchByEmail(email);
                break;
            case "3":
                System.out.print("Enter registration number: ");
                String regNo = scanner.nextLine();
                Student student = studentService.getStudentByRegNo(regNo);
                if (student != null) {
                    results.add(student);
                }
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        if (results.isEmpty()) {
            System.out.println("No students found.");
        } else {
            System.out.println("Search Results:");
            results.forEach(System.out::println);
        }
    }

    private static void updateStudent() {
        System.out.print("Enter student ID to update: ");
        String id = scanner.nextLine();
        Student student = studentService.getStudentById(id);
        
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        
        System.out.print("Enter new full name: ");
        String fullName = scanner.nextLine();
        System.out.print("Enter new email: ");
        String email = scanner.nextLine();
        
        if (studentService.updateStudent(id, fullName, email)) {
            System.out.println("Student updated successfully.");
        } else {
            System.out.println("Failed to update student.");
        }
    }

    private static void deactivateStudent() {
        System.out.print("Enter student ID to deactivate: ");
        String id = scanner.nextLine();
        
        if (studentService.deactivateStudent(id)) {
            System.out.println("Student deactivated successfully.");
        } else {
            System.out.println("Student not found.");
        }
    }

    private static void viewStudentProfile() {
        System.out.print("Enter student ID: ");
        String id = scanner.nextLine();
        Student student = studentService.getStudentById(id);
        
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        
        System.out.println("=== Student Profile ===");
        System.out.println(student.getDisplayInfo());
        System.out.println("Created: " + student.getCreatedAt());
        System.out.println("Status: " + (student.isActive() ? "Active" : "Inactive"));
        System.out.println("GPA: " + student.getGpa());
        System.out.println("Enrolled Courses: " + student.getEnrolledCourses());
    }

    // Course Management
    private static void manageCourses() {
        boolean inCourseMenu = true;
        while (inCourseMenu) {
            System.out.println();
            System.out.println("=== Course Management ===");
            System.out.println("1. Add Course");
            System.out.println("2. List All Courses");
            System.out.println("3. Search Courses");
            System.out.println("4. Update Course");
            System.out.println("5. Assign Instructor");
            System.out.println("6. Sort Courses");
            System.out.println("7. Back to Main Menu");
            System.out.print("Choose an option (1-7): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    addCourse();
                    break;
                case "2":
                    listAllCourses();
                    break;
                case "3":
                    searchCourses();
                    break;
                case "4":
                    updateCourse();
                    break;
                case "5":
                    assignInstructor();
                    break;
                case "6":
                    sortCourses();
                    break;
                case "7":
                    inCourseMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addCourse() {
        try {
            System.out.print("Enter course code (e.g., CSE101): ");
            String courseCodeStr = scanner.nextLine();
            CourseCode courseCode = CourseCode.parse(courseCodeStr);
            
            System.out.print("Enter course title: ");
            String title = scanner.nextLine();
            
            System.out.print("Enter department: ");
            String department = scanner.nextLine();
            
            System.out.print("Enter description: ");
            String description = scanner.nextLine();
            
            System.out.print("Enter credits: ");
            int credits = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Enter instructor ID (optional): ");
            String instructorId = scanner.nextLine();
            if (instructorId.trim().isEmpty()) {
                instructorId = null;
            }
            
            System.out.println("Available semesters:");
            for (Semester semester : Semester.values()) {
                System.out.println("- " + semester);
            }
            System.out.print("Enter semester: ");
            String semesterStr = scanner.nextLine();
            Semester semester = Semester.valueOf(semesterStr.replace(" ", "_"));

            Course course = courseService.addCourse(courseCode, title, department, 
                    description, credits, instructorId, semester);
            System.out.println("Course added successfully: " + course);
        } catch (Exception e) {
            System.err.println("Error adding course: " + e.getMessage());
        }
    }

    private static void listAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
        } else {
            System.out.println("All Courses:");
            courses.forEach(System.out::println);
        }
    }

    private static void searchCourses() {
        System.out.println("Search by:");
        System.out.println("1. Instructor");
        System.out.println("2. Department");
        System.out.println("3. Semester");
        System.out.println("4. Title");
        System.out.print("Choose option (1-4): ");
        
        String choice = scanner.nextLine();
        List<Course> results = new ArrayList<>();
        
        switch (choice) {
            case "1":
                System.out.print("Enter instructor ID: ");
                String instructorId = scanner.nextLine();
                results = courseService.searchByInstructor(instructorId);
                break;
            case "2":
                System.out.print("Enter department: ");
                String department = scanner.nextLine();
                results = courseService.searchByDepartment(department);
                break;
            case "3":
                System.out.println("Available semesters:");
                for (Semester semester : Semester.values()) {
                    System.out.println("- " + semester);
                }
                System.out.print("Enter semester: ");
                String semesterStr = scanner.nextLine();
                Semester semester = Semester.valueOf(semesterStr.replace(" ", "_"));
                results = courseService.searchBySemester(semester);
                break;
            case "4":
                System.out.print("Enter title to search: ");
                String title = scanner.nextLine();
                results = courseService.searchByTitle(title);
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        if (results.isEmpty()) {
            System.out.println("No courses found.");
        } else {
            System.out.println("Search Results:");
            results.forEach(System.out::println);
        }
    }

    private static void updateCourse() {
        System.out.print("Enter course ID to update: ");
        String id = scanner.nextLine();
        Course course = courseService.getCourseById(id);
        
        if (course == null) {
            System.out.println("Course not found.");
            return;
        }
        
        System.out.print("Enter new title: ");
        String title = scanner.nextLine();
        System.out.print("Enter new description: ");
        String description = scanner.nextLine();
        System.out.print("Enter new credits: ");
        int credits = Integer.parseInt(scanner.nextLine());
        
        if (courseService.updateCourse(id, title, description, credits)) {
            System.out.println("Course updated successfully.");
        } else {
            System.out.println("Failed to update course.");
        }
    }

    private static void assignInstructor() {
        System.out.print("Enter course ID: ");
        String courseId = scanner.nextLine();
        System.out.print("Enter instructor ID: ");
        String instructorId = scanner.nextLine();
        
        if (courseService.assignInstructor(courseId, instructorId)) {
            System.out.println("Instructor assigned successfully.");
        } else {
            System.out.println("Course not found.");
        }
    }

    private static void sortCourses() {
        System.out.println("Sort by:");
        System.out.println("1. Course Code");
        System.out.println("2. Title");
        System.out.println("3. Credits");
        System.out.print("Choose option (1-3): ");
        
        String choice = scanner.nextLine();
        Course[] sortedCourses = null;
        
        switch (choice) {
            case "1":
                sortedCourses = courseService.getCoursesSortedByCode();
                break;
            case "2":
                sortedCourses = courseService.getCoursesSortedByTitle();
                break;
            case "3":
                sortedCourses = courseService.getCoursesSortedByCredits();
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        System.out.println("Sorted Courses:");
        for (Course course : sortedCourses) {
            System.out.println(course);
        }
    }

    // Enrollment Management
    private static void manageEnrollments() {
        boolean inEnrollmentMenu = true;
        while (inEnrollmentMenu) {
            System.out.println();
            System.out.println("=== Enrollment Management ===");
            System.out.println("1. Enroll Student");
            System.out.println("2. Unenroll Student");
            System.out.println("3. List Enrollments");
            System.out.println("4. Record Grade");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose an option (1-5): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    enrollStudent();
                    break;
                case "2":
                    unenrollStudent();
                    break;
                case "3":
                    listEnrollments();
                    break;
                case "4":
                    recordGrade();
                    break;
                case "5":
                    inEnrollmentMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void enrollStudent() {
        try {
            System.out.print("Enter student ID: ");
            String studentId = scanner.nextLine();
            System.out.print("Enter course code (e.g., CSE101): ");
            String courseCodeStr = scanner.nextLine();
            CourseCode courseCode = CourseCode.parse(courseCodeStr);
            
            System.out.println("Available semesters:");
            for (Semester semester : Semester.values()) {
                System.out.println("- " + semester);
            }
            System.out.print("Enter semester: ");
            String semesterStr = scanner.nextLine();
            Semester semester = Semester.valueOf(semesterStr.replace(" ", "_"));

            Enrollment enrollment = enrollmentService.enrollStudent(studentId, courseCode, semester);
            System.out.println("Student enrolled successfully: " + enrollment);
        } catch (DuplicateEnrollmentException e) {
            System.err.println("Enrollment failed: " + e.getMessage());
        } catch (MaxCreditLimitExceededException e) {
            System.err.println("Enrollment failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error enrolling student: " + e.getMessage());
        }
    }

    private static void unenrollStudent() {
        System.out.print("Enter student ID: ");
        String studentId = scanner.nextLine();
        System.out.print("Enter course code (e.g., CSE101): ");
        String courseCodeStr = scanner.nextLine();
        CourseCode courseCode = CourseCode.parse(courseCodeStr);
        
        System.out.println("Available semesters:");
        for (Semester semester : Semester.values()) {
            System.out.println("- " + semester);
        }
        System.out.print("Enter semester: ");
        String semesterStr = scanner.nextLine();
        Semester semester = Semester.valueOf(semesterStr.replace(" ", "_"));

        if (enrollmentService.unenrollStudent(studentId, courseCode, semester)) {
            System.out.println("Student unenrolled successfully.");
        } else {
            System.out.println("Enrollment not found.");
        }
    }

    private static void listEnrollments() {
        System.out.println("List enrollments by:");
        System.out.println("1. Student");
        System.out.println("2. Course");
        System.out.println("3. Semester");
        System.out.print("Choose option (1-3): ");
        
        String choice = scanner.nextLine();
        List<Enrollment> enrollments = new ArrayList<>();
        
        switch (choice) {
            case "1":
                System.out.print("Enter student ID: ");
                String studentId = scanner.nextLine();
                enrollments = enrollmentService.getStudentEnrollments(studentId);
                break;
            case "2":
                System.out.print("Enter course code (e.g., CSE101): ");
                String courseCodeStr = scanner.nextLine();
                CourseCode courseCode = CourseCode.parse(courseCodeStr);
                System.out.print("Enter semester: ");
                String semesterStr = scanner.nextLine();
                Semester semester = Semester.valueOf(semesterStr.replace(" ", "_"));
                enrollments = enrollmentService.getCourseEnrollments(courseCode, semester);
                break;
            case "3":
                System.out.println("Available semesters:");
                for (Semester semester : Semester.values()) {
                    System.out.println("- " + semester);
                }
                System.out.print("Enter semester: ");
                String semesterStr2 = scanner.nextLine();
                Semester semester2 = Semester.valueOf(semesterStr2.replace(" ", "_"));
                enrollments = enrollmentService.getSemesterEnrollments(semester2);
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        if (enrollments.isEmpty()) {
            System.out.println("No enrollments found.");
        } else {
            System.out.println("Enrollments:");
            enrollments.forEach(System.out::println);
        }
    }

    private static void recordGrade() {
        System.out.print("Enter student ID: ");
        String studentId = scanner.nextLine();
        System.out.print("Enter course code (e.g., CSE101): ");
        String courseCodeStr = scanner.nextLine();
        CourseCode courseCode = CourseCode.parse(courseCodeStr);
        
        System.out.println("Available semesters:");
        for (Semester semester : Semester.values()) {
            System.out.println("- " + semester);
        }
        System.out.print("Enter semester: ");
        String semesterStr = scanner.nextLine();
        Semester semester = Semester.valueOf(semesterStr.replace(" ", "_"));
        
        System.out.println("Grade by:");
        System.out.println("1. Percentage");
        System.out.println("2. Letter Grade");
        System.out.print("Choose option (1-2): ");
        
        String gradeChoice = scanner.nextLine();
        
        try {
            if ("1".equals(gradeChoice)) {
                System.out.print("Enter percentage score: ");
                double percentage = Double.parseDouble(scanner.nextLine());
                if (enrollmentService.recordGrade(studentId, courseCode, semester, percentage)) {
                    System.out.println("Grade recorded successfully.");
                } else {
                    System.out.println("Enrollment not found.");
                }
            } else if ("2".equals(gradeChoice)) {
                System.out.println("Available grades:");
                for (Grade grade : Grade.values()) {
                    System.out.println("- " + grade);
                }
                System.out.print("Enter grade: ");
                String gradeStr = scanner.nextLine();
                Grade grade = Grade.valueOf(gradeStr);
                if (enrollmentService.recordGrade(studentId, courseCode, semester, grade)) {
                    System.out.println("Grade recorded successfully.");
                } else {
                    System.out.println("Enrollment not found.");
                }
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (Exception e) {
            System.err.println("Error recording grade: " + e.getMessage());
        }
    }

    // Grades and Transcript Management
    private static void manageGradesAndTranscripts() {
        boolean inGradeMenu = true;
        while (inGradeMenu) {
            System.out.println();
            System.out.println("=== Grades & Transcript ===");
            System.out.println("1. Generate Student Transcript");
            System.out.println("2. Generate Semester Transcript");
            System.out.println("3. View GPA Distribution");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose an option (1-4): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    generateStudentTranscript();
                    break;
                case "2":
                    generateSemesterTranscript();
                    break;
                case "3":
                    viewGPADistribution();
                    break;
                case "4":
                    inGradeMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void generateStudentTranscript() {
        System.out.print("Enter student ID: ");
        String studentId = scanner.nextLine();
        
        try {
            Transcript transcript = transcriptService.generateTranscript(studentId);
            System.out.println(transcript);
        } catch (Exception e) {
            System.err.println("Error generating transcript: " + e.getMessage());
        }
    }

    private static void generateSemesterTranscript() {
        System.out.print("Enter student ID: ");
        String studentId = scanner.nextLine();
        
        System.out.println("Available semesters:");
        for (Semester semester : Semester.values()) {
            System.out.println("- " + semester);
        }
        System.out.print("Enter semester: ");
        String semesterStr = scanner.nextLine();
        Semester semester = Semester.valueOf(semesterStr.replace(" ", "_"));
        
        try {
            Transcript transcript = transcriptService.generateSemesterTranscript(studentId, semester);
            System.out.println(transcript);
        } catch (Exception e) {
            System.err.println("Error generating transcript: " + e.getMessage());
        }
    }

    private static void viewGPADistribution() {
        System.out.println(transcriptService.getGPADistribution());
    }

    // Import/Export Management
    private static void manageImportExport() {
        boolean inFileMenu = true;
        while (inFileMenu) {
            System.out.println();
            System.out.println("=== Import/Export Data ===");
            System.out.println("1. Import Students from CSV");
            System.out.println("2. Import Courses from CSV");
            System.out.println("3. Export Students to CSV");
            System.out.println("4. Export Courses to CSV");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose an option (1-5): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    importStudents();
                    break;
                case "2":
                    importCourses();
                    break;
                case "3":
                    exportStudents();
                    break;
                case "4":
                    exportCourses();
                    break;
                case "5":
                    inFileMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void importStudents() {
        System.out.print("Enter CSV filename (e.g., students.csv): ");
        String filename = scanner.nextLine();
        
        try {
            List<Student> students = fileService.importStudentsFromCSV(filename);
            for (Student student : students) {
                studentService.addStudent(student.getRegNo(), student.getFullName(), student.getEmail());
            }
            System.out.println("Imported " + students.size() + " students successfully.");
        } catch (IOException e) {
            System.err.println("Error importing students: " + e.getMessage());
        }
    }

    private static void importCourses() {
        System.out.print("Enter CSV filename (e.g., courses.csv): ");
        String filename = scanner.nextLine();
        
        try {
            List<Course> courses = fileService.importCoursesFromCSV(filename);
            for (Course course : courses) {
                courseService.addCourse(course.getCourseCode(), course.getTitle(), 
                        course.getDepartment(), course.getDescription(), 
                        course.getCredits(), course.getInstructorId(), course.getSemester());
            }
            System.out.println("Imported " + courses.size() + " courses successfully.");
        } catch (IOException e) {
            System.err.println("Error importing courses: " + e.getMessage());
        }
    }

    private static void exportStudents() {
        System.out.print("Enter output filename (e.g., students_export.csv): ");
        String filename = scanner.nextLine();
        
        try {
            List<Student> students = studentService.getAllStudents();
            fileService.exportStudentsToCSV(students, filename);
            System.out.println("Exported " + students.size() + " students successfully.");
        } catch (IOException e) {
            System.err.println("Error exporting students: " + e.getMessage());
        }
    }

    private static void exportCourses() {
        System.out.print("Enter output filename (e.g., courses_export.csv): ");
        String filename = scanner.nextLine();
        
        try {
            List<Course> courses = courseService.getAllCourses();
            fileService.exportCoursesToCSV(courses, filename);
            System.out.println("Exported " + courses.size() + " courses successfully.");
        } catch (IOException e) {
            System.err.println("Error exporting courses: " + e.getMessage());
        }
    }

    // Backup Management
    private static void manageBackup() {
        boolean inBackupMenu = true;
        while (inBackupMenu) {
            System.out.println();
            System.out.println("=== Backup Management ===");
            System.out.println("1. Create Backup");
            System.out.println("2. List Backups");
            System.out.println("3. Show Backup Size");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose an option (1-4): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    createBackup();
                    break;
                case "2":
                    listBackups();
                    break;
                case "3":
                    showBackupSize();
                    break;
                case "4":
                    inBackupMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void createBackup() {
        try {
            Path backupPath = fileService.createBackup();
            long size = fileService.calculateBackupSize(backupPath);
            System.out.println("Backup created successfully at: " + backupPath);
            System.out.println("Backup size: " + fileService.formatFileSize(size));
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
        }
    }

    private static void listBackups() {
        try {
            List<Path> backups = fileService.listBackups();
            if (backups.isEmpty()) {
                System.out.println("No backups found.");
            } else {
                System.out.println("Available Backups:");
                for (Path backup : backups) {
                    long size = fileService.calculateBackupSize(backup);
                    System.out.println("- " + backup.getFileName() + " (" + fileService.formatFileSize(size) + ")");
                }
            }
        } catch (IOException e) {
            System.err.println("Error listing backups: " + e.getMessage());
        }
    }

    private static void showBackupSize() {
        try {
            long totalSize = fileService.calculateTotalBackupSize();
            System.out.println("Total backup size: " + fileService.formatFileSize(totalSize));
        } catch (IOException e) {
            System.err.println("Error calculating backup size: " + e.getMessage());
        }
    }

    // Reports
    private static void generateReports() {
        boolean inReportMenu = true;
        while (inReportMenu) {
            System.out.println();
            System.out.println("=== Reports ===");
            System.out.println("1. Student Statistics");
            System.out.println("2. Top Students");
            System.out.println("3. GPA Distribution");
            System.out.println("4. Semester Statistics");
            System.out.println("5. Course Popularity");
            System.out.println("6. Back to Main Menu");
            System.out.print("Choose an option (1-6): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    showStudentStatistics();
                    break;
                case "2":
                    showTopStudents();
                    break;
                case "3":
                    showGPADistribution();
                    break;
                case "4":
                    showSemesterStatistics();
                    break;
                case "5":
                    showCoursePopularity();
                    break;
                case "6":
                    inReportMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void showStudentStatistics() {
        Student.Statistics stats = studentService.getStatistics();
        System.out.println("Student Statistics:");
        System.out.println(stats);
    }

    private static void showTopStudents() {
        System.out.print("Enter number of top students to show: ");
        int limit = Integer.parseInt(scanner.nextLine());
        
        List<Student> topStudents = enrollmentService.getTopStudents(limit);
        System.out.println("Top " + limit + " Students:");
        
        // Enhanced for loop demonstration
        int rank = 1;
        for (Student student : topStudents) {
            System.out.println(rank + ". " + student.getFullName() + " - GPA: " + student.getGpa());
            rank++;
        }
    }

    private static void showGPADistribution() {
        System.out.println(transcriptService.getGPADistribution());
    }

    private static void showSemesterStatistics() {
        System.out.println(transcriptService.getSemesterStatistics());
    }

    private static void showCoursePopularity() {
        System.out.println(transcriptService.getCoursePopularityStats());
    }

    // Advanced Java concepts demonstration
    private static void demonstrateAdvancedConcepts() {
        System.out.println("\n=== Advanced Java Concepts Demo ===");
        
        // Do-while loop demonstration
        int attempts = 0;
        do {
            System.out.print("Enter a number between 1-10 (attempt " + (attempts + 1) + "): ");
            try {
                int number = Integer.parseInt(scanner.nextLine());
                if (number < 1 || number > 10) {
                    System.out.println("Number must be between 1-10. Try again.");
                    attempts++;
                    continue; // Continue statement demonstration
                }
                System.out.println("Valid number entered: " + number);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                attempts++;
            }
        } while (attempts < 3);
        
        // Upcast and downcast demonstration
        System.out.println("\n--- Upcast/Downcast Demo ---");
        List<Person> people = new ArrayList<>();
        people.add(new Student("STU001", "REG001", "John Doe", "john@example.com"));
        people.add(new Instructor("INST001", "Dr. Smith", "smith@example.com", "CS", "Professor"));
        
        for (Person person : people) {
            // Upcast: Person reference to access common methods
            System.out.println("Person: " + person.getDisplayInfo());
            
            // Downcast with instanceof check
            if (person instanceof Student) {
                Student student = (Student) person; // Downcast
                System.out.println("  Student GPA: " + student.getGpa());
                System.out.println("  Student Reg No: " + student.getRegNo());
            } else if (person instanceof Instructor) {
                Instructor instructor = (Instructor) person; // Downcast
                System.out.println("  Instructor Department: " + instructor.getDepartment());
                System.out.println("  Instructor Title: " + instructor.getTitle());
            }
        }
        
        // Anonymous inner class demonstration
        System.out.println("\n--- Anonymous Inner Class Demo ---");
        List<String> courseNames = Arrays.asList("Java Programming", "Data Structures", "Algorithms");
        
        // Anonymous inner class for custom sorting
        courseNames.sort(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.length() - s2.length(); // Sort by length
            }
        });
        
        System.out.println("Courses sorted by length:");
        for (String course : courseNames) {
            System.out.println("  " + course);
        }
        
        // Multi-catch exception handling demonstration
        System.out.println("\n--- Multi-catch Exception Demo ---");
        try {
            // Simulate different types of exceptions
            String testString = null;
            if (testString == null) {
                throw new NullPointerException("Test null pointer");
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            System.out.println("Caught exception: " + e.getClass().getSimpleName());
            System.out.println("Message: " + e.getMessage());
        } finally {
            System.out.println("Finally block executed - cleanup code here");
        }
        
        // Demonstrate primitive variables and operators
        demonstratePrimitivesAndOperators();
        
        System.out.println("\nAdvanced concepts demonstration completed!");
    }

    // Demonstration of primitive variables and operator precedence
    private static void demonstratePrimitivesAndOperators() {
        System.out.println("\n=== Primitive Variables & Operator Precedence Demo ===");
        
        // Primitive variables demonstration
        byte byteVar = 127;                    // 8-bit signed integer
        short shortVar = 32000;                // 16-bit signed integer
        int intVar = 1000000;                  // 32-bit signed integer
        long longVar = 1000000000L;            // 64-bit signed integer
        float floatVar = 3.14f;                // 32-bit floating point
        double doubleVar = 3.14159265359;      // 64-bit floating point
        char charVar = 'A';                    // 16-bit Unicode character
        boolean booleanVar = true;             // true or false
        
        System.out.println("Primitive variables:");
        System.out.println("  byte: " + byteVar);
        System.out.println("  short: " + shortVar);
        System.out.println("  int: " + intVar);
        System.out.println("  long: " + longVar);
        System.out.println("  float: " + floatVar);
        System.out.println("  double: " + doubleVar);
        System.out.println("  char: " + charVar);
        System.out.println("  boolean: " + booleanVar);
        
        // Operator precedence demonstration
        System.out.println("\nOperator Precedence Demo:");
        int a = 5, b = 3, c = 2;
        
        // Without parentheses - follows operator precedence
        int result1 = a + b * c;  // Multiplication first: 5 + (3 * 2) = 11
        System.out.println("a + b * c = " + result1 + " (multiplication first)");
        
        // With parentheses - explicit precedence
        int result2 = (a + b) * c;  // Addition first: (5 + 3) * 2 = 16
        System.out.println("(a + b) * c = " + result2 + " (addition first)");
        
        // Complex expression demonstrating precedence
        int result3 = a * b + c * a - b / c;  // *, / first, then +, -
        System.out.println("a * b + c * a - b / c = " + result3);
        
        // Bitwise operators demonstration
        System.out.println("\nBitwise Operators Demo:");
        int x = 12;  // Binary: 1100
        int y = 10;  // Binary: 1010
        
        System.out.println("x = " + x + " (binary: " + Integer.toBinaryString(x) + ")");
        System.out.println("y = " + y + " (binary: " + Integer.toBinaryString(y) + ")");
        System.out.println("x & y = " + (x & y) + " (AND)");
        System.out.println("x | y = " + (x | y) + " (OR)");
        System.out.println("x ^ y = " + (x ^ y) + " (XOR)");
        System.out.println("~x = " + (~x) + " (NOT)");
        System.out.println("x << 2 = " + (x << 2) + " (left shift)");
        System.out.println("x >> 2 = " + (x >> 2) + " (right shift)");
        
        // Logical operators demonstration
        System.out.println("\nLogical Operators Demo:");
        boolean p = true, q = false;
        System.out.println("p = " + p + ", q = " + q);
        System.out.println("p && q = " + (p && q) + " (logical AND)");
        System.out.println("p || q = " + (p || q) + " (logical OR)");
        System.out.println("!p = " + (!p) + " (logical NOT)");
        System.out.println("p ^ q = " + (p ^ q) + " (logical XOR)");
        
        // Relational operators demonstration
        System.out.println("\nRelational Operators Demo:");
        System.out.println("a == b: " + (a == b));
        System.out.println("a != b: " + (a != b));
        System.out.println("a > b: " + (a > b));
        System.out.println("a < b: " + (a < b));
        System.out.println("a >= b: " + (a >= b));
        System.out.println("a <= b: " + (a <= b));
        
        System.out.println("\nPrimitive variables and operators demonstration completed!");
    }
}


