# 📖 CCRM Usage Guide

<div align="center">

![CCRM Logo](https://img.shields.io/badge/CCRM-Usage%20Guide-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=openjdk)
![Status](https://img.shields.io/badge/Status-Ready%20to%20Use-brightgreen?style=for-the-badge)

*Your comprehensive guide to mastering the Campus Course & Records Manager*

</div>

---

## 🚀 Getting Started

### 📋 Prerequisites Checklist

Before diving into CCRM, ensure you have:

- [ ] **Java Development Kit (JDK) 17+** installed and configured
- [ ] **Terminal/Command Prompt** access
- [ ] **Basic understanding** of command-line interfaces
- [ ] **Sample data files** (included in `test-data/` directory)

### ⚡ Quick Setup

#### Step 1: Compile the Application

<details>
<summary><b>🪟 Windows Users (PowerShell/CMD)</b></summary>

```powershell
# Navigate to your CCRM directory
cd C:\path\to\CCRM

# Compile all Java source files
javac -d out -cp src src/edu/ccrm/config/AppConfig.java src/edu/ccrm/cli/CCRM.java src/edu/ccrm/model/*.java src/edu/ccrm/model/enums/*.java src/edu/ccrm/model/interfaces/*.java src/edu/ccrm/model/value/*.java src/edu/ccrm/exceptions/*.java src/edu/ccrm/service/*.java

# Verify compilation (should show no errors)
echo "Compilation completed successfully!"
```

</details>

<details>
<summary><b>🐧 Linux/macOS Users (Terminal)</b></summary>

```bash
# Navigate to your CCRM directory
cd /path/to/CCRM

# Compile all Java source files
find src -name "*.java" | xargs javac -d out

# Verify compilation (should show no errors)
echo "Compilation completed successfully!"
```

</details>

#### Step 2: Launch the Application

```bash
# Run with assertions enabled (recommended for development)
java -ea -cp out edu.ccrm.cli.CCRM

# Alternative: Run without assertions (production mode)
java -cp out edu.ccrm.cli.CCRM
```

#### Step 3: Verify Installation

When the application starts, you should see:

```
Java Platforms: ME (embedded), SE (standard), EE (enterprise). Running SE.
JDK Version: 17.0.x
JVM: OpenJDK 64-Bit Server VM

=== CCRM Main Menu ===
1. Manage Students
2. Manage Courses
3. Enrollment
4. Grades & Transcript
5. Import/Export Data
6. Backup & Show Backup Size
7. Reports
8. Advanced Java Concepts Demo
9. AppConfig Info
10. Exit
Choose an option (1-10):
```

🎉 **Congratulations!** CCRM is now running successfully.

## Main Menu Options

### 1. Manage Students
- **Add Student**: Create new student records
- **List All Students**: View all students in the system
- **Search Students**: Find students by name, email, or registration number
- **Update Student**: Modify student information
- **Deactivate Student**: Mark student as inactive
- **View Student Profile**: Display detailed student information

### 2. Manage Courses
- **Add Course**: Create new course records using Builder pattern
- **List All Courses**: View all courses in the system
- **Search Courses**: Find courses by instructor, department, semester, or title
- **Update Course**: Modify course information
- **Assign Instructor**: Assign instructors to courses
- **Sort Courses**: Sort courses by code, title, or credits

### 3. Enrollment
- **Enroll Student**: Enroll students in courses with business rule validation
- **Unenroll Student**: Remove student enrollments
- **List Enrollments**: View enrollments by student, course, or semester
- **Record Grade**: Record grades by percentage or letter grade

### 4. Grades & Transcript
- **Generate Student Transcript**: Create complete academic transcript
- **Generate Semester Transcript**: Create semester-specific transcript
- **View GPA Distribution**: Display GPA statistics across all students

### 5. Import/Export Data
- **Import Students from CSV**: Load student data from CSV files
- **Import Courses from CSV**: Load course data from CSV files
- **Export Students to CSV**: Save student data to CSV files
- **Export Courses to CSV**: Save course data to CSV files

### 6. Backup & Show Backup Size
- **Create Backup**: Create timestamped backup of all data
- **List Backups**: View available backup directories
- **Show Backup Size**: Display total backup storage usage

### 7. Reports
- **Student Statistics**: View enrollment and GPA statistics
- **Top Students**: Display highest-performing students
- **GPA Distribution**: Show GPA distribution across students
- **Semester Statistics**: View enrollment statistics by semester
- **Course Popularity**: Display course enrollment statistics

## Sample Data

### Students CSV Format
```csv
id,regNo,fullName,email,status,enrolledCourseCodes,createdAt
1,REG2025-0001,Jane Doe,jane.doe@example.edu,ACTIVE,"CSE101;MAT101",2025-09-01T10:00:00
2,REG2025-0002,John Smith,john.smith@example.edu,ACTIVE,,2025-09-02T09:30:00
```

### Courses CSV Format
```csv
id,courseCode,title,description,credits,department,instructorId,semester,status
CRS0001,CSE101,Introduction to Computer Science,Basic programming concepts and problem solving,3,Computer Science,,FALL_2025,ACTIVE
CRS0002,MAT101,Calculus I,Introduction to differential calculus,4,Mathematics,,FALL_2025,ACTIVE
```

### Instructors CSV Format
```csv
id,fullName,email,department,title,status,assignedCourses
INST001,Dr. Sarah Johnson,sarah.johnson@university.edu,Computer Science,Professor,ACTIVE,CSE101;CSE102
INST002,Dr. Michael Chen,michael.chen@university.edu,Mathematics,Associate Professor,ACTIVE,MAT101
```

## Business Rules

### Enrollment Rules
- Maximum 18 credits per semester per student
- Students cannot enroll in the same course twice in the same semester
- Only active students can enroll in courses
- Only active courses can be enrolled in

### Grading Rules
- Grades are calculated from percentage scores (0-100)
- Letter grades: A+ (97+), A (93-96), A- (90-92), B+ (87-89), B (83-86), B- (80-82), C+ (77-79), C (73-76), C- (70-72), D+ (67-69), D (60-66), F (<60)
- Incomplete (I) and Withdrawal (W) grades do not count toward GPA
- GPA is calculated as weighted average of grade points

## Error Handling

### Common Exceptions
- **DuplicateEnrollmentException**: Student already enrolled in course
- **MaxCreditLimitExceededException**: Student exceeds credit limit
- **IllegalArgumentException**: Invalid input parameters
- **IOException**: File I/O errors

### Error Recovery
- All operations include comprehensive error handling
- User-friendly error messages are displayed
- System continues running after errors
- Data integrity is maintained

## Advanced Features

### Stream API Usage
- Student search and filtering
- Course sorting and searching
- GPA calculations and statistics
- Report generation

### Lambda Expressions
- Sorting comparators
- Predicate filtering
- Functional interface implementations

### NIO.2 File Operations
- Modern file I/O using Path and Files classes
- Recursive directory operations
- Backup creation and management

### Design Patterns
- **Singleton**: AppConfig for configuration management
- **Builder**: Course creation with optional parameters
- **Strategy**: Different search and sort strategies

## Troubleshooting

### Compilation Issues
- Ensure JDK 17+ is installed and on PATH
- Check that all source files are in correct package structure
- Verify classpath includes all dependencies

### Runtime Issues
- Enable assertions with `-ea` flag
- Check file permissions for data directory
- Ensure CSV files are properly formatted

### Performance Considerations
- Large datasets may require increased heap size
- Use `-Xmx` flag for memory-intensive operations
- Consider data pagination for very large result sets

## Best Practices

### Data Management
- Regular backups of important data
- Validate input data before processing
- Use transactions for related operations

### Code Organization
- Follow package naming conventions
- Use meaningful variable and method names
- Include comprehensive error handling

### Testing
- Test with sample data before production use
- Verify business rule enforcement
- Test error conditions and edge cases