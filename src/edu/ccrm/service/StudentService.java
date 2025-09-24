package edu.ccrm.service;

import edu.ccrm.model.Student;
import edu.ccrm.model.interfaces.Searchable;
import edu.ccrm.model.value.CourseCode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class for student management operations.
 * Demonstrates service layer pattern and Stream API usage.
 */
public class StudentService implements Searchable<Student> {
    private final List<Student> students;
    private int nextId;

    public StudentService() {
        this.students = new ArrayList<>();
        this.nextId = 1;
    }

    // CRUD operations
    public Student addStudent(String regNo, String fullName, String email) {
        String id = "STU" + String.format("%04d", nextId++);
        Student student = new Student(id, regNo, fullName, email);
        students.add(student);
        return student;
    }

    public Student getStudentById(String id) {
        return students.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Student getStudentByRegNo(String regNo) {
        return students.stream()
                .filter(s -> s.getRegNo().equals(regNo))
                .findFirst()
                .orElse(null);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    public List<Student> getActiveStudents() {
        return students.stream()
                .filter(Student::isActive)
                .collect(Collectors.toList());
    }

    public boolean updateStudent(String id, String fullName, String email) {
        Student student = getStudentById(id);
        if (student != null) {
            student.setFullName(fullName);
            student.setEmail(email);
            return true;
        }
        return false;
    }

    public boolean deactivateStudent(String id) {
        Student student = getStudentById(id);
        if (student != null) {
            student.setActive(false);
            return true;
        }
        return false;
    }

    public boolean activateStudent(String id) {
        Student student = getStudentById(id);
        if (student != null) {
            student.setActive(true);
            return true;
        }
        return false;
    }

    // Search operations
    @Override
    public Student[] search(Predicate<Student> predicate) {
        return students.stream()
                .filter(predicate)
                .toArray(Student[]::new);
    }

    public List<Student> searchByName(String name) {
        return students.stream()
                .filter(s -> s.getFullName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Student> searchByEmail(String email) {
        return students.stream()
                .filter(s -> s.getEmail().toLowerCase().contains(email.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Student> searchByCourse(CourseCode courseCode) {
        return students.stream()
                .filter(s -> s.isEnrolledIn(courseCode))
                .collect(Collectors.toList());
    }

    // Statistics
    public Student.Statistics getStatistics() {
        int totalStudents = students.size();
        int activeStudents = (int) students.stream().filter(Student::isActive).count();
        double averageGPA = students.stream()
                .filter(Student::isActive)
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0.0);
        
        return new Student.Statistics(totalStudents, averageGPA, activeStudents);
    }

    // Course enrollment
    public boolean enrollStudentInCourse(String studentId, CourseCode courseCode) {
        Student student = getStudentById(studentId);
        if (student != null && student.isActive()) {
            student.enrollInCourse(courseCode);
            return true;
        }
        return false;
    }

    public boolean unenrollStudentFromCourse(String studentId, CourseCode courseCode) {
        Student student = getStudentById(studentId);
        if (student != null) {
            student.unenrollFromCourse(courseCode);
            return true;
        }
        return false;
    }

    // Data management
    public void clearAllStudents() {
        students.clear();
        nextId = 1;
    }

    public int getStudentCount() {
        return students.size();
    }
}
