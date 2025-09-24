package edu.ccrm.model;

import edu.ccrm.model.interfaces.Persistable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Instructor entity extending Person.
 * Demonstrates inheritance and encapsulation.
 */
public class Instructor extends Person implements Persistable {
    private String department;
    private String title;
    private List<String> assignedCourses;

    public Instructor(String id, String fullName, String email, String department, String title) {
        super(id, fullName, email);
        this.department = Objects.requireNonNull(department, "Department cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.assignedCourses = new ArrayList<>();
    }

    // Getters
    public String getDepartment() {
        return department;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getAssignedCourses() {
        return new ArrayList<>(assignedCourses);
    }

    // Setters
    public void setDepartment(String department) {
        this.department = Objects.requireNonNull(department, "Department cannot be null");
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "Title cannot be null");
    }

    // Business methods
    public void assignCourse(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be null or empty");
        }
        if (!assignedCourses.contains(courseCode)) {
            assignedCourses.add(courseCode);
        }
    }

    public void unassignCourse(String courseCode) {
        assignedCourses.remove(courseCode);
    }

    public boolean isAssignedTo(String courseCode) {
        return assignedCourses.contains(courseCode);
    }

    @Override
    public String getPersonType() {
        return "Instructor";
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Instructor: %s %s (%s) - %s", title, fullName, department, email);
    }

    @Override
    public String toCSV() {
        StringBuilder courses = new StringBuilder();
        for (int i = 0; i < assignedCourses.size(); i++) {
            if (i > 0) courses.append(";");
            courses.append(assignedCourses.get(i));
        }
        
        return String.format("%s,%s,%s,%s,%s,%s,%s", 
                           id, fullName, email, department, title, 
                           active ? "ACTIVE" : "INACTIVE", courses.toString());
    }

    public static Instructor fromCSV(String csvData) {
        String[] fields = csvData.split(",");
        if (fields.length < 6) {
            throw new IllegalArgumentException("Invalid CSV data for Instructor");
        }
        
        Instructor instructor = new Instructor(fields[0], fields[1], fields[2], fields[3], fields[4]);
        instructor.setActive("ACTIVE".equals(fields[5]));
        
        if (fields.length > 6 && !fields[6].isEmpty()) {
            String[] courseCodes = fields[6].split(";");
            for (String courseCode : courseCodes) {
                if (!courseCode.trim().isEmpty()) {
                    instructor.assignCourse(courseCode.trim());
                }
            }
        }
        
        return instructor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Instructor that = (Instructor) obj;
        return Objects.equals(department, that.department) && 
               Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), department, title);
    }

    @Override
    public String toString() {
        return String.format("Instructor{id='%s', fullName='%s', department='%s', " +
                           "title='%s', assignedCourses=%d, active=%s}", 
                           id, fullName, department, title, assignedCourses.size(), active);
    }
}
