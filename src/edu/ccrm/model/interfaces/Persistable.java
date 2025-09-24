package edu.ccrm.model.interfaces;

/**
 * Interface for entities that can be persisted to storage.
 * Demonstrates interface usage in the domain model.
 */
public interface Persistable {
    /**
     * Get a unique identifier for this entity.
     * @return the unique identifier
     */
    String getId();

    /**
     * Convert this entity to a CSV representation.
     * @return CSV string representation
     */
    String toCSV();

    /**
     * Create an entity from CSV data.
     * @param csvData the CSV string data
     * @return the created entity
     */
    static Persistable fromCSV(String csvData) {
        throw new UnsupportedOperationException("fromCSV must be implemented by implementing classes");
    }
}
