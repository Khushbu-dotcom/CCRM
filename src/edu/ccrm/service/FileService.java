package edu.ccrm.service;

import edu.ccrm.model.Course;
import edu.ccrm.model.Instructor;
import edu.ccrm.model.Student;
import edu.ccrm.model.interfaces.Persistable;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Service class for file operations including import/export and backup.
 * Demonstrates NIO.2 usage, file I/O, and recursive operations.
 */
public class FileService {
    private final Path dataDirectory;
    private final Path backupDirectory;

    public FileService(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.backupDirectory = dataDirectory.resolve("backups");
        createDirectoriesIfNotExist();
    }

    private void createDirectoriesIfNotExist() {
        try {
            Files.createDirectories(dataDirectory);
            Files.createDirectories(backupDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directories", e);
        }
    }

    // Import operations
    public List<Student> importStudentsFromCSV(String filename) throws IOException {
        Path filePath = dataDirectory.resolve(filename);
        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + filename);
        }

        List<Student> students = new ArrayList<>();
        List<String> lines = Files.readAllLines(filePath);
        
        // Skip header line
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (!line.isEmpty()) {
                try {
                    Student student = Student.fromCSV(line);
                    students.add(student);
                } catch (Exception e) {
                    System.err.println("Error parsing student data: " + line + " - " + e.getMessage());
                }
            }
        }
        
        return students;
    }

    public List<Course> importCoursesFromCSV(String filename) throws IOException {
        Path filePath = dataDirectory.resolve(filename);
        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + filename);
        }

        List<Course> courses = new ArrayList<>();
        List<String> lines = Files.readAllLines(filePath);
        
        // Skip header line
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (!line.isEmpty()) {
                try {
                    Course course = Course.fromCSV(line);
                    courses.add(course);
                } catch (Exception e) {
                    System.err.println("Error parsing course data: " + line + " - " + e.getMessage());
                }
            }
        }
        
        return courses;
    }

    public List<Instructor> importInstructorsFromCSV(String filename) throws IOException {
        Path filePath = dataDirectory.resolve(filename);
        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + filename);
        }

        List<Instructor> instructors = new ArrayList<>();
        List<String> lines = Files.readAllLines(filePath);
        
        // Skip header line
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (!line.isEmpty()) {
                try {
                    Instructor instructor = Instructor.fromCSV(line);
                    instructors.add(instructor);
                } catch (Exception e) {
                    System.err.println("Error parsing instructor data: " + line + " - " + e.getMessage());
                }
            }
        }
        
        return instructors;
    }

    // Export operations
    public void exportStudentsToCSV(List<Student> students, String filename) throws IOException {
        Path filePath = dataDirectory.resolve(filename);
        List<String> lines = new ArrayList<>();
        
        // Add header
        lines.add("id,regNo,fullName,email,status,enrolledCourseCodes,createdAt");
        
        // Add data
        for (Student student : students) {
            lines.add(student.toCSV());
        }
        
        Files.write(filePath, lines);
    }

    public void exportCoursesToCSV(List<Course> courses, String filename) throws IOException {
        Path filePath = dataDirectory.resolve(filename);
        List<String> lines = new ArrayList<>();
        
        // Add header
        lines.add("id,courseCode,title,description,credits,department,instructorId,semester,status");
        
        // Add data
        for (Course course : courses) {
            lines.add(course.toCSV());
        }
        
        Files.write(filePath, lines);
    }

    public void exportInstructorsToCSV(List<Instructor> instructors, String filename) throws IOException {
        Path filePath = dataDirectory.resolve(filename);
        List<String> lines = new ArrayList<>();
        
        // Add header
        lines.add("id,fullName,email,department,title,status,assignedCourses");
        
        // Add data
        for (Instructor instructor : instructors) {
            lines.add(instructor.toCSV());
        }
        
        Files.write(filePath, lines);
    }

    // Backup operations
    public Path createBackup() throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path backupPath = backupDirectory.resolve("backup_" + timestamp);
        Files.createDirectories(backupPath);
        
        // Copy all CSV files to backup
        try (Stream<Path> files = Files.list(dataDirectory)) {
            files.filter(path -> path.toString().endsWith(".csv"))
                 .forEach(csvFile -> {
                     try {
                         Files.copy(csvFile, backupPath.resolve(csvFile.getFileName()));
                     } catch (IOException e) {
                         System.err.println("Error copying file: " + csvFile + " - " + e.getMessage());
                     }
                 });
        }
        
        return backupPath;
    }

    public long calculateBackupSize(Path backupPath) throws IOException {
        return calculateDirectorySize(backupPath);
    }

    public long calculateTotalBackupSize() throws IOException {
        return calculateDirectorySize(backupDirectory);
    }

    // Recursive utility method
    private long calculateDirectorySize(Path directory) throws IOException {
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            return 0;
        }

        try (Stream<Path> paths = Files.walk(directory)) {
            return paths.filter(Files::isRegularFile)
                       .mapToLong(this::getFileSize)
                       .sum();
        }
    }

    private long getFileSize(Path file) {
        try {
            return Files.size(file);
        } catch (IOException e) {
            System.err.println("Error getting file size: " + file + " - " + e.getMessage());
            return 0;
        }
    }

    // List backup directories
    public List<Path> listBackups() throws IOException {
        List<Path> backups = new ArrayList<>();
        try (Stream<Path> paths = Files.list(backupDirectory)) {
            paths.filter(Files::isDirectory)
                 .filter(path -> path.getFileName().toString().startsWith("backup_"))
                 .sorted((p1, p2) -> p2.getFileName().compareTo(p1.getFileName())) // Most recent first
                 .forEach(backups::add);
        }
        return backups;
    }

    // Format file size for display
    public String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    // Get data directory
    public Path getDataDirectory() {
        return dataDirectory;
    }

    public Path getBackupDirectory() {
        return backupDirectory;
    }
}
