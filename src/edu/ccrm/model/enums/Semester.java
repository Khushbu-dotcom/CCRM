package edu.ccrm.model.enums;

/**
 * Represents academic semesters with their associated year.
 * Demonstrates enum usage in the domain model.
 */
public enum Semester {
    FALL_2024(2024, "Fall"),
    SPRING_2025(2025, "Spring"),
    SUMMER_2025(2025, "Summer"),
    FALL_2025(2025, "Fall"),
    SPRING_2026(2026, "Spring"),
    SUMMER_2026(2026, "Summer");

    private final int year;
    private final String season;

    Semester(int year, String season) {
        this.year = year;
        this.season = season;
    }

    public int getYear() {
        return year;
    }

    public String getSeason() {
        return season;
    }

    @Override
    public String toString() {
        return season + " " + year;
    }

    /**
     * Get the next semester in sequence.
     */
    public Semester next() {
        return switch (this) {
            case FALL_2024 -> SPRING_2025;
            case SPRING_2025 -> SUMMER_2025;
            case SUMMER_2025 -> FALL_2025;
            case FALL_2025 -> SPRING_2026;
            case SPRING_2026 -> SUMMER_2026;
            case SUMMER_2026 -> SUMMER_2026; // Last semester
        };
    }
}
