package com.sattvabite.order.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a sequence counter for generating unique order numbers.
 */
@Document(collection = "sequence")
public class Sequence {
    @Id
    private String id;
    
    /**
     * The current sequence value.
     */
    private int sequence;
    
    public Sequence() {
        // Default constructor
    }
    
    public Sequence(String id, int sequence) {
        this.id = id;
        this.sequence = sequence;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Gets the current sequence value.
     * 
     * @return the current sequence value
     */
    public int getSequence() {
        return sequence;
    }
    
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
    
    /**
     * Increments and returns the next sequence value.
     * 
     * @return the next sequence value
     */
    public int getNextSequence() {
        return ++sequence;
    }
    
    /**
     * Creates a new Sequence instance with the given ID and initial sequence value.
     * 
     * @param id the sequence ID
     * @param initialValue the initial sequence value
     * @return a new Sequence instance
     */
    public static Sequence create(String id, int initialValue) {
        return new Sequence(id, initialValue);
    }
}
