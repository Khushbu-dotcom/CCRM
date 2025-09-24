package edu.ccrm.service;

import edu.ccrm.model.Course;
import edu.ccrm.model.Instructor;
import edu.ccrm.model.enums.Semester;
import edu.ccrm.model.interfaces.Searchable;
import edu.ccrm.model.value.CourseCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class for course management operations.
 * Demonstrates service layer pattern, Stream API, and Arrays utilities.
 */
public class CourseService implements Searchable<Course> {
    private final List<Course> courses;
    private int nextId;

    public CourseService() {
        this.courses = new ArrayList<>();
        this.nextId = 1;
    }

    // CRUD operations
    public Course addCourse(CourseCode courseCode, String title, String department, 
                           String description, int credits, String instructorId, Semester semester) {
        String id = "CRS" + String.format("%04d", nextId++);
        Course course = new Course.Builder(id, courseCode, title, department)
                .description(description)
                .credits(credits)
                .instructorId(instructorId)
                .semester(semester)
                .build();
        courses.add(course);
        return course;
    }

    public Course getCourseById(String id) {
        return courses.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Course getCourseByCode(CourseCode courseCode) {
        return courses.stream()
                .filter(c -> c.getCourseCode().equals(courseCode))
                .findFirst()
                .orElse(null);
    }

    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }

    public List<Course> getActiveCourses() {
        return courses.stream()
                .filter(Course::isActive)
                .collect(Collectors.toList());
    }

    public boolean updateCourse(String id, String title, String description, int credits) {
        Course course = getCourseById(id);
        if (course != null) {
            course.setTitle(title);
            course.setDescription(description);
            course.setCredits(credits);
            return true;
        }
        return false;
    }

    public boolean assignInstructor(String courseId, String instructorId) {
        Course course = getCourseById(courseId);
        if (course != null) {
            course.setInstructorId(instructorId);
            return true;
        }
        return false;
    }

    public boolean deactivateCourse(String id) {
        Course course = getCourseById(id);
        if (course != null) {
            course.setActive(false);
            return true;
        }
        return false;
    }

    public boolean activateCourse(String id) {
        Course course = getCourseById(id);
        if (course != null) {
            course.setActive(true);
            return true;
        }
        return false;
    }

    // Search operations
    @Override
    public Course[] search(Predicate<Course> predicate) {
        return courses.stream()
                .filter(predicate)
                .toArray(Course[]::new);
    }

    public List<Course> searchByInstructor(String instructorId) {
        return courses.stream()
                .filter(c -> instructorId.equals(c.getInstructorId()))
                .collect(Collectors.toList());
    }

    public List<Course> searchByDepartment(String department) {
        return courses.stream()
                .filter(c -> c.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    public List<Course> searchBySemester(Semester semester) {
        return courses.stream()
                .filter(c -> c.getSemester().equals(semester))
                .collect(Collectors.toList());
    }

    public List<Course> searchByTitle(String title) {
        return courses.stream()
                .filter(c -> c.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Sorting operations using Arrays utilities
    public Course[] getCoursesSortedByCode() {
        Course[] courseArray = courses.toArray(new Course[0]);
        Arrays.sort(courseArray, (c1, c2) -> 
            c1.getCourseCode().toString().compareTo(c2.getCourseCode().toString()));
        return courseArray;
    }

    public Course[] getCoursesSortedByTitle() {
        Course[] courseArray = courses.toArray(new Course[0]);
        Arrays.sort(courseArray, (c1, c2) -> 
            c1.getTitle().compareTo(c2.getTitle()));
        return courseArray;
    }

    public Course[] getCoursesSortedByCredits() {
        Course[] courseArray = courses.toArray(new Course[0]);
        Arrays.sort(courseArray, (c1, c2) -> Integer.compare(c1.getCredits(), c2.getCredits()));
        return courseArray;
    }

    // Advanced search with multiple criteria
    public List<Course> searchByMultipleCriteria(String instructorId, String department, 
                                                Semester semester, int minCredits) {
        return courses.stream()
                .filter(c -> instructorId == null || instructorId.equals(c.getInstructorId()))
                .filter(c -> department == null || c.getDepartment().equalsIgnoreCase(department))
                .filter(c -> semester == null || c.getSemester().equals(semester))
                .filter(c -> c.getCredits() >= minCredits)
                .collect(Collectors.toList());
    }

    // Statistics
    public int getCourseCount() {
        return courses.size();
    }

    public int getActiveCourseCount() {
        return (int) courses.stream().filter(Course::isActive).count();
    }

    public double getAverageCredits() {
        return courses.stream()
                .mapToInt(Course::getCredits)
                .average()
                .orElse(0.0);
    }

    // Data management
    public void clearAllCourses() {
        courses.clear();
        nextId = 1;
    }
}
