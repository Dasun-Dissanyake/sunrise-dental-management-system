package com.sunrisedental.dto;

import com.sunrisedental.model.Dentist;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Dentist details.
 */
public class DentistResponse {

    private Long id;
    private String dentistNumber;
    private String fullName;
    private String specialization;
    private String contactNumber;
    private boolean active;
    private LocalDateTime createdAt;

    public DentistResponse() {
    }

    public DentistResponse(Long id, String dentistNumber, String fullName, String specialization, String contactNumber, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.dentistNumber = dentistNumber;
        this.fullName = fullName;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static DentistResponse fromDentist(Dentist dentist) {
        if (dentist == null) {
            return null;
        }
        return new DentistResponse(
                dentist.getId(),
                dentist.getDentistNumber(),
                dentist.getFullName(),
                dentist.getSpecialization(),
                dentist.getContactNumber(),
                dentist.isActive(),
                dentist.getCreatedAt()
        );
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
