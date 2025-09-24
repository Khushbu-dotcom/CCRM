package edu.ccrm.model;

import edu.ccrm.model.enums.Semester;
import edu.ccrm.model.interfaces.Persistable;
import edu.ccrm.model.value.CourseCode;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Course entity with Builder pattern.
 * Demonstrates Builder pattern, encapsulation, and value objects.
 */
public class Course implements Persistable {
    private String id;
    private CourseCode courseCode;
    private String title;
    private String description;
    private int credits;
    private String department;
    private String instructorId;
    private Semester semester;
    private LocalDateTime createdAt;
    private boolean active;

    // Private constructor for Builder pattern
    private Course(Builder builder) {
        this.id = builder.id;
        this.courseCode = builder.courseCode;
        this.title = builder.title;
        this.description = builder.description;
        this.credits = builder.credits;
        this.department = builder.department;
        this.instructorId = builder.instructorId;
        this.semester = builder.semester;
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    // Builder class
    public static class Builder {
        private String id;
        private CourseCode courseCode;
        private String title;
        private String description = "";
        private int credits = 3;
        private String department;
        private String instructorId;
        private Semester semester = Semester.FALL_2025;

        public Builder(String id, CourseCode courseCode, String title, String department) {
            this.id = Objects.requireNonNull(id, "ID cannot be null");
            this.courseCode = Objects.requireNonNull(courseCode, "Course code cannot be null");
            this.title = Objects.requireNonNull(title, "Title cannot be null");
            this.department = Objects.requireNonNull(department, "Department cannot be null");
        }

        public Builder description(String description) {
            this.description = description != null ? description : "";
            return this;
        }

        public Builder credits(int credits) {
            if (credits <= 0) {
                throw new IllegalArgumentException("Credits must be positive");
            }
            this.credits = credits;
            return this;
        }

        public Builder instructorId(String instructorId) {
            this.instructorId = instructorId;
            return this;
        }

        public Builder semester(Semester semester) {
            this.semester = Objects.requireNonNull(semester, "Semester cannot be null");
            return this;
        }

        public Course build() {
            return new Course(this);
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public CourseCode getCourseCode() {
        return courseCode;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getCredits() {
        return credits;
    }

    public String getDepartment() {
        return department;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public Semester getSemester() {
        return semester;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return active;
    }

    // Setters
    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "Title cannot be null");
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public void setCredits(int credits) {
        if (credits <= 0) {
            throw new IllegalArgumentException("Credits must be positive");
        }
        this.credits = credits;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public void setSemester(Semester semester) {
        this.semester = Objects.requireNonNull(semester, "Semester cannot be null");
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toCSV() {
        return String.format("%s,%s,%s,%s,%d,%s,%s,%s,%s", 
                           id, courseCode.toString(), title, description, credits, 
                           department, instructorId != null ? instructorId : "", 
                           semester.toString(), active ? "ACTIVE" : "INACTIVE");
    }

    public static Course fromCSV(String csvData) {
        String[] fields = csvData.split(",");
        if (fields.length < 9) {
            throw new IllegalArgumentException("Invalid CSV data for Course");
        }
        
        CourseCode courseCode = CourseCode.parse(fields[1]);
        Course course = new Builder(fields[0], courseCode, fields[2], fields[5])
                .description(fields[3])
                .credits(Integer.parseInt(fields[4]))
                .instructorId(fields[6].isEmpty() ? null : fields[6])
                .semester(Semester.valueOf(fields[7].replace(" ", "_")))
                .build();
        
        course.setActive("ACTIVE".equals(fields[8]));
        return course;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Course{id='%s', courseCode=%s, title='%s', credits=%d, " +
                           "department='%s', instructorId='%s', semester=%s, active=%s}", 
                           id, courseCode, title, credits, department, instructorId, semester, active);
    }
}
