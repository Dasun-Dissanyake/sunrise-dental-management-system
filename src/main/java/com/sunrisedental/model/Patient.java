package com.sunrisedental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA Patient entity representing dental clinic patients in Sunrise Dental Management System.
 */
@Entity
@Table(name = "patients", indexes = {
        @Index(name = "idx_patient_number", columnList = "patient_number"),
        @Index(name = "idx_full_name", columnList = "full_name"),
        @Index(name = "idx_contact_number", columnList = "contact_number")
})
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_number", nullable = false, unique = true, length = 20)
    private String patientNumber;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String address;

    @NotBlank(message = "Contact number is required")
    @Column(name = "contact_number", nullable = false, length = 20)
    private String contactNumber;

    @Column(length = 100)
    private String email;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 20)
    private String gender;

    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @Column(nullable = false)
    private boolean active = true;

    public Patient() {
    }

    public Patient(String patientNumber, String fullName, String address, String contactNumber,
                   String email, LocalDate dateOfBirth, String gender) {
        this.patientNumber = patientNumber;
        this.fullName = fullName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        if (this.registrationDate == null) {
            this.registrationDate = LocalDateTime.now();
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
