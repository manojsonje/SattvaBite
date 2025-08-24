package com.sattvabite.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Base DTO class with common fields for all DTOs.
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseDto {
    protected String id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected Long version;
    
    /**
     * Gets the ID of the entity.
     * @return the ID as a String
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the ID of the entity.
     * @param id the ID to set
     */
    public void setId(String id) {
        this.id = id;
    }
}
