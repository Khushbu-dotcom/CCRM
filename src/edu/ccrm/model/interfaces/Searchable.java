package edu.ccrm.model.interfaces;

import java.util.function.Predicate;

/**
 * Generic interface for entities that can be searched.
 * Demonstrates generic interface usage.
 * @param <T> the type of entity being searched
 */
public interface Searchable<T> {
    /**
     * Search for entities matching the given predicate.
     * @param predicate the search criteria
     * @return array of matching entities
     */
    T[] search(Predicate<T> predicate);

    /**
     * Find a single entity matching the given predicate.
     * @param predicate the search criteria
     * @return the matching entity or null if not found
     */
    default T find(Predicate<T> predicate) {
        T[] results = search(predicate);
        return results.length > 0 ? results[0] : null;
    }

    /**
     * Check if any entity matches the given predicate.
     * @param predicate the search criteria
     * @return true if at least one entity matches
     */
    default boolean exists(Predicate<T> predicate) {
        return search(predicate).length > 0;
    }
}
