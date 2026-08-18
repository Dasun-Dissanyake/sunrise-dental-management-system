package com.sunrisedental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * JPA Dentist entity representing clinic dental practitioners in Sunrise Dental Management System.
 */
@Entity
@Table(name = "dentists", indexes = {
        @Index(name = "idx_dentist_number", columnList = "dentist_number"),
        @Index(name = "idx_dentist_full_name", columnList = "full_name")
})
public class Dentist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dentist_number", nullable = false, unique = true, length = 20)
    private String dentistNumber;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Specialization is required")
    @Column(nullable = false, length = 100)
    private String specialization;

    @NotBlank(message = "Contact number is required")
    @Column(name = "contact_number", nullable = false, length = 20)
    private String contactNumber;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Dentist() {
    }

    public Dentist(String dentistNumber, String fullName, String specialization, String contactNumber) {
        this.dentistNumber = dentistNumber;
        this.fullName = fullName;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDentistNumber() {
        return dentistNumber;
    }

    public void setDentistNumber(String dentistNumber) {
        this.dentistNumber = dentistNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
