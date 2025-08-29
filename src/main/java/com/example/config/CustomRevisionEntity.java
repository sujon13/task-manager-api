package com.example.config;

import jakarta.persistence.Entity;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import java.time.LocalDateTime;

@Setter
@Entity
@RevisionEntity(CustomRevisionListener.class) // listener to populate fields
public class CustomRevisionEntity extends DefaultRevisionEntity {

    private String updatedBy;

    private LocalDateTime updatedAt;

    // getters and setters
}
