package edu.ccrm.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class for Person entities.
 * Demonstrates inheritance, encapsulation, and abstraction.
 */
public abstract class Person {
    protected String id;
    protected String fullName;
    protected String email;
    protected LocalDateTime createdAt;
    protected boolean active;

    protected Person(String id, String fullName, String email) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.fullName = Objects.requireNonNull(fullName, "Full name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return active;
    }

    // Setters
    public void setFullName(String fullName) {
        this.fullName = Objects.requireNonNull(fullName, "Full name cannot be null");
    }

    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "Email cannot be null");
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Abstract method to get the person's type.
     * Demonstrates abstract method usage.
     */
    public abstract String getPersonType();

    /**
     * Abstract method to get display information.
     * Demonstrates polymorphism through method overriding.
     */
    public abstract String getDisplayInfo();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s{id='%s', fullName='%s', email='%s', active=%s}", 
                           getPersonType(), id, fullName, email, active);
    }
}
