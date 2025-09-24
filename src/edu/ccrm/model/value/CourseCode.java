package edu.ccrm.model.value;

import java.util.Objects;

/**
 * Immutable value class representing a course code.
 * Demonstrates immutable value objects and proper equals/hashCode implementation.
 */
public final class CourseCode {
    private final String department;
    private final String number;

    public CourseCode(String department, String number) {
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be null or empty");
        }
        if (number == null || number.trim().isEmpty()) {
            throw new IllegalArgumentException("Number cannot be null or empty");
        }
        this.department = department.trim().toUpperCase();
        this.number = number.trim();
    }

    public String getDepartment() {
        return department;
    }

    public String getNumber() {
        return number;
    }

    /**
     * Parse a course code from string format (e.g., "CSE101").
     */
    public static CourseCode parse(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be null or empty");
        }
        
        String trimmed = courseCode.trim().toUpperCase();
        int i = 0;
        while (i < trimmed.length() && Character.isLetter(trimmed.charAt(i))) {
            i++;
        }
        
        if (i == 0 || i >= trimmed.length()) {
            throw new IllegalArgumentException("Invalid course code format: " + courseCode);
        }
        
        String department = trimmed.substring(0, i);
        String number = trimmed.substring(i);
        
        return new CourseCode(department, number);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CourseCode that = (CourseCode) obj;
        return Objects.equals(department, that.department) && 
               Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(department, number);
    }

    @Override
    public String toString() {
        return department + number;
    }
}
